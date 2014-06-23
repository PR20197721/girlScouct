<div class="setupCalendar">
	<p>Set up your meeting dates and times:</p>
	<form>
	<div class="row">
		<div class="small-3 columns">Start Date:</div>
		<div class="small-6 columns date">
			<input type="text" id="calStartDt" value="<%=user.getYearPlan().getCalStartDate()==null ? "" : dateFormat4.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>" />
		</div>
		<div class="small-5 columns"><input type="text" id="calTime" value="<%=user.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_MIN) : dateFormat44.format(new java.util.Date(user.getYearPlan().getCalStartDate())) %>"/></div>
		<div class="small-3 columns">
			<select id="calAP">
<%
	String AM = "PM";
	if( user.getYearPlan().getCalStartDate() !=null ){
		AM = dateFormat41.format(new java.util.Date(user.getYearPlan().getCalStartDate()));
	} 
%>
				<option value="pm" <%=AM.equals("PM") ? " SELECTED" : "" %>>pm</option>
				<option value="am" <%=AM.equals("AM") ? " SELECTED" : "" %>>am</option>
			</select>
		</div>
		<div class="small-3 columns">Frequency:</div>
		<div class="small-4 columns">
			<select id="calFreq">
				<option value="weekly" <%= user.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>
				<option value="biweekly"  <%= user.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>
			</select>
		</div>
	</div>
	<p>Do not schedule a meeting during the week of:</p>
<%
	String exlDates = user.getYearPlan().getCalExclWeeksOf();
	exlDates= exlDates==null ? "" : exlDates;
%>
	<ul>
		<li><label><input type="checkbox" name="exclDt" value="10/01/2014" <%=exlDates.contains("07/04/2014") ? "CHECKED" : ""  %>/>&nbsp;07/04/2014 (Independence Day)</label></li>
		<li><label><input type="checkbox" name="exclDt" value="08/01/2014" <%=exlDates.contains("09/01/2014") ? "CHECKED" : ""  %>/>09/01/2014 (Labor Day)</label></li>
		<li><label><input type="checkbox" name="exclDt" value="07/01/2014" <%=exlDates.contains("10/13/2014") ? "CHECKED" : ""  %>/>10/13/2014 (Columbus Day)</label></li>
		<li><label><input type="checkbox" name="exclDt" value="04/01/2014" <%=exlDates.contains("11/27/2014") ? "CHECKED" : ""  %>/>11/27/2014 (Thanksgiving)</label></li>
		<li><label><input type="checkbox" name="exclDt" value="01/01/2014" <%=exlDates.contains("12/25/2014") ? "CHECKED" : ""  %>/>12/25/2014 (Christmas)</label></li>
	</ul>
	<br/><input type="button" value="create calendar" onclick="buildSched()"/>
	</form>
</div>
<div id="calView"></div>
<script type="text/javascript">
$( "#calStartDt" ).datepicker({
        minDate: 0,
        showOn: "button",
        buttonImage: "/etc/designs/girlscouts-vtk/clientlibs/css/images/calendar-pick.png",
        buttonImageOnly: true
});
</script>
