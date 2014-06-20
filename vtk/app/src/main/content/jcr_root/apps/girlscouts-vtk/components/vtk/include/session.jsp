<%!
java.text.SimpleDateFormat fmtDate= new java.text.SimpleDateFormat("MM/dd/yyyy");
java.text.SimpleDateFormat fmtHr= new java.text.SimpleDateFormat(" hh:mm a");
java.text.SimpleDateFormat fmt= new java.text.SimpleDateFormat("MMM dd  hh:mm a");
java.text.SimpleDateFormat fmtX= new java.text.SimpleDateFormat("d");
java.text.SimpleDateFormat dateFormat55 = new java.text.SimpleDateFormat("EEE MMM dd,yyyy hh:mm a");
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MMM dd yyyy hh:mm a");
%>
<%
HttpSession session = request.getSession();
org.girlscouts.vtk.auth.models.ApiConfig apiConfig =null;
if( session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName())!=null ){
   apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));

   System.err.println("APICONFIG: "+ apiConfig.getAccessToken() +" User: "+ apiConfig.getUserId() +" URL: "+ apiConfig.getInstanceUrl() );
   out.println("<!-- APICONFIG: "+ apiConfig.getAccessToken() +" User: "+ apiConfig.getUserId() +" URL: "+ apiConfig.getInstanceUrl() +" -->");


}else{

	out.println("loging first");
	return;
}


if( apiConfig.getTroops()==null || apiConfig.getTroops().size()<=0 ){
	out.println("<span class='error'>Sorry, this user is not part of a GirlScouts' SalesForce campaign. Please ask your council admin for access.</span>");
	return;
}

System.err.println(1);
UserDAO userDAO = sling.getService(UserDAO.class);
User user= (User)session.getValue("VTK_user");
if( user ==null){
	
	System.err.println(2);
        //user= userDAO.getUser( request.getParameter("userId"));
        user= userDAO.getUser( apiConfig.getUserId() +"_"+ apiConfig.getTroops().get(0).getTroopId());
        System.err.println(3);
        //first time - new user
        if( user==null ){
                user = new User(apiConfig.getUserId()+"_"+ apiConfig.getTroops().get(0).getTroopId());
        }
        System.err.println(4);
        user.setApiConfig(apiConfig);
        System.err.println(5);
        user.setTroop( apiConfig.getTroops().get(0) );
        System.err.println(6);
        user.setSfTroopId( user.getTroop().getTroopId() );
        System.err.println(7);
        user.setSfUserId( user.getApiConfig().getUserId() );
        System.err.println(8);
        user.setSfTroopName( user.getTroop().getTroopName() ); 
        System.err.println(9);
        session.putValue("VTK_user", user);
        System.err.println(10);
}
%>
