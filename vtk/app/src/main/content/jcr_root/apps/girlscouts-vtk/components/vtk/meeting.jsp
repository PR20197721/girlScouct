<%@page import="java.util.Calendar"%>
<%@ page import="org.girlsscout.vtk.models.user.*, org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>


	   <%User user= (User)session.getValue("VTK_user");%>
       <%@include file="include/headerDev.jsi" %>         
       <script type="text/javascript" src="js/vtk/meeting.js"></script>
       <a href="/content/girlscouts-vtk/en/vtk.plan.html?ageLevel=brownie">Back to Plan</a>
       <div id="errInfo"></div>
       
       <%
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
       				Meeting meetingInfo = new MeetingDAOImpl().getMeeting(meeting.getRefId());
       				
       				if( calT !=null){ //sched
    					meetingDate = Calendar.getInstance();
    					meetingDate.setTimeInMillis(Long.parseLong(calT.nextToken()));
           		    }
       				
       				%><%@include file="include/meetingView.jsi" %><%
       				break;
       			}
       		
       			if( calT !=null){ calT.nextToken(); }
       		}
       %>
       
       <%@include file="include/footer.jsi" %>
       <%!
       java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
       %>