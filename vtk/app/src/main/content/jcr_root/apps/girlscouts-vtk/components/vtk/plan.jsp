<%@ page import="org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo" title="tmp"></div>
<div id="planBody">
<%!
	String activeTab = "plan";
	boolean showVtkNav = true;
%>
<%
	YearPlanDAO yearPlanDAO = sling.getService(YearPlanDAO.class);
	java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = user.getApiConfig().getTroops();
	if (troops.size() > 1) {
%>
<div id="troop" class="row">
	<div class="large-12 troopPrompt columns">
		Current troop profile:
	</div>
	<div class="large-12 troopSelect columns">
		<select id="reloginid" onchange="relogin()">
<%
		for(int i=0;i<troops.size();i++){
%> 
			<option value="<%=troops.get(i).getTroopId() %>" <%= user.getTroop().getTroopId().equals(troops.get(i).getTroopId()) ? "SELECTED" : ""%>><%= troops.get(i).getTroopName() %> : <%= troops.get(i).getGradeLevel() %></option> 
<% 
		}
%>
		</select>
	</div>
</div>
<%
	}
%>
<%@include file="include/vtk-nav.jsp"%>
<% if( user.getYearPlan()!=null){ %> 
	<ul id="vtkSubNav">
		<li>
			<a href="javascript:void(0)" onclick="newLocCal()">Specify Meeting Dates and Locations</a>
		</li>
		<li>|</li>
		<li>
			<a href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html" >Add Meeting</a>
		</li>
		<li>|</li>
		<li>
			<a href="javascript:void(0)" onclick="newActivity()">Add Activity</a>
		</li>
		<li><a href="javascript:void(0)" id="plan_hlp_hrf"><img align="right" src="/etc/designs/girlscouts-usa-green/images/help-icon.png"/></a></li>
	</ul>
<%} %>
<%if( user.getYearPlan()==null ){ %>
	<p>To start planning your year, select a Year Plan</p>
<%}%>
	<div class="sectionHeader">YEAR PLAN LIBRARY&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(user.getYearPlan()!=null){%>
		<a href="javascript:void(0)" onclick="yesPlan()" id="showHideReveal">reveal <span class="arrowDirection">&#9660;</span></a>
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
java.util.Iterator<YearPlan> yearPlans = yearPlanDAO.getAllYearPlans(ageLevel).listIterator();

while (yearPlans.hasNext()) {
	YearPlan yearPlan = yearPlans.next();
%>
			<div class="row">
				<div class="large-8 columns"><input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>')" /></div>
				<div class="large-16 columns"><%=yearPlan.getDesc()%>-<%=yearPlan.getId()%></div>
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
