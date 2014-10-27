<div class="sectionBar">Manage Activities</div>
<br/>
<%
if( troop.getYearPlan().getActivities()!=null && troop.getYearPlan().getActivities().size()>0){
%>
<ul id="manageActivities">
<%
	for(int t=0;t<troop.getYearPlan().getActivities().size(); t++){
%>
	<li>
		<%=troop.getYearPlan().getActivities().get(t).getDate() %>
		<b><%=troop.getYearPlan().getActivities().get(t).getName() %></b>
		<a href="javascript:void(0)" onclick="rmCustActivity('<%=troop.getYearPlan().getActivities().get(t).getPath()%>')">Remove</a>
	</li>
<%
	}
%>
</ul>
<%
} else {
%>
	No activities.
<%
}
%>
