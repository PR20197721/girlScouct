<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/locationManage.jsp -->
<%!
        String activeTab = "community";
        boolean showVtkNav = true;
%>
<div id="locMsg1"></div>
<div class="locationListing">
        <div class="row">
<%
java.util.List <Location> locations = user.getYearPlan().getLocations();
if( locations==null || locations.size()<=0){
	out.println("No locations");
	return;
}
for(int i=0;i<locations.size();i++){
	Location location = locations.get(i);
%>
        <div class="row">
		<div class="small-4 columns">&mdash; <a href="javascript:void(0)" onclick="rmLocation('<%=location.getUid()%>'); ">Remove</a></div>
                <div class="small-10 columns"><%=location.getName() %></div>
                <div class="small-10 columns"><%=location.getAddress() %></div>
	</div>
        <div class="row">
                <div class="small-4 columns">&nbsp;</div>
                <div class="small-20 columns">
			<a href="javascript:void(0)" onclick="applyLocToAllMeetings('<%=location.getPath()%>')">Apply to All meetings</a>
			<br/>
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
				<li><input type="checkbox" name="<%=location.getName() %>" value="<%=date%>" <%= mLoc.equals(location.getPath() ) ? "CHECKED" : ""%> /><%=FORMAT_MMddYYYY.format(date) %>
				
				
				<%if( ((MeetingE)_comp).getCancelled()!=null && ((MeetingE)_comp).getCancelled().equals("true")){%>
   					<span style="background-color:red;">Canceled</span>
   				<%} %>
				
				
				</li>
<% 
                        }else{
%>
				<li><%= mLoc.equals(location.getPath() ) ? "YES" : ""%> <del><%=FORMAT_MMddYYYY.format(date) %></del></li>
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
	</div>
<%
}
%>
</div>
