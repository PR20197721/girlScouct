<%
        if (troops != null && troops.size() > 1) {
        	
        	Cookie cookie = new Cookie("vtk_prefTroop", user.getTroop().getGradeLevel());
        	cookie.setMaxAge(-1); 
        	response.addCookie(cookie);

%>
<div id="troop" class="row">
        <div class="small-24 medium-24 large-24 troopPrompt columns">Current troop profile:
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

<%
	if ( user.getYearPlan()!=null ){ 
%>
	<div class="row">
		<div class="small-24 medium-24 large-18 columns">
<%
	}
%>
	<div class="centered-table">
		<ul class="tabs group">
		<% if ("myTroop".equals(activeTab)) { %>
				<li class="active"><span class="label">My Troop</span></li>
		<% } else { %>
		<li><a href="/content/girlscouts-vtk/en/vtk.myTroop.html" class="label">My Troop</a></li>
		<% } %>
		<% if ("plan".equals(activeTab)) { %>
			<li class="active"><span class="label">Year Plan</span></li>
		<% } else { %>
			<li ><a href="/content/girlscouts-vtk/en/vtk.html" class="label">Year Plan</a></li>
		<% } %>
		<% if ("planView".equals(activeTab)) { %>
			<li class="active"><span class="label">Meeting Plan</span></li>
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
		<% if ("finances".equals(activeTab)) { %>
				<li class="active"><span class="label">Finances</span></li>
		<% } else { %>
		<li><a href="/content/girlscouts-vtk/en/vtk.finances.html" class="label">Finances</a></li>
		<% } %>
		</ul>
	</div>
<%
        if ( user.getYearPlan()!=null ){ 
%>
		</div>
		<div class="show-for-large large-6 columns">&nbsp;</div>
	</div>
<%
        }
%>
</div>
<div class="show-for-small">
			<a href="#" id="vtk-main-menu-button" onclick="$('#vtk-main-menu').toggle()" class="large button expand">Menu</a>
			<br/>
			<ul id="vtk-main-menu">
			<% if ("myTroop".equals(activeTab)) { %>
				<li class="active"><a href="#">My Troop</a></li>
			<% } else { %>
				<li><a href="/content/girlscouts-vtk/en/vtk.myTroop.html">My Troop</a></li>
			<% } %>
			<% if ("plan".equals(activeTab)) { %>
				<li class="active has-dropdown">
					<a href="#">Year Plan</a>
					<ul class="dropdown">
						<li>
							<a href="#" onclick="newLocCal()">Specify Meeting Dates and Locations</a>
						</li>
						<li>
							 <a href="#" onclick="doMeetingLib()">Add Meeting</a>
						</li>
						<li>
							<a href="#" onclick="newActivity()">Add Activity</a>
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
				<li class="active"><a href="#">Meeting Plan</a></li>
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
			<% if ("finances".equals(activeTab)) { %>
				<li class="active"><a href="#">Finances</a></li>
			<% } else { %>
				<li><a href="/content/girlscouts-vtk/en/vtk.finances.html">Finances</a></li>
			<% } %>
			</ul>
</div>
