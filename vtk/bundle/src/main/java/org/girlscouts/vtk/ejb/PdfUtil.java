package org.girlscouts.vtk.ejb;

import java.io.IOException;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.File;
import java.io.PrintWriter;
import java.rmi.ServerException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.dao.MeetingDAO;
import com.day.cq.commons.jcr.JcrUtil;
import java.io.DataOutputStream;
import com.itextpdf.text.DocumentException;
import java.io.DataOutput;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.Document;
import java.util.*;
import java.util.stream.IntStream;

import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.dao.*;
import org.girlscouts.vtk.ejb.*;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.utils.VtkUtil;
import java.io.IOException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.rmi.ServerException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.dao.MeetingDAO;

import com.day.cq.commons.jcr.JcrUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.*;
import com.itextpdf.tool.xml.parser.*;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import java.nio.charset.Charset;
import java.io.File;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.tool.xml.pipeline.html.*;
import com.itextpdf.tool.xml.*;
import com.itextpdf.tool.xml.pipeline.*;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import java.io.StringReader;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.draw.LineSeparator;
import javax.jcr.Node;

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
	
	//servlet
	public void createPrintPdf(String act, Troop troop, User user, String mid, SlingHttpServletResponse response){
		try{
				generate( act,  troop,  user,  mid, response.getOutputStream());
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
		
		MeetingE meeting = null;
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
					ResourceResolver resourceResolver = sessionFactory.getResourceResolver();//resolverFactory.getAdministrativeResourceResolver(null);
					Resource res = resourceResolver.getResource("/content/vtkcontent/en/vtk-pdf/jcr:content/content/middle/par/vtk_pdf");
					if (res != null) {
						Node node = res.adaptTo(Node.class);
						footer[0] = node.getProperty("footerLine1").getString();
						footer[1] = node.getProperty("footerLine2").getString();
					}
			} catch (Exception e) {
					e.printStackTrace();
			}
		
		Document document = new Document(PageSize.A4, 20f, 20f, 20f, 50f);
		String CSS = "div{line-height:20px;position:relative;} li{line-height:20px;position:relative;} p{line-height:20px;position:relative;} table {margin-top:10px; margin-left:20px;margin-right:20px;} tr { text-align: center; } th { font-size:12px;} td {font-family: 'Trefoil Sans', Times, serif;text-align:left;font-size:12px;line-height:20px;position:relative;}";
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter writer  = null;
		try{
				writer = PdfWriter.getInstance(document, outputStream); //response.getOutputStream());
				HeaderFooter event=  new HeaderFooter(footer, gradeLevel);
		        writer.setPageEvent(event);
		}catch(Exception e){e.printStackTrace();}
		
		//TODO: check if writer null

		CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);
 
        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
        
        // Pipelines
        ElementList elements = new ElementList();
        ElementHandlerPipeline pdf = new ElementHandlerPipeline(elements, null);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
 
        // XML Worker
        XMLWorker worker = new XMLWorker(css, true);
        XMLParser p = new XMLParser(worker);
        document.open();
        int countElements =0;
		try{
			StringBuilder overViewData = new StringBuilder();
			if (act.equals("isOverview") || act.equals("isAll")) {
				overViewData.append("<table style=\"border:10px;padding-top:8px;\" style=\"font-family: 'Trefoil Sans'; font-size:12px;\">");
				overViewData.append("<tr><td style=\"padding-top:20px; padding-bottom:5px; text-transform: uppercase; color:#000; font-size:12px;font-family: 'Trefoil SemiBold', Times, serif;\">MEETING OVERVIEW</td></tr>");
				overViewData.append("<tr><td style=\"padding-bottom:20px; font-color:#000;font-size:22px;font-family: 'Trefoil Sans Regular'\">" + meetingInfo.getName() + "</td></tr>");
				overViewData.append("</table>");
				overViewData.append( "<p style=\"font-family: 'Trefoil Sans'; font-size:12px;\">"+ VtkUtil.fmtHtml(meetingInfoItems.get("overview").getStr()) +"</p>");
				p.parse(new ByteArrayInputStream(overViewData.toString().getBytes()));
				try{
					countElements = addElements(document, elements, countElements);
				}catch(Exception e){e.printStackTrace();}
				
			}

			StringBuilder agendaData= new StringBuilder();
			if (act.equals("isAgenda") || act.equals("isActivity")|| act.equals("isAll")) {
				agendaData.append("<table style=\"border:10px;padding-top:8px;\" style=\"font-family: 'Trefoil Sans'; font-size:12px;\">");
				agendaData.append("<tr><td  style=\"padding-top:20px; padding-bottom:5px; text-transform: uppercase; color:#000; font-size:12px;font-family: 'Trefoil SemiBold', Times, serif;\">AGENDA ACTIVITIES</td></tr>");
				agendaData.append("<tr><td style=\"padding-bottom:20px; font-color:#000;font-size:22px;font-family: 'Trefoil Sans Regular'\">" + meetingInfo.getName() + "</td></tr>");
				
    			java.util.List<Activity> activities = meetingInfo.getActivities();
				Collections.sort(activities, new Comparator<Activity>() {
					public int compare(Activity activity1, Activity activity2) {
						return activity1.getActivityNumber() - activity2.getActivityNumber();
					}
				});

				StringBuilder builder = new StringBuilder();
				for (Activity __activity : activities) {

					String description = __activity.getActivityDescription();


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
				}catch(Exception e){e.printStackTrace();}
			}
			
			StringBuilder materialsData = new StringBuilder();
			if (act.equals("isMaterials") || act.equals("isAll")) {
				String mainMaterials = meetingInfoItems.get("materials").getStr();
				materialsData.append("<table style=\"border:10px;padding-top:8px;\" style=\"font-family: 'Trefoil Sans'; font-size:12px;\">");
				materialsData.append("<tr><td style=\"padding-top:20px; padding-bottom:5px; text-transform: uppercase; color:#000; font-size:12px;font-family: 'Trefoil SemiBold', Times, serif;\">MATERIALS LIST</td></tr>");
				materialsData.append("<tr><td style=\"padding-bottom:20px; font-color:#000;font-size:22px;font-family: 'Trefoil Sans Regular'\">" + meetingInfo.getName() + "</td></tr>");
				//materialsData.append("<tr><td>" + (mainMaterials ==null ? "" : mainMaterials)+"</td></tr>");
				
					
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
				}catch(Exception e){e.printStackTrace();}
				
			}
			
			try {
		        document.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	        public HeaderFooter(String footer[], String _gradeLevel){
	        	footerTxtLine1= footer[0];
	        	footerTxtLine2= footer[1];
	        	gradeLevel= _gradeLevel;
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
	            
	            String baseUrl = (String) configManager.getConfig("baseUrl"); 
	            Image img = Image.getInstance( baseUrl+ "/content/dam/girlscouts-vtkcontent/Print-PDF/"+ gradeLevel +"/topBanner.jpg");///Users/yakobal/Downloads/banner.jpeg");
	            img.setBorder(Image.NO_BORDER);
	            tabHead.addCell(img);
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
	        	
	        	}catch(Exception e){e.printStackTrace();}
	        	
	        }
	        
	        
	        @Override
	        public void onEndPage(PdfWriter writer, Document document) {
	        }
	    }
	    
	    
	    
}//edn main 


