<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<script>



function assignAid(aidId, meetingId, assetName){
	  
	  $.ajax({
			cache: false,
			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
			type: 'POST',
			data: { 
				addAids:aidId,
				meetingId: meetingId,
				assetName:assetName,
				a:Date.now()
			},
			success: function(result) {

			}
		});
		//document.location="/content/girlscouts-vtk/en/vtk.resource.html";
		applyAids(aidId);
}

</script>



<a href="javascript:void(0)"  onclick="xClose()" class=".ui-widget-overlay" >CLOSE</a>
<%

	String aidId= request.getParameter("aidId");
//System.err.println("ADDDDIDDIDI: "+ aidId+ " : " + request.getParameter("aidName"));

	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
	if( sched==null || (sched.size()==0)){out.println( "No Cal set up"); return;}
	MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
	
	
	java.util.Iterator itr= sched.keySet().iterator();
	while( itr.hasNext() ){
		java.util.Date dt= (java.util.Date) itr.next();
		//MeetingE meeting = (MeetingE) sched.get(dt);
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
		
		
		
		if( assets!=null )
			for(int i=0;i<assets.size();i++){
				
				if( assets.get(i).getRefId().equals(aidId ) )
					{out.println("<br/>SELECTED");}
			}
		%>
			<br/>
			<a href="javascript:void(0)" onclick="assignAid('<%=aidId %>', '<%=_comp.getUid()%>', '<%=request.getParameter("aidName")%>')"><span style="background-color:orange;"><%=displayName%> </span></a>
			
			<%=_comp.getType() %> 
			
		<%
	}
%>