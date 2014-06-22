<p>Set up your meeting dates and times:</p>
<form>
	Start Date<input type="text" id="calStartDt" value="<%=user.getYearPlan().getCalStartDate()==null ? "" : dateFormat4.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>" />
	Time<input type="text" id="calTime" value="<%=user.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_MIN) : dateFormat44.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>"/>
	<select id="calAP">
<%
	String AM = "AM";
	if( user.getYearPlan().getCalStartDate() !=null ){
		AM = dateFormat41.format(new java.util.Date(user.getYearPlan().getCalStartDate()));
	} 
%>
		<option value="pm" <%=AM.equals("PM") ? " SELECTED" : "" %>>pm</option>
		<option value="am" <%=AM.equals("AM") ? " SELECTED" : "" %>>am</option>
	</select>
	Frequency: 
	<select id="calFreq">
		<option value="weekly" <%= user.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>
		<option value="biweekly"  <%= user.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>
	</select>
	<div style="padding-top:10px;">Do not schedule a meeting during the week of</div>
	<br/>
<%
	String exlDates = user.getYearPlan().getCalExclWeeksOf();
	exlDates= exlDates==null ? "" : exlDates;
%>
	<br/>
	<input type="checkbox" name="exclDt" value="10/01/2014" <%=exlDates.contains("07/04/2014") ? "CHECKED" : ""  %>/>07/04/2014
	<input type="checkbox" name="exclDt" value="08/01/2014" <%=exlDates.contains("09/01/2014") ? "CHECKED" : ""  %>/>09/01/2014
        <input type="checkbox" name="exclDt" value="07/01/2014" <%=exlDates.contains("10/13/2014") ? "CHECKED" : ""  %>/>10/13/2014
	<input type="checkbox" name="exclDt" value="07/01/2014" <%=exlDates.contains("11/11/2014") ? "CHECKED" : ""  %>/>11/11/2014
	<input type="checkbox" name="exclDt" value="04/01/2014" <%=exlDates.contains("11/27/2014") ? "CHECKED" : ""  %>/>11/27/2014
	<input type="checkbox" name="exclDt" value="01/01/2014" <%=exlDates.contains("12/25/2014") ? "CHECKED" : ""  %>/>12/25/2014
	<br/><input type="button" value="create calendar" onclick="buildSched()"/>
</form>
<div id="calView"></div>
