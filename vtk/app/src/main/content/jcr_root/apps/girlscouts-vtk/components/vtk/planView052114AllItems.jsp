



--- 1400290200000 
--- 1402968600000 
--- 1405560600000 
--- 1408239000000 
--- 1410917400000 
--- 1413509400000 
--- 1416191400000 
--- 1418783400000



 <%@ page import="org.girlsscout.vtk.models.user.*, org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>
<%
User user= (User) session.getValue("VTK_user");

java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());

java.util.Date searchDate=null;
if( request.getParameter("elem") !=null )
	searchDate = new java.util.Date( Long.parseLong(  request.getParameter("elem")  ) );
else
	searchDate = sched.keySet().iterator().next();
	
YearPlanComponent _comp= sched.get(searchDate);


java.util.Iterator itr = sched.keySet().iterator();

%>

   <%@include file="include/headerDev.jsi" %>     
       
       <h1>YEar Plan Sched</h1>
       
       
       
       
       
       <table>
       <%
       while( itr.hasNext() ){
    	   
    	   java.util.Date date = (java.util.Date ) itr.next();
    	    
    	   YearPlanComponent _comp= sched.get(date);
    	   
       		%>
       			<tr>
       			<td>
       			
       			
       			<%=date %>
       	
       			
       			 </td>
       			<td>
       			
       			
       			<%
       			
       				switch( _comp.getType() ){
       				
       					case ACTIVITY :
       					%>  <%@include file="include/viewYearPlanActivity.jsi" %>    <% 
       					break;
       					
       					case MEETING :
           					%>  <%@include file="include/viewYearPlanMeeting.jsi" %>    <% 
           					break;
       				}
       			%>
       			
       			</td>
       			</tr>
       		<% 
       	}
       
       %>
       </table>
       
       
       