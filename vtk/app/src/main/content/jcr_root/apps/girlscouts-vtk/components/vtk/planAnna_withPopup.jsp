<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.troop.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<% request.getSession().setAttribute( "isReloadedWindow", "true"); %>
<%@include file="include/session.jsp"%>

<div id="errInfo"></div>
<script>var fixVerticalSizing = false;</script>
<%!String activeTab = "plan";
	boolean showVtkNav = true;%>
<%@include file="include/tab_navigation.jsp"%>
<%	if( !hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID ) ) { %>
		<span class="error">You have no permission to view year plan</span>
		<% return; }
				if( troop.getYearPlan()!=null ) {
						// split resource panel
		%>
		<div id="panelWrapper" class="row content">
			<div class="columns">
				<% } %>
				<% if(troop.getYearPlan()!=null) { /*inline utility menu*/ %>
					<div class="hide-for-small hide-for-print row crumbs">
						<div class="column large-20 medium-24 large-centered medium-centered">
							<div class="row">
								<div class="columns large-20">
									<%	if( hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ) { %>
									<ul id="sub-nav" class="inline-list hide-for-print">
										<li><a data-reveal-id="modal_meeting" title="Metting Dates and Location">Meeting Dates and Locations</a></li>
										<li><a href="#" onclick="doMeetingLib()" title="Add Meeting">Add Meeting</a></li>
										<li><a href="#" onclick="newActivity()" title="Add Activity">Add Activity</a></li>
									</ul>
									<% } %>
								</div>
								<div class="columns large-4">
									<ul class="inline-list" id="util-links">
										<li><a class="icon" onclick="self.location = 'c'" title="download plan"><i class="icon-download"></i></a></li>
										<li><a class="icon" onclick="javascript:window.print()" title="print plan"><i class="icon-printer"></i></a></li>
										<li><a class="icon" onclick="javascript:window.print()" title="help"><i class="icon-questions-answers"></i></a></li>
									</ul>
								</div>
							</div>
						</div>
					</div><!--/hide-->
				<% } %>
				<%-- Show/Hide the year plan library---%>
				<% if(troop.getYearPlan()!=null && hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID) ){ %>
				<div class="sectionHeader row">
					<div class="column large-20 medium-20 large-centered medium-centered">
						<p id="showHideReveal" onclick="yesPlan()" class="hide-for-print">VIEW YEAR PLAN LIBRARY</p>
						<!--<a href="#" onclick="yesPlan()" id="showHideReveal"
						class="hide-for-print">reveal</a>&nbsp;<span id="arrowDirection"
						class="hide-for-print arrowDirection">&#9660;</span> -->
					</div>
				</div>
				<%
					}
				%>
				<div id="yearPlanSelection" <%= (troop.getYearPlan()!=null) ? "style=\"display: none\"":" " %>>
					<div class="row">
						<p class="large-20 medium-20 large-centered medium-centered columns">To start planning your year, select a Year Plan.</p>
					</div>
	        <%
						String ageLevel=  troop.getTroop().getGradeLevel();
						ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
						ageLevel=ageLevel.toLowerCase().trim();

						String confMsg="";
						if( troop.getYearPlan()!=null ){
							if( troop.getYearPlan().getAltered()!=null && troop.getYearPlan().getAltered().equals("true") ){
								confMsg ="Are You Sure? You will lose customizations that you have made";
							}	
						}
						java.util.Iterator<YearPlan> yearPlans = yearPlanUtil.getAllYearPlans(user, ageLevel).listIterator();
							while (yearPlans.hasNext()) {
								YearPlan yearPlan = yearPlans.next();
						%>
						<div class="row">
							<div class="large-2 columns large-push-2">
									<input type="radio" id="r_<%=yearPlan.getId()%>" class="radio1" name="group1" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')" />
									<label for="r_<%=yearPlan.getId()%>"></label>
									<!-- <input type="submit" name="" value="<%=yearPlan.getName()%>" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')" /> -->
							</div>
							<div class="large-18 columns large-pull-2">
								<a href="#" onclick="x('<%=yearPlan.getId()%>', '<%=yearPlan.getPath()%>', '<%=confMsg%>', '<%=yearPlan.getName()%>')"><%=yearPlan.getName()%></a>
								<p><%=yearPlan.getDesc()%></p>
							</div>
						</div><!--/row-->
						<% }	%>
				</div><!--/yearPlanSelection-->
				<div id="yearPlanMeetings" style="display:<%=(troop.getYearPlan()!=null) ? "block" : "none"%>">
					<!--loadMeeting function in the footer.js-->
				</div><!--/yearPlanMeetings-->
				<%
					if (troop.getYearPlan() != null) {
				%>
			</div><!--/columns-->

		</div><!--/panelWrapper-->
<!-- Pannel was removed as per new design.	
			<div id="panelRight" class="small-24 medium-24 large-6 columns hide-for-print">
				<h2 id="resourceListing">Featured Resources:</h2>
				<br />
				<ul>

					<%
						java.util.List<Asset> assets = yearPlanUtil
									.getGlobalResources(troop.getYearPlan().getResources());
							for (int i = 0; i < assets.size(); i++) {
								Asset asset = assets.get(i);
					%><li>- <a href="<%=asset.getRefId()%>" target="_blank"><%=asset.getTitle()%></a></li>
					<%
						}
					%>
				</ul>
			</div> -->
			<%
				}
			%>
	
<%@include file="include/modals/modal_meeting_location.jsp"%>


