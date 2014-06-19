<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%

java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy");
java.text.SimpleDateFormat dateFormat41 = new java.text.SimpleDateFormat("a");
java.text.SimpleDateFormat dateFormat42 = new java.text.SimpleDateFormat("hh");
java.text.SimpleDateFormat dateFormat43 = new java.text.SimpleDateFormat("mm");
java.text.SimpleDateFormat dateFormat44 = new java.text.SimpleDateFormat("hh:mm");


%>
<script>
$(function() {
	
	//dev $( "#calStartDt" ).datepicker();
    // PROD
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
