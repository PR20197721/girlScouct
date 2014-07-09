<li class="ui-state-default ui-state-disabled activity">
        <div  class="row">
                <div class="large-4 columns">
                        <div class="planSquare">
				<%= df.format(activity.getDate())%>
                        </div>
		</div>
                <div class="large-20 columns">
			<b><%= activity.getName()%></b>
			<br/><%= df.format(activity.getDate())%> to <%=df.format(activity.getEndDate()) %>
		
			<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Activity</a>
		</div>
	</div> 
</li>
