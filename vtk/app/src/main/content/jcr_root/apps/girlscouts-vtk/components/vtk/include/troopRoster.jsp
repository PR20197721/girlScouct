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
com.itextpdf.text.pdf.PdfPTable,
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
    
        StringBuffer pdfData= new StringBuffer();
    
		pdfData.append("<table >");
		pdfData.append("<tr><td>"+ (troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) )+"  "+troop.getSfTroopName() +"</td></tr>");

    

        pdfData.append("<tr><td>"+FORMAT_MMM_dd_yyyy.format( new java.util.Date() )+"</td></tr> ");

        pdfData.append("<tr><td>"+ (contacts==null ? "" : contacts.size() )+" GIRLS</td></tr>");

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
    

            pdfData.append("<tr><td>"+( gsContact.getFirstName() +" "+ gsContact.getRole()) +"</td>");
            pdfData.append("<td>"+ (caregiver==null ? "" : (caregiver.getFirstName()==null ? "" : caregiver.getFirstName())) +" "+ ((caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ))+"</td>");
            pdfData.append("<td>"+ gsContact.getEmail() +"</td>");
            pdfData.append("<td>"+ (gsContact.getPhone() ==null ? "" : gsContact.getPhone())+"</td></tr>");

       }

 		pdfData.append("</table></td></tr></table>");

       Document document = new Document();
String CSS = "tr { text-align: center; } th { background-color: #efefef; padding: 3px; } td {text-align:left; font-size:10px; background-color: #FFFFFF;  padding: 3px; }";
    


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