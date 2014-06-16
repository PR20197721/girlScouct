<dl class="tabs" data-tab >
	<dd ><a href="#panel2-1">My Troup</a></dd>
<% if ("plan".equals(activeTab)) { %>
	<dd class="active">Year Plan</dd>
<% } else { %>
	<dd ><a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a></dd>
<% } %>
<% if ("planView".equals(activeTab)) { %>
        <dd class="active">MeetingPlan</dd>
<% } else { %>
	<dd>
<%if( user.getYearPlan()!=null ){ %>
		<a href="/content/girlscouts-vtk/en/vtk.planView.html">Meeting Plan</a>
<%}else{%>
		<a href="#">Meeting Plan</a>
<%} %>
	</dd>
<% } %>
<% if ("resource".equals(activeTab)) { %>
        <dd class="active">Resources</dd>
<% } else { %>
	<dd><a href="/content/girlscouts-vtk/en/vtk.resource.html">Resources</a></dd>
<% } %>
<% if ("community".equals(activeTab)) { %>
	<dd class="active">Community</dd>
<% } else { %>
	<dd><a href="#panel2-5">Community</a></dd>
<% } %>
</dl>
