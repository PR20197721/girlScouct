<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
  

    <%   
    HttpSession session = request.getSession();
    User user  = (User) session.getValue("VTK_user");
    java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
    
  
    %>
    
<p>Drag and drop to reorder meetings</p>
<ul id="sortable123" >

<% 

int meetingCount=0;
java.util.Iterator itr = sched.keySet().iterator();
while( itr.hasNext() ){
	
	java.util.Date date = (java.util.Date) itr.next();
	YearPlanComponent _comp= sched.get(date);
  switch( _comp.getType() ){
	case ACTIVITY :
		Activity activity = (Activity) _comp;
		%>  <%@include file="include/viewActivity.jsp" %>    <% 
		break;
	
	case MEETING :
		meetingCount++;
		MeetingE meetingE =(MeetingE)_comp;
		%>  <%@include file="include/viewMeeting.jsp" %>    <% 
		break;
  }       	
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
        		   
        		   
        		   
        		   <%!
        		   
        		   java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MMM dd hh:mm a");
        		   %>