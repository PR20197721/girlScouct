<%= request.getParameter("planId")%>
<%@ page import="org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>
  
<%
    String yearPlanId= request.getParameter("planId");
    java.util.Iterator <Meeting> meetings = null;
    		
    if( request.getParameter("isRefresh")!=null ){ //load from cache
    	
    	
    	meetings = ((java.util.List<Meeting>) session.getValue("VTK_meetings")).listIterator();
    	System.err.println("REFRESHED meetings");
    }else{ //pull
    	
 
    	MeetingDAO meetingDAO = new MeetingDAOImpl();
    	java.util.List <Meeting> _meetings = meetingDAO.getAllMeetings(yearPlanId);
    
    	meetings = _meetings.listIterator();
    	session.putValue("VTK_meetings", _meetings);
    	
    }
    %>
    <h1>Meeting (planID:<%=yearPlanId %>)  </h1>
      <ul id="sortable123">
      
      
      
      
      
      <% 
    while(meetings.hasNext()){
    	
    	Meeting meeting = meetings.next();
    	
    	%>
    	
    	
 			<li value="<%=meeting.getId()%>">
 			
 			    <div style="float:right;width:40px;background-color:#efefef;border:1px solid #000;"><%=meeting.getAidTags() %></div>
 				<div style="width:40px;float:left;background-color:green;color:#FFF;"><%=meeting.getDate() %></div>
 			
 				Meeting: <%=meeting.getId() %> - <%=meeting.getName() %> 
 				<%=meeting.getBlurb() %>
 				</li>
	
    	<% 
    }
    %>
      
      
      
      
      </ul>
    done
    
    
    <script>
  $("#sortable123").sortable(
        				   
        				   {
        				       
        				        update:  function (event, ui) {
        				        	doUpdMeeting()
        				        }
        				}
        		   );
        		   </script>