package org.girlscouts.vtk.ejb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.JcrCollectionHoldString;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.utils.VtkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

@Component
@Service(value = PdfUtil.class)
public class PdfUtil {

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	YearPlanUtil yearPlanUtil;
	
	@Reference
	ConfigManager configManager;
	
	@Reference
	private SessionFactory sessionFactory;
	
	private static Logger log = LoggerFactory.getLogger(PdfUtil.class);
	
	//servlet
	public void createPrintPdf(String act, Troop troop, User user, String mid, SlingHttpServletResponse response){
		try{
			log.debug("Creating pdf");
				generate( act,  troop,  user,  mid, response.getOutputStream());
			log.debug("PDF created");
		}catch(Exception e){e.printStackTrace();}
	}
	
	//email attachment
	public ByteArrayOutputStream createPrintPdf(String act, Troop troop, User user, String mid){
		ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
		try{
			generate( act,  troop,  user,  mid, outputStream);
		}catch(Exception e){e.printStackTrace();}
		return outputStream;
	}
	
	public void generate(String act, Troop troop, User user, String mid, java.io.OutputStream outputStream){
		log.debug("Generating PDf for act="+act+", user="+user.getSid()+", troop="+troop.getSfTroopName()+"-"+ troop.getSfTroopId()+", mid="+mid);
		MeetingE meeting = null;
		ResourceResolver resourceResolver = null;
		java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++){
			if (meetings.get(i).getUid().equals(mid)) {
				meeting = meetings.get(i);
				break;
			}
		}
		
		Meeting meetingInfo = null;
		try{
			meetingInfo= yearPlanUtil.getMeeting(user, troop, meeting.getRefId());
		}catch(Exception e){e.printStackTrace();}
		String gradeLevel = meetingInfo.getLevel().toLowerCase();
		Map<String, JcrCollectionHoldString> meetingInfoItems = meetingInfo.getMeetingInfo();
		
		String footer[] = new String[2];
		
		try {
			resourceResolver = sessionFactory.getResourceResolver();
			log.debug("resourceResolver="+resourceResolver);
			Resource res = resourceResolver.getResource("/content/vtkcontent/en/vtk-pdf/jcr:content/content/middle/par/vtk_pdf");
			log.debug("/content/vtkcontent/en/vtk-pdf/jcr:content/content/middle/par/vtk_pdf="+res);
			if (res != null) {
				Node node = res.adaptTo(Node.class);
				footer[0] = node.getProperty("footerLine1").getString();
				footer[1] = node.getProperty("footerLine2").getString();
			}
		} catch (Exception e) {
			log.error("Exception occured: ", e);
		}
		
		Document document = new Document(PageSize.A4, 20f, 20f, 20f, 50f);
		String CSS = "div{line-height:20px;position:relative;} li{line-height:20px;position:relative;} p{line-height:20px;position:relative;} table {margin-top:10px; margin-left:20px;margin-right:20px;} tr { text-align: center; } th { font-size:12px;} td {font-family: 'Trefoil Sans', Times, serif;text-align:left;font-size:12px;line-height:20px;position:relative;}";
		PdfWriter writer  = null;
		try{
			log.debug("Getting instance of PdfWriter");
			writer = PdfWriter.getInstance(document, outputStream);
			HeaderFooter event=  new HeaderFooter(footer, gradeLevel, resourceResolver);
	        writer.setPageEvent(event);
		}catch(Exception e){
			log.error("Exception occured: ", e);
		}
		
