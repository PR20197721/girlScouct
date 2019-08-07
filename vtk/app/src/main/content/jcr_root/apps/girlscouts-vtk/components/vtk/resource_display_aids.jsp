<%-- display aids --%>
<%
    Page categoryPage = manager.getPage(categoryParam);
    if (categoryPage != null) {
        if (categoryPage.getProperties().get("type", "").equals("meetingAidsLike")) {
%>
<table width="90%" align="center" class="browseMeetingAids">
    <tr>
        <th colspan="3"><%=categoryPage.getTitle()%>
        </th>
    </tr>
    <%
        Resource r1 = resourceResolver.resolve(categoryPage.getProperties().get("refPath", ""));
        StringBuilder builder = new StringBuilder();
        Iterator<Resource> resIter = r1.listChildren();
        while (resIter.hasNext()) {
            Resource resPage = resIter.next();
            displayAllChildren_pdf_fmt(resPage, builder, xssAPI);
        }
    %><%=builder.toString()%><%
%></table>
<%
} else if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
%>
<table width="90%" align="center" class="browseMeetingAids">
    <tr>
        <th colspan="3">Meeting Aids</th>
    </tr>
    <%
        //LOCAL AIDS
        try {
            Iterator<Resource> iter = levelMeetingsRoot.listChildren();
            while (iter.hasNext()) {
                Resource meetingResource = iter.next();
                String meetingId = meetingResource.getPath().substring(meetingResource.getPath().lastIndexOf("/"));
                meetingId = meetingId.replace("/", "");
                java.util.List<org.girlscouts.vtk.models.Asset> lresources = yearPlanUtil.getAllResources(user, troop, LOCAL_MEETING_AID_PATH + "/" + meetingId);//meeting.getId());
                for (int i = 0; i < lresources.size(); i++) {
                    org.girlscouts.vtk.models.Asset la = lresources.get(i);
                    String lAssetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(la.getDocType());
    %>
    <tr>
        <td width="40">
            <%
                if (lAssetImage != null) {
            %> <img src="<%=lAssetImage%>" width="40" height="40" border="0"/>
            <%
                }
            %>
        </td>
        <td><a class="previewItem" href="<%=la.getRefId()%>"
               target="_blank"><%=la.getTitle()%>
        </a></td>
        <td width="60">
            <%if (la.getIsOutdoorRelated()) { %>
            <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png">
            <% } %>
        </td>
        <td width="40">
            <%
                if (VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            %>
            <input type="button" value="Add to Meeting"
                   onclick="applyAids('<%=la.getRefId()%>', '<%=la.getTitle()%>', '<%=AssetComponentType.AID%>' )"
                   class="button linkButton"/> <%
            }
        %>
        </td>
    </tr>
    <%
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.util.List<org.girlscouts.vtk.models.Asset> gresources = yearPlanUtil.getAllResources(user, troop, GLOBAL_MEETING_AID_PATH + "/");
        for (int i = 0; i < gresources.size(); i++) {
            org.girlscouts.vtk.models.Asset a = gresources.get(i);
            String assetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(a.getDocType());
    %>
    <tr>
        <td width="40">
            <%
                if (assetImage != null) {
            %> <img src="<%=assetImage%>" width="40" height="40" border="0"/>
            <%
                }
            %>
        </td>
        <td><a class="previewItem" href="<%=a.getRefId()%>"
               target="_blank"><%=a.getTitle()%>
        </a></td>
        <td width="60">
            <%if (a.getIsOutdoorRelated()) { %>
            <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png">
            <% } %>
        </td>
        <td width="40">
            <%
                if (VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            %>
            <input type="button" value="Add to Meeting"
                   onclick="applyAids('<%=a.getRefId()%>', '<%=a.getTitle()%>', '<%=AssetComponentType.AID%>' )"
                   class="button linkButton"/> <%
            }
        %>
        </td>
    </tr>
    <%
        }
    %>
</table>
<%
} else if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_OVERVIEWS)) {
%><%=displayMeetingOverviews(user, troop, resourceResolver, yearPlanUtil, levelMeetingsRoot)%>
<%
} else {
%>
<div><%=categoryPage.getTitle()%>
</div>
<%
%>
<ul>
    <%
        StringBuilder builder = new StringBuilder();
        Iterator<Page> resIter = categoryPage.listChildren();
        while (resIter.hasNext()) {
            Page resPage = resIter.next();
            displayAllChildren(resPage, builder, xssAPI);
        }
    %><%=builder.toString()%>
    <%
    %>
</ul>
<%
        }
    }
%>