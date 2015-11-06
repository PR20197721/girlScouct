<div class="content">
	<div id="<%=ii %>" style="display:block;" class="setupCalendar">
		<h3>***Agenda Item: <%= _activity.getName() %></h3>
		<select onchange="durEditActiv(this.options[this.selectedIndex].value, '<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">
			<option value="5"  <%= (_activity.getDuration()==5)  ? "SELECTED" : "" %>>5</option>
			<option value="10" <%= (_activity.getDuration()==10) ? "SELECTED" : "" %>>10</option>
			<option value="15" <%= (_activity.getDuration()==15) ? "SELECTED" : "" %>>15</option>
			<option value="20" <%= (_activity.getDuration()==20) ? "SELECTED" : "" %>>20</option>
			<option value="25" <%= (_activity.getDuration()==25) ? "SELECTED" : "" %>>25</option>
			<option value="30" <%= (_activity.getDuration()==30) ? "SELECTED" : "" %>>30</option>
		</select>
		<section class="right">
			<input type="button" onclick="location.reload();" value="Save and Back to meeting" class="btn  button linkButton" />
			<button class="btn button linkButton" onclick="return rmAgenda('<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">Delete This Agenda Item</button>
		</section>
		<%if (_activity.getActivityDescription() !=null && !_activity.getActivityDescription().isEmpty()) {
			out.println("<div class=\"clearfix\">"+_activity.getActivityDescription()+"</div>");
		}%>
	</div>
</div>
