<%@page
	import="java.io.DataOutputStream, com.itextpdf.text.DocumentException,java.io.DataOutput,com.itextpdf.text.pdf.PdfPTable"%>
<%@page import="com.itextpdf.text.pdf.PdfWriter"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="com.itextpdf.text.Document, java.io.*, java.net.*"%>
<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
	<%
    	response.setContentType("application/pdf");
	Document document = new Document(); 
    try {
			URL oracle = new URL("http://localhost:4503/content/girlscouts-vtk/en/vtk.include.troopRoster.html");
	        BufferedReader in = new BufferedReader(
	        new InputStreamReader(oracle.openStream()));
	
	        StringBuffer binputLine= new StringBuffer();
        	String inputLine="";
	        while ((inputLine = in.readLine()) != null)
	           binputLine.append(inputLine);
	        in.close();
        



		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		com.itextpdf.text.html.simpleparser.HTMLWorker htmlWorker = new com.itextpdf.text.html.simpleparser.HTMLWorker(
				document);
        htmlWorker.parse(new java.io.StringReader(binputLine.toString()));
		document.close();
	} catch (DocumentException e) {
		e.printStackTrace();
	}
%>