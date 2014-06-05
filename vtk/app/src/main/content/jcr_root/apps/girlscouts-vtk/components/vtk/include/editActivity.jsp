


<div id="<%=_activity.getActivityNumber() %>" style="display:none;">


<div>
<select onchange="durEditActiv(this.options[this.selectedIndex].value, '<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">

<option value="<%=_activity.getDuration()%> %>" SELECTED><%=_activity.getDuration()%></option>
<option value="10">10</option>
<option value="5">5</option>
</select>
</div>


	Agenda Item: <%= _activity.getName() %>
	<br/><a href="javascript:void(0)" onclick="rmAgenda('<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">Delete This Agenda Item</a>
	
	<p> <%=_activity.getActivityDescription()%></p>
	
</div>