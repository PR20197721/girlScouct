<%@page
	import="java.io.DataOutputStream, com.itextpdf.text.DocumentException,java.io.DataOutput,com.itextpdf.text.pdf.PdfPTable"%>
<%@page import="com.itextpdf.text.pdf.PdfWriter"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="com.itextpdf.text.Document"%>

<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>

<%
	response.setContentType("application/pdf");
	Document document = new Document(); 

	String str = ".";
	String act = request.getParameter("act");
	if (act == null)
		act = "";

	MeetingE meeting = null;
	java.util.List<MeetingE> meetings = troop.getYearPlan()
			.getMeetingEvents();
	for (int i = 0; i < meetings.size(); i++)
		if (meetings.get(i).getUid()
				.equals(request.getParameter("mid"))) {
			meeting = meetings.get(i);
			break;
		}
	Meeting meetingInfo = yearPlanUtil.getMeeting(user,troop,
			meeting.getRefId());
	//java.util.List<Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = meetingInfo
			.getMeetingInfo();

	if (act.equals("isActivity")) {

		//str += meetingInfoItems.get("detailed activity plan").getStr();
      List<Activity> activities = meetingInfo.getActivities();
                Collections.sort(activities, new Comparator<Activity>() {
                    public int compare(Activity activity1, Activity activity2) {
                        return activity1.getActivityNumber() - activity2.getActivityNumber();
                    }
                });

                StringBuilder builder = new StringBuilder();
                for (Activity activity : activities) {
                    builder.append("<p><b>Activity " + Integer.toString(activity.getActivityNumber()));
                    builder.append(": " + activity.getName() + "</b></p>");

                    String description = activity.getIsOutdoor() ? activity.getActivityDescription_outdoor() :  activity.getActivityDescription();
                    if (!description.contains("Time Allotment")) {
                        builder.append("<p style=\"font-family: tahoma, arial, helvetica, sans-serif; font-size: 12px;\"><b>Time Allotment</b></p>");
                        builder.append("<p>" + Integer.toString(activity.getDuration()) + " minutes");
                    }
                    builder.append(description+"</br>");
                    
                }
                str += builder.toString();
	} else if (act.equals("isMaterials"))
		str = meetingInfoItems.get("materials").getStr();
	else if (act.equals("isOverview")) {
		str = "<h2>" + meetingInfo.getName() + ": introduction</h2>";
		str += meetingInfoItems.get("overview").getStr();
	} else if (act.equals("isAgenda")){
		java.util.List<Activity> _activities = meetingInfo.getActivities();
		if( meeting.getMeetingInfo()!=null){
			_activities = meeting.getMeetingInfo().getActivities();
		}
		str="";
		for(int z=0;z<_activities.size();z++){
			
			str+="<div style=\"background-color:yellow;\"><br/><br/><br/><b>Activity# "+(z+1)+" Name:"+_activities.get(z).getName() +"</b></div>"+
			    "<div style=\"background-red;\">Duration:"+_activities.get(z).getDuration()+"</div>"+
			    "<div>"; 			 
			 if( _activities.get(z).getIsOutdoor() ){ 
			     str+=_activities.get(z).getActivityDescription_outdoor() ;
			  }else{
			     str+=_activities.get(z).getActivityDescription();
			  } 
			str+="</div>";
		}
		
	}
	try {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();
		com.itextpdf.text.html.simpleparser.HTMLWorker htmlWorker = new com.itextpdf.text.html.simpleparser.HTMLWorker(
				document);
		htmlWorker.parse(new java.io.StringReader(str));
		document.close();
	} catch (DocumentException e) {
		e.printStackTrace();
	}
%>