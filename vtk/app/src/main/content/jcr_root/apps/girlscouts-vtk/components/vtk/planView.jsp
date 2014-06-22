<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- <!-- apps/girlscouts-vtk/components/vtk/planView.jsp -->
<%!
        String activeTab = "planView";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<div class="tabs-content">
    <div class="content" id="panel2-1"></div>
    <div class="content" id="panel2-2"></div>
    <div class="content" id="panel2-3"></div>
    <div class="content" id="panel2-4"></div>
    <div class="content" id="panel2-5"></div>
</div>
<%
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
       						if(!searchDate.before( new java.util.Date() )){
           						  %>  
           						  <%@include file="include/viewYearPlanMeeting.jsp" %>  
           						  <%@include file="include/manageCommunications.jsp" %>
           						  <% 
       						}else{
       							%>  <%@include file="include/viewYearPlanMeeting_locked.jsp" %>    <% 
       						}
           					break;
       				}       			
       %>
       <div id="editAgenda"></div>
