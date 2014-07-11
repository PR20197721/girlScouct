<ul class="tabs group">
	<li><a href="#panel2-1" class="label">My Troop</a></li>
<% if ("plan".equals(activeTab)) { %>
	<li class="active"><span class="label">Year Plan</span></li>
<% } else { %>
	<li ><a href="/content/girlscouts-vtk/en/vtk.html" class="label">Year Plan</a></li>
<% } %>
<% if ("planView".equals(activeTab)) { %>
        <li class="active label">MeetingPlan</li>
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
        <li class="active label">Resources</li>
<% } else { %>
	<li><a href="/content/girlscouts-vtk/en/vtk.resource.html" class="label">Resources</a></li>
<% } %>
<% if ("community".equals(activeTab)) { %>
	<li class="active label">Community</li>
<% } else { %>
	<li><a href="#panel2-5" class="label">Community</a></li>
<% } %>
</ul>
