<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<script>



function assignAid(aidId, meetingId){
	  
	  $.ajax({
			cache: false,
			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
			type: 'POST',
			data: { 
				addAids:aidId,
				meetingId: meetingId,
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
	if( sched==null || (sched.size()==0)){out.println( "No Cal set up"); return;}
	
	
	
	java.util.Iterator itr= sched.keySet().iterator();
	while( itr.hasNext() ){
		java.util.Date dt= (java.util.Date) itr.next();
		MeetingE meeting = (MeetingE) sched.get(dt);
		
		java.util.List<Asset> assets = meeting.getAssets();
		if( assets!=null )
			for(int i=0;i<assets.size();i++){
				
				if( assets.get(i).getRefId().equals(aidId ) )
					{out.println("SELECTED");}
			}
		%><li> <a href="javascript:void(0)" onclick="assignAid('<%=aidId %>', '<%=meeting.getUid()%>')"><%= dt %></a></li><%
	}
%>