<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/planView.jsp -->
<%
        String activeTab = "planView";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<%
  if( user.getYearPlan()!=null){
  // split resource panel
%>
<div id="panelWrapper" class="row">
  <div id="panelLeft" class="small-24 medium-24 large-18 columns">
<%
  }

	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan(), false);
	if( sched==null || (sched.size()==0)){out.println( "You must first select a year plan."); return;}
	java.util.List<java.util.Date> dates =new java.util.ArrayList<java.util.Date>(sched.keySet());
	long nextDate=0, prevDate=0;
	java.util.Date searchDate= null;

	if( request.getParameter("elem") !=null ) {
		searchDate = new java.util.Date( Long.parseLong(  request.getParameter("elem")  ) );	
	}else if( session.getValue("VTK_planView_memoPos") !=null ){
			searchDate= new java.util.Date( (Long)session.getValue("VTK_planView_memoPos")  );
	} else {
		
		if( user.getYearPlan().getSchedule()==null)
			searchDate = (java.util.Date) sched.keySet().iterator().next();
		else{
			
		  java.util.Iterator itr = sched.keySet().iterator();
		  while( itr.hasNext() ){
			searchDate= (java.util.Date)itr.next();
			if( searchDate.after( new java.util.Date() ) )
				break;
			
		  }
	    }
			
	}

	int currInd =dates.indexOf(searchDate);
        int meetingCount = currInd+1;

	if( dates.size()-1 > currInd )
		nextDate = ((java.util.Date)dates.get(currInd+1)).getTime();
	if( currInd>0 )
		prevDate = ((java.util.Date)dates.get(currInd-1)).getTime();
	
	session.putValue("VTK_planView_memoPos", searchDate.getTime());
    YearPlanComponent _comp= sched.get(searchDate);
    MeetingE meeting = null;
    List<Asset> _aidTags = null;
    Meeting meetingInfo = null;

%>
  <div id="planMsg"></div>
<%
	try {
	if ( _comp.getType() == YearPlanComponentType.MEETING) {
		meeting = (MeetingE) _comp;
		meetingInfo = meetingDAO.getMeeting(  meeting.getRefId() );
		
		meeting.setMeetingInfo(meetingInfo);
		
		java.util.List <Activity> _activities = meetingInfo.getActivities();
		java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  meetingInfo.getMeetingInfo();

		boolean isLocked=false;
		if(searchDate.before( new java.util.Date() ) && user.getYearPlan().getSchedule()!=null ) isLocked= true;

		boolean isCanceled =false;
		if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
			isCanceled  = true;
		}

		_aidTags = meeting.getAssets();

		java.util.Date sysAssetLastLoad =  sling.getService(org.girlscouts.vtk.helpers.DataImportTimestamper.class).getTimestamp(); //SYSTEM QUERY
		if(meeting.getLastAssetUpdate()==null || meeting.getLastAssetUpdate().before(sysAssetLastLoad) ){

			_aidTags = _aidTags ==null ? new java.util.ArrayList() : _aidTags;

			//rm cachables
			java.util.List aidToRm= new java.util.ArrayList();
			for(int i=0;i<_aidTags.size();i++){
				if( _aidTags.get(i).getIsCachable() )
					aidToRm.add( _aidTags.get(i));
			}

			for(int i=0;i<aidToRm.size();i++)
				_aidTags.remove( aidToRm.get(i));

			//query aids cachables
			 java.util.List __aidTags =  meetingDAO.getAids( meetingInfo.getAidTags(), meetingInfo.getId(), meeting.getUid());

			//merge lists aids
			_aidTags.addAll( __aidTags );

			//query resources cachables
			java.util.List __resources =  meetingDAO.getResources( meetingInfo.getResources(), meetingInfo.getId(), meeting.getUid());

			//merge lists resources
			_aidTags.addAll( __resources );

			meeting.setLastAssetUpdate( new java.util.Date() );
			meeting.setAssets( _aidTags);
		}
%><%@include file="include/viewYearPlanMeeting.jsp" %><%
	} else {
%><%@include file="include/viewYearPlanActivity.jsp" %><%
	}
	} catch (NullPointerException npe) {
		npe.printStackTrace();
	}
        if( user.getYearPlan()!=null){
%>
        </div>
        <div id="panelRight" class="small-24 medium-24 large-6 columns hide-for-print">
        <h2 id="resourceListing">Resources:</h2>
    		<ul>
        <%
        int planMeetingResourceCount = 0;

        if( _aidTags!=null ) {
        	for(int i=0;i<_aidTags.size();i++){
        		org.girlscouts.vtk.models.Asset asset = _aidTags.get(i);
        		if( asset.getType()!=null )
        		 if( asset.getType(false)!=  org.girlscouts.vtk.dao.AssetComponentType.RESOURCE ) continue;
        			planMeetingResourceCount++;
        %>
        		<li>- <a href="<%=asset.getRefId()%>" target="_blank"><%=asset.getTitle() %></a></li> 
        <%
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
.modal-example-body p,
.modal-example-header h4 {
    margin: 0;
}
.modal-example-body {
    padding: 20px;
}
</style>

<script>
function x12(xx, ttl, id){
	
	var y = document.getElementById('ifr');
	y.src= xx;
	
	 $.fn.custombox( document.getElementById(id) );
	
	 document.getElementById('xyz').innerHTML= ttl;
}

</script>
