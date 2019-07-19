<%@ page
        import="org.girlscouts.vtk.auth.permission.Permission,org.girlscouts.vtk.models.Asset, org.girlscouts.vtk.osgi.component.dao.AssetComponentType" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../session.jsp" %>
<%
    org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView(user, selectedTroop, request);
    java.util.List<String> existingAids = new java.util.ArrayList();
    List<Asset> _aidTags = planView.getAidTags();
	if(_aidTags!=null)
	for(int i=0;i<_aidTags.size();i++){
		org.girlscouts.vtk.models.Asset asset = _aidTags.get(i);
		if( asset.getType(false)!=  AssetComponentType.AID ) continue;
		existingAids.add(asset.getRefId());
	}
	final String MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
	java.util.List<org.girlscouts.vtk.models.Asset> gresources = null;
	try{
		gresources= yearPlanUtil.getAllResources(user, selectedTroop, MEETING_AID_PATH+"/");
	}catch(Exception e){e.printStackTrace();}
	%>
    <!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_meeting_aids.jsp -->

	   <div class="header clearfix">
          <h3 class="columns large-22">Meeting aids</h3>
          <a class="close-reveal-modal columns large-2" href="#"><span style="color: black; font-size: 22px; padding-right: 10px; font-weight: normal;">X</span></a>
        </div>
        <div class="scroll">
            <div class="content">
				<table width="90%" align="center" class="browseMeetingAids">
					<%
					if( gresources!=null)
				  	 for(int i=0;i<gresources.size();i++) {
				  		try{
						org.girlscouts.vtk.models.Asset a = gresources.get(i);
						String assetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(a.getDocType());
					%>
						<tr>
							<td class="browseMeetingAidsImage">
								<% if (assetImage != null) { %>	
									<img src="<%= assetImage %>" width="40" height="40" border="0"/>
								<% } %>
							</td>
					 		<td><a class="previewItem LINK" title="<%= a.getTitle() %>" href="<%=a.getRefId() %>" target="_blank"><%= a.getTitle() %></a> </td>
					 		<td class="vtk-oudoor-table-view">
									<% if(a.getIsOutdoorRelated()){ 
										if( !existingAids.contains(a.getRefId()) && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MEETING_ID ) ) { %>
											<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/indoor.png" width="40" height="40">
									  <%  } else { %>
											<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png" width="40" height="40">
									   <% }
									   } %>
					 		</td>
					 		<td>
					 			<% if( !existingAids.contains(a.getRefId()) && VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MEETING_ID ) ){ %>
					 				 <input type="button" style="    min-width: 100px;     padding: 9px 5px;white-space: normal; word-break: break-word;" value="Add to Meeting" onclick="assignAid('<%=a.getRefId()%>', '<%=planView.getYearPlanComponent().getUid()%>', '<%=a.getTitle()%>','<%=a.getDocType()%>')" class="button linkButton"/>
					 			<%} else {%>
									<p class="button disabled" style="    min-width: 100px;    padding: 9px 5px; white-space: normal; word-break: break-word;">Exists</p>
					 			<%} %>
					 		</td>
						</tr>
				 	<% }catch(Exception e){e.printStackTrace();}} %>
				</table>		
	  </div>
	</div>
<script>
    function assignAid(aidId, meetingId, assetName, assetDocType) {
        $.ajax({
            cache: false,
            url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
            type: 'POST',
            data: {
                act: 'AddAid',
                addAids: aidId,
                meetingId: meetingId,
                assetName: assetName,
                assetDocType: assetDocType,
                assetType: 'AID',
                a: Date.now()
            },
            success: function (result) {
                vtkTrackerPushAction('AddMeetingAid');
                location.reload();
            }
        });
    }
</script>
