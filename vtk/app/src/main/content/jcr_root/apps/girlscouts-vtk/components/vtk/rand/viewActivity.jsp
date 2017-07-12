
<li class="row meeting activity ui-state-default ui-state-disabled" onclick='self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>"'>
	<div class="column large-20 medium-20 large-centered medium-centered">
		<div class="large-3 medium-3 small-6 columns">
			<div class="bg-square">
				<div class="date">
		    	<p class="month"><%= VtkUtil.formatDate(VtkUtil.FORMAT_MONTH, activity.getDate())%></p>
		      <p class="day"><%= VtkUtil.formatDate(VtkUtil.FORMAT_DAY_OF_MONTH, activity.getDate())%></p>
		      <p class="hour"><%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, date) %></p>
			  </div>
			</div><!--/square-->
		</div>

		<div class="large-24 medium-24 small-18 columns">
			<p class='subtitle'><%= activity.getName()%></p>
			<p class="blurb"><%=activity.getLocationName() %></p>
		</div>
	</div><!--/columns-->
</li>
