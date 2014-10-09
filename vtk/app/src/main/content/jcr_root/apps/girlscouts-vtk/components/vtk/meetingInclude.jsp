<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<%   
	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
	
	try{
		
		if( troop.getYearPlan()!=null)
		troop.getYearPlan().setMilestones( yearPlanUtil.getCouncilMilestones( ""+troop.getTroop().getCouncilCode() ) );
	}catch(Exception e){e.printStackTrace();}
	if( troop.getYearPlan().getMilestones() ==null )
		troop.getYearPlan().setMilestones(new java.util.ArrayList() );
			
	for(int i=0;i<troop.getYearPlan().getMilestones().size();i++)
		sched.put( troop.getYearPlan().getMilestones().get(i).getDate(), troop.getYearPlan().getMilestones().get(i) );


%>
<h1 class="yearPlanTitle"><%=troop.getYearPlan().getName() %></h1>
<br/><p>Drag and drop to reorder meetings <%=hasPermission(troop, Permission.PERMISSION_MOVE_MEETING_ID)%></p> 
<ul id="<%= hasPermission(troop, Permission.PERMISSION_MOVE_MEETING_ID) ? "sortable123" : ""%>">
<% 
if( troop.getYearPlan().getSchedule()!=null ){ //sched exists
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
		case MILESTONE :
			Milestone milestone = (Milestone) _comp;
			%>  <%@include file="include/viewMilestone.jsp" %>    <% 
			break;
	} 	
 }
}else{ //no sched
	
	
	int meetingCount=0;

	//display activities
	java.util.Iterator itr = sched.keySet().iterator();
	 while( itr.hasNext() ){
		java.util.Date date = (java.util.Date) itr.next();
		YearPlanComponent _comp= sched.get(date);
		
		switch( _comp.getType() ){
			case ACTIVITY :
				Activity activity = (Activity) _comp;
				%>  <%@include file="include/viewActivity.jsp" %>    <% 
				break;

			
		} 	
	 }
	 
	 //displ others
	 itr = sched.keySet().iterator();
	 while( itr.hasNext() ){
			java.util.Date date = (java.util.Date) itr.next();
			YearPlanComponent _comp= sched.get(date);
			
			switch( _comp.getType() ){
			case MEETING :
				meetingCount++;
				MeetingE meetingE =(MeetingE)_comp;
				%>  <%@include file="include/viewMeeting.jsp" %>    <% 
				break;
			case MILESTONE :
				Milestone milestone = (Milestone) _comp;
				%>  <%@include file="include/viewMilestone.jsp" %>    <% 
				break;
				
			} 	
		 }
	
	
	
}


%>
</ul>
<script>
	$(function() {
                var scrollTarget = "";
                if (Modernizr.touch) {
                        // touch device
                        scrollTarget = ".touchscroll";
                } else {
                        $(".touchscroll").hide();
                }
                $("#sortable123").sortable({
                        items: "li:not(.ui-state-disabled)",
                        delay:150,
                        cursor: "move" ,
                        distance: 5,
                        opacity: 0.5 ,
                        scroll: true,
                        scrollSensitivity: 10 ,
                        tolerance: "intersect" ,
                        handle: scrollTarget,
			update:  function (event, ui) {
				doUpdMeeting();
			}
                });
                $( "#sortable123 li" ).disableSelection();
	});
</script>



