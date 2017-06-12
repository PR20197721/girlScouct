<%@ page import="java.io.StringReader,com.itextpdf.text.html.simpleparser.HTMLWorker,com.itextpdf.text.DocumentException,com.itextpdf.text.pdf.PdfWriter,java.io.ByteArrayOutputStream,java.util.Set,java.util.HashSet,com.itextpdf.text.pdf.PdfWriter,com.itextpdf.text.Document,java.util.List, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp"%>
<%
    SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd, yyyy");
    response.setContentType("application/pdf");
	List<Contact> contacts =
	contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");
	Document document = new Document(); 
	
	StringBuffer pdfData= new StringBuffer("<div class=\"row\">");
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

	try{
	   ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, response.getOutputStream());
			document.open();
			HTMLWorker htmlWorker = new HTMLWorker(
					document);
	        htmlWorker.parse(new StringReader(pdfData.toString()));
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

	%>