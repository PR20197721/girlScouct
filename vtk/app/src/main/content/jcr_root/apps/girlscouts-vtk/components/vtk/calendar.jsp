
<%@page import="java.util.Iterator"%>
<%@ page import="org.girlscouts.vtk.models.user.*,org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<a href="/content/girlscouts-vtk/en/vtk.html">Return to Plan</a>

<%

HttpSession session = request.getSession();
User user= (User) session.getValue("VTK_user");
MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());

%>       
  
       <h1>Manage Calendar</h1>
       <div id="locMsg"></div>
       
       <% 
     
    	  int i=0;
    	  java.util.Iterator itr= sched.keySet().iterator();
    	  while( itr.hasNext() ){
    		  
    	  	i++;
			java.util.Date date = (java.util.Date)itr.next();
			MeetingE meeting = (MeetingE)sched.get(date);
		
%>


<div >

   <table>
   	<tr>
   	<td>
   		<% if( date.after(new java.util.Date() )){ %>
   			<a href="javascript:void(0)" onclick="manageCalElem('<%=date.getTime()%>')">IMG</a>
   		<% }else{ %>
   			IMG
   		<% } %>
   		
   	<td><%=i %>
   	<td><%= dateFormat0.format( date ) %>
   	<td>
   	
   	
   	<%=   	meetingDAO.getMeeting(  meeting.getRefId() ).getName() %>
   </table>
	
	
</div>
	   <% }%>
       


<%!
java.text.SimpleDateFormat dateFormat0 = new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a");


/*
org.joda.time.format.DateTimeFormatter fmtDate = org.joda.time.format.DateTimeFormat.forPattern("MM/d/yyyy");
org.joda.time.format.DateTimeFormatter fmtHr = org.joda.time.format.DateTimeFormat.forPattern("HH:mm");
org.joda.time.format.DateTimeFormatter fmtHr1 = org.joda.time.format.DateTimeFormat.forPattern("hh:mm");
*/
%>
      
       