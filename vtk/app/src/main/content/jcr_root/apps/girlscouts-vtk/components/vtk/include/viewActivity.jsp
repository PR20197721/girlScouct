

<li class="ui-state-default ui-state-disabled activity">
        <div  class="row">
                <div class="large-4 medium-5 small-24 columns">
			<div class="planSquare centered-table">
				
				<% if (activity.getCancelled()!=null && activity.getCancelled().equals("true") )  {%>
					<div class="cancelled"><div class="cross">X</div></div>
				<%}%>
				
				<div class="date">
					<div class="cal"><span class="month"><%= FORMAT_MONTH.format(activity.getDate())%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(activity.getDate())%></span></div>
				</div>
			</div>
			<br/><br/>
		</div>
                <div class="large-20 medium-19 small-24 columns">
			<div class="planMain">
				<h2><%= activity.getName()%></h2>
				<br/>
				<p class="blurb"><%= FORMAT_MMM_dd_yyyy_hhmm_AMPM.format(activity.getDate())%> to <%=FORMAT_MMM_dd_yyyy_hhmm_AMPM.format(activity.getEndDate()) %></p>
				<p class="blurb"><%=activity.getLocationName() %></p>
				<br/>
				<%if( hasPermission(troop, Permission.PERMISSION_VIEW_ACTIVITY_ID) ){ %>
				  <a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Activity</a>
			    <%} %>
			</div>
		</div>
	</div> 
</li>
