 <%@page import="java.util.Iterator"%>
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>


<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>



<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd ><a href="/content/girlscouts-vtk/en/vtk.html?ageLevel=brownie">Year Plan</a></dd>
  <dd class="active">Meeting Plan</dd>
  <dd><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
    <div class="content" id="panel2-1"></div>
    <div class="content" id="panel2-2"></div>
    <div class="content" id="panel2-3"></div>
    <div class="content" id="panel2-4"></div>
    <div class="content" id="panel2-5"></div>
</div>

<%
HttpSession session = request.getSession();

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
       
       <h1>Year Plan Sched</h1>
       <div id="planMsg"></div>
      <% 
       				switch( _comp.getType() ){
       					case ACTIVITY :
       					%>  <%@include file="include/viewYearPlanActivity.jsp" %>    <% 
       					break;
       					
       					case MEETING :
       						
           					%>  <%@include file="include/viewYearPlanMeeting.jsp" %>    <% 
           					
           					break;
       				}       			
       %>
       
       <div id="editAgenda"></div>
       <%@include file="include/manageCommunications.jsp" %>
       
       <%!
       java.text.SimpleDateFormat fmtDate= new java.text.SimpleDateFormat("MM/dd/yyyy");
       java.text.SimpleDateFormat fmtHr= new java.text.SimpleDateFormat(" hh:mm a");
       java.text.SimpleDateFormat fmt= new java.text.SimpleDateFormat("MMM dd  hh:mm a");
       java.text.SimpleDateFormat fmtX= new java.text.SimpleDateFormat("d");
       %>