<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/locations.jsp  -->
<h2>Specify Dates and Locations</h2>
<a href="/content/girlscouts-vtk/en/vtk.html?rand=<%= new java.util.Date().getTime()%>">Return to Plan</a>

<div class="sectionBar">Manage Calendar</div>
<div id="calMng">
<%
	if (user.getYearPlan() != null) {
		if( user.getYearPlan().getSchedule() ==null ){ 
%>
<%@include file="include/calSched.jsp" %>
<%
		}else{
%>
			LOADING CALENDAR.....<script>loadCalMng()</script>
<%
		}
%>
<%@include file="include/location.jsp" %>
<br/><br/>
<%@include file="include/manageActivities.jsp" %>
<%
	} else {
%>
	<span class="error">This year plan has no meetings.<br/> Please select a different year plan.</span>
<%
	}
%>
</div>
