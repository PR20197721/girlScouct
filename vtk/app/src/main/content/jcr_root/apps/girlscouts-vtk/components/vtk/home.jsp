<%@page import="org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.models.Troop,
                org.girlscouts.vtk.osgi.component.ConfigManager,
                org.girlscouts.vtk.osgi.component.CouncilMapper" %>
<%@ page import="org.girlscouts.vtk.osgi.component.util.VtkUtil" %>
<%@ page import="java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    CouncilMapper councilMapper = sling.getService(CouncilMapper.class);
    HttpSession session = request.getSession();
    ApiConfig apiConfig = null;
    boolean isHideSignIn = false;
    boolean isHideMember = false;
    String communityUrl = "";
    String councilId = "0";
    String usercounciId = "0";
    String[] gsLearnMap = {"999", "306", "240", "360", "438", "319", "512", "387", "582", "654", "583", "467", "169", "354", "614", "642", "281", "497", "367", "200", "564", "647", "499", "126", "368", "612", "688", "204", "477", "661", "538", "608", "134", "377", "506", "450", "634", "110", "116", "345", "556", "635", "536", "131", "603", "687", "263", "478", "563", "346", "282", "607", "313"};
    String gsLearnCouncil = "";
    List<Troop> userTroops = null;
    try {
        apiConfig = ((ApiConfig) session.getAttribute(ApiConfig.class.getName()));
    } catch (Exception e) {
        e.printStackTrace();
    }
    if (apiConfig != null && !apiConfig.isFail()) {
        usercounciId = apiConfig.getUser().getAdminCouncilId();
        userTroops = apiConfig.getUser().getTroops();
        if (apiConfig.getUser().isAdmin() && (userTroops == null || userTroops.size() <= 0)) {
            Troop dummyVTKAdminTroop = new Troop();
            dummyVTKAdminTroop.setPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.getPermissionTokens(org.girlscouts.vtk.auth.permission.Permission.GROUP_ADMIN_PERMISSIONS));
            dummyVTKAdminTroop.setTroopId("none");
            dummyVTKAdminTroop.setSfTroopName("vtk_virtual_troop");
            dummyVTKAdminTroop.setSfCouncil(apiConfig.getUser().getAdminCouncilId());
            dummyVTKAdminTroop.setSfUserId("none");
            dummyVTKAdminTroop.setSfTroopId("none");
            dummyVTKAdminTroop.setCouncilCode(apiConfig.getUser().getAdminCouncilId());
            dummyVTKAdminTroop.setTroopName("vtk_virtual_troop");
            dummyVTKAdminTroop.setGradeLevel("CA");
            String councilPath = "/vtk" + VtkUtil.getCurrentGSYear() + "/" + dummyVTKAdminTroop.getSfCouncil();
            dummyVTKAdminTroop.setCouncilPath(councilPath);
            String troopPath = councilPath + "/troops/" + dummyVTKAdminTroop.getSfTroopId();
            dummyVTKAdminTroop.setPath(troopPath);
            // user.setPermissions(user_troop.getPermissionTokens());
            userTroops.add(dummyVTKAdminTroop);
        }
        String branch = null;
        try {
            councilId = userTroops.get(0).getCouncilCode();
            branch = councilMapper.getCouncilBranch(councilId);
        } catch (Exception e) {
            String refererCouncil = VtkUtil.getCouncilInClient(request);
            if (refererCouncil != null && !refererCouncil.isEmpty() && refererCouncil.length() > 3) {
                branch = "/content/" + refererCouncil;
            } else {
                branch = councilMapper.getCouncilBranch();
            }
        }
        // language
        branch += "/en/jcr:content";
        ValueMap valueMap = (ValueMap) resourceResolver.resolve(branch).adaptTo(ValueMap.class);
        isHideSignIn = valueMap.get("hideVTKButton", "").equals("true");
        isHideMember = valueMap.get("hideMemberButton", "").equals("true");
    }
    // Get URL for community page
    ConfigManager configManager = (ConfigManager) sling.getService(ConfigManager.class);
    if (configManager != null) {
        communityUrl = configManager.getConfig("communityUrl");
    }
    if (session.getAttribute("VTK_troop") == null && userTroops != null && userTroops.size() > 0) {
        session.setAttribute("VTK_troop", userTroops.get(0));
    }
    for (int i = 0; i < gsLearnMap.length; i++) {
        if (councilId.equals(gsLearnMap[i]) || usercounciId.equals(gsLearnMap[i])) {
            gsLearnCouncil = gsLearnMap[i];
        }
    }
