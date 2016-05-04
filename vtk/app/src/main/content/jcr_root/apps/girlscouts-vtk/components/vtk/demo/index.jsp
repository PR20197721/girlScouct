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
<%

HttpSession session = request.getSession();
System.err.println("tata old "+ session.getId() );
session.invalidate();
session = request.getSession();
System.err.println("tata new "+ session.getId() );

session.putValue("VTK_troop",null);
session.putValue(org.girlscouts.vtk.auth.models.User.class.getName(),null);
session.putValue(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), null);
session.putValue(org.girlscouts.vtk.models.User.class.getName(), null);
      

Cookie killMyCookie = new Cookie("girl-scout-name", null);
killMyCookie.setMaxAge(0);
killMyCookie.setPath("/");
response.addCookie(killMyCookie);

final ConnectionFactory connectionFactory = sling.getService(ConnectionFactory.class);
final TroopDAO troopDAO = sling.getService(TroopDAO.class);


boolean isGroupDemo= request.getParameter("isGroupDemo") !=null ? true : false;
String contactId= request.getParameter("user");
//System.err.println("user : "+ contactId);

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

/*
//getTroop
java.util.List<org.girlscouts.vtk.salesforce.Troop> troops  = new org.girlscouts.vtk.auth.dao.SalesforceDAO(
        troopDAO, connectionFactory).troopInfo( user,  apiConfig, contactId );
*/
System.err.println("tataDD: "+ apiConfig.getTroops().size() );

 //java.util.List<org.girlscouts.vtk.salesforce.Troop > _troops= new java.util.ArrayList();
 for(int i=0;i<apiConfig.getTroops().size();i++){
	org.girlscouts.vtk.salesforce.Troop troop = apiConfig.getTroops().get(i);
	
     if( !isGroupDemo && troop.getPermissionTokens().contains(13)){ //if not parent
	    	troop.setTroopId( "SHARED_"+session.getId()+"_"+troop.getTroopId() ); //group
	    	
     }else{
    	    troop.setTroopId(troop.getTroopId() ); 
     }
	    	
    java.util.Set perms = troop.getPermissionTokens();
    
    perms.remove(304); //edit troop photo
    perms.remove(631); //PERMISSION_SEND_EMAIL_MT_ID
    perms.remove(641); //PERMISSION_SEND_EMAIL_act_ID
    perms.remove(642); //email all parents
   System.err.println("tata perms: "+ perms); 
    
    troop.setPermissionTokens(perms);
    System.err.println("tata perms after: "+ troop.getPermissionTokens()); 
	
 }

//apiConfig.setTroops(troops);
//apiConfig.setTroops(_troops);
session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), apiConfig);
System.err.println("tataxx: "+ apiConfig.getTroops().size() );
org.girlscouts.vtk.models.User vtkUser = new org.girlscouts.vtk.models.User();
vtkUser.setApiConfig(apiConfig);
if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
    vtkUser.setCurrentYear(""+VtkUtil.getCurrentGSYear());
    }
session.setAttribute(org.girlscouts.vtk.models.User.class.getName(), vtkUser);

if( request.getParameter("prefGradeLevel")!=null ){
	System.err.println("tata adding coolie: vtk_prefTroop" +request.getParameter("prefGradeLevel") );
	
	//set prefTroop
	Cookie cookie = new Cookie("vtk_prefTroop", request.getParameter("prefGradeLevel"));
	cookie.setMaxAge(-1);
	cookie.setPath("/");
	response.addCookie(cookie);
	
}//edn if pref level
//System.err.println("tata prefGradeLevel: "+ request.getParameter("prefGradeLevel"));

response.sendRedirect("/content/girlscouts-vtk/en/vtk.html");
%>