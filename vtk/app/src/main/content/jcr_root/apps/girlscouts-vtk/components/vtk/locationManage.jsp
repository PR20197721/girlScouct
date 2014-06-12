  
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
  
  
  
  
  
  


<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd ><a href="/content/girlscouts-vtk/en/vtk.html">Year Plan</a></dd>
  <dd class="active"><a href="/content/girlscouts-vtk/en/vtk.planView.html">Meeting Plan</a></dd>
  <dd ><a href="#panel2-4">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
    <div class="content" id="panel2-1"></div>
    <div class="content" id="panel2-2"></div>
    <div class="content" id="panel2-3"></div>
    <div class="content" id="panel2-4"></div>
    <div class="content" id="panel2-5"></div>
</div>




<div id="locMsg1"></div>
<div>
<%
HttpSession session = request.getSession();

User user= (User)session.getValue("VTK_user");
java.util.List <Location> locations = user.getYearPlan().getLocations();
if( locations==null || locations.size()<=0){ out.println("No locations");return;}

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
			  if( user.getYearPlan().getSchedule()!=null){	
				java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
				java.util.Iterator itr=  sched.keySet().iterator();
				while( itr.hasNext()){
					java.util.Date date = (java.util.Date) itr.next();
					YearPlanComponent _comp= sched.get(date);
					
					
					if( _comp.getType() != YearPlanComponentType.MEETING ) continue;
					
					String mLoc = ((MeetingE)_comp).getLocationRef();
					mLoc = mLoc==null ? "" : mLoc;
					%>
						<li><input type="checkbox" name="<%=location.getName() %>" value="<%=date%>" <%= mLoc.equals(location.getPath() ) ? "CHECKED" : ""%> /><%=fmtDate.format(date) %></li>
						
						
					<% 
				}
			  
				%>
				<input type="button" value="assign locations" onclick="updLocations('<%=location.getPath()%>', '<%=location.getName()%>')"/>
			<%} %>
			</div>
		</div>
	<%}%>
</div>



<%!
java.text.SimpleDateFormat fmtDate= new java.text.SimpleDateFormat("MM/dd/yyyy");
%>