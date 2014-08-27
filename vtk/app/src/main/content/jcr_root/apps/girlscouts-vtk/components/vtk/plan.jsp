
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="errInfo"></div>

<style>
@media print {
	  * {
	    background: transparent !important;
	    color: black !important;
	    /* Black prints faster: h5bp.com/s */
	    box-shadow: none !important;
	    text-shadow: none !important; 
	    
	    
	  }

	  a,
	  a:visited {
		  
	    text-decoration: underline; 
	 content: ""; 
	  
	  }

	  a[href]:after {
		  
	   content: " (" attr(href) ")"; 
	  content: ""; 
	   
	  }

	  abbr[title]:after {
	    content: " --(" attr(title) ")";
	  content: ""; 
	   
	  }

	  .ir a:after,
	  a[href^="javascript:"]:after,
	  a[href^="#"]:after {
	    content: ""; 
	   
	  }

	  pre,
	  blockquote {
	    border: 1px solid #999999;
	    page-break-inside: avoid; }

	  thead {
	    display: table-header-group;
	    /* h5bp.com/t */ }

	  tr,
	  img {
	    page-break-inside: avoid; }

	  img {
	    max-width: 100% !important; }

	  @page {
	    margin: 0.5cm; }

	  p,
	  h2,
	  h3 {
	    orphans: 3;
	    widows: 3; }

	  h2,
	  h3 {
	    page-break-after: avoid; }

	  .hide-on-print {
	    display: none !important; }

	  .print-only {
	    display: block !important; }

	  .hide-for-print {
	    display: none !important; }

	  .show-for-print {
	    display: inherit !important; } 
	    
	  a.show-for-large-up { display:none;}
	 
	 
	
 }
	    
	    
	
	
	
	
</style>

<%!
	String activeTab = "plan";
	boolean showVtkNav = true;
/*
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
*/
%>
<%@include file="include/vtk-nav.jsp"%>
<%
	if( user.getYearPlan()!=null){
		// split resource panel
%>
<div id="panelWrapper" class="row">
	<div id="panelLeft" class="small-24 medium-24 large-18 columns">
<%
	}
%>
<% if( user.getYearPlan()!=null){ %> 
<div class="hide-for-small">
	<div class="row subNavRow">
		<div class="large-22 medium-22 small-20 columns subNavColumn">
			<div class="centered-table">
				<ul id="vtkSubNav">
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
		<div class="large-2 medium-2 small-4 columns calendarDownload">
			<div class="icons">
			<%if(user.getYearPlan().getSchedule()!=null){ %>
				<a onclick="self.location = '/content/girlscouts-vtk/en/cal.ics'"><img alt="Calendar Download" src="/etc/designs/girlscouts-vtk/images/calendar-download.png" width="39" height="20" border="0" class="align-right"/></a>
			<%} %>
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
<% if(user.getYearPlan()!=null){%>
		<a href="#" onclick="yesPlan()" id="showHideReveal">reveal</a>&nbsp;<span id="arrowDirection" class="arrowDirection">&#9660;</span>
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
	if( user.getYearPlan().getAltered()!=null && !user.getYearPlan().getAltered().equals("") ){
		confMsg ="Are You Sure? You will lose customizations that you have made";
	}
			/*
			if( ( user.getYearPlan().getAltered()!=null && !user.getYearPlan().getAltered().equals("") ))&&
			( isDtMeetings(user.getYearPlan().getSchedule(), 0) || user.getYearPlan().getSchedule()==null ) )
		{confMsg ="Are You Sure? You will lose customizations that you have made";}
	else if( isDtMeetings(user.getYearPlan().getSchedule(), 1))
		{confMsg ="Are You Sure? This will modify plans on /after [date]. Any customization for meeting(s) will be lost."; }
*/
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
	<div id="yearPlanMeetings" style="display:<%=(user.getYearPlan()!=null) ? "block" : "none" %>">
<% if(user.getYearPlan()!=null){%>
		<script>$(document).ready(function(){loadMeetings();});</script>
<% } %>
	</div>
<%
        if( user.getYearPlan()!=null){ 
%>
	</div>
        <div id="panelRight" class="small-24 medium-24 large-6 columns">
		<h2 id="resourceListing">Featured Resources:</h2>
		<br/>
		<ul>
		
			<%
				java.util.List <Asset> assets = meetingDAO.getGlobalResources( user.getYearPlan().getResources());
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

