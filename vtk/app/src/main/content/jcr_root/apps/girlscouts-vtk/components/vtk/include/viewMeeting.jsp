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

	<li <%if( hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) ){ %>
			onclick='self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>"'
			<%}%>
		class="row meeting <%=( troop.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "ui-state-default" : "ui-state-default ui-state-disabled"%>" value="<%=meetingCount%>">
		
		<div class="columns large-21 push-1">
	    <div class="large-3 medium-3 small-24 columns">
				<div class="bg-square  <%=(!calendarNotSet && isCanceled) ? "canceled" : "" %>">
					<div class="count"><%= meetingCount %></div>
					<div class="date">
			    	<p class="month"><%=calendarNotSet ? "Meeting" : FORMAT_MONTH.format(date) %></p>
			      <p class="day"><%=calendarNotSet ? meetingCount : FORMAT_DAY_OF_MONTH.format(date) %></p>
			      <p class="hour"><%=calendarNotSet ? meetingCount : FORMAT_hhmm_AMPM.format(date) %></p>
				  </div>
					<!-- <%if(!calendarNotSet && isCanceled) { %> 
						<div class="cancelled">X</div>
					<% } %> -->
				</div><!--/square-->
				<!-- 	TODO for new redesign remove	<div class="centered-table" style="display:none;">
					<div class="show-for-small smallBadge">
						<%
							String img= "";
							try {
								img= meetingE.getRefId().substring( meetingE.getRefId().lastIndexOf("/")+1).toUpperCase();
								if(img.contains("_") )img= img.substring(0, img.indexOf("_"));
							}catch(Exception e){
								e.printStackTrace();
							}
						%> 
						<img width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
					</div>
				</div> -->
			</div>     
	    <div class="large-20 medium-19 small-24 columns">
				<p class='subtitle'>
					<% if(isCanceled){ %>
						<span><strong>Meeting Canceled:</strong></span>
					<% } %>
				  <%=meeting.getName() %>
			  </p>
				<p class="category"><%=meeting.getCat()%></p>
				<p class="blurb"><%=meeting.getBlurb() %></p>
				<!-- not in the design<%if( hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID) ){ %>
					<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Meeting</a>
				<%} %> -->
			</div>
			<div class="large-4 medium-5 columns">
				<img width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
			</div>
	  </div><!--/columns-->
</li>
