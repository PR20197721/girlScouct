


<div class="setupCalendar">
	<p>Set up your meeting dates and times:</p>
	<form>
	<div class="row">
		<div class="small-24 medium-4 large-4 columns"><label for="calStartDt" ACCESSKEY=d>Start Date:</label></div>
		<div class="small-24 medium-8 large-8 columns date">
			<input type="text" id="calStartDt" name="calStartDt" value="<%=troop.getYearPlan().getCalStartDate()==null ? "" : VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, new java.util.Date(troop.getYearPlan().getCalStartDate())) %>" />
		</div>
    <div class="small-24 medium-4 large-4 columns"><label for="calTime" ACCESSKEY=t>Time:</label></div>
		<div class="small-24 medium-8 large-8 columns">
			<input type="text" id="calTime" value="<%=troop.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN) : VtkUtil.formatDate(VtkUtil.FORMAT_hhmm,new java.util.Date(troop.getYearPlan().getCalStartDate())) %>"/>
			<select id="calAP">
				<%
					String AM = "PM";
					if( troop.getYearPlan().getCalStartDate() !=null ){
						AM = VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, new java.util.Date(troop.getYearPlan().getCalStartDate()));
					} 
				%>
				<option value="pm" <%=AM.equals("PM") ? " SELECTED" : "" %>>pm</option>
				<option value="am" <%=AM.equals("AM") ? " SELECTED" : "" %>>am</option>
			</select>
		</div>
	</div>
	<div class="row">
		<div class="small-24 medium-4 large-4 columns"><label for="calFreq" ACCESSKEY=f>Frequency:</label></div>
		<div class="small-24 medium-8 large-8 columns">
			<select id="calFreq">
							
				<option value="weekly" <%= troop.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>
				<option value="biweekly"  <%= troop.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>
				<option value="monthly"  <%= troop.getYearPlan().getCalFreq().equals("monthly") ? " SELECTED" : "" %>>monthly</option>
			</select>
		</div>
		<div class="hide-for-small medium-12 large-12">&nbsp;</div>
	</div>
	<p><label for="exclDt" ACCESSKEY=s>Do not schedule a meeting during the week of :</label></p>
<%
	String exlDates = troop.getYearPlan().getCalExclWeeksOf();
	exlDates= exlDates==null ? "" : exlDates;
%>
		<ul class="doubleList">
		<%
		UserGlobConfig ubConf =troopUtil.getUserGlobConfig();
		

		//out.println("VacationDates: "+ ubConf.getVacationDates() );
		
		/*** SAVE API
			ubConf.setVacationDates("|09/07/2014|10/12/2014|");
			userDAO.updateUserGlobConfig();
			*/
		%>
			<li><label><input type="checkbox" name="exclDt" value="09/07/2014" <%=("".equals(exlDates) || exlDates.contains("09/07/2014")) ? "CHECKED" : ""  %>/>09/07/2014 (Labor Day)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="10/12/2014" <%=("".equals(exlDates) || exlDates.contains("10/12/2014")) ? "CHECKED" : ""  %>/>10/12/2014 (Columbus Day)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="11/11/2014" <%=("".equals(exlDates) || exlDates.contains("11/11/2014")) ? "CHECKED" : ""  %>/>11/11/2014 (Veteran's Day)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="11/26/2014" <%=("".equals(exlDates) || exlDates.contains("11/26/2014")) ? "CHECKED" : ""  %>/>11/26/2014 (Thanksgiving)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="12/25/2014" <%=("".equals(exlDates) || exlDates.contains("12/25/2014")) ? "CHECKED" : ""  %>/>12/25/2014 (Christmas)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="01/01/2015" <%=("".equals(exlDates) || exlDates.contains("01/01/2015")) ? "CHECKED" : ""  %>/>01/01/2015 (New Years)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="01/19/2015" <%=("".equals(exlDates) || exlDates.contains("01/19/2015")) ? "CHECKED" : ""  %>/>01/19/2015 (Martin Luther King, Jr.)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="02/16/2015" <%=("".equals(exlDates) || exlDates.contains("02/16/2015")) ? "CHECKED" : ""  %>/>02/16/2015 (Washington's Birthday)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="05/25/2015" <%=("".equals(exlDates) || exlDates.contains("02/25/2015")) ? "CHECKED" : ""  %>/>05/25/2015 (Memorial Day)</label></li>
			<li><label><input type="checkbox" name="exclDt" value="07/04/2015" <%=("".equals(exlDates) || exlDates.contains("07/04/2015")) ? "CHECKED" : ""  %>/>07/04/2015 (Independence Day)</label></li>
		
		</ul>
		<div class="linkButtonWrapper">
			<input type="button" value="create calendar" onclick="buildSched()" class="button linkButton"/>
		</div>
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
