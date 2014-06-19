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

UserDAO userDAO = sling.getService(UserDAO.class);
User user= (User)session.getValue("VTK_user");
if( user ==null){
        //user= userDAO.getUser( request.getParameter("userId"));
        user= userDAO.getUser( apiConfig.getUserId() +"_"+ apiConfig.getTroops().get(0).getTroopId());

        //first time - new user
        if( user==null ){
                user = new User(apiConfig.getUserId()+"_"+ apiConfig.getTroops().get(0).getTroopId());
        }

        user.setTroop( apiConfig.getTroops().get(0) );
        user.setApiConfig(apiConfig);
        session.putValue("VTK_user", user);
}
%>
