<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<% 
	MeetingE meeting = null;
	java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
	for(int i=0;i<meetings.size();i++)
		if( meetings.get(i).getUid().equals( request.getParameter("mid"))) {
			meeting= meetings.get(i);
			break;
		}  
	Meeting meetingInfo = yearPlanUtil.getMeeting( user, meeting.getRefId() );
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();
%> 

<%if( request.getParameter("isOverview")!=null ){%>
	<span class="editable_textarea" id="editMeetingOverview"><%=meetingInfoItems.get("overview").getStr() %></span>
<%}else if( request.getParameter("isActivity")!=null ){%>
	<span class="editable_textarea" id="editMeetingActivity"><%=meetingInfoItems.get("detailed activity plan").getStr() %> </span>
<%}else if( request.getParameter("isMaterials")!=null ){%>
	<span class="editable_textarea" id="editMeetingMaterials"><%=meetingInfoItems.get("materials").getStr() %></span> 
<%}else if( request.getParameter("isAgenda")!=null ){


	//java.util.List <Activity> _activities = meetingInfo.getActivities();
	try {
		meetingUtil.sortActivity(_activities);
	} catch(Exception e){e.printStackTrace();}
	
	for(int ii=0;ii< _activities.size();ii++){ 
		Activity _activity = _activities.get(ii);
		if(ii == Integer.parseInt(request.getParameter("isAgenda"))){	%>
			<%@include file="include/modals/modal_agenda_edit.jsp" %> 
			<% break; }
	}
} %>
