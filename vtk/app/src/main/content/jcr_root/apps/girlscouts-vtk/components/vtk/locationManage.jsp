<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/locationManage.jsp -->
<%!
        String activeTab = "community";
        boolean showVtkNav = true;
%>
<div id="locMsg1"></div>
<div>
<%
java.util.List <Location> locations = user.getYearPlan().getLocations();
if( locations==null || locations.size()<=0){
	out.println("No locations");
	return;
}
for(int i=0;i<locations.size();i++){
	Location location = locations.get(i);
%>
	<div style="border:4px solid yellow;">
		Name: <%=location.getName() %>
		<br/>Address: <%=location.getAddress() %>
		<br/>City: <%=location.getCity() %>
		<br/>State: <%=location.getState() %>
		<br/>Zip: <%=location.getZip() %>
		<a href="javascript:void(0)" onclick="rmLocation('<%=location.getUid()%>'); ">remove</a> ||
		<a href="javascript:void(0)" onclick="applyLocToAllMeetings('<%=location.getPath()%>')">Apply to All meetings</a>
		<div style="padding:30px; background-color:gray;">
<% 
	if( user.getYearPlan().getSchedule()!=null){	
		java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
		java.util.Iterator itr=  sched.keySet().iterator();
		while( itr.hasNext()){
			java.util.Date date = (java.util.Date) itr.next();
			YearPlanComponent _comp= sched.get(date);
			if( _comp.getType() != YearPlanComponentType.MEETING ) {
				continue;
			}
			String mLoc = ((MeetingE)_comp).getLocationRef();
			mLoc = mLoc==null ? "" : mLoc;
			if( date.after( new java.util.Date()) ){
%>
			<li><input type="checkbox" name="<%=location.getName() %>" value="<%=date%>" <%= mLoc.equals(location.getPath() ) ? "CHECKED" : ""%> /><%=fmtDate.format(date) %></li>
<% 
			}else{
%>
			<li><%= mLoc.equals(location.getPath() ) ? "YES" : ""%> <del><%=fmtDate.format(date) %></del></li>
<% 
			}
		}
%>
			<input type="button" value="assign locations" onclick="updLocations('<%=location.getPath()%>', '<%=location.getName()%>')"/>
<%
	}
%>
		</div>
	</div>
<%
}
%>
</div>
