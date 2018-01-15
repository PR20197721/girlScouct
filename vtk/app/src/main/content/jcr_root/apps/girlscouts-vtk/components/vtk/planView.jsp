<%

	if( false ){
		
		
		response.sendRedirect("/content/girlscouts-vtk/en/vtk.rand.meeting.html?elem=189320400000");
		return;
	}
%>


<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/planView.jsp -->
<%
		String activeTab = "planView";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<%
		  if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID ) ) {
					%><span class="error">You have no permission to view meeting(s)</span><%
					return;
		  }

      	  if( troop.getYearPlan()!=null){
		                // split resource panel
					%>
					<div id="panelWrapper" class="row">
						<div id="panelLeft" class="small-24 medium-24 large-18 columns">
							<%
			}
    
      	  	
      			
      	  	PlanView planView = meetingUtil.planView(user, troop, request);
      	  	long nextDate=planView.getNextDate(), prevDate=planView.getPrevDate();
			java.util.Date searchDate= planView.getSearchDate();
			MeetingE meeting = planView.getMeeting();
			List<Asset> _aidTags = planView.getAidTags();
			Meeting meetingInfo = meeting.getMeetingInfo();
			YearPlanComponent _comp = planView.getYearPlanComponent();
			int meetingCount = planView.getMeetingCount();
			java.util.List <Activity> _activities = meetingInfo.getActivities();
			java.util.Map<String, JcrCollectionHoldString> meetingInfoItems= meetingInfo.getMeetingInfo();
			int currInd= planView.getCurrInd();
			
			boolean isCanceled =false;
			if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
				isCanceled  = true;
			}
			
			boolean isLocked=false;
			if(searchDate.before( new java.util.Date() ) && troop.getYearPlan().getSchedule()!=null ) isLocked= true;

			
			if( _comp ==null ){
				%><span class="error">
				A co-leader has made changes to the schedule of the Year Plan that affect this meeting. 
				<a href="/content/girlscouts-vtk/en/vtk.plan.html">Click here</a> to go to the Year Plan view to see this changes and access the updated version of this meeting.
				</span><% 
				return;
			}
			
			%><div id="planMsg"></div><%
			
			
			try {
					if ( _comp.getType() == YearPlanComponentType.MEETING) {		
						%><%@include file="include/viewYearPlanMeeting.jsp"%>
						<%
					} else {
						%><%@include file="include/viewYearPlanActivity.jsp"%>
						<%
					}
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}
		        if( troop.getYearPlan()!=null){
		%>
	</div>
	<div id="panelRight" class="small-24 medium-24 large-6 columns">
		<h2 id="resourceListing">Resources:</h2>
		<br />
		<ul>
			<%
				int planMeetingResourceCount = 0;
				if( _aidTags!=null ) {
					for(int i=0;i<_aidTags.size();i++){
						org.girlscouts.vtk.models.Asset asset = _aidTags.get(i);
						if( asset.getType()!=null )
						if( asset.getType(false)!=  org.girlscouts.vtk.dao.AssetComponentType.RESOURCE ) continue;
						planMeetingResourceCount++;
				
						%><li>- <a href="<%=asset.getRefId()%>" target="_blank"><%=asset.getTitle()%></a></li><%
					}
				}
			%>
		</ul>

	</div>
</div>
<%
	}
%>
<div id="editAgenda"></div>
<style>
.modal-example-content {
	width: 600px;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
	background-color: #FFF;
	border: 1px solid rgba(0, 0, 0, 0.2);
	border-radius: 6px;
	outline: 0 none;
}

.modal-example-header {
	border-bottom: 1px solid #E5E5E5;
	padding: 15px;
}

.modal-example-body p,.modal-example-header h4 {
	margin: 0;
}

.modal-example-body {
	padding: 20px;
}
</style>

<script>

	function x12(xx, ttl, id) {

		var y = document.getElementById('ifr');
		y.src = xx;

		$.fn.custombox(document.getElementById(id));

		document.getElementById('xyz').innerHTML = ttl;
	}
</script>
