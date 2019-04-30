<%@include file="/libs/foundation/global.jsp" %>
<%@include file="include/session.jsp" %>
<%-- VTK tab --%>
<%
    String activeTab = "resource";
    boolean showVtkNav = true;
    String sectionClassDefinition = "";

    String branch = "/content/vtkcontent", councilName = "vtkcontent";
    try {
        apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig) session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
        String councilId = userTroops.get(0).getCouncilCode();
        branch = sling.getService(CouncilMapper.class).getCouncilBranch(councilId);
        councilName = sling.getService(CouncilMapper.class).getCouncilName(councilId);
    } catch (Exception e) {
        e.printStackTrace();
    }

    //String resourcesPagePath = branch + "/en/resources2/jcr:content";
    String resourcesPagePath = ("vtkcontent".equals(councilName) ? "/content/vtkcontent" : "/content/vtk-resources2/" + councilName) + "/en/resources2/jcr:content";
%>

<%@include file="include/bodyTop.jsp" %>

<sling:include path="<%= resourcesPagePath %>" replaceSelectors="content"/>

<script>
    var __currentLevel__ = "<%=VtkUtil.formatLevel(user, selectedTroop)%>";
    var fixVerticalSizing = true;
    loadNav('resource');
</script>
<%@include file="include/bodyBottom.jsp" %>