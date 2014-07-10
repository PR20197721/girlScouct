<% 
	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
	Meeting meeting = meetingDAO.getMeeting(meetingE.getRefId());
	boolean isCanceled =false;
	if (meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true") ) {
		 isCanceled=true;
	}
%>


<li  class="meeting <%=( user.getYearPlan().getSchedule()==null || new java.util.Date().before(date)) ? "ui-state-default" : "ui-state-default ui-state-disabled"%>" value="<%=meetingCount%>">
	<div  class="row">
		<div class="large-4 columns">
			<div class="planSquare">
				<div class="count"><%= meetingCount %></div>
<%
		if (isCanceled) {
%>
				<div class="cancelled"><div class="cross">X</div></div>
<%
		}
%>
				<div class="date">
<%
	if (user.getYearPlan().getSchedule()!=null) {
%>
                                        <div class="cal"><span class="month"><%= FORMAT_MONTH.format(date)%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(date)%><br/></span><span class="time"><%= FORMAT_hhmm_AMPM.format(date)%></span></div>
<%
	}
%>
				</div>
			</div>
		</div>
		<div class="large-16 columns">
<%

if( meetingE.getCancelled()!=null && meetingE.getCancelled().equals("true")){%>
			<span style="color:#FFF; background-color:red;">CANCELLED</span>
<% }

%>
			<h1><%=meeting.getName() %></h1>
		
			<span class="tags"><%=meeting.getAidTags() %></span>
		
			<p class="blurb"><%=meeting.getBlurb() %></p>
			<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=date.getTime()%>" style="color:#008f50;">View Meeting</a>
		</div>
		<div class="large-4 columns">
			<img width="100" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/badge.png"/>
		</div>
	</div>
</li>

    	


