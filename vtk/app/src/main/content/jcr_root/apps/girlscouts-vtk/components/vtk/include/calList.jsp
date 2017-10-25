
<!-- <input type="button" value="Click here to change your recurring meeting date and time." name="" onclick="showAlterYearPlanStartDate()" class="button" /> -->

	<%
	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
	%>
    <div id="locMsg"></div>
	<p>Select the <i class="icon-calendar"></i> to change the date, time, or cancel an individual meeting. 
	You can also group multiple  meetings to a single date. 
	Select the <i class="icon-gear"></i> to use the planning wizard to reconfigure the calendar from that date forward.</p>
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
			Meeting _meeting= yearPlanUtil.getMeeting( user, troop, meeting.getRefId() ); 
		%>
		<tr>
		  <td>
		  
		    <a onclick="manageCalElem('<%=date.getTime()%>');" title="calendar"><i class="icon-calendar"></i></a>
		 
		   </td>
		  <td><span><%=currentMeeting %></span></td>
		  <td><span><%= VtkUtil.formatDate(VtkUtil.FORMAT_CALENDAR_DATE,  date ) %></span></td>
		  
		  <td><span><%= _meeting.getName() %>
				<%if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")) { %>
					<span class="alert">(Cancelled)</span>
				<% } %></span></td>
		  <td class="vtk_age_level <%= _meeting.getLevel() %>"><%= (_meeting.getLevel() ==null && _meeting.getLevel().length()>0) ? "" : _meeting.getLevel().charAt(0) %></td>
		  <td>
		  <% if( date!=null && date.after( new java.util.Date() ) ){%>
		 	 <a onclick="showAlterYearPlanStartDate('<%= date.getTime() %>', '<%= (sched.size() +1 - currentMeeting) %>')" title="settings"><i class="icon-gear"></i></a>
		  <%} %>
		  </td>
		</tr>
		<% }%>
	</table>
