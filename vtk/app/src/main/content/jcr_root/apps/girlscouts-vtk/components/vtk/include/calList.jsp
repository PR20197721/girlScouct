
<!-- <input type="button" value="Click here to change your recurring meeting date and time." name="" onclick="showAlterYearPlanStartDate()" class="button" /> -->


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
	<tr>
	  <td><a onclick="manageCalElem('<%=date.getTime()%>');" title="calendar"><i class="icon-calendar"></i></a></td>
	  <td><span><%=currentMeeting %></span></td>
	  <td><span><%= FORMAT_CALENDAR_DATE.format( date ) %></span></td>
	  <td><span><%= yearPlanUtil.getMeeting( user, meeting.getRefId() ).getName() %>
			<%if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")) { %>
				<span class="alert">(Cancelled)</span>
			<% } %></span></td>
	  <td><a onclick="showAlterYearPlanStartDate()" title="settings"><i class="icon-gear"></i></a></td>
	</tr>

<% }%>
</table>
