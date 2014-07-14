<div class="sectionBar">Manage Activities</div>
<br/>
<%
if( user.getYearPlan().getActivities()!=null && user.getYearPlan().getActivities().size()>0){
%>
<ul id="manageActivities">
<%
	for(int t=0;t<user.getYearPlan().getActivities().size(); t++){
%>
	<li>
		<%=user.getYearPlan().getActivities().get(t).getDate() %>
		<b><%=user.getYearPlan().getActivities().get(t).getName() %></b>
		<a href="javascript:void(0)" onclick="rmCustActivity('<%=user.getYearPlan().getActivities().get(t).getPath()%>')">Remove</a>
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
