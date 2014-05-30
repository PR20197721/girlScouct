<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlsscout.vtk.models.user.*, org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>
  
<a href="javascript:void(0)" onclick="newActivity()">Add Activity</a> ||
<a href="javascript:void(0)" onclick="newLocCal()">Location&Calendar</a>

    <%
  
    String yearPlanId= request.getParameter("planId");
    java.util.Iterator <MeetingE> meetings = null;
    		
    if( request.getParameter("isRefresh")!=null ){ //load from cache
    	
    	
    	meetings = ((java.util.List<MeetingE>) session.getValue("VTK_meetings")).listIterator();
    	System.err.println("REFRESHED meetings "+ (meetings==null)  );
    }else{ //pull
    	
    	
 
    	MeetingDAO meetingDAO = new MeetingDAOImpl();
    	java.util.List <MeetingE> _meetings = meetingDAO.getAllUsersEventMeetings( (User) session.getValue("VTK_user") ,yearPlanId);
    
    	meetings = _meetings.listIterator();
    	session.putValue("VTK_meetings", _meetings);
    	
    }

    %>
    
<h1>Meeting (planID:<%=yearPlanId %>)  </h1>
  <ul id="sortable123" >

<% 
	int count=0;
System.err.println(count);
    while(meetings.hasNext()){
    	
    	count++;
    	System.err.println(count +": "+ (meetings==null));
    	MeetingE meetingE = meetings.next();
    	
    	System.err.println( "REfId: "+(meetingE==null) );
    	Meeting meeting = new MeetingDAOImpl().getMeeting(meetingE.getRefId());
    	System.err.println("meeting is null "+ (meeting==null));
    	%>
    	
    	
 			<li value="<%=count%>">
 			
 			    <div style="float:right;width:40px;background-color:#efefef;border:1px solid #000;"><%=meeting.getAidTags() %></div>
 				<div style="width:40px;float:left;background-color:green;color:#FFF;"><%=meeting.getDate() %></div>
 			
 				Meeting: <%=meeting.getId() %> - <%=meeting.getName() %> 
 				<%=meeting.getBlurb() %>
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