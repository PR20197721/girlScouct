<%@ page import="org.girlscouts.vtk.auth.permission.Permission,
    org.girlscouts.vtk.models.Asset,
    java.util.List,
    java.util.ArrayList,
    org.girlscouts.vtk.utils.GSUtils,
    org.girlscouts.vtk.models.PlanView,
    org.girlscouts.vtk.osgi.component.dao.AssetComponentType" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../session.jsp" %>
<%
    PlanView planView = meetingUtil.planView(user, selectedTroop, request);
    List<String> existingAids = new ArrayList<>();
    List<Asset> _aidTags = planView.getAidTags();
    if (_aidTags != null) {
		for (int i = 0; i < _aidTags.size(); i++) {
			Asset asset = _aidTags.get(i);
			if (asset.getType(false) != AssetComponentType.AID) continue;
			existingAids.add(asset.getRefId());
		}
	}
    final String MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
    List<Asset> gresources = null;
    try {
        gresources = yearPlanUtil.getAllResources(user, selectedTroop, MEETING_AID_PATH + "/");
    } catch (Exception e) {
        e.printStackTrace();
    }

    final String type = request.getParameter("type");
    boolean forMeetingAid = type.equals("meeting-aids");
    final String MODAL_TITLE = forMeetingAid ? "Meeting aids" : "Additional resources"; // Used for display
    final String MODAL_ACT = forMeetingAid ? "AddAid" : "AddResource"; // Used for routing
    final String MODAL_EVENT = forMeetingAid ? "AddMeetingAid" : "AddResource"; // Used for analytics
%>
<!-- apps/girlscouts-vtk/components/vtk/include/modals/modal_meeting_aids.jsp -->

<div class="header clearfix">
    <h3 class="columns large-22"><%=MODAL_TITLE%></h3>
    <a class="close-reveal-modal columns large-2" href="#"><span style="color: black; font-size: 22px; padding-right: 10px; font-weight: normal;">X</span></a>
</div>
<div class="scroll">
    <div class="content">
        <table width="90%" align="center" class="browseMeetingAids"><%
            if (gresources != null) {
                for (int i = 0; i < gresources.size(); i++) {
                    try {
                        Asset a = gresources.get(i);
                        String assetImage = GSUtils.getDocTypeImageFromString(a.getDocType());
                        boolean hasEditPermission = VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_MEETING_ID);
                        boolean isExistingAid = existingAids.contains(a.getRefId());
                        boolean canAddAid = !isExistingAid && hasEditPermission;
                        %><tr>
                            <td class="browseMeetingAidsImage"><%
                                if (assetImage != null) {
                                    %><img src="<%=assetImage%>" width="40" height="40" border="0"/><%
                                }
                            %></td>
                            <td>
                                <a class="previewItem LINK" title="<%=a.getTitle()%>" href="<%=a.getRefId()%>" target="_blank"><%=a.getTitle()%></a>
                            </td>
                            <td class="vtk-oudoor-table-view"><%
                                if (a.getIsOutdoorRelated()) {
                                    if (canAddAid) {
                                        %><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/indoor.png" width="40" height="40"><%
                                    } else {
                                        %><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png" width="40" height="40"><%
                                    }
                                }
                            %></td>
                            <td><%
                                if (canAddAid) {
                                    %><input type="button" style="min-width: 100px; padding: 9px 5px; white-space: normal; word-break: break-word;" value="Add to Meeting" onclick="assignAid('<%=a.getRefId()%>', '<%=planView.getYearPlanComponent().getUid()%>', '<%=a.getTitle()%>','<%=a.getDocType()%>')" class="button linkButton"/><%
                                } else {
                                    %><p class="button disabled" style="min-width: 100px; padding: 9px 5px; white-space: normal; word-break: break-word;">Exists</p><%
                                }
                            %></td>
                        </tr><%
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } 
            }
        %></table>
    </div>
</div>
<script>
    function assignAid(aidId, meetingId, assetName, assetDocType) {
        $.ajax({
            cache: false,
            url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
            type: 'POST',
            data: {
                act: '<%=MODAL_ACT%>',
                addAids: aidId,
                meetingId: meetingId,
                assetName: assetName,
                assetDocType: assetDocType,
                assetType: 'AID',
                a: Date.now()
            },
            success: function (result) {
                vtkTrackerPushAction('<%=MODAL_EVENT%>');
                location.reload();
            }
        });
    }
</script>
