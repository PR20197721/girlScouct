<% 
	Meeting meeting = meetingDAO.getMeeting(meetingE.getRefId());
	boolean isCanceled =false;
	boolean calendarNotSet = false;
	if (user.getYearPlan().getSchedule() == null) {
		calendarNotSet = true;
	}
	if (meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true") ) {
		 isCanceled=true;
	}
%>


<li  class="meeting <%=( user.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "ui-state-default" : "ui-state-default ui-state-disabled"%>" value="<%=meetingCount%>">
	<div  class="row">
                <div class="large-4 medium-5 small-7 columns">
			<div class="planSquare">
<%
	if (calendarNotSet) {
%>
				<div class="date">
					<div class="cal"><span class="month">Meeting<br/></span><span class="day"><%= meetingCount %><br/></span></div>
				</div>
<%
	} else {
%>
				<div class="count"><%= meetingCount %></div>
<%
		if (isCanceled) {
%>
				<div class="cancelled"><div class="cross">X</div></div>
<%
		}
%>
				<div class="date">
					<div class="cal"><span class="month"><%= FORMAT_MONTH.format(date)%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(date)%><br/></span><span class="time hide-for-small"><%= FORMAT_hhmm_AMPM.format(date)%></span></div>
				</div>
<%
	}
%>
			</div>
			<div class="show-for-small smallBadge"><img width="100" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/badge.png"/></div>
		</div>
                <div class="large-16 medium-14 small-17 columns">
			<div class="planMain">
				<h2>
<%
if( meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true")){%>
				<span class="alert">(Cancelled)</span>
<% }

%>
				<%=meeting.getName() %></h2>
				<p class="tags"><%=meeting.getAidTags() %></p>
				<p class="show-for-small"><%= FORMAT_hhmm_AMPM.format(date)%></p>
				<br/>
				<p class="blurb"><%=meeting.getBlurb() %></p>
				<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>">View Meeting</a>
			</div>
		</div>
		<div class="large-4 medium-5 hide-for-small columns">
			<img width="100" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/badge.png"/>
		</div>
	</div>
</li>

    	


