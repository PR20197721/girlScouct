<%@ page import="org.girlscouts.vtk.utils.*, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<% 

    final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);

	try{
		final TroopUtil troopUtil = sling.getService(TroopUtil.class);
		
		HttpSession session = request.getSession();
		//org.girlscouts.vtk.auth.models.ApiConfig apiConfig= VtkUtil.getApiConfig(session);
	   // if( apiConfig!=null && !apiConfig.isDemoUser() ){ out.println("No permission(s) to perform this operation. "); return; }
		
	   if( configManager.getConfig("isDemoSite") ==null || !configManager.getConfig("isDemoSite").equals("true")){
    	   { out.println("No permission(s) to perform this operation. "); return; }
       }
	   
	    Troop troop = VtkUtil.getTroop(session);
	    troopUtil.rmTroop( troop );
		session.putValue("VTK_troop",null);
		session.putValue(org.girlscouts.vtk.auth.models.User.class.getName(),null);
		session.putValue(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), null);
		session.putValue(org.girlscouts.vtk.models.User.class.getName(), null);
				
		
		javax.servlet.http.Cookie killMyCookie = new javax.servlet.http.Cookie("girl-scout-name", null);
		killMyCookie.setMaxAge(0);
		killMyCookie.setPath("/");
		response.addCookie(killMyCookie);

	}catch(Exception e){e.printStackTrace();}


    response.sendRedirect( configManager.getConfig("baseUrl") + "/content/girlscouts-demo/en.html");
%>
 

