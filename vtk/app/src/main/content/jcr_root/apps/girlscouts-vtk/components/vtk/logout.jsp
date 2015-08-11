<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%
    final UserUtil userUtil = sling.getService(UserUtil.class);
    final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
    org.girlscouts.vtk.helpers.CouncilMapper councilMapper = sling.getService(org.girlscouts.vtk.helpers.CouncilMapper.class);

    HttpSession session = request.getSession();
    User user = ((org.girlscouts.vtk.models.User) session
        .getAttribute(org.girlscouts.vtk.models.User.class.getName()));
    Troop troop = (Troop) session.getValue("VTK_troop");
    
    String councilId= request.getParameter("cid");
    if( councilId==null && troop!=null )       
        councilId= troop.getSfCouncil();

    //revoke auth token
    if( user!=null)
            userUtil.logoutApi( user.getApiConfig(), false);
    
    //invalidate session
    if( session!=null)
    	session.invalidate();
    
    String councilHomeUrl = configManager.getConfig("baseUrl") +  councilMapper.getCouncilUrl(councilId) +"en.html";
    response.sendRedirect(councilHomeUrl);
%>
