<%@page import="java.io.DataOutputStream, com.itextpdf.text.DocumentException,java.io.DataOutput,com.itextpdf.text.pdf.PdfPTable"%>
<%@page import="com.itextpdf.text.pdf.PdfWriter"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="com.itextpdf.text.Document"%>       

<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<%
response.setContentType("application/pdf");
Document document = new Document();

String str=".";
String act= request.getParameter("act");
if( act==null ) act="";

MeetingE meeting = null;
java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
for (int i = 0; i < meetings.size(); i++)
    if (meetings.get(i).getUid().equals(request.getParameter("mid"))) {
        meeting = meetings.get(i);
        break;
    }
Meeting meetingInfo = yearPlanUtil.getMeeting(user,meeting.getRefId());
java.util.List<Activity> _activities = meetingInfo.getActivities();
java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = meetingInfo.getMeetingInfo();

if( act.equals("isActivity") )
    str = meetingInfoItems.get("detailed activity plan").getStr();
else if( act.equals("isMaterials") )
    str= meetingInfoItems.get("materials").getStr();
else if( act.equals("isOverview") )
    str =meetingInfoItems.get("overview").getStr();


try{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		document.add(new com.itextpdf.text.Paragraph(str));
		document.close();
		

}catch(DocumentException e){
e.printStackTrace();
}

%>