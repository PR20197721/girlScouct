<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<% 

    if( !apiConfig.isDemoUser() ){ out.println("No permission(s) to perform this operation. "); return; }
	troopUtil.rmTroop(troop);
	session.putValue("VTK_troop",null);
	session.putValue(org.girlscouts.vtk.auth.models.User.class.getName(),null);
	session.putValue(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), null);
	session.putValue(org.girlscouts.vtk.models.User.class.getName(), null);
    response.sendRedirect("http://localhost:4503/content/girlscouts-shared/demo.html");
%>
 

