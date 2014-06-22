<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/locations.jsp  -->
<script>
$(function() {
	$( "#calStartDt" ).datepicker({minDate: 0});
});
</script>
<h1>Specify Dates and Locations</h1>
<div style="background-color:gray; color:#fff;">Manage Calendar</div>
<div id="calMng">
	<%if( user.getYearPlan().getSchedule() ==null ){ %>
		<%@include file="include/calSched.jsp" %>
	<%}else{%>
		LOADING CALENDAR.....<script>loadCalMng()</script>
	<%} %>
</div>
<%@include file="include/location.jsp" %>
<br/><br/>
<%@include file="include/manageActivities.jsp" %>
