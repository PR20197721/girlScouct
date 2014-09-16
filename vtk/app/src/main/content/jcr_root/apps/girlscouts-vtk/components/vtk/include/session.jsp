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
java.text.DecimalFormat FORMAT_COST_CENTS = new java.text.DecimalFormat("#0.00");

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
        troop.setCouncilId("123");
        troop.setTroopName("test");
        

        troops.add(troop);
        config.setTroops(troops);

        session.setAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), config);
}

%>
<%


boolean isMultiUserFullBlock=true;
final ActivityDAO activityDAO = sling.getService(ActivityDAO.class);
final LocationDAO locationDAO = sling.getService(LocationDAO.class);
final CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
final LocationUtil locationUtil = sling.getService(LocationUtil.class);
final MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
final EmailUtil emailUtil = sling.getService(EmailUtil.class);
final YearPlanDAO yearPlanDAO = sling.getService(YearPlanDAO.class);
final UserDAO userDAO = sling.getService(UserDAO.class);
final MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);

HttpSession session = request.getSession();

int timeout = session.getMaxInactiveInterval();
//out.print(session.getId()+" : " + timeout);
response.setHeader("Refresh", timeout + "; URL = /content/girlscouts-vtk/en/vtk.logout.html");
boolean isTest = false;
if( isTest ) {
	autoLogin(session);
}

org.girlscouts.vtk.auth.models.ApiConfig apiConfig =null;
try {
	if( session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName())!=null ){
		apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
		//out.println("<!-- APICONFIG: "+ apiConfig.getAccessToken() +" User: "+ apiConfig.getUserId() +" URL: "+ apiConfig.getInstanceUrl() +" -->");
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
if( apiConfig.getTroops()==null || apiConfig.getTroops().size()<=0 || 
	( apiConfig.getTroops().get(0).getType()==1 )){
	out.println("Council Code: "+apiConfig.getTroops().get(0).getCouncilCode() );
	out.println("<span class='error'>Sorry, this user is not part of a valid GirlScouts' campaign. Please ask your council admin for SalesForce access.</span>");
	return;
}

// Set user for session
User user= (User)session.getValue("VTK_user");
if( user ==null){
	
	org.girlscouts.vtk.salesforce.Troop prefTroop = apiConfig.getTroops().get(0);
		  
		
		   
		   Cookie[] cookies = request.getCookies();
		   if( cookies != null ){
		      tata:for (int i = 0; i < cookies.length; i++){
		         if( cookies[i].getName().equals("vtk_prefTroop")){
		    	 	
		        	 
		        	 for(int ii=0;ii< apiConfig.getTroops().size(); ii++)
		        		 if( apiConfig.getTroops().get(ii).getGradeLevel().equals( cookies[i].getValue( ) ) ){
		        			 prefTroop= apiConfig.getTroops().get(ii); 
		        			 break tata;
		        		 }
		        	 
		         }
		        
		      }
		  }
		
		
		
		
	
        user= userDAO.getUser( "/vtk/"+prefTroop.getCouncilCode()+
        		"/"+prefTroop.getTroopId()+
        		"/users/"+ apiConfig.getUserId() +"_"+ prefTroop.getTroopId());
        
  			
        //first time - new user
        if( user==null ){
                //user = new User(apiConfig.getUserId()+"_"+ apiConfig.getTroops().get(0).getTroopId());
               user = new User( "/vtk/"+prefTroop.getCouncilCode()+
        		"/"+prefTroop.getTroopId()+"/users/",
        		 apiConfig.getUserId() +"_"+ prefTroop.getTroopId() );
        }
        user.setApiConfig(apiConfig);
        
       
		
        
       if(isTest){ 
		org.girlscouts.vtk.salesforce.Troop caca= prefTroop;
		caca.setGradeLevel("1-Brownie");
		user.setTroop(caca);
       }else
		user.setTroop( prefTroop );
       /*
       System.err.println("caca >> " + user.getTroop().getCouncilId() +" : "+ user.getTroop().getCouncilCode() );  
       System.err.println(user.getYearPlan() ==null );
       System.err.println( meetingDAO==null);
       user.getYearPlan().setMilestones( meetingDAO.getCouncilMilestones( ""+user.getTroop().getCouncilCode() ) );
	   System.err.println( user.getYearPlan().getMilestones() );	 
       */
		user.setSfTroopId( user.getTroop().getTroopId() );
		user.setSfUserId( user.getApiConfig().getUserId() );
		user.setSfTroopName( user.getTroop().getTroopName() ); 
		user.setSfTroopAge( user.getTroop().getGradeLevel() );
		user.setSfCouncil( user.getTroop().getCouncilCode()+"");
		session.setAttribute("VTK_user", user);
       
		
		
		//System.err.println("** user: "+ user.getCurrentUser() +" : "+ session.getId() );
		//out.println( session.getId() +" : "+ user.getCurrentUser() );
		if( session!=null && user.getCurrentUser()!=null &&  !session.getId().equals( user.getCurrentUser() )){  
			/*
			%>
			<span class='error'>Warning: another user is currently logged into this account. To continue, please logout and login again.  (This will cause the other user to be logged out.)</span>
			<%
			
			return;
			*/
		 }else{
			 //System.err.println("test");
			 user.setCurrentUser( session.getId() );
			 if( isMultiUserFullBlock)
			   if( user.getYearPlan()!=null)
			 	 userDAO.updateUser(user);
				
		 }
		user.setCurrentUser( session.getId() );
		
		
}else{
	
	
	//out.println(session.getId() +" : "+user.getCurrentUser());
	if(false){//user.getYearPlan()!=null &&  !meetingDAO.isCurrentUserId(user, session.getId() )){  

		%>
		<span class='error'>Warning: another user is currently logged into this account. To continue, please logout and login again.  (This will cause the other user to be logged out.)</span>
		<%
		
		return;
	 }
	
	
}


// Set troops for this user session
java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = (java.util.List<org.girlscouts.vtk.salesforce.Troop>) session.getAttribute("USER_TROOP_LIST");

if (session.getAttribute("USER_TROOP_LIST") == null) {
	troops = user.getApiConfig().getTroops();
	session.setAttribute("USER_TROOP_LIST", troops);
}
%>


	<%
	//out.println("ERR_CODE: "+ user.getErrCode() +": " + session.getId() );
		//if( user!=null && user.getLastModified()!=null) {
			
			//if( user.getLastModified().getTime().equals( new java.util.Date("1/2/1976") ) ){
			if( user.getErrCode()!=null && user.getErrCode().equals("111") ){
				%>
					<div style="color:#fff; background-color:red;">Warning: another user is currently logged into this account. To continue, please logout and login again.  (This will cause the other user to be logged out.) error 111-in db</div>
				<%
			}
			
			//if( user.getLastModified().getTime().equals( new java.util.Date("1/3/1976") ) ){
			
				
				
		if(  isMultiUserFullBlock && user!=null && user.getYearPlan()!=null && !meetingDAO.isCurrentUserId(user, user.getCurrentUser() ) )
		{
			%><div style="color:#fff; background-color:red;">Warning: another user is currently logged into this account. To continue, please logout and login again.  (This will cause the other user to be logged out.) error 111.1-another user</div><% 
			
			return;
		}
		
		
		
			if( user.getErrCode()!=null && user.getErrCode().equals("112") ){
				%>
					<div style="color:#fff; background-color:red;">Warning: another user is currently logged into this account. To continue, please logout and login again.  (This will cause the other user to be logged out.) error 112-another user</div>
				<%
			}
		//}
	
	
	
	%>
	
	


