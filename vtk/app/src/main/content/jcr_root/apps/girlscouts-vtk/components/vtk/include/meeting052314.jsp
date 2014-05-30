<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlsscout.vtk.models.user.*, org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>
  
<a href="javascript:void(0)" onclick="newActivity()">Add Activity</a> ||
<a href="javascript:void(0)" onclick="newLocCal()">Location&Calendar</a>

    <%
    //String yearPlanId= request.getParameter("planId");
    User user  = (User) session.getValue("VTK_user");
    //out.println("USER asdfasdfad "+ user.getId());
    
    
    java.util.Iterator <MeetingE> meetings = user.getYearPlan().getMeetingEvents().listIterator();
    String yearPlanId= user.getYearPlan().getId();
    
    
    		/*
    if( request.getParameter("isRefresh")!=null ){ //load from cache
    	
    	
    	meetings = ((java.util.List<MeetingE>) session.getValue("VTK_meetings")).listIterator();
    	System.err.println("REFRESHED meetings "+ (meetings==null)  );
    }else{ //pull
    	
    	
 
    	MeetingDAO meetingDAO = new MeetingDAOImpl();
    	//java.util.List <MeetingE> _meetings = meetingDAO.getAllUsersEventMeetings( (User) session.getValue("VTK_user") ,yearPlanId);
    java.util.List <MeetingE> _meetings = user.getYearPlan().getMeetingEvents();
    	meetings = _meetings.listIterator();
    	//session.putValue("VTK_meetings", _meetings);
    	
    }
*/
    %>
    
<h1>Meeting   </h1>
  <ul id="sortable123" >

<% 

	

	int count=0;



//CALENDAR DATES
java.util.StringTokenizer calT = null;
if( user.getYearPlan().getSchedule() !=null){
String schedStr = user.getYearPlan().getSchedule().getDates();
calT= new java.util.StringTokenizer( schedStr, ",");
}

    while(meetings.hasNext()){
    	
    	count++;
    	
    	MeetingE meetingE = meetings.next();
    	
    	Meeting meeting = new MeetingDAOImpl().getMeeting(meetingE.getRefId());
   
    	%>
    	
    
    <span style="background-color:green; color:#FFF; padding:20px;">
    	<% if( calT !=null){ //sched
 					java.util.Date tmpDate = new java.util.Date();
 					tmpDate.setTime(Long.parseLong(calT.nextToken()));
 					%> <%= tmpDate %>  <%  
 			}else{
 				%> <%= count %>  <% 
 			}
 				%>
 			</span>
 			
 			
 				
 				
 				
 			<li value="<%=count%>">
 			
 				
 				
 				
 			    <div style="float:right;width:40px;background-color:#efefef;border:1px solid #000;"><%=meeting.getAidTags() %></div>
 				
 				<%if( meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true")){ %>
 					<span style="color:#FFF; background-color:red;">CANCELLED</span>
 				<%} %>
 				
 				
 				Meeting: <%=meeting.getId() %> - <%=meeting.getName() %> 
 				<%=meeting.getBlurb() %>
 				
 				
 				<a href="meeting.jsp?mid=<%=meetingE.getPath()%>">View Meeting</a>
 				</li>
			
    	<% 
    }
    %>
</ul>
<script>
  $("#sortable123").sortable(
        				   
        				   {
        				       
        				        update:  function (event, ui) {
        				        	doUpdMeeting()
        				        }
        				}
        		   );
        		   </script>