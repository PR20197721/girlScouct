<%@ page import="org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo" title="tmp"> </div>
<div id="newActivity" title="New Activity"> </div>
<div id="newLocationCal" title="New Location & Calendar"> </div>
<div style="background-color:#FFF;">
<%!
	String activeTab = "plan";
	boolean showVtkNav = true;
%>
<%
	YearPlanDAO yearPlanDAO = sling.getService(YearPlanDAO.class);
%>
<%@include file="include/vtk-nav.jsp"%>
	<div class="tabs-content">
		<div class="content" id="panel2-1">
			<p>First panel content goes here...  <a href="javascript:void(0)" onclick="x()">test</a></p>
		</div>
		<div class="content active" id="panel2-2">
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
			</ul>
<%} %>
<%if( user.getYearPlan()==null ){ %>
			<p>To start planning your year, select a Year Plan</p>
			<a href="javascript:void(0)" id="plan_hlp_hrf">help</a>
<%}%>
			<div class="sectionHeader">YEAR PLAN LIBRARY&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(user.getYearPlan()!=null){%>
				<a href="javascript:void(0)" onclick="yesPlan()">reveal</a>
<%} %>
			</div>
<% if(user.getYearPlan()!=null){%>
			<div id="div2" style="display:none;">
<%}else{ %>
			<div id="div2" >
<%} 
	java.util.Iterator<YearPlan> yearPlans = yearPlanDAO.getAllYearPlans(apiConfig.getUser().getAgeLevel()).listIterator();
	while (yearPlans.hasNext()) {
		YearPlan yearPlan = yearPlans.next();
%>
				<div>
					<input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>')" />
					<%=yearPlan.getDesc()%>-<%=yearPlan.getId()%>
				</div>
<%} %>
			</div>
			<div id="div1" style="display:<%=(user.getYearPlan()!=null) ? "block" : "none" %>">
<% if(user.getYearPlan()!=null){%>
				<script>$(document).ready(function(){loadMeetings();});</script>
<% } %>
			</div>
		</div>
		<div class="content" id="panel2-3">
			<p>Third panel content goes here...</p>
		</div>
		<div class="content" id="panel2-4">
			<p>Fourth panel content goes here...</p>
		</div>
		<div class="content" id="panel2-5">
			<p>Fourth panel content goes here...</p>
		</div>
	</div>
	<br/>
	<div id="plan_hlp" style="display:none;"><h1>Year Plan Help:</h1><ul><li>asdf></li><li>asdf></li><li>asdf></li></ul></div>
</div>
