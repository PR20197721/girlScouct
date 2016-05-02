<%@page import="org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,
                org.girlscouts.vtk.utils.VtkUtil" %>

<%@include file="/libs/foundation/global.jsp" %> 

<%
    HttpSession session = request.getSession();
    org.girlscouts.vtk.auth.models.ApiConfig apiConfig =null;
    boolean isHideSignIn = false;
    boolean isHideMember = false;
    String communityUrl = "";
    int councilIdInt = 0;
    String councilId = "0";
    try{
         apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
    }catch(Exception e){e.printStackTrace();}
    if (apiConfig != null && !apiConfig.isFail()) {

	    CouncilMapper mapper = sling.getService(CouncilMapper.class);
	    String branch = null;
	    try {
	        councilIdInt = apiConfig.getTroops().get(0).getCouncilCode();
	        councilId = Integer.toString(councilIdInt);
	        branch = mapper.getCouncilBranch(councilId);
	    } catch (Exception e) {
	        String refererCouncil = VtkUtil.getCouncilInClient(request);
	        if (refererCouncil != null && !refererCouncil.isEmpty()) {
	            branch = "/content/" + refererCouncil;
	        } else {
	            branch = mapper.getCouncilBranch();
	        }
	    }
	
	    // language
	    branch += "/en/jcr:content";
	    ValueMap valueMap = (ValueMap)resourceResolver.resolve(branch).adaptTo(ValueMap.class);
	    isHideSignIn = valueMap.get("hideVTKButton", "").equals("true");
	    isHideMember = valueMap.get("hideMemberButton", "").equals("true");
    }
    
    // Get URL for community page
    ConfigManager configManager = (ConfigManager)sling.getService(ConfigManager.class);
    if (configManager != null) {
        communityUrl = configManager.getConfig("communityUrl");
    }
%>

<!--
<% 
    out.print(councilId); 
    
%>
-->
 
<!-- apps/girlscouts/components/three-column-page/content.jsp -->
<!--PAGE STRUCTURE: MAIN-->
<div class="row content">
<!--PAGE STRUCTURE: LEFT CONTENT START-->
  <div class="large-5 hide-for-medium hide-for-small columns mainLeft">
    <div id="leftContent">

    <!-- apps/girlscouts/components/three-column-page/left.jsp -->
      <div class="cascading-menus">

      </ul>
        <script>
        $(document).ready(function() {
          $('#main .side-nav li.active.current').parent().parent().find(">div>a").css({"font-weight":"bold", "color":"#414141"});
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

                <ul class="large-block-grid-2 medium-block-grid-2 small-block-grid-1 ">
                  <li>
                    <% if (!isHideSignIn && apiConfig!=null) { 
                    
                    
                    
                        
                        String vtkLanding = "/content/girlscouts-vtk/en/vtk.html";
                        String userRole = null;
                        if (!apiConfig.isFail()) {
            if ( apiConfig.getTroops() != null && apiConfig.getTroops().size() >0) {
                userRole = apiConfig.getTroops().get(0).getRole();
            }
                        userRole= userRole ==null ? "" : userRole;
                        if( apiConfig!=null && (userRole.equals("PA") || apiConfig.getUser().isAdmin() )){
                            vtkLanding="/content/girlscouts-vtk/en/myvtk/" + councilId + "/vtk.resource.html";   
                        }
                        }
                        
                    %>
                    <!-- Begin of VTK icon -->
                    <a href="<%=vtkLanding%>">
                    <img src="/etc/designs/girlscouts-vtk/images/btn_VTK.jpg"/></a>
                    <p> If you&rsquo;re a Troop or Co-Leader, click here! For Daisy, Brownie, and Junior troops, the Volunteer Toolkit comes with pre-populated plans for everything-a full year of Girl Scouts right there on your device! Cadette, Senior, and Ambassador troops don&rsquo;t get pre-populated meetings yet (coming soon), but you can still access great planning features.
                   </p>
                    <% } %>
                  </li>
                  <li>
                    <% if (!isHideMember) { %>
                    <a href="<%= communityUrl %>"><img src="/etc/designs/girlscouts-vtk/images/btn_member_profile.jpg"/></a>
                    <p>Do you want to change your member profile or contact details? Do you need to renew a membership? Go to the Girl Scout Member Community for access to your member profile.</p>
                    <%}//edn if %>
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
</div><!--/content-->

