<%!
java.text.SimpleDateFormat FORMAT_MMddYYYY = new java.text.SimpleDateFormat("MM/dd/yyyy");
java.text.SimpleDateFormat FORMAT_hhmm_AMPM = new java.text.SimpleDateFormat("hh:mm a");
java.text.SimpleDateFormat FORMAT_hhmm = new java.text.SimpleDateFormat("hh:mm");

java.text.SimpleDateFormat FORMAT_AMPM = new java.text.SimpleDateFormat("a");
java.text.SimpleDateFormat FORMAT_MONTH = new java.text.SimpleDateFormat("MMM");
java.text.SimpleDateFormat FORMAT_DAY_OF_MONTH = new java.text.SimpleDateFormat("d");
java.text.SimpleDateFormat FORMAT_MONTH_DAY = new java.text.SimpleDateFormat("MMM d");


java.text.SimpleDateFormat FORMAT_MMM_dd_hhmm_AMPM= new java.text.SimpleDateFormat("MMM dd hh:mm a");
java.text.SimpleDateFormat FORMAT_MEETING_REMINDER = new java.text.SimpleDateFormat("EEE MMM dd,yyyy hh:mm a");

java.text.SimpleDateFormat FORMAT_MMM_dd_yyyy_hhmm_AMPM = new java.text.SimpleDateFormat("MMM dd yyyy hh:mm a");

java.text.SimpleDateFormat FORMAT_CALENDAR_DATE = new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a");

java.text.NumberFormat FORMAT_CURRENCY = java.text.NumberFormat.getCurrencyInstance();
java.text.DecimalFormat FORMAT_COST_CENTS = new java.text.DecimalFormat("#.00");


%>
<%

HttpSession session = request.getSession();

int timeout = session.getMaxInactiveInterval();
//out.println( "***** " + timeout + " : "+ session.getId() );
response.setHeader("Refresh", timeout + "; URL = /content/girlscouts-vtk/en/vtk.logout.html");



boolean isTest = false;
if( isTest )
	autoLogin(session);
	

org.girlscouts.vtk.auth.models.ApiConfig apiConfig =null;
try {
if( session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName())!=null ){
	apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
	out.println("<!-- APICONFIG: "+ apiConfig.getAccessToken() +" User: "+ apiConfig.getUserId() +" URL: "+ apiConfig.getInstanceUrl() +" -->");
}else{
	out.println("Your session has timed out.  Please login.");
	return;
}
} catch (ClassCastException cce) {
	session.invalidate();
	log.error("ApiConfig class cast exception -- probably due to restart.  Logging out user.");
        out.println("Your session has timed out.  Please login.");
	return;
}
if( apiConfig.getTroops()==null || apiConfig.getTroops().size()<=0 ){
	out.println("<span class='error'>Sorry, this user is not part of a valid GirlScouts' campaign. Please ask your council admin for SalesForce access.</span>");
	return;
}

UserDAO userDAO = sling.getService(UserDAO.class);
User user= (User)session.getValue("VTK_user");

if( user ==null){
	
        //user= userDAO.getUser( apiConfig.getUserId() +"_"+ apiConfig.getTroops().get(0).getTroopId());
        user= userDAO.getUser( "/vtk/"+apiConfig.getTroops().get(0).getCouncilCode()+
        		"/"+apiConfig.getTroops().get(0).getTroopName()+
        		"/users/"+ apiConfig.getUserId() +"_"+ apiConfig.getTroops().get(0).getTroopId());
        
  			
        //first time - new user
        if( user==null ){
                //user = new User(apiConfig.getUserId()+"_"+ apiConfig.getTroops().get(0).getTroopId());
               user = new User( "/vtk/"+apiConfig.getTroops().get(0).getCouncilCode()+
        		"/"+apiConfig.getTroops().get(0).getTroopName()+"/users/",
        		 apiConfig.getUserId() +"_"+ apiConfig.getTroops().get(0).getTroopId() );
        }
        user.setApiConfig(apiConfig);
        
       if(isTest){ 
        org.girlscouts.vtk.salesforce.Troop caca= apiConfig.getTroops().get(0);
        caca.setGradeLevel("1-Brownie");
        user.setTroop(caca);
       }else
        user.setTroop( apiConfig.getTroops().get(0) );
        
        
        user.setSfTroopId( user.getTroop().getTroopId() );
        user.setSfUserId( user.getApiConfig().getUserId() );
        user.setSfTroopName( user.getTroop().getTroopName() ); 
        session.putValue("VTK_user", user);
}
%>

<%!

public void autoLogin(HttpSession session){
	

	org.girlscouts.vtk.auth.models.ApiConfig config = new org.girlscouts.vtk.auth.models.ApiConfig();
    config.setId("test");
    config.setAccessToken("test");
    config.setInstanceUrl("etst");
    config.setUserId("caca");
    config.setUser(new org.girlscouts.vtk.auth.models.User() );
    
    java.util.List <org.girlscouts.vtk.salesforce.Troop > troops= new java.util.ArrayList();
    org.girlscouts.vtk.salesforce.Troop troop = new org.girlscouts.vtk.salesforce.Troop();
    troop.setCouncilCode(1);
    troop.setGradeLevel("1-Brownie");
    troop.setTroopId("caca");
    troop.setTroopName("test");
    
    troops.add(troop);
    config.setTroops(troops);
    
    session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), config);
    
	
}
%>
