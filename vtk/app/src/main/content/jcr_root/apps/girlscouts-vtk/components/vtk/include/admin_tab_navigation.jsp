<div id="troop" class="row hide-for-print">
  <div class="columns large-7 medium-7 right">
    <select>
    <option>Admin View</option>
    </select>
  </div>
</div>
<div class="hide-for-print tab-wrapper row">
    <div class="columns large-22 large-centered small-24">
		<dl class="tabs hide-for-small">
			<%-- Add hasPermission accordingly  --%>
			<dd <%= "summary".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_summary.html">Summary</a>
			</dd>
			<dd <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_plan.html">Year Plans</a>
			</dd>
			<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MILESTONE_ID)) { %>
			<dd <%= "milestones".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_milestones.html">Milestones</a>
			</dd>
			<% } %>
			<dd <%= "finances".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_finances.html">Finances</a>
			</dd>
			<dd <%= "reports".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
			</dd>
			<dd <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_resource.html">Resources</a>
			</dd>
			<dd <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_profile.html">Profile</a>
			</dd>
		</dl>
		<div class="dropdown show-for-small hide-for-print">
        <a id="vtk-main-menu-button" onclick="$('#vtk-main-menu').slideToggle('slow')" class="expand">Menu</a>
        <ul id="vtk-main-menu" class="hide-for-print" style="display: none;">
          			<%-- Add hasPermission accordingly  --%>
			<li <%= "summary".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_summary.html">Summary</a>
			</li>
			<li <%= "plan".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_plan.html">Year Plans</a>
			</li>
			<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MILESTONE_ID)) { %>
			<li <%= "milestones".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_milestones.html">Milestones</a>
			</li>
			<% } %>
			<li <%= "finances".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_finances.html">Finances</a>
			</li>
			<li <%= "reports".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_reports.html">Reports</a>
			</li>
			<li <%= "resource".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_resource.html">Resources</a>
			</li>
			<li <%= "profile".equals(activeTab) ? "class='active'" : "" %>>
				<a href="/content/girlscouts-vtk/en/vtk.admin_profile.html">Profile</a>
			</li>
        </ul>
      </div>
	</div>

</div><!--/hide-for-print-->
