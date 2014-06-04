<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
	HttpSession session = request.getSession();
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

Start Date<input type="text" id="calStartDt" />
Time<input type="text" id="calTime" value="09:30"/>
<select id="calAP"><option value="pm">pm</option> <option value="am">am</option></select>
Freq<select id="calFreq"><option value="weekly">weekly</option> <option value="monthly" SELECTED>monthly</option></select>

<div style="padding-top:10px;">Do not schedule a meeting during the week of</div>
<input type="checkbox" name="exclDt" value="10/01/2014"/>10/01/2014
<input type="checkbox" name="exclDt" value="08/01/2014"/>08/01/2014
<input type="checkbox" name="exclDt" value="07/01/2014"/>07/01/2014
<input type="checkbox" name="exclDt" value="04/01/2014"/>04/01/2014
<input type="checkbox" name="exclDt" value="01/01/2014"/>01/01/2014

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