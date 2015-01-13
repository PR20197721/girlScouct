		<% java.util.List <String> existingAids = new java.util.ArrayList();
				List<Asset> _aidTags = planView.getAidTags();	

	if(_aidTags!=null)
	for(int i=0;i<_aidTags.size();i++){
	org.girlscouts.vtk.models.Asset asset = _aidTags.get(i);
	if( asset.getType(false)!=  org.girlscouts.vtk.dao.AssetComponentType.AID ) continue;
		existingAids.add(asset.getRefId());
	}
	final String MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
	java.util.List<org.girlscouts.vtk.models.Asset> gresources = yearPlanUtil.getAllResources(user,MEETING_AID_PATH+"/"); 
	%>

	<div id="modal_meeting_aids" class="reveal-modal" data-reveal>
	<div class="header clearfix">
	  <h3 class="columns large-22">MEETING date and locations</h3>
	  <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
	</div>
		<table width="90%" align="center" class="browseMeetingAids">
			<% 
		  	for(int i=0;i<gresources.size();i++) {
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
		 		<td width="40">
		 			<% if( !existingAids.contains(a.getRefId()) && hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID ) ){ %>
		 				 <input type="button" value="Add to Meeting" onclick="assignAid('<%=a.getRefId()%>', '<%=planView.getYearPlanComponent().getUid()%>', '<%=a.getTitle()%>')" class="button linkButton"/>
		 			<%} else {%>
						<p class="btn button disabled" style="width:100%">Exists</p>
		 			<%} %>
		 		</td>
			</tr>
		 	<% } %>
		</table>
	</div>

<script>
  function assignAid(aidId, meetingId, assetName, assetDesc){
	  $.ajax({
			cache: false,
			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
			type: 'POST',
			data: { 
				act:'AddAid',
				addAids:aidId,
				meetingId: meetingId,
				assetName:assetName,
				assetDesc:assetDesc,
				assetType:'AID',
				a:Date.now()
			},
			success: function(result) {
				location.reload();
			}
		});
	}	
	</script>
