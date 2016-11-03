<%@ page
  import="java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../session.jsp"%>	
	
<% 
	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView(user, troop, request);
	java.util.List <String> existingAids = new java.util.ArrayList();
	List<Asset> _aidTags = planView.getAidTags();	

	if(_aidTags!=null)
	for(int i=0;i<_aidTags.size();i++){
		org.girlscouts.vtk.models.Asset asset = _aidTags.get(i);
		if( asset.getType(false)!=  org.girlscouts.vtk.dao.AssetComponentType.AID ) continue;
		existingAids.add(asset.getRefId());
	}
	final String MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
	java.util.List<org.girlscouts.vtk.models.Asset> gresources = null;
System.err.println("test1");	
	try{
		gresources= yearPlanUtil.getAllResources(user, troop, MEETING_AID_PATH+"/"); 
	
	}catch(Exception e){e.printStackTrace();}
System.err.println("test2");	
	%>
    <!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_meeting_aids.jsp -->

	   <div class="header clearfix">
          <h3 class="columns large-22">Meeting aids</h3>
          <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
        </div>
        <div class="scroll">
            <div class="content">
				<table width="90%" align="center" class="browseMeetingAids">
					<% 
System.err.println("test3");
					if( gresources!=null)
				  	 for(int i=0;i<gresources.size();i++) {
				  		try{
						org.girlscouts.vtk.models.Asset a = gresources.get(i);
						String assetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(a.getDocType());
					%>
						<tr>
							<td width="40">
								<% if (assetImage != null) { %>	
									<img src="<%= assetImage %>" width="40" height="40" border="0"/>
								<% } %>
							</td>
					 		<td><a class="previewItem" href="<%=a.getRefId() %>" target="_blank"><%= a.getTitle() %></a> </td>
					 		<td width="60">
							    <% if (a.getIsOutdoorRelated()) { %>	
				 		          <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png">
				 		        <% } %>
					 		</td>
					 		<td width="40">
					 			<% if( !existingAids.contains(a.getRefId()) && VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ){ %>
					 				 <input type="button" value="Add to Meeting" onclick="assignAid('<%=a.getRefId()%>', '<%=planView.getYearPlanComponent().getUid()%>', '<%=a.getTitle()%>','<%=a.getDocType()%>')" class="button linkButton"/>
					 			<%} else {%>
									<p class="button disabled" style="width:100%">Exists</p>
					 			<%} %>
					 		</td>
						</tr>
				 	<% }catch(Exception e){e.printStackTrace();}} %>
				</table>		
	  </div>
	</div>
	
<script>
  function assignAid(aidId, meetingId, assetName, assetDocType){
	  $.ajax({
			cache: false,
			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
			type: 'POST',
			data: { 
				act:'AddAid',
				addAids:aidId,
				meetingId: meetingId,
				assetName:assetName,
				assetDocType:assetDocType,
				assetType:'AID',
				a:Date.now()
			},
			success: function(result) {
				vtkTrackerPushAction('AddMeetingAid');
				location.reload();
			}
		});
	}	
	</script>
