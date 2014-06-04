  
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
  
<div id="locMsg1"></div>
<div>
<%
User user= (User)session.getValue("VTK_user");
java.util.List <Location> locations = user.getYearPlan().getLocations();
if( locations==null ){ out.println("No locations");return;}
for(int i=0;i<locations.size();i++){
	Location location = locations.get(i);
	%>
		<div style="border:4px solid yellow;">
			Name: <%=location.getName() %>
			<br/>Address: <%=location.getAddress() %>
			<br/>City: <%=location.getCity() %>
			<br/>State: <%=location.getState() %>
			<br/>Zip: <%=location.getZip() %>
			<a href="javascript:void(0)" onclick="rmLocation('<%=location.getName()%>'); location.reload();">remove</a> ||
			<a href="javascript:void(0)" onclick="applyLocToAllMeetings('<%=location.getPath()%>')">Apply to All meetings</a>
			<div style="padding:30px; background-color:gray;">
				<% 
				java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
				java.util.Iterator itr=  sched.keySet().iterator();
				while( itr.hasNext()){
					java.util.Date date = (java.util.Date) itr.next();
					%>
						<li><input type="checkbox" name="<%=location.getName() %>" value="<%=date%>"/><%=fmtDate.format(date) %></li>
					<% 
				}
				%>
				<input type="button" value="assign locations" onclick="updLocations('<%=location.getPath()%>', '<%=location.getName()%>')"/>
			</div>
		</div>
	<%}%>
</div>



<%!
java.text.SimpleDateFormat fmtDate= new java.text.SimpleDateFormat("MM/dd/yyyy");
%>