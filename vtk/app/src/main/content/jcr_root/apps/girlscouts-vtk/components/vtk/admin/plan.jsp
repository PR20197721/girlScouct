
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>

<%

 //user= new User( );
YearPlan plan = new YearPlan();
plan.setName("My custom plan");
java.util.List  <MeetingE> meetings = new java.util.ArrayList  <MeetingE> ();
plan.setMeetingEvents( meetings );
user.setYearPlan( plan );
//session.setAttribute("VTK_user", user);
%>