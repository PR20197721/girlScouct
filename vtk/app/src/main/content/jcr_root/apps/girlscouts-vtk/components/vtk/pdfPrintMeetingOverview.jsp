<%@page import="com.itextpdf.text.Document,
                com.itextpdf.text.DocumentException,
                com.itextpdf.text.pdf.PdfWriter,
                javax.jcr.Node,
                javax.jcr.Session,
                java.io.ByteArrayOutputStream, org.slf4j.Logger, org.slf4j.LoggerFactory, java.util.Map" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%
    Logger vtklog = LoggerFactory.getLogger(this.getClass().getName());
    response.setContentType("application/pdf");
    Document document = new Document();
    StringBuilder output = new StringBuilder();
    String meeting_overview = "", meeting_name = "";
    String meetingPath = request.getParameter("meetingPath");
    if (meetingPath == null) {
        output.append("Meeting overview not found");
    } else {
        Session jcr_session = null;
        Node meetingNode = null, meetingInfoNode = null;
        try {
            jcr_session = resourceResolver.adaptTo(Session.class);
            meetingNode = jcr_session.getNode(meetingPath);
            meeting_name = meetingNode.getProperty("name").getString();
            meetingInfoNode = jcr_session.getNode(meetingPath + "/meetingInfo/overview");
            meeting_overview = meetingInfoNode.getProperty("str").getString();

        } catch (Exception e) {
            vtklog.error("Error Occurred: ", e);
        }
    }
    if (!"".equals(meeting_name)) {
        output.append("<h2>" + meeting_name + ": introduction</h2>");
        output.append(meeting_overview);
    }
    try {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        com.itextpdf.text.html.simpleparser.HTMLWorker htmlWorker = new com.itextpdf.text.html.simpleparser.HTMLWorker(
                document);
        htmlWorker.parse(new java.io.StringReader(output.toString()));
        document.close();
    } catch (DocumentException e) {
        e.printStackTrace();
    }
%>