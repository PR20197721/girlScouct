 <%@page import="java.util.Iterator"%>
<%@ page import="org.girlsscout.vtk.models.user.*, org.girlsscout.vtk.models.*,org.girlsscout.vtk.dao.*,org.girlsscout.vtk.ejb.*" %>


<script type="text/javascript" src="js/vtk/planView.js"></script>

<%
User user= (User) session.getValue("VTK_user");

if( user ==null ){ out.println("No user"); return;} 
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
if( sched==null || (sched.size()==0)){out.println( "No Cal set up"); return;}
java.util.List<java.util.Date> dates =new java.util.ArrayList<java.util.Date>(sched.keySet());
long nextDate=0, prevDate=0;
java.util.Date searchDate=null;
if( request.getParameter("elem") !=null )
	searchDate = new java.util.Date( Long.parseLong(  request.getParameter("elem")  ) );	
else
	searchDate = (java.util.Date) sched.keySet().iterator().next();

int currInd =dates.indexOf(searchDate);
if( dates.size()-1 > currInd )
	nextDate = ((java.util.Date)dates.get(currInd+1)).getTime();
if( currInd>0 )
	prevDate = ((java.util.Date)dates.get(currInd-1)).getTime();

YearPlanComponent _comp= sched.get(searchDate);

%>
       <%@include file="include/headerDev.jsi" %>     
       
       <h1>Year Plan Sched</h1>
       <div id="planMsg"></div>
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
       
       <div id="editAgenda"></div>
       <%@include file="include/manageCommunications.jsi" %>
       <%@include file="include/footer.jsi" %>  
       
       <%!
       java.text.SimpleDateFormat fmtDate= new java.text.SimpleDateFormat("MM/dd/yyyy");
       java.text.SimpleDateFormat fmtHr= new java.text.SimpleDateFormat(" hh:mm a");
       java.text.SimpleDateFormat fmt= new java.text.SimpleDateFormat("MMM dd  hh:mm a");
       java.text.SimpleDateFormat fmtX= new java.text.SimpleDateFormat("d");
       %>