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

//System.err.println("VTKKKKK");
//out.println(user.getPath() +" *** "+user.getId());

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
		
		
		<%if(false){//user.getYearPlan().getSchedule()!=null){ %>
			<li><a href="/content/girlscouts-vtk/controllers/vtk.calendarSynch.html">Calendar Synch</a></li>
		<%} %>
		
		
	</ul>
<%} %>
<%if( user.getYearPlan()==null ){ %>
	<p>To start planning your year, select a Year Plan</p>
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
java.util.Iterator<YearPlan> yearPlans = yearPlanDAO.getAllYearPlans(ageLevel).listIterator();


String confMsg="";
if( user.getYearPlan()!=null ){
	if( (user.getYearPlan().getAltered()!=null && !user.getYearPlan().getAltered().equals("")) &&
			( isDtMeetings(user.getYearPlan().getSchedule(), 0) || user.getYearPlan().getSchedule()==null ) )
		{confMsg ="Are You Sure? You will lose customizations that you have made";}
	else if( isDtMeetings(user.getYearPlan().getSchedule(), 1))
		{confMsg ="Are You Sure? This will modify plans on /after [date]. Any customization for meeting(s) will be lost."; }
}



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

	<%!
	

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
	
