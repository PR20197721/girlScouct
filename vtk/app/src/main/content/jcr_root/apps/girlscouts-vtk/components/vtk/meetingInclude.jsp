<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%   
	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
%>
<p>Drag and drop to reorder meetings</p>
<ul id="sortable123">
<% 


System.err.println(1);
int meetingCount=0;
java.util.Iterator itr = sched.keySet().iterator();
while( itr.hasNext() ){
	java.util.Date date = (java.util.Date) itr.next();
	YearPlanComponent _comp= sched.get(date);
	System.err.println(2);
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
	
	
	System.err.println(3);
}
%>
</ul>
<script>
	$(function() {
		$( "#sortable123" ).sortable({
			items: "li:not(.ui-state-disabled)",
			update:  function (event, ui) {
				doUpdMeeting()
			}
		});
		$( "#sortable123 li" ).disableSelection();
	});
</script>
