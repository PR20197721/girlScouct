<%
	if (troops != null && troops.size() > 1) {
		Cookie cookie = new Cookie("vtk_prefTroop", troop.getTroop().getGradeLevel());
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
%>

<div id="troop" class="row hide-for-print">
	<div class="columns large-7 medium-7 right">
		<select id="reloginid" onchange="relogin()">
			<%
				for (int i = 0; i < troops.size(); i++) {
			%>
			<option value="<%=troops.get(i).getTroopId()%>"
				<%=troop.getTroop().getTroopId()
							.equals(troops.get(i).getTroopId()) ? "SELECTED"
							: ""%>><%=troops.get(i).getTroopName()%>
				:
				<%=troops.get(i).getGradeLevel()%></option>
			<%
				}
			%>
		</select>
	</div>
</div>
<%
	}
%>
<div class="hide-for-print tab-wrapper">
	<%
		//if (troop.getYearPlan() != null) {
	%>
	<div class="row">
		<div class="columns">
			<%
			//	}
			%>

			<dl class="tabs" data-tab>
			<% if(hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID)) { %>
					<dd <%= "myTroop".equals(activeTab) ? "class='active'" : "" %>>
						<a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
					</dd>
			<% } %>
			<% if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) { %>
					<dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
						<a href="/content/girlscouts-vtk/en/vtk.plan.html">Year Plan</a>
					</dd>
			<% } %>
			<% if(hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) { %>
				<dd <%= "planView".equals(activeTab) ? "class='active'" : "" %>>
					 <a <%= troop.getYearPlan() != null ? "href='/content/girlscouts-vtk/en/vtk.planView.html'" :  "href='#' onClick='alert(\"Please select a year plan\")'"  %>>Meeting Plan</a>
				</dd>
			<%	} %>
				<dd <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
					<a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a>
				</dd>
			
			<% if( hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
					<dd <%= "finances".equals(activeTab) ? "class='active'" : "" %>>
						<a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a>
					</dd>
				<% } 	%>
			<!--<% // to do add this to javA if(hasPermission(troop, Permission.PERMISSION_VIEW_PROFILE) ) { %>-->
				<dd <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
					<a href="/content/girlscouts-vtk/en/vtk.profile.html">Profile</a>
				</dd>
			<!--<% // } 	%>-->
			</dl>
			<%
			//	if (troop.getYearPlan() != null) {
			%>
		</div><!--/columns-->
	</div><!--/row-->
	<%
		//}
	%>
</div><!--/hide-for-print-->

<!-- 
New redesign this is not needed.
<div class="show-for-small hide-for-print">
	<a href="#" id="vtk-main-menu-button"
		onclick="$('#vtk-main-menu').toggle()" class="large button expand">Menu</a>
	<br />
	<ul id="vtk-main-menu" class="hide-for-print">
		<%
			if ("myTroop".equals(activeTab)) {
		%>
		<li class="active"><a href="#">My Troop</a></li>
		<%
			} else {
		%>
			<li>
				<% if( hasPermission(troop, Permission.PERMISSION_VIEW_TROOP_ID) ){ %>
					<a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a>
				<%}else{ %>
					<span>My Troop</span>
				<%} %>
			</li>
		<%
			}
		%>
		<%
			if ("plan".equals(activeTab)) {
		%>
		<li class="active has-dropdown"><a href="#">Year Plan</a>
			<ul class="dropdown hide-for-print">
				<li><a href="#" onclick="newLocCal()">Specify Meeting Dates
						and Locations</a></li>
				<li><a href="#" onclick="doMeetingLib()">Add Meeting</a></li>
				<li><a href="#" onclick="newActivity()">Add Activity</a></li>
				<li><a
					onclick="self.location = '/content/girlscouts-vtk/en/cal.ics'">Download
						Calendar</a></li>
			</ul></li>
		<%
			} else {
		%>
		
			<%if(hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID )){ %>
				<li><a href="/content/girlscouts-vtk/en/vtk.plan.html">Year Plan</a></li>
			<%}else{ %>
				<li><span>Year Plan</span></li>
			<%} %>
		</li>
		<%
			}
		%>
		<%
			if ("planView".equals(activeTab)) {
		%>
			<li class="active"><a href="#">Meeting Plan</a></li>
		<%
			} else {
		%>
		<li>
			<%
				if (troop.getYearPlan() != null && hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) ) {
			%> <a href="/content/girlscouts-vtk/en/vtk.planView.html">Meeting Plan</a>
			<%
				} else {
			%> <a href="#" onclick="alert('Please select a year plan')">Meeting Plan</a> <%
 	}
 %>
		</li>
		<%
			}
		%>
		<%
			if ("resource".equals(activeTab)) {
		%>
		<li class="active"><a href="#">Resources</a></li>
		<%
			} else {
		%>
		<li><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></li>
		<%
			}
		%>
		<%
			if ("finances".equals(activeTab)) {
		%>
		<li class="active"><a href="#">Finances</a></li>
		<%
			} else {
		%>
		<li>
			<% if( hasPermission(troop, Permission.PERMISSION_VIEW_FINANCE_ID) ){ %>
				<a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a></li>
			<%}else{ %>
				<span>Finances</span>
			<%} %>	
		<%
			}
		%>
	</ul>
</div> -->


