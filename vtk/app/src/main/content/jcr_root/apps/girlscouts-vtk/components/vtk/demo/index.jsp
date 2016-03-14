<%@page import="java.net.URLEncoder"%>
<%@page import="java.text.SimpleDateFormat,
                org.apache.commons.lang3.time.FastDateFormat,
                org.girlscouts.vtk.models.Troop,
                org.girlscouts.vtk.auth.permission.*,
                org.girlscouts.vtk.utils.VtkUtil,
                org.apache.commons.lang3.time.FastDateFormat,
                org.apache.sling.runmode.RunMode"%>
<%@page
    import="java.util.Comparator,org.codehaus.jackson.map.ObjectMapper,org.joda.time.LocalDate,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,
                org.girlscouts.vtk.modifiedcheck.ModifiedChecker, com.day.image.Layer, java.awt.geom.Rectangle2D, java.awt.geom.Rectangle2D.Double, com.day.cq.commons.jcr.JcrUtil, org.apache.commons.codec.binary.Base64, com.day.cq.commons.ImageHelper, com.day.image.Layer, java.io.ByteArrayInputStream, java.io.ByteArrayOutputStream, java.awt.image.BufferedImage, javax.imageio.ImageIO,
                org.girlscouts.vtk.helpers.TroopHashGenerator"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>

<cq:defineObjects />
<h1>Demo site home page</h1>
<%
final ConnectionFactory connectionFactory = sling.getService(ConnectionFactory.class);
final TroopDAO troopDAO = sling.getService(TroopDAO.class);

HttpSession session = request.getSession();

boolean isGroupDemo= request.getParameter("isGroupDemo") !=null ? true : false;
String contactId= request.getParameter("user");
System.err.println("user : "+ contactId);

org.girlscouts.vtk.auth.models.ApiConfig apiConfig=  new org.girlscouts.vtk.auth.models.ApiConfig();
apiConfig.setUserId(contactId);
apiConfig.setDemoUser(true);
apiConfig.setDemoUserName(contactId);


//getUser
org.girlscouts.vtk.auth.models.User  user=  new org.girlscouts.vtk.auth.dao.SalesforceDAO(
        troopDAO, connectionFactory).getUser( apiConfig);
apiConfig.setUser(user);
user.setName(contactId);
session.setAttribute(org.girlscouts.vtk.auth.models.User.class.getName(), user);

//getTroop
java.util.List<org.girlscouts.vtk.salesforce.Troop> troops  = new org.girlscouts.vtk.auth.dao.SalesforceDAO(
        troopDAO, connectionFactory).troopInfo( user,  apiConfig, contactId );

 for(int i=0;i<troops.size();i++){
	org.girlscouts.vtk.salesforce.Troop troop = troops.get(i);
	if( !isGroupDemo && troop.getPermissionTokens().contains(13)){ //if not parent
	    	troop.setTroopId( "SHARED_"+session.getId()+"_"+troop.getTroopId() );
	}
 }

apiConfig.setTroops(troops);
session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), apiConfig);

org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
vtkUser.setApiConfig(apiConfig);
if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
    vtkUser.setCurrentYear(""+VtkUtil.getCurrentGSYear());
    }
session.setAttribute(org.girlscouts.vtk.models.User.class.getName(), vtkUser);


if( false ){
%>
<a href="/content/girlscouts-vtk/en/vtk.html">GO TO VTK</a>
<br/><br/>
<table>
 <tr>
    <th>User name</th>
    <th>Permissions</th>
    <th>Login as/Single mode</th>
    <th>Login as/Group mode</th>
 </tr>
 <tr>
  <td>Alice</td>
  <td>DP/ADMIN</td>
  <td><a href="/content/girlscouts-vtk/en/vtk.demo.index.html?user=Alice">VTK</a></td>
  <td><a href="/content/girlscouts-vtk/en/vtk.demo.index.html?user=Alice&isGroupDemo=true">Group VTK</a></td>
 
 </tr>
 
   <td>Leaderw2Kids</td>
  <td>Parent</td>
  <td><a href="/content/girlscouts-vtk/en/vtk.demo.index.html?user=Leaderw2Kids">VTK</a></td>
  <td><a href="/content/girlscouts-vtk/en/vtk.demo.index.html?user=Leaderw2Kids&isGroupDemo=true">Group VTK</a></td>
 </tr>
</table>

<%}%>