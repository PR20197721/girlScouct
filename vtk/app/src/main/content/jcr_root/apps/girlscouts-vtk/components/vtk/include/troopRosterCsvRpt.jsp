<%@ page import="
    com.day.text.csv.Csv,
 	org.girlscouts.vtk.ejb.*,
    java.util.*,
	org.girlscouts.vtk.models.*,
	org.girlscouts.vtk.dao.*,
	java.io.StringWriter"
%>

<%@include file="/libs/foundation/global.jsp" %>
<%@include file="session.jsp"%>
<%

	SimpleDateFormat FORMAT_MMM_dd_yyyy = new SimpleDateFormat("MMM dd, yyyy");
    response.setHeader("Content-Encoding", "UTF-8");
    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition","inline; filename=TroopRoster.csv");


	StringWriter writer = new StringWriter();
	Csv csvWriter = new Csv();
	csvWriter.writeInit(writer);

	List<Contact> contacts = (List<Contact>) session.getAttribute("vtk_cachable_contacts");

	csvWriter.writeRow((troop.getSfTroopAge().substring( troop.getSfTroopAge().indexOf("-") +1) )+"  "+troop.getSfTroopName());
	csvWriter.writeRow(FORMAT_MMM_dd_yyyy.format( new java.util.Date() ) );
	csvWriter.writeRow((contacts==null ? "" : contacts.size() ) + " Girls");


    if( contacts!=null)
            for (Contact gsContact : contacts) {

                if( ! "Girl".equals( gsContact.getRole() ) ) continue;
                 Contact caregiver = VtkUtil.getSubContact( gsContact, 1);
        
                //check permission again:must be TL
                if(!(VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) ||
                         user.getApiConfig()==null || user.getApiConfig().getUser().getContactId().equals(caregiver.getContactId() ) ) ){ continue; }

                csvWriter.writeRow( ( gsContact.getFirstName() +" "+ gsContact.getRole()) +","+
                    ( caregiver==null ? "" : (caregiver.getFirstName()==null ? "" : caregiver.getFirstName()) ) 
                    	+" "+ ((caregiver.getLastName() ==null ? "" :caregiver.getLastName()  ) )+","+
                    gsContact.getEmail() +","+
                    (gsContact.getPhone() ==null ? "" : gsContact.getPhone()) 
                );

           }
	csvWriter.close();

	writer.close();
	String csvContents = writer.toString();
    out.println(csvContents);




%>