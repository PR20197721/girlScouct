<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

       <a href="/content/girlscouts-vtk/en/vtk.plan.html">Back to Plan</a>
       <div id="errInfo"></div>
       
       <%

       		String meetingPath = request.getParameter("mid");
       		java.util.List <MeetingE> meetings= troop.getYearPlan().getMeetingEvents();
       		
       		java.util.StringTokenizer calT = null;
       		if( troop.getYearPlan().getSchedule() !=null){
       			String schedStr = troop.getYearPlan().getSchedule().getDates();
       			calT= new java.util.StringTokenizer( schedStr, ",");
       		} 
       		java.util.Calendar meetingDate=null;
       		for(int i=0;i<meetings.size();i++){
       			MeetingE meeting = meetings.get(i);
       			
       			if( meeting.getPath().equals( meetingPath )){
       				Meeting meetingInfo = meetingDAO.getMeeting(meeting.getRefId());
       				
       				if( calT !=null){ //sched
    					meetingDate = Calendar.getInstance();
    					meetingDate.setTimeInMillis(Long.parseLong(calT.nextToken()));
           		    }
       				
       				%><%@include file="include/meetingView.jsp" %><%
       				break;
       			}
       		
       			if( calT !=null){ calT.nextToken(); }
       		}
       %>
