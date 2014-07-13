<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
%>       
       <div id="locMsg"></div>
       <% 
     
    	  int i=0;
    	  java.util.Iterator itr= sched.keySet().iterator();
    	  while( itr.hasNext() ){
    		  
    		//  i++;	
			java.util.Date date = (java.util.Date)itr.next();
			YearPlanComponent _comp= sched.get(date);
			if(_comp.getType() != YearPlanComponentType.MEETING) continue;
			MeetingE meeting = (MeetingE)sched.get(date);
			i++;
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
   	<td><%= FORMAT_CALENDAR_DATE.format( date ) %>
   	<td>
   	
   	
   	<%=   	meetingDAO.getMeeting(  meeting.getRefId() ).getName() %>
   	
   	
   	<%if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){%>
   		<span style="background-color:red;">Canceled</span>
   		<%} %>
   		
   		
   </table>
	
	
</div>
	   <% }%>
