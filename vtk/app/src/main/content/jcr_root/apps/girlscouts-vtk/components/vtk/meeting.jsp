<%@page import="java.util.Calendar"%>
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>


	   <%User user= (User)session.getValue("VTK_user");%>
       <a href="/content/girlscouts-vtk/en/vtk.plan.html?ageLevel=brownie">Back to Plan</a>
       <div id="errInfo"></div>
       
       <%
			MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);

       		String meetingPath = request.getParameter("mid");
       		java.util.List <MeetingE> meetings= user.getYearPlan().getMeetingEvents();
       		
       		java.util.StringTokenizer calT = null;
       		if( user.getYearPlan().getSchedule() !=null){
       			String schedStr = user.getYearPlan().getSchedule().getDates();
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
       
       <%!
       java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
       %>