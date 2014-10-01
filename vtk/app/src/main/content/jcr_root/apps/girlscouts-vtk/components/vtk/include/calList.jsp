<%
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
%>
<div id="locMsg"></div>
<table cellpadding="5" cellspacing="0" class="yearMeetingList" width="100%">
<%
int currentMeeting=0;
java.util.Iterator itr= sched.keySet().iterator();
while( itr.hasNext() ){
	java.util.Date date = (java.util.Date)itr.next();
	YearPlanComponent _comp= sched.get(date);
	if(_comp.getType() != YearPlanComponentType.MEETING){
		 continue;
	}
	MeetingE meeting = (MeetingE)sched.get(date);
	currentMeeting++;
%>
<% if( date.after(new java.util.Date() )){ %>
        <tr onclick="manageCalElem('<%=date.getTime()%>');">
<% }else{ %>
	<tr>
<% } %>
		<td width="26">
<% if( date.after(new java.util.Date() )){ %>
			<a href="#" onclick="manageCalElem('<%=date.getTime()%>')">
				<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/calendar-pick.png" alt="Calender" width="22" height="22"/>
			</a>
<% }else{ %>
                        <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/calendar-pick-past.png" alt="Calender Expired" width="22" height="22"/>
<% } %>
		</td>
		<td width="5"><%=currentMeeting %></td>
		<td><%= FORMAT_CALENDAR_DATE.format( date ) %></td>
		<td><%= meetingDAO.getMeeting(  meeting.getRefId() ).getName() %>
			<%if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){%>
				<span class="alert">(Cancelled)</span>
			<%} %>
		</td>
	</tr>
<% }%>
</table>
