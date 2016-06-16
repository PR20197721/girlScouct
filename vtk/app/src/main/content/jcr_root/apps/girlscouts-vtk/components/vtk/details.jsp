<%@ page
  import="java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<%
  String activeTab = "planView";
  boolean showVtkNav = true;
    
	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView(user, troop, request, true);
	
	if( planView.getYearPlanComponent().getType() == YearPlanComponentType.MEETINGCANCELED || planView.getYearPlanComponent().getType() == YearPlanComponentType.MEETING ){
		%><%@include file="meeting_react2.jsp"%><% 
	}else if( planView.getYearPlanComponent().getType() == YearPlanComponentType.ACTIVITY ){
		%><%@include file="activity_react2.jsp"%><%
	}
%>
<script>
//need to call it again here.
$(document).ready(function(){
  resizeWindow();
}) 
</script>



<%@include file="include/notes.jsp"%>