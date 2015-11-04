<% 
	Meeting meeting = yearPlanUtil.getMeeting(user, meetingE.getRefId());
	boolean isCanceled =false;
	boolean calendarNotSet = false;
	if (troop.getYearPlan().getSchedule() == null) {
		calendarNotSet = true;
	}
	if (meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true") ) {
		 isCanceled=true;
	}
%>
<li
	<%if( VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) ){ %>
	onclick='self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>"'
	<%}%>
	class="row meeting <%=( troop.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "ui-state-default" : "ui-state-default ui-state-disabled"%>"
	value="<%=meetingCount%>">

	<div class="column large-20 medium-20 large-centered medium-centered">
		<div class="large-3 medium-3 small-4 columns">
		<img class="touchscroll" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/touchscroll-small.png" width="21" height="34">
			<div class="bg-square  <%=(!calendarNotSet && isCanceled) ? "canceled" : "" %>">
				<div class="count"><%= meetingCount %></div>
				<div class="date">
					<p class="month"><%=calendarNotSet ? "Meeting" : VtkUtil.formatDate(VtkUtil.FORMAT_MONTH,date) %></p>
					<p class="day"><%=calendarNotSet ? meetingCount : VtkUtil.formatDate(VtkUtil.FORMAT_DAY_OF_MONTH,date) %></p>
					<p class="hour"><%=calendarNotSet ? meetingCount : VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM,date) %></p>
				</div>

			</div>
			<!--/square-->
		</div>

		<div class="large-22 medium-22 small-17 columns">
			<p class='subtitle'>
				<% if(isCanceled){ %>
				<span><strong>Meeting Canceled:</strong></span>
				<% } %>
				<%=meeting.getName() %>
			</p>
			<p class="category"><%=meeting.getCat()%></p>
			<p class="blurb"><%=meeting.getBlurb() %></p>
		</div>
		<div class="large-2 medium-2 columns hide-for-small">
			<img
				src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"
				alt="<%=img%>" />
		</div>
	</div>
	<!--/columns-->
</li>