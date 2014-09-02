
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<!-- include for edit framework to work %@include file="../admin/toolbar.jsp"% -->
<%


/*
 //user= new User( );
System.err.println( "Path :"+ (user.getPath() ));//.getPath());
//if(true)return;
User exist_user= userDAO.getUser( "/vtk/1/caca/users/caca_caca");
if( exist_user ==null ){
	//user.setPath("/vtk/1/caca/users/caca_caca");
	session.setAttribute("VTK_user", user);
}
*/

if( user.getYearPlan()==null ){
	YearPlan plan = new YearPlan();
	plan.setName("My custom plan");
	java.util.List  <MeetingE> meetings = new java.util.ArrayList  <MeetingE> ();
	plan.setMeetingEvents( meetings );
	user.setYearPlan( plan );
	//session.setAttribute("VTK_user", user);
}

%>


<jsp:forward page="/content/girlscouts-vtk/controllers/vtk.plan.html"/>