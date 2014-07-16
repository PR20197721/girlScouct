<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<% 
	MeetingE meeting = null;
	java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
	for(int i=0;i<meetings.size();i++)
		if( meetings.get(i).getUid().equals( request.getParameter("mid")) ) 
		{
			meeting= meetings.get(i);
			break;
		}

    
	Meeting meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
	java.util.List <Activity> _activities = meetingInfo.getActivities();
	java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();

%>


<%if( request.getParameter("isOverview")!=null ){%>
	<h3>Overview:</h3><%=meetingInfoItems.get("overview").getStr() %> 
<%}else if( request.getParameter("isActivity")!=null ){%>
	
	
	<%

	java.util.Iterator itr1=  meetingInfoItems.keySet().iterator(); 
	while( itr1.hasNext()){
		String name= (String) itr1.next();
		if( name.trim().toLowerCase().equals("overview")) continue;
		if( name.trim().toLowerCase().equals("meeting id")) continue;
%>
			<h3><%=name %></h3><%=meetingInfoItems.get(name).getStr() %>
<%
	}

%>
<%}else if( request.getParameter("isAgenda")!=null ){


	//java.util.List <Activity> _activities = meetingInfo.getActivities();
	for(int ii=0;ii< _activities.size();ii++){ 
		Activity _activity = _activities.get(ii);
		if( ii == Integer.parseInt( request.getParameter("isAgenda")  ) ){
			
			%>
			<%@include file="include/editActivity.jsp" %> 
			<%
			
			break;
		}
	}
	
 }%>