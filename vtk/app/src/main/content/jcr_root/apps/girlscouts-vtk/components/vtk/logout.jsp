<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,java.lang.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%
    final UserUtil userUtil = sling.getService(UserUtil.class);
    final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
    org.girlscouts.vtk.helpers.CouncilMapper councilMapper = sling.getService(org.girlscouts.vtk.helpers.CouncilMapper.class);

    HttpSession session = request.getSession();
    String councilId= request.getParameter("cid");
    User user = null;
    Troop troop = null;
    if (session != null) {
    	user = ((org.girlscouts.vtk.models.User) session.getAttribute(org.girlscouts.vtk.models.User.class.getName()));
        troop = (Troop) session.getValue("VTK_troop");
    }
    
    if( troop!=null ) {
    	// council lookup
        councilId= troop.getSfCouncil();
    }
    
    if( councilId==null || councilId.trim().equals("")){
        councilId = getCouncilCookie(request);
    }

    //revoke auth token
    if( user!=null) {
        userUtil.logoutApi( user.getApiConfig(), false);
    }
    
    //invalidate session
    if( session!=null) {
    	session.invalidate();
    } 
 
    if( request.getParameter("isVtkLogin")!=null && request.getParameter("isVtkLogin").equals("true")){
    	response.sendRedirect(configManager.getConfig("targetUrl"));
    }else if( request.getParameter("isCommunityLogin")!=null && request.getParameter("isCommunityLogin").equals("true")){            
        response.sendRedirect(configManager.getConfig("communityUrl"));
    }else{
        //String councilHomeUrl = configManager.getConfig("baseUrl") +  councilMapper.getCouncilUrl(councilId) +"en.html"; //baseUrl for local. no etc mapping
        String councilHomeUrl = "";
        try {
            Integer.parseInt(councilId);
            
            councilHomeUrl = councilMapper.getCouncilUrl(councilId) +"en.html";
            
        } catch (Exception e) {
            councilHomeUrl = resourceResolver.map("/content/" + councilId + "/en.html");
        }
        if( councilHomeUrl!=null && councilHomeUrl.contains("uat.gswcf.org") ){
        	
        }else{
                    response.sendRedirect(councilHomeUrl);
        }
    	
    }

%>

<%!
public String getCouncilCookie(HttpServletRequest request) {
    String councilCode = "";
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("vtk_referer_council")) {
                councilCode = cookies[i].getValue();
            }
        }
    }
    return councilCode;
}

%>
