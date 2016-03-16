<%@ page import="org.girlscouts.vtk.utils.*, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<% 

	try{
		final TroopUtil troopUtil = sling.getService(TroopUtil.class);
		
		HttpSession session = request.getSession();
		org.girlscouts.vtk.auth.models.ApiConfig apiConfig= VtkUtil.getApiConfig(session);
	    if( apiConfig!=null && !apiConfig.isDemoUser() ){ out.println("No permission(s) to perform this operation. "); return; }
		
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


    response.sendRedirect("http://localhost:4503/content/girlscouts-demo/en.html");
%>
 

