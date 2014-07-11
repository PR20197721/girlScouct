<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/locations.jsp  -->
<h2>Specify Dates and Locations</h2>
<!--  <a class="closeText" href="#" onclick="$('#gsModal').dialog('close')">Return to Plan</a> -->
<a href="/content/girlscouts-vtk/en/vtk.html?rand=<%= new java.util.Date().getTime()%>">Return to Plan</a>

<div class="sectionBar">Manage Calendar</div>
<div id="calMng">
	<%if( user.getYearPlan().getSchedule() ==null ){ %>
		<%@include file="include/calSched.jsp" %>
	<%}else{%>
		LOADING CALENDAR.....<script>loadCalMng()</script>
	<%} %>
</div>



        <%if(user.getYearPlan().getSchedule() !=null ){ %>
<%@include file="include/location.jsp" %>
<br/><br/>
<%@include file="include/manageActivities.jsp" %>
        <%} %>

