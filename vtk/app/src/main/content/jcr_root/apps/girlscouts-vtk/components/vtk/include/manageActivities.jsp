<div style="background-color:gray; color:#fff;">Manage Activities</div>

<%
//org.girlscouts.vtk.models.user.User user= (org.girlscouts.vtk.models.user.User) session.getValue("VTK_user");
if( user.getYearPlan().getActivities()!=null && user.getYearPlan().getActivities().size()>0){
	for(int t=0;t<user.getYearPlan().getActivities().size(); t++){
		
		%>
		<div>
		<%=user.getYearPlan().getActivities().get(t).getDate() %>
		<b><%=user.getYearPlan().getActivities().get(t).getName() %></b>
		<a href="javascript:void(0)" onclick="rmCustActivity('<%=user.getYearPlan().getActivities().get(t).getPath()%>')">Remove</a>
		</div>
		<%
		
	}
}else out.println("none");
%>