%>
<!--<%=councilId%>-->
<!-- apps/girlscouts/components/three-column-page/content.jsp -->
<!--PAGE STRUCTURE: MAIN-->
<div class="row content">
    <!--PAGE STRUCTURE: LEFT CONTENT START-->
    <div class="large-5 hide-for-medium hide-for-small columns mainLeft">
        <div id="leftContent">
            <!-- apps/girlscouts/components/three-column-page/left.jsp -->
            <div class="cascading-menus">
                <script>
                    $(document).ready(function () {
                        $('#main .side-nav li.active.current').parent().parent().find(">div>a").css({
                            "font-weight": "bold",
                            "color": "#414141"
                        });
                    });
                </script>
            </div>
            <div class="par parsys"></div>
        </div>
    </div><!--/main-left-->
    <!--PAGE STRUCTURE: LEFT CONTENT STOP-->
    <!--PAGE STRUCTURE: MAIN CONTENT START-->
    <div class="large-19 medium-24 small-24 columns mainRight">
        <div class="breadcrumbWrapper">
            <div class="breadcrumb-trail breadcrumb">
                <nav class="breadcrumbs"></nav>
            </div>
        </div>
        <div class="row mainRightBottom">
            <div class="large-18 medium-18 small-24 columns rightBodyLeft">
                <!--PAGE STRUCTURE: MIDDLE CONTENT START-->
                <!-- apps/girlscouts/components/three-column-page/middle.jsp -->
                <div id="mainContent" class="welcome-page">
                    <div class="par parsys">
                        <%@include file="include/vtkError.jsp" %>
                        <div class="text parbase section"><h1>Welcome.</h1></div>
                        <% if (councilId.equals(gsLearnCouncil) || usercounciId.equals(gsLearnCouncil)) { %>
                        <ul class="large-block-grid-3 medium-block-grid-2 small-block-grid-1 ">
                                <%}else{%>
                            <ul class="large-block-grid-2 medium-block-grid-2 small-block-grid-1 ">
                                <%}%>
                                <li>
                                    <% if (!isHideSignIn && apiConfig != null) {
                                        String vtkLanding = "/content/girlscouts-vtk/en/vtk.html";
                                        String userRole = null;
                                        if (!apiConfig.isFail()) {
                                            if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
                                                userRole = apiConfig.getTroops().get(0).getRole();
                                            }
                                            userRole = userRole == null ? "" : userRole;
                                            //if( apiConfig!=null && (userRole.equals("PA") || apiConfig.getUser().isAdmin() )){
                                            if (apiConfig != null && userRole != null && userRole.equals("DP")) {
                                                ;
                                            } else if (apiConfig != null && apiConfig.getUser().isAdmin()) {
                                                vtkLanding = "/myvtk/" + councilMapper.getCouncilName(councilId) + "/vtk.resource.html";
                                            }
                                        }
                                    %>
                                    <!-- Begin of VTK icon -->
                                    <a href="<%=vtkLanding%>">
                                        <img src="/etc/designs/girlscouts-vtk/images/btn_VTK.jpg"/></a>
                                    <p>
                                        Access badges and Journeys for all levels, and much more from the Girl Scout National Program Portfolio. Plus, find meeting planning tools and resources for groups of all sizes. It&rsquo;s your virtual Girl Scout assistant!<br/><br/>Troop Leaders & Co-Leaders can co-plan activities, email caregivers in your troop and enter troop finances at the end of the year.
                                    </p>
                                    <% } %>
                                </li>
                                <li>
                                    <% if (!isHideMember) { %>
                                    <a href="<%= communityUrl %>"><img src="/etc/designs/girlscouts-vtk/images/btn_member_profile.jpg"/></a>
                                    <p>Do you want to change your member profile or contact details? Do you need to renew a membership? Go to the Girl Scout Member Community for access to your member profile.</p>
                                    <%}//edn if %>
                                </li>
                                <li>
                                    <% if (councilId.equals(gsLearnCouncil) || usercounciId.equals(gsLearnCouncil)) { %>
                                    <a href="https://gsmembers.force.com/members/idp/login?app=0sp0f000000k9bw"><img src="/etc/designs/girlscouts-vtk/images/btn_member_gslearn.jpg"/></a>
                                    <p>
                                        Get training and resources to support you in your volunteer role and have an amazing Girl Scout year!<br/><br/>Everything on gsLearn is designed to help you get started. You can return at any time to review and refresh your Girl Scout knowledge!
                                    </p>
                                    <%}%>
                                </li>
                            </ul>
                            <div class="text parbase section"></div>
                    </div>
                </div><!--/mainContent-->
                <!--PAGE STRUCTURE: MIDDLE CONTENT STOP-->
            </div><!--/rightBodyLeft-->
            <!--PAGE STRUCTURE: RIGHT CONTENT START-->
            <div id="rightContent" class="large-6 medium-6 small-24 columns">
                <!-- apps/girlscouts/components/three-column-page/right.jsp -->
                <div class="advertisement"></div>
            </div>
            <!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
        </div><!--/mainRightBottom-->
    </div><!--/mainRight-->
    <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
<!--/content-->
<%
    Resource maintenanceNode = resourceResolver.getResource("/content/vtkcontent/en/vtk-maintenanceBanner/jcr:content/content/middle/par/breaking-news");
    if (maintenanceNode != null) {
        try {
            Node node = maintenanceNode.adaptTo(Node.class);
            if (node.hasProperty("newstype")) {
                String popupHeader = "";
                String popupBody = "";
                if (!"None".equals(node.getProperty("newstype").getString())) {
                    if (node.hasProperty("popupHeader")) {
                        popupHeader = node.getProperty("popupHeader").getString();
                    }
                    if (node.hasProperty("popupBody")) {
                        popupBody = node.getProperty("popupBody").getString();
                    }
%>
<!--PAGE STRUCTURE: MAINTENANCE NOTIFICATION-->
<div id="maintenanceModal" class="maintenance">
    <!-- Modal content -->
    <div class="maintenance-content">
        <div class="modal-header">
            <div class="vtk-maintenance-news-button">
                <span id="modal-close-button">X</span>
            </div>
            <div class="maintenanceHeader"><%= popupHeader %> </br></div>
        </div>
        <div class="modal-body">
            <p id="maintenanceBody"><%= popupBody %>
            </p>
        </div>
        <div class="modal-footer">
            <strong>-The GSUSA VTK Team</strong>
        </div>
    </div>
</div>
<%
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
%>