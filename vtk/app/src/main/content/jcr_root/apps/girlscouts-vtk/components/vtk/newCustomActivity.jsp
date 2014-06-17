<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="newLocationModal">
<script>
$(function() {
	$( "#newCustActivity_date" ).datepicker({minDate: 0});
  });
</script>   
<form>
 	<div style="background-color:gray; color:#fff;">Create a Custom Activity</div>
	<div id="newCustActivity_err" style="color:red;"></div>
	Name of Activity <font color="red">*</font> <input type="text" id="newCustActivity_name" value="" style="width:200px;"/>
	<br/>Date: ex:05/07/2014<input type="text"  id="newCustActivity_date"  style="width:160px;"/>
	<br/>Start Time: ex: 18:15<input type="text" id="newCustActivity_startTime" value="<%=org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_MIN %>" style="width:100px;" />
		<select id="newCustActivity_startTime_AP"><option value="am">am</option><option value="pm">pm</option></select>
	<br/>End Time: ex: 09:10<input type="text" id="newCustActivity_endTime" value="<%=org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_END_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_END_TIME_MIN %>"  style="width:100px;"/>
		<select id="newCustActivity_endTime_AP"><option value="am">am</option><option value="pm">pm</option></select>
	<br/>Location Name <input type="text" id="newCustActivity_locName" value="" style="width:100px;"/>
	<br/>Location Address <input type="text" id="newCustActivity_locAddr" value="" style="width:100px;"/>	
	<br/><textarea id="newCustActivity_txt" rows="4" cols="5"  style="width:300px;"></textarea>
	<input type="button" value="Add Activity" id="newCustActivity" onclick="createNewCustActivity()"/>
 </form>
 
 
 <form>
<div style="background-color:gray; color:#fff;">Add activity from the Council Calendar</div>
 Find Activity by <input type="text" value="" id="existActivSFind"/>
 <br/>Month and Year
 <select id="existActivSMon"><option value="01">Jan</option> <option value="02">Feb</option></select>
 <select id="existActivSYr"><option value="2014">2014</option> <option value="2015">2015</option></select>
 <br/>Date Range 
 <input type="text" id="existActivSDtFrom" />
  <input type="text"  id="existActivSDtTo"  />
  
  <br/>Region
  <select id="existActivSReg"><option value="region1">Region1</select>
  
  <br/>Program Level
  <input type="checkbox" value="1" name="existActivSLevl"/>1
  <input type="checkbox" value="2" name="existActivSLevl"/>2
  <input type="checkbox" value="3" name="existActivSLevl"/>3
  <input type="checkbox" value="4" name="existActivSLevl"/>4
  <input type="checkbox" value="5" name="existActivSLevl"/>5
  <input type="checkbox" value="6" name="existActivSLevl"/>6
  
  <br/>Categories
  <input type="checkbox" value="1" name="existActivSCat"/>1
  <input type="checkbox" value="2" name="existActivSCat"/>2
  <input type="checkbox" value="3" name="existActivSCat"/>3
  <input type="checkbox" value="4" name="existActivSCat"/>4
  <input type="checkbox" value="5" name="existActivSCat"/>5
  <input type="checkbox" value="6" name="existActivSCat"/>6
  
  <br/><input type="button" value="View Activity" onclick="searchActivity()" />
  
  <div id="listExistActivity"></div>
 
 
 </form>
</div>
