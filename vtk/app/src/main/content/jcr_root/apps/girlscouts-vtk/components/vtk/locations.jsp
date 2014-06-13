<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy");
java.text.SimpleDateFormat dateFormat41 = new java.text.SimpleDateFormat("a");
java.text.SimpleDateFormat dateFormat42 = new java.text.SimpleDateFormat("hh");
java.text.SimpleDateFormat dateFormat43 = new java.text.SimpleDateFormat("mm");
java.text.SimpleDateFormat dateFormat44 = new java.text.SimpleDateFormat("hh:mm");
	HttpSession session = request.getSession();
org.girlscouts.vtk.models.user.User user= (org.girlscouts.vtk.models.user.User) session.getValue("VTK_user");
%>
<script>
$(function() {
	
    $( "#calStartDt" ).datepicker();
   
  });
</script>

<h1>Specify Dates and Locations</h1>
<div style="background-color:gray; color:#fff;">Manage Calendar</div>
<div style="border:1px solid red;">
<p>Set up your meeting dates and times:</p>
<form>

Start Date<input type="text" id="calStartDt" value="<%=user.getYearPlan().getCalStartDate()==null ? "" : dateFormat4.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>" />
Time<input type="text" id="calTime" value="<%=user.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_MIN) : dateFormat44.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>"/>
<select id="calAP">
<%if( user.getYearPlan().getCalStartDate()!=null){ %>
	<option value="<%=user.getYearPlan().getCalStartDate()==null ? "" : dateFormat41.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>" SELECTED><%=user.getYearPlan().getCalStartDate()==null ? "" : dateFormat41.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %></option>
<%} %>
<option value="pm">pm</option> <option value="am">am</option></select>

Freq<select id="calFreq">

<option value="<%=user.getYearPlan().getCalFreq() %>"><%=user.getYearPlan().getCalFreq() %></option>
<option value="weekly">weekly</option>
<option value="biweekly" SELECTED>biweekly</option>
    </select>

<div style="padding-top:10px;">Do not schedule a meeting during the week of</div>
<br/>
<% String exlDates = user.getYearPlan().getCalExclWeeksOf();
exlDates= exlDates==null ? "" : exlDates;
%>
<br/>
<input type="checkbox" name="exclDt" value="10/01/2014" <%=exlDates.contains("10/01/2014") ? "CHECKED" : ""  %>/>10/01/2014
<input type="checkbox" name="exclDt" value="08/01/2014" <%=exlDates.contains("08/01/2014") ? "CHECKED" : ""  %>/>08/01/2014
<input type="checkbox" name="exclDt" value="07/01/2014" <%=exlDates.contains("07/01/2014") ? "CHECKED" : ""  %>/>07/01/2014
<input type="checkbox" name="exclDt" value="04/01/2014" <%=exlDates.contains("04/01/2014") ? "CHECKED" : ""  %>/>04/01/2014
<input type="checkbox" name="exclDt" value="01/01/2014" <%=exlDates.contains("01/01/2014") ? "CHECKED" : ""  %>/>01/01/2014

<br/><input type="button" value="create calendar" onclick="buildSched()"/>
<input type="button" value="manage calendar" onclick="document.location='/content/girlscouts-vtk/en/vtk.calendar.html'" />

</form>

<div id="calView"></div>
</div>


<%@include file="include/location.jsp" %>

<br/><br/>
<%@include file="include/manageActivities.jsp" %>



</body>
</html>