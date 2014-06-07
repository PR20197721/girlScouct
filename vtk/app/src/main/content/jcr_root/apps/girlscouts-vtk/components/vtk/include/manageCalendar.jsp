<%
	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
%>


<div >

   <table>
   	<tr>
   	<td><a href="javascript:void(0)" onclick="document.getElementById('<%=(i+1) %>').style.display = 'block';">IMG</a>
   	<td><%=(i+1) %>
   	<td><%= sched.get(i).toString(fmt) %>
   	<td>
   	
   	
   	<%=   	meetingDAO.getMeeting(  user.getYearPlan().getMeetingEvents().get(i).getRefId() ).getName() %>
   </table>
	
	<div id="<%=(i+1) %>" style="padding:40px; background-color:gray;display:none; border:1px solid red;">
		Change Date: <input type="text" value="<%= sched.get(i).toString(fmtDate) %>" id="cngDate<%=(i+1) %>" onclick="dtPicker('cngDate<%=(i+1) %>')" />
		Change Time: <input tyle="text" id="cngTime<%=(i+1) %>" value="<%= sched.get(i).toString(fmtHr) %>"/>
		<select id="cngAP<%=(i+1) %>"><option value="pm">pm</option> <option value="am">am</option></select>
		
		<input type="checkbox" id="isCancellMeeting<%=(i+1) %>" />Cancel Meeting
		<input type="button" value="update" onclick="updSched1('<%=(i+1)%>','<%=user.getYearPlan().getMeetingEvents().get(i).getPath()%>','<%=sched.get(i).getMillis()%>')"/>
	</div>
</div>