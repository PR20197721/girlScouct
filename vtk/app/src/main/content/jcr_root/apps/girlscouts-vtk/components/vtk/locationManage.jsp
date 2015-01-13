<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/locationManage.jsp -->
<%!
        String activeTab = "community";
        boolean showVtkNav = true;
%>
<%
java.util.List <Location> locations = troop.getYearPlan().getLocations();
if( locations==null || locations.size()<=0){
	out.println("Applies to "+troop.getYearPlan().getMeetingEvents().size()+" of "+troop.getYearPlan().getMeetingEvents().size()+" meetings");
	return;
}
%>

<div id="locMsg1"></div>
<div class="locationListing">
    <%
    for(int i=0;i<locations.size();i++){
    	Location location = locations.get(i);
    %>
	<div class="locationResultElement">
        <div class="row">
        <!-- 		<div class="small-8 columns"><a href="javascript:void(0)" onclick="rmLocation('<%=location.getUid()%>'); " class="button linkButton">-&nbsp;Remove</a></div> -->
        <div class="small-10 columns"><h3><%=location.getName() %></h3></div>
        <div class="small-14 columns"><%=location.getAddress() %></div>
	    </div>
	    
        <div class="row">
<!--      <div class="small-4 columns">&nbsp;</div> -->
        <div class="small-20 columns">
    			<div class="locationList">
    			<ul class="clearfix">
            <% 
              if( troop.getYearPlan().getSchedule()!=null){    
              java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
              java.util.Iterator itr=  sched.keySet().iterator();
                while( itr.hasNext()){
                  java.util.Date date = (java.util.Date) itr.next();
                  YearPlanComponent _comp= sched.get(date);
                if( _comp.getType() != YearPlanComponentType.MEETING ) {
                  continue;
                }
                String mLoc = ((MeetingE)_comp).getLocationRef();
                mLoc = mLoc==null ? "" : mLoc;
                if( date.after( new java.util.Date()) ) {
              %>
      				<li><input type="checkbox" name="<%=location.getName() %>" value="<%=date%>" <%= mLoc.equals(location.getPath() ) ? "CHECKED" : ""%> /><%=FORMAT_MMddYYYY.format(date) %>
      				<%if( ((MeetingE)_comp).getCancelled()!=null && ((MeetingE)_comp).getCancelled().equals("true")){%>
      					<span class="alert">(Cancelled)</span>
         				<%} %>
      				</li>
            <% 
                }else{
            %>
            				<li><%= mLoc.equals(location.getPath() ) ? "Activity day past" : ""%> <del><%=FORMAT_MMddYYYY.format(date) %></del></li>
            <% 
                      }
              }
            %>
          </ul>
  				<div class="linkButton Wrapper">
  					<button class="btn" onclick="updLocations('<%=location.getPath()%>', '<%=location.getName()%>')" class="button linkButton">Assign to checked locations</button>
  					<button class="btn" onclick="applyLocToAllMeetings('<%=location.getPath()%>')" class="button linkButton">Apply to all meetings</button>
            <button class="btn right" onclick="rmLocation('<%=location.getUid()%>');">Remove</button>
  				</div>
        <% } %>
			</div>
		</div>
	</div>
	</div>
<%
}
%>
</div>
