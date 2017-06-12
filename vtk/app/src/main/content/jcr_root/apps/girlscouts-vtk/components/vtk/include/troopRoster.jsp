<%@ page import="com.itextpdf.tool.xml.pipeline.css.CSSResolver,
java.io.ByteArrayInputStream,
com.itextpdf.text.pdf.PdfWriter,
     com.itextpdf.text.Document,
 com.itextpdf.text.DocumentException,
 com.itextpdf.text.pdf.PdfWriter,
 com.itextpdf.tool.xml.XMLWorker,
 com.itextpdf.tool.xml.XMLWorkerHelper,
 com.itextpdf.tool.xml.css.CssFile,
 com.itextpdf.tool.xml.css.StyleAttrCSSResolver,
 com.itextpdf.tool.xml.html.Tags,
com.itextpdf.tool.xml.parser.XMLParser,
 com.itextpdf.tool.xml.pipeline.css.CSSResolver,
 com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline,
 com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline,
 com.itextpdf.tool.xml.pipeline.html.HtmlPipeline,
com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline,
 com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext,
java.nio.charset.Charset,
 java.io.File,
com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline,
 java.io.FileInputStream,
 java.io.FileOutputStream,
 java.io.IOException,
com.itextpdf.text.pdf.*,
 com.itextpdf.tool.xml.pipeline.html.*,
    com.itextpdf.tool.xml.*, com.itextpdf.tool.xml.pipeline.*,com.itextpdf.text.html.simpleparser.StyleSheet,java.io.StringReader,com.itextpdf.text.html.simpleparser.HTMLWorker,com.itextpdf.text.DocumentException,com.itextpdf.text.pdf.PdfWriter,java.io.ByteArrayOutputStream,java.util.Set,java.util.HashSet,com.itextpdf.text.pdf.PdfWriter,com.itextpdf.text.Document,java.util.List, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp"%>

<% 

    SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd, yyyy");
response.setContentType("application/pdf");
	List<Contact> contacts =
	contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");
/*
Document document = new Document(); 



StringBuffer pdfData= new StringBuffer("<html><head>");

pdfData.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/etc/designs/girlscouts-vtk/clientlibs/css/js.css\">");
pdfData.append("<style>h1 {color: red;}</style></head><body>");
pdfData.append("<h1>hello jc</h1>");

pdfData.append("<div class=\"row\">");
	pdfData.append("<div class=\"small-12 large-12 columns\">"+ (troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) )+"  "+troop.getSfTroopName() +"</div>");
	pdfData.append("</div>");

	pdfData.append("<div class=\"row\">");
	pdfData.append("<div class=\"small-12 large-12 columns\">"+FORMAT_MMM_dd_yyyy.format( new java.util.Date() )+"</div> ");
	pdfData.append("</div>");
	
	pdfData.append("<div class=\"row\">");
	pdfData.append("<div class=\"small-3 large-3 columns\">"+ (contacts==null ? "" : contacts.size() )+" GIRLS</div>");
	pdfData.append("<div class=\"small-9 large-9 columns\"></div>");
	pdfData.append("</div>");
	
	pdfData.append("<div class=\"row\">");
	pdfData.append("<div class=\"small-3 large-3 columns\">GIRL SCOUT</div>");
	pdfData.append("<div class=\"small-3 large-3 columns\">PARENT GUARDIAN</div>");
	pdfData.append("<div class=\"small-3 large-3 columns\">PARENT EMAIL</div>");
	pdfData.append("<div class=\"small-3 large-3 columns\">PARENT PHONE</div>");
	pdfData.append("</div>");



    if( contacts!=null)
	    for (Contact gsContact : contacts) {
	   	if( ! "Girl".equals( gsContact.getRole() ) ) continue;
	     Contact caregiver = VtkUtil.getSubContact( gsContact, 1);
	     	
	   	//check permission again:must be TL
	   	if(!(VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) ||
	             user.getApiConfig()==null || user.getApiConfig().getUser().getContactId().equals(caregiver.getContactId() ) ) ){ continue; }

		pdfData.append("<div class=\"row\">");
		pdfData.append("<div class=\"small-3 large-3 columns\">"+( gsContact.getFirstName() +" "+ gsContact.getRole()) +"</div>");
		pdfData.append("<div class=\"small-3 large-3 columns\">"+ (caregiver==null ? "" : (caregiver.getFirstName()==null ? "" : caregiver.getFirstName())) +" "+ ((caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ))+"</div>");
		pdfData.append("<div class=\"small-3 large-3 columns\">"+ gsContact.getEmail() +"</div>");
		pdfData.append("<div class=\"small-3 large-3 columns\">"+ (gsContact.getPhone() ==null ? "" : gsContact.getPhone())+"</div>");
		pdfData.append("</div>");
   }
    pdfData.append("</body></html>");
    */
Document document = new Document();
document.open();
//StringBuffer pdfData= new StringBuffer("hellow");

StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"2\">");
        sb.append("<tr>");
        sb.append("<th>Sr. No.</th>");
        sb.append("<th>Text Data</th>");
        sb.append("<th>Number Data</th>");
        sb.append("</tr>");
        for (int i = 0; i < 10; ) {
            i++;
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(i);
            sb.append("</td>");
            sb.append("<td>This is text data ");
            sb.append(i);
            sb.append("</td>");
            sb.append("<td>");
            sb.append(i);
            sb.append("</td>");
            sb.append("</tr>");
        }


        sb.append("</table>");

 String CSS = "tr { text-align: center; } th { background-color: lightgreen; padding: 3px; } td {background-color: lightblue;  padding: 3px; }";



		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter writer  =PdfWriter.getInstance(document, response.getOutputStream());
/*
		com.itextpdf.tool.xml.pipeline.css.CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);
 
        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
 
        // Pipelines
        PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
 
        // XML Worker
        XMLWorker worker = new XMLWorker(css, true);
        XMLParser p = new XMLParser(worker);
//p.parse(new ByteArrayInputStream("<table><tr><td>asdf</td></tr></table>".getBytes()));
//p.parse( new ByteArrayInputStream(sb.toString(), Charset.forName("UTF-8"));
 p.parse(new ByteArrayInputStream(sb.toString().getBytes()));
*/




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
        p.parse(new ByteArrayInputStream(sb.toString().getBytes()));


document.open();
document.add( (PdfPTable)elements.get(0) );
document.close();


	%>