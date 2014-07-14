<%
        if (troops != null && troops.size() > 1) {
%>
<div id="troop" class="row">
        <div class="large-24 troopPrompt columns">
                Current troop profile:
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
<div class="hide-for-small full-width-wrapper underline">
	<div class="centered-table">
		<ul class="tabs group">
			<li><a href="#" class="label off">My Troop</a></li>
		<% if ("plan".equals(activeTab)) { %>
			<li class="active"><span class="label">Year Plan</span></li>
		<% } else { %>
			<li ><a href="/content/girlscouts-vtk/en/vtk.html" class="label">Year Plan</a></li>
		<% } %>
		<% if ("planView".equals(activeTab)) { %>
			<li class="active"><span class="label">MeetingPlan</span></li>
		<% } else { %>
			<li>
		<%if( user.getYearPlan()!=null ){ %>
				<a href="/content/girlscouts-vtk/en/vtk.planView.html" class="label">Meeting Plan</a>
		<%}else{%>
				<a href="#" onclick="alert('Please select a year plan')" class="label">Meeting Plan</a>
		<%} %>
			</li>
		<% } %>
		<% if ("resource".equals(activeTab)) { %>
			<li class="active"><span class="label">Resources</span></li>
		<% } else { %>
			<li><a href="/content/girlscouts-vtk/en/vtk.resource.html" class="label">Resources</a></li>
		<% } %>
		<li><a href="#" class="label off">Community</a></li>
		</ul>
	</div>
</div>
<div class="show-for-small">
			<a href="#" id="vtk-main-menu-button" onclick="$('#vtk-main-menu').toggle()" class="large button expand">Menu</a>
			<br/>
			<ul id="vtk-main-menu">
				<li><a href="#" class="off">My Troop</a></li>
			<% if ("plan".equals(activeTab)) { %>
				<li class="active has-dropdown">
					<a href="#">Year Plan</a>
					<ul class="dropdown">
						<li>
							<a href="javascript:void(0)" onclick="newLocCal()">Specify Meeting Dates and Locations</a>
						</li>
						<li>
							 <a href="/content/girlscouts-vtk/en/vtk.meetingLibrary.html" >Add Meeting</a>
						</li>
						<li>
							<a href="javascript:void(0)" onclick="newActivity()">Add Activity</a>
						</li>
						<li>
							<a onclick="self.location = '/content/girlscouts-vtk/en/cal.ics'">Download Calendar</a>
						</li>
					</ul>
				</li>
			<% } else { %>
				<li><a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a></li>
			<% } %>
			<% if ("planView".equals(activeTab)) { %>
				<li class="active"><a href="#">MeetingPlan</a></li>
			<% } else { %>
				<li>
			<%if( user.getYearPlan()!=null ){ %>
					<a href="/content/girlscouts-vtk/en/vtk.planView.html">Meeting Plan</a>
			<%}else{%>
					<a href="#" onclick="alert('Please select a year plan')">Meeting Plan</a>
			<%} %>
				</li>
			<% } %>
			<% if ("resource".equals(activeTab)) { %>
				<li class="active"><a href="#">Resources</a></li>
			<% } else { %>
				<li><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></li>
			<% } %>
				<li><a href="#" class="off">Community</a></li>
			</ul>
</div>
<!--
	<a href="#" class="item off">
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>My Troop</label>
	</a>
        <a href="/content/girlscouts-vtk/en/vtk.html" class="item">
	<% if ("plan".equals(activeTab)) { %>
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Year Plan</label>
	<% } else { %>
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Year Plan</label>
	<% } %>
	</a>
	<%if( user.getYearPlan()!=null ){ %>
		<a href="/content/girlscouts-vtk/en/vtk.planView.html" class="item">
	<%}else{%>
		<a href="#" onclick="alert('Please select a year plan')" class="item">
	<%} %>
	<% if ("planView".equals(activeTab)) { %>
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Meeting</label>
	<% } else { %>
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Meeting</label>
	<% } %>
	</a>
	<a href="/content/girlscouts-vtk/en/vtk.resource.html" class="item">
	<% if ("resource".equals(activeTab)) { %>
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Resources</label>
	<% } else { %>
		<img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Resources</label>
	<% } %>
	</a>
        <a href="#" class="item off">
                <img src="/content/dam/girlscouts-shared/images/logo/small/gateway-logo-small.png" />
		<label>Community</label>
	</a>
-->
