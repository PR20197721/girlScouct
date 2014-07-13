<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo" title="tmp"></div>
<div id="planBody">
<%!
	String activeTab = "plan";
	boolean showVtkNav = true;

	public boolean isDtMeetings(Cal cal, int x){

		if( cal==null || cal.getDates()==null) return false;
		String dates= cal.getDates();

		java.util.StringTokenizer t= new java.util.StringTokenizer(dates, ",");
		if( x==0 ){
			while( t.hasMoreElements() )
				if( new java.util.Date().before( new java.util.Date(Long.parseLong(t.nextToken())) ) )
					return true;

		}else{
			while( t.hasMoreElements() )
				if( new java.util.Date().after( new java.util.Date(Long.parseLong(t.nextToken())) ) )
					return true;

		}
		return false;
	}
%>
<%@include file="include/vtk-nav.jsp"%>
<% if( user.getYearPlan()!=null){ %> 
<div class="row hide-for-small">
	<div class="large-22 medium-22 small-20 columns">
		<div class="centered-table">
			<ul id="vtkSubNav">
				<li>
					<a href="javascript:void(0)" onclick="newLocCal()">Specify Meeting Dates and Locations</a>
				</li>
				<li>|</li>
				<li>
					 <a href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html" >Add Meeting</a>
					<!--  <a href="javascript:void(0)" onclick="doMeetingLib()">Add Meeting</a> -->
				</li>
				<li>|</li>
				<li>
					<a href="javascript:void(0)" onclick="newActivity()">Add Activity</a>
				</li>
			</ul>
		</div>
<!--
		<div class="show-for-small">
			<script>
				function smallAction(act) {
					if (act == null || act === "") {
						return;
					} else {
						if (act.indexOf("/") > -1) {
							window.location.href = act;
						} else {
							eval(act + "()");
						}
					}
				}
			</script>
			<select id="vtkSubNavSmall" onChange="smallAction(this.options[selectedIndex].value)">
				<option value="" selected="selected">Select an action</option>
				<option value="newLocCal">Specify Meeting Dates and Locations</option>
                                <option value="/content/girlscouts-vtk/en/vtk.meetingLibrary.html">Add Meeting</option>
                                <option value="newActivity">Add Activity</option>
                        </select>
		</div>
-->
	</div>
	<div class="large-2 medium-2 small-4 columns">
		<div class="icons">
		<%if(user.getYearPlan().getSchedule()!=null){ %>
			<a onclick="self.location = '/content/girlscouts-vtk/en/cal.ics'"><img alt="Calendar Download" src="/etc/designs/girlscouts-vtk/images/calendar-download.png" width="39" height="20" border="0" class="align-right"/></a>
                <%} %>
<!--
			<a href="javascript:void(0)" id="plan_calendarhlp_hrf" onclick="x12('plan_calendarhlp_hrf')"><img src="/etc/designs/girlscouts-usa-green/images/help-icon.png" class="align-right"/></a>
-->
		</div>
	</div>
</div>
<%} %>
<%if( user.getYearPlan()==null ){ %>
	<div class="instructions">
		<p>To start planning your year, select a Year Plan</p>
<!--
		<a href="javascript:void(0)" id="plan_calendarhlp_hrf" onclick="x12('plan_calendarhlp_hrf')">
		<img align="right" src="/etc/designs/girlscouts-usa-green/images/help-icon.png"/></a>
-->
	</div>
<%}%>
	<div class="sectionHeader">YEAR PLAN LIBRARY&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(user.getYearPlan()!=null){%>
		<a href="javascript:void(0)" onclick="yesPlan()" id="showHideReveal">reveal</a> <span id="arrowDirection" class="arrowDirection">&#9660;</span>
<%} %>
	</div>
<% if(user.getYearPlan()!=null){%>
	<div id="yearPlanSelection" style="display:none;">
<%}else{ %>
	<div id="yearPlanSelection" >
<%} 
String ageLevel=  user.getTroop().getGradeLevel();
ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
ageLevel=ageLevel.toLowerCase().trim();

String confMsg="";
if( user.getYearPlan()!=null ){
	if( (user.getYearPlan().getAltered()!=null && !user.getYearPlan().getAltered().equals("")) &&
			( isDtMeetings(user.getYearPlan().getSchedule(), 0) || user.getYearPlan().getSchedule()==null ) )
		{confMsg ="Are You Sure? You will lose customizations that you have made";}
	else if( isDtMeetings(user.getYearPlan().getSchedule(), 1))
		{confMsg ="Are You Sure? This will modify plans on /after [date]. Any customization for meeting(s) will be lost."; }
}


java.util.Iterator<YearPlan> yearPlans = yearPlanDAO.getAllYearPlans(ageLevel).listIterator();
while (yearPlans.hasNext()) {
	YearPlan yearPlan = yearPlans.next();
%>
			<div class="row">
				<div class="large-8 columns"><input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg %>', '<%=yearPlan.getName()%>')" /></div>
				<div class="large-16 columns"><%=yearPlan.getDesc()%></div>
			</div>
			<hr/>
<%} %>
		</div>
		<div id="yearPlanMeetings" style="display:<%=(user.getYearPlan()!=null) ? "block" : "none" %>">
<% if(user.getYearPlan()!=null){%>
		<script>$(document).ready(function(){loadMeetings();});</script>
<% } %>
		</div>
	</div>
