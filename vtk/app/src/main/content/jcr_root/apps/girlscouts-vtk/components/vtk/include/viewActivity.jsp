<li class="row meeting activity ui-state-default ui-state-disabled" onclick='self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>"'>
	<div class="columns large-21 push-1">
		<div class="large-3 medium-3 columns">
			<div class="bg-square">
				<div class="date">
		    	<p class="month"><%= FORMAT_MONTH.format(activity.getDate())%></p>
		      <p class="day"><%= FORMAT_DAY_OF_MONTH.format(activity.getDate())%></p>
		      <p class="hour"><%= FORMAT_hhmm_AMPM.format(date) %></p>
			  </div>
			</div><!--/square-->
		</div>

		<div class="large-20 medium-20 columns pull-4">
			<p class='subtitle'><%= activity.getName()%></p>
<!-- 			<p class="category"><%= FORMAT_MMM_dd_yyyy_hhmm_AMPM.format(activity.getDate())%> to <%=FORMAT_MMM_dd_yyyy_hhmm_AMPM.format(activity.getEndDate()) %></p> -->
			<p class="blurb"><%=activity.getLocationName() %></p>
			<%if( hasPermission(troop, Permission.PERMISSION_VIEW_ACTIVITY_ID) ){ %>
			<!-- <a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Activity</a> -->
			<%} %>
		</div>
	</div><!--/columns-->
</li>
