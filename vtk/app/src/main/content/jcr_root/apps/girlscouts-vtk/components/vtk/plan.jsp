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






<%=user.getId() %>
<div id="troop" style="background-color:lightyellow">
   <select id="reloginid" style="border:3px solid green; background-color:red;" onchange="relogin()">
	<%
		java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = user.getApiConfig().getTroops();// org.girlscouts.vtk.salesforce.tester3().troopInfo(apiConfig, user.getApiConfig().getUser().getContactId() );
		for(int i=0;i<troops.size();i++){
				%> <option value="<%=troops.get(i).getTroopId() %>" <%= user.getTroop().getTroopId().equals(troops.get(i).getTroopId()) ? "SELECTED" : ""%>><b>Troop:</b><%= troops.get(i).getTroopName() %> &nbsp; >><b>GradeLevel:</b> <%= troops.get(i).getGradeLevel() %></a></option> <% 
		}
	%>
	</select>
</div>

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
				<li><a href="javascript:void(0)" id="plan_hlp_hrf"><img align="right" src="/etc/designs/girlscouts-usa-green/images/help-icon.png"/></a></li>
			</ul>
<%} %>
                        <p>
<%if( user.getYearPlan()==null ){ %>
			To start planning your year, select a Year Plan
<%}%>
                        </p>
			<div class="sectionHeader">YEAR PLAN LIBRARY&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(user.getYearPlan()!=null){%>
				<a href="javascript:void(0)" onclick="yesPlan()" id="showHideReveal">reveal</a>
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
	
	System.err.println("***" + ageLevel);
	
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
