<li class="ui-state-default ui-state-disabled activity">
        <div  class="row">
                <div class="large-4 columns">
			<div class="planSquare">
				<div class="date">
                                        <div class="cal"><span class="month"><%= FORMAT_MONTH.format(activity.getDate())%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(activity.getDate())%></span></div>
				</div>
			</div>
		</div>
                <div class="large-20 columns">
			<b><%= activity.getName()%></b>
			<br/><%= FORMAT_MMM_dd_yyyy_hhmm_AMPM.format(activity.getDate())%> to <%=FORMAT_MMM_dd_yyyy_hhmm_AMPM.format(activity.getEndDate()) %>
			<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Activity</a>
		</div>
	</div> 
</li>
