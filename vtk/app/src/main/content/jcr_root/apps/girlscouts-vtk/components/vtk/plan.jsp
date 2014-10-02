
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.troop.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo"></div>


<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/print.css" type="text/css">

<%!
	String activeTab = "plan";
	boolean showVtkNav = true;

%>
<%@include file="include/vtk-nav.jsp"%>
<%
	if( troop.getYearPlan()!=null){
		// split resource panel
%>
<div id="panelWrapper" class="row">
	<div id="panelLeft" class="small-24 medium-24 large-18 columns">
<%
	}
%>



<% if( troop.getYearPlan()!=null){ %> 
<div class="hide-for-small">
	<div class="row subNavRow">
		<div class="large-22 medium-22 small-20 columns subNavColumn">
			<div class="centered-table">
				<ul id="vtkSubNav" class="hide-for-print">
					<li>
						<a href="javascript:void(0)" onclick="newLocCal()">Meeting&nbsp;Dates&nbsp;and&nbsp;Locations</a>
					</li>
					<li>|</li>
					<li>
						 <a href="javascript:void(0)" onclick="doMeetingLib()">Add&nbsp;Meeting</a> 
					</li>
					<li>|</li>
					<li>
						<a href="javascript:void(0)" onclick="newActivity()">Add&nbsp;Activity</a>
					</li>
				</ul>
			</div>
		</div>
		
		<div class="large-1 medium-1 small-2 columns calendarDownload">
			<div class="icons">
			
			  <!-- <a onclick="javascript:void(0)" onclick="javascript:window.print()"><img alt="Print" src="/etc/designs/girlscouts-vtk/images/calendar-download.png" width="39" height="20" border="0" class="align-right"/>*</a> -->
			  <%if(troop.getYearPlan().getSchedule()!=null){ %>
				<a onclick="self.location = '/content/girlscouts-vtk/en/cal.ics'"><img alt="Calendar Download" src="/etc/designs/girlscouts-vtk/images/calendar-download.png" width="39" height="20" border="0" class="align-right"/></a>
			  <%} %>
			
			
			
			</div>
		</div>
		
		
		
		<div class="large-1 medium-1 small-2 columns calendarDownload">
			<div class="icons">
			
			  <a onclick="javascript:window.print()">
				<img alt="Print" src="/etc/designs/girlscouts-vtk/images/print.png" width="39" height="20" border="0" class="align-right"/>
			  </a>
				
			</div>
		</div>
		
		
		
	</div>
</div>
<% } else { %>
	<div class="instructions">
		<p>To start planning your year, select a Year Plan</p>
	</div>
	<script>
		fixVerticalSizing = true;
	</script>
<%}%>
	<div class="sectionHeader">YEAR PLAN LIBRARY&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<% if(troop.getYearPlan()!=null){%>
		<a href="#" onclick="yesPlan()" id="showHideReveal" class="hide-for-print">reveal</a>&nbsp;<span id="arrowDirection" class="hide-for-print arrowDirection">&#9660;</span>
<%} %>
	</div>
<% if(troop.getYearPlan()!=null){%>
	<div id="yearPlanSelection" style="display:none;">
<%}else{ %>
	<div id="yearPlanSelection" >
<%} 
String ageLevel=  troop.getTroop().getGradeLevel();
ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
ageLevel=ageLevel.toLowerCase().trim();

String confMsg="";
if( troop.getYearPlan()!=null ){
	if( troop.getYearPlan().getAltered()!=null && troop.getYearPlan().getAltered().equals("true") ){
		confMsg ="Are You Sure? You will lose customizations that you have made";
	}
			
}


java.util.Iterator<YearPlan> yearPlans = yearPlanDAO.getAllYearPlans(ageLevel).listIterator();
while (yearPlans.hasNext()) {
	YearPlan yearPlan = yearPlans.next();
%>
		<div class="row">
			<div class="large-8 columns">
				<div style=" background-color:#efefef; padding:10px; margin-left:10px; text-align:center; ">
						<a href="javascript:void(0)" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg %>', '<%=yearPlan.getName()%>')"><%=yearPlan.getName()%></a> 
				</div>
				<!--  <input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg %>', '<%=yearPlan.getName()%>')" /> -->
			</div>
			<div class="large-16 columns"><%=yearPlan.getDesc()%></div>
		</div>
			<hr/>
<%} %>
	</div>
	<div id="yearPlanMeetings" style="display:<%=(troop.getYearPlan()!=null) ? "block" : "none" %>">
<% if(troop.getYearPlan()!=null){%>
		<script>$(document).ready(function(){loadMeetings();});</script>
<% } %>
	</div>
<%
        if( troop.getYearPlan()!=null){ 
%>
	</div>
        <div id="panelRight" class="small-24 medium-24 large-6 columns">
		<h2 id="resourceListing">Featured Resources:</h2>
		<br/>
		<ul>
		
			<%
				java.util.List <Asset> assets = meetingDAO.getGlobalResources( troop.getYearPlan().getResources());
				for(int i=0;i<assets.size();i++){
					Asset asset = assets.get(i);
					%><li>- <a href="<%=asset.getRefId()%>" target="_blank"><%=asset.getTitle() %></a></li> <% 
				}
				
			%>
		</ul>
	</div>
</div>
<%
        }
%>
</div>

