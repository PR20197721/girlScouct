<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<%
  String activeTab = "planView";
  boolean showVtkNav = true;
    
	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView1(user, troop, request);
	
out.println(planView.getYearPlanComponent().getType());

	switch( planView.getYearPlanComponent().getType() ) {
	  case MEETING:
		%><%@include file="meeting_react2.jsp"%><%
		break;
	  case ACTIVITY:
		%><%@include file="activity_react2.jsp"%><%
		break;
	}
%>