		//TODO: check if writer null
		log.debug("Adding css");
		CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);
 
        log.debug("Adding HTML");
        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        
        // Pipelines
        ElementList elements = new ElementList();
        ElementHandlerPipeline pdf = new ElementHandlerPipeline(elements, null);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
 
        log.debug("Adding XML Worker");
        // XML Worker
        XMLWorker worker = new XMLWorker(css, true);
        log.debug("Adding XML Parser");
        XMLParser p = new XMLParser(worker);
        
        document.open();
        int countElements =0;
		try{
			StringBuilder overViewData = new StringBuilder();
			if (act.equals("isOverview") || act.equals("isAll")) {
				log.debug("Generating PDf for meeting overview");
				overViewData.append("<table style=\"border:10px;padding-top:8px;\" style=\"font-family: 'Trefoil Sans'; font-size:12px;\">");
				overViewData.append("<tr><td style=\"padding-top:20px; padding-bottom:5px; text-transform: uppercase; color:#000; font-size:12px;font-family: 'Trefoil SemiBold', Times, serif;\">MEETING OVERVIEW</td></tr>");
				overViewData.append("<tr><td style=\"padding-bottom:20px; font-color:#000;font-size:22px;font-family: 'Trefoil Sans Regular'\">" + meetingInfo.getName() + "</td></tr>");
				overViewData.append("</table>");
				overViewData.append( "<p style=\"font-family: 'Trefoil Sans'; font-size:12px;\">"+ VtkUtil.fmtHtml(meetingInfoItems.get("overview").getStr()) +"</p>");
				p.parse(new ByteArrayInputStream(overViewData.toString().getBytes()));
				try{
					countElements = addElements(document, elements, countElements);
				}catch(Exception e){
					log.error("Exception occured while generating meeting overview pdf", e);
				}
				
			}

			StringBuilder agendaData= new StringBuilder();
			if (act.equals("isAgenda") || act.equals("isActivity")|| act.equals("isAll")) {
				log.debug("Generating PDf for meeting Agenda/Activity/All");
				agendaData.append("<table style=\"border:10px;padding-top:8px;\" style=\"font-family: 'Trefoil Sans'; font-size:12px;\">");
				agendaData.append("<tr><td  style=\"padding-top:20px; padding-bottom:5px; text-transform: uppercase; color:#000; font-size:12px;font-family: 'Trefoil SemiBold', Times, serif;\">AGENDA ACTIVITIES</td></tr>");
				agendaData.append("<tr><td style=\"padding-bottom:20px; font-color:#000;font-size:22px;font-family: 'Trefoil Sans Regular'\">" + meetingInfo.getName() + "</td></tr>");				
    			java.util.List<Activity> activities = meetingInfo.getActivities();
				Collections.sort(activities, new Comparator<Activity>() {
					public int compare(Activity activity1, Activity activity2) {
						return activity1.getActivityNumber() - activity2.getActivityNumber();
					}
				});

				for (Activity __activity : activities) {
					java.util.List<Activity> subActivities = __activity.getMultiactivities();
					Activity selectedActivity = VtkUtil.findSelectedActivity( subActivities );
                    if( selectedActivity ==null && (subActivities!=null &&  subActivities.size()>1)){                      
                    	agendaData.append("<tr><td ><b>Activity "+ __activity.getActivityNumber()+": Select Your Activity </b><br/></td></tr><tr><td>&nbsp; </td></tr>");                		
                    }else{
                    	selectedActivity = selectedActivity==null ? subActivities.get(0) : selectedActivity;
                    	String title = "";
						title += ("<tr><td><b>Activity " + __activity.getActivityNumber() ); 
                		if(subActivities.size()!=1 ){
                			title += (" - Choice "+ selectedActivity.getActivityNumber() );
                		}
                		title += (": ");
                		title+= ( subActivities.size()==1 ? __activity.getName() : selectedActivity.getName()) ;
                		title+=("</b></td></tr> <tr><td style=\"line-height:2px;position:relative;\">&nbsp;</td></tr>");
                		
                		agendaData.append( title );
                		agendaData.append("<tr><td>"+VtkUtil.fmtHtml(selectedActivity.getActivityDescription())+"</td></tr><tr><td style=\"line-height:2px;position:relative;\">&nbsp;</td></tr>");

                    }
				}
	
		
		
				agendaData.append("</table>");
				if(act.equals("isAll"))document.newPage();
				p.parse(new ByteArrayInputStream(agendaData.toString().getBytes()));
				try{
					countElements = addElements(document, elements, countElements);
				}catch(Exception e){
					log.error("Exception occured while generating Agenda/Activity/All pdf", e);
				}
			}
			
			StringBuilder materialsData = new StringBuilder();
			if (act.equals("isMaterials") || act.equals("isAll")) {
				log.debug("Generating PDf for meeting Materials");
				meetingInfoItems.get("materials").getStr();
				materialsData.append("<table style=\"border:10px;padding-top:8px;\" style=\"font-family: 'Trefoil Sans'; font-size:12px;\">");
				materialsData.append("<tr><td style=\"padding-top:20px; padding-bottom:5px; text-transform: uppercase; color:#000; font-size:12px;font-family: 'Trefoil SemiBold', Times, serif;\">MATERIALS LIST</td></tr>");
				materialsData.append("<tr><td style=\"padding-bottom:20px; font-color:#000;font-size:22px;font-family: 'Trefoil Sans Regular'\">" + meetingInfo.getName() + "</td></tr>");
				java.util.List<Activity>activities =  meetingInfo.getActivities();
				            Collections.sort(activities, new Comparator<Activity>() {
									public int compare(Activity activity1, Activity activity2) {
										return activity1.getActivityNumber() - activity2.getActivityNumber();
									}
								});

				            	for(Activity activity: activities){
									java.util.List<Activity> subActivities = activity.getMultiactivities();
				                    Activity selectedActivity = VtkUtil.findSelectedActivity( subActivities );
				                    if( selectedActivity ==null && (subActivities!=null &&  subActivities.size()>1)){
				                    	materialsData.append("<tr><td><b>Activity "+activity.getActivityNumber()  +" : Select Your Activity</b> </td></tr>");
				                		
				                    }else{
				                    	String materials = selectedActivity==null ? 
				                        		subActivities.get(0).getMaterials()
				                    			: selectedActivity.getMaterials();
				                        		
				                    	materialsData.append("<tr><td> <b><br/>Activity "+activity.getActivityNumber() );
				                		if(subActivities.size()!=1 ){
				                			materialsData.append(" - Choice "+selectedActivity.getActivityNumber() );
				                		}
				                		
				                		materialsData.append(" : "+(subActivities.size()==1 ? activity.getName(): selectedActivity.getName()) +"</b></td></tr>");
				                		materialsData.append("<tr><td style=\"line-height:20px;position:relative;\"> <br/>"+(materials ==null ? "" : materials)+"</td></tr>");
				                		
				                    }
				                    materialsData.append("<tr><td style=\"line-height:2px;position:relative;\">&nbsp;</td></tr>");
				            	}
							
				
				materialsData.append("</table>");
				if( act.equals("isAll") ) document.newPage();
				p.parse(new ByteArrayInputStream(materialsData.toString().getBytes()));
				try{
					countElements = addElements(document, elements, countElements);
				}catch(Exception e){
					log.error("Exception occured while generating Materials pdf", e);
				}
				
			}			
			try {
		        document.close();
			} catch (Exception e) {
				log.error("Exception occured while closing pdf document: ", e);
			}
		
		} catch (Exception e) {
			log.error("Exception occured: ", e);
		}finally {
			if(resourceResolver != null) {
				try {
					resourceResolver.close();
				}catch(Exception e) {
					log.error("Exception closing resource resolver",e);
				}
				
			}
		}
	}

	private int addElements( Document document, ElementList elements, int countElements ) throws Exception{
		for(int i=countElements;i<elements.size();i++){
    	if (elements.get(i) instanceof com.itextpdf.text.pdf.PdfPTable )
    		document.add( (PdfPTable)elements.get(i) );
    	else if( elements.get(i) instanceof  com.itextpdf.text.Paragraph)
    		document.add( (com.itextpdf.text.Paragraph)elements.get(i) );
    	else if( elements.get(i) instanceof  com.itextpdf.text.List )
    		document.add( (com.itextpdf.text.List)elements.get(i) );
    	countElements++;
		}
		return countElements;
	}
	    
	    public class HeaderFooter extends PdfPageEventHelper {
	       int pagenumber=0;
	        String footerTxtLine1="", footerTxtLine2, gradeLevel;
	        ResourceResolver rr;
	        Image header = null;
	        public HeaderFooter(String footer[], String _gradeLevel, ResourceResolver _rr){
	        	footerTxtLine1= footer[0];
	        	footerTxtLine2= footer[1];
	        	gradeLevel= _gradeLevel;
	        	rr = _rr;
	        	header = loadHeaderImage(_gradeLevel, rr);
	        }
	        
	        private Image loadHeaderImage(String _gradeLevel, ResourceResolver rr2) {
	        	 String imagePath = "/content/dam/girlscouts-vtkcontent/Print-PDF/"+ gradeLevel +"/topBanner.jpg";
	        	log.debug("Loading image from crx at {}",imagePath);
	            try{
	            	Resource assetRes = rr.resolve(imagePath);
	            	if(assetRes!= null) {
	            		Asset asset = assetRes.adaptTo(Asset.class);
	            		Rendition original = asset.getOriginal();
	            		InputStream is = original.getStream();
	            		Image img = Image.getInstance(IOUtils.toByteArray(is));
	            		img.setBorder(Image.NO_BORDER);
	            		return img;
	            	}			            
	            }catch(Exception e2) {
	            	log.error("Exception thrown loading {}",imagePath, e2);
	            }
	            return null;
			}

			@Override
	        public void onStartPage(PdfWriter writer, Document document) {
	        	pagenumber++;
	            
	        	try{
		            PdfPTable tabHead = new PdfPTable(1);
		            
		            tabHead.setWidthPercentage(100);
		            tabHead.setSpacingBefore(0f);
		            tabHead.setSpacingAfter(0f);
		           
		            tabHead.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		            PdfPCell cell;
		            cell = new PdfPCell();
		            cell.setBorder(PdfPCell.NO_BORDER);
		            tabHead.addCell(cell);
		            if(header != null) {
		            	tabHead.addCell(header);
		            }
		            document.add( tabHead);	            
		            LineSeparator separator = new LineSeparator();
		            Chunk linebreak = new Chunk(separator);
		            separator.setLineColor(BaseColor.BLACK);
		            
		        	Font ffont = new Font();
		        	ffont.setSize(7);
		            ColumnText.showTextAligned(writer.getDirectContent(),
		                    Element.ALIGN_CENTER,  new Phrase(linebreak),
		                    (document.left() + document.right()) / 2, document.bottom() - 8, 0);
		            
		        	ColumnText.showTextAligned(writer.getDirectContent(),
		                    Element.ALIGN_RIGHT , new Phrase(String.format("page %d", pagenumber), ffont),
		                    document.right(), document.bottom() - 38, 0);
		            ColumnText.showTextAligned(writer.getDirectContent(),
		                    Element.ALIGN_LEFT, new Paragraph(footerTxtLine1, ffont),
		                    document.left() , document.bottom() - 18, 0);
		        	
		        	ColumnText.showTextAligned(writer.getDirectContent(),
		                    Element.ALIGN_LEFT, new Phrase(footerTxtLine2, ffont),
		                    document.left() , document.bottom() - 28, 0);	        	
	        	}catch(Exception e){
	        		log.error("Exception occured: ", e);
	        	}
	        	
	        }

	        @Override
	        public void onEndPage(PdfWriter writer, Document document) {
	        }
	    }
}//edn main 


