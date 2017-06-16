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
 com.itextpdf.text.PageSize,
 com.itextpdf.text.pdf.PdfPTable,
 com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline,
 java.io.FileInputStream,
 java.io.FileOutputStream,
 java.io.IOException,
 com.itextpdf.text.pdf.*,
 com.itextpdf.tool.xml.pipeline.html.*,
 com.itextpdf.tool.xml.*, 
 com.itextpdf.tool.xml.pipeline.*,
 com.itextpdf.text.html.simpleparser.StyleSheet,
 java.io.StringReader,
 com.itextpdf.text.html.simpleparser.HTMLWorker,
 com.itextpdf.text.DocumentException,
 com.itextpdf.text.pdf.PdfWriter,
 java.io.ByteArrayOutputStream,
 java.util.Set,java.util.HashSet,com.itextpdf.text.pdf.PdfWriter,
 com.itextpdf.text.Document,java.util.List, 
 org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,
 org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp"%>

<% 
	    response.setHeader("Content-Disposition", "inline; filename=\"TroopRoster.pdf\"");

        SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd, yyyy");
        response.setContentType("application/pdf");
        List<Contact> contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");
        StringBuffer pdfData= new StringBuffer();
    
        pdfData.append("<table cellspacing=\"0\" cellpadding=\"2\">");
		pdfData.append("<tr><td style=\"font-size:22px; text-align:center\">"+ (troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) )+"  "+troop.getSfTroopName() +"</td></tr>");
        pdfData.append("<tr><td style=\"font-size:14px;text-align:center;padding:0 0 30px 0;\">"+FORMAT_MMM_dd_yyyy.format( new java.util.Date() )+"<br /><br /></td></tr> ");
        pdfData.append("<tr><td style=\"padding:0 0 0px 0px; font-size:12px;\">&nbsp;&nbsp;&nbsp;&nbsp;"+ (contacts==null ? "" : contacts.size() )+" GIRLS</td></tr>");
        
        pdfData.append("<tr><td><table>");
        pdfData.append("<tr><th>GIRL SCOUT</th>");
        pdfData.append("<th>PARENT GUARDIAN</th>");
        pdfData.append("<th>PARENT EMAIL</th>");
        pdfData.append("<th>PARENT PHONE</th>");
        pdfData.append("</tr>");

        if( contacts!=null)
            for (Contact gsContact : contacts) {

            if( ! "Girl".equals( gsContact.getRole() ) ) continue;
             Contact caregiver = VtkUtil.getSubContact( gsContact, 1);
    
            //check permission again:must be TL
            if(!(VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) ||
                     user.getApiConfig()==null || user.getApiConfig().getUser().getContactId().equals(caregiver.getContactId() ) ) ){ continue; }

            pdfData.append("<tr><td style=\"width:20%;max-width:20%;\">"+( gsContact.getFirstName() +" "+ gsContact.getRole()) +"</td>");
            pdfData.append("<td style=\"width:20%;max-width:20%;\">"+ (caregiver==null ? "" : (caregiver.getFirstName()==null ? "" : caregiver.getFirstName())) +" "+ ((caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ))+"</td>");
            pdfData.append("<td style=\"width:40%;max-width:40%;\">"+ gsContact.getEmail() +"</td>");
            pdfData.append("<td style=\"width:20%;max-width:20%;\">"+ (gsContact.getPhone() ==null ? "" : gsContact.getPhone())+"</td>");
			pdfData.append("</tr>");

       }

 	    pdfData.append("</table></td></tr></table>");

        Document document = new Document(PageSize.A4, 5f, 5f, 10f, 5f);
        String CSS = "tr { text-align: center; } th { font-size:11px; background-color: #eee; padding: 3px 5px; text-align:left } td {text-align:left;  font-size:13px; padding: 3px 5px;}";
    
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter writer  =PdfWriter.getInstance(document, response.getOutputStream());
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
        p.parse(new ByteArrayInputStream(pdfData.toString().getBytes()));
        document.open();

        for(int i=0;i<elements.size();i++){
            document.add( (PdfPTable)elements.get(i) );
        }
        document.close();


	%>
