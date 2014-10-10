
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<!-- include for edit framework to work %@include file="../admin/toolbar.jsp"% -->
<%




if( troop.getYearPlan()==null ){
	YearPlan plan = new YearPlan();
	plan.setName("My custom plan");
	java.util.List  <MeetingE> meetings = new java.util.ArrayList  <MeetingE> ();
	plan.setMeetingEvents( meetings );
	troop.setYearPlan( plan );
	
}

%>


<jsp:forward page="/content/girlscouts-vtk/controllers/vtk.plan.html"/>