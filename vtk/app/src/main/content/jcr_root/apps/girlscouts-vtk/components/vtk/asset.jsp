<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<script>
function assignAid(aidId, meetingId, assetName, assetDesc){
	  
	  $.ajax({
			cache: false,
			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
			type: 'POST',
			data: { 
				addAids:aidId,
				meetingId: meetingId,
				assetName:assetName,
				assetDesc:assetDesc,
				a:Date.now()
			},
			success: function(result) {

			}
		});
		//document.location="/content/girlscouts-vtk/en/vtk.resource.html";
		applyAids(aidId);
}
</script>
<%
	String aidId= request.getParameter("aidId");
	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
	if( sched==null || (sched.size()==0)){
%>
<span class="instruction">You must first select a year plan before adding resources.</span>
<%
	} else {
%>
<div>Adding: <b><%=request.getParameter("aidName")%></b></div>
<span class="instruction">Please select the meeting(s) where you would like to add this resource:</span>

<br/>
<ul>
<%
		MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
		java.util.Iterator itr= sched.keySet().iterator();
		while( itr.hasNext() ){
			java.util.Date dt= (java.util.Date) itr.next();
			YearPlanComponent _comp= sched.get(dt);
			String displayName ="";
			java.util.List<Asset> assets = null;
			switch( _comp.getType() ){
				case ACTIVITY :
					displayName=((Activity)_comp).getName();
					assets =  ((Activity)_comp).getAssets();
					break;
				case MEETING :
					Meeting meetingInfo =meetingDAO.getMeeting( ((MeetingE) _comp).getRefId() );
					displayName=meetingInfo.getName();
					assets =  ((MeetingE) _comp).getAssets(); 
					break;
			}
			boolean meetingIsSelected = false;
			if( assets!=null ) {
				for(int i=0;i<assets.size();i++){
					if( assets.get(i).getRefId().equals(aidId ) ) {
						meetingIsSelected = true;
					}
				}
			}
%>
			<li class="_comp.getType().toLowerCase()">
<%
			if (meetingIsSelected) {
%>
				<%=displayName%> (added)
<%
			} else {
%>
                                <a href="javascript:void(0)" onclick="assignAid('<%=aidId %>', '<%=_comp.getUid()%>', '<%=request.getParameter("aidName")%>')"><%=displayName%></a>
<%
                        }
%>
			</li>
<%
		}
	}
%>
</ul>
<br/><hr/><br/>
<center>
<a href="javascript:void(0)"  onclick="xClose()" class=".ui-widget-overlay" >CLOSE</a>
</center>
