<%@page import="org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,
                org.girlscouts.vtk.utils.VtkUtil, org.girlscouts.vtk.models.*, 
                org.girlscouts.vtk.ejb.*, java.util.*, org.apache.jackrabbit.commons.JcrUtils, org.girlscouts.vtk.dao.*" %>
<%@include file="/libs/foundation/global.jsp" %>

 
<%
HttpSession session = request.getSession();
final TroopDAO troopDAOImpl = sling.getService(TroopDAO.class);
Troop troop = new Troop();


troop.setPath("/vtk2015/alex");
troopDAOImpl.test( null,  troop);
%>

test1