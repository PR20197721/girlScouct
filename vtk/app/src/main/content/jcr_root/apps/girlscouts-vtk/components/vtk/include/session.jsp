<%@page import="org.girlscouts.vtk.models.Troop"%>
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
//final UserDAO userDAO = sling.getService(UserDAO.class);
final MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
final TroopDAO troopDAO = sling.getService(TroopDAO.class);

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
Troop troop= (Troop)session.getValue("VTK_user");
if( troop ==null || troop.isRefresh() ){
	
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
		
		
		
		
	
        troop= troopDAO.getTroop( "/vtk/"+prefTroop.getCouncilCode()+
        		"/"+prefTroop.getTroopId()+
        		"/users/"+ apiConfig.getUserId() +"_"+ prefTroop.getTroopId());
        
  			
        //first time - new user
        if( troop==null ){
        	/*
                //user = new User(apiConfig.getUserId()+"_"+ apiConfig.getTroops().get(0).getTroopId());
               troop = new Troop( "/vtk/"+prefTroop.getCouncilCode()+
        		"/"+prefTroop.getTroopId(),
        		 apiConfig.getUserId() +"_"+ prefTroop.getTroopId() );
               */
               troop= troopDAO.createTroop( ""+prefTroop.getCouncilCode(), prefTroop.getTroopId() );
        }
        troop.setApiConfig(apiConfig);
        
       
		
        
       if(isTest){ 
		org.girlscouts.vtk.salesforce.Troop caca= prefTroop;
		caca.setGradeLevel("1-Brownie");
		troop.setTroop(caca);
       }else
		troop.setTroop( prefTroop );
		troop.setSfTroopId( troop.getTroop().getTroopId() );
		troop.setSfUserId( troop.getApiConfig().getUserId() );
		troop.setSfTroopName( troop.getTroop().getTroopName() ); 
		troop.setSfTroopAge( troop.getTroop().getGradeLevel() );
		troop.setSfCouncil( troop.getTroop().getCouncilCode()+"");
		
		
		
		 //cancelled activity check 091814	        
        if( troop!=null && troop.getYearPlan()!=null && troop.getYearPlan().getActivities() !=null && troop.getYearPlan().getActivities().size()>0){
        	activityDAO.checkCanceledActivity(troop);
        }
		
		session.setAttribute("VTK_user", troop);
       if( session!=null && troop.getCurrentTroop()!=null &&  !session.getId().equals( troop.getCurrentTroop() )){  
			;
		 }else{
			 
			 troop.setCurrentTroop( session.getId() );
			 if( isMultiUserFullBlock)
			   if( troop.getYearPlan()!=null)
			 	 troopDAO.updateTroop(troop);
				
		 }
		troop.setCurrentTroop( session.getId() );
		
		
}else{
	
	
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
	troops = troop.getApiConfig().getTroops();
	session.setAttribute("USER_TROOP_LIST", troops);
}
%>
<a href="/content/girlscouts-vtk/en/vtk.controller.html?resetCal=true">RESET CAL</a>

	<%

		if( troop.getErrCode()!=null && troop.getErrCode().equals("111") ){
			%>
				<div style="color:#fff; background-color:red;">Warning: another user is currently logged into this account or you have no permissions. To continue, please logout and login again.  (This will cause the other user to be logged out.) error 111-in db</div>
			<%
			}
			
		if(  isMultiUserFullBlock && troop!=null && troop.getYearPlan()!=null && !meetingDAO.isCurrentTroopId(troop, troop.getCurrentTroop() ) )
		{
			%><div style="color:#fff; background-color:red;">

			Warning: another user is currently logged into this account or you have no permissions. To continue, please logout and login again.  (This will cause the other user to be logged out.) error 111.1-another user</div><% 
			troop.setRefresh(true);
			return;
		    }
		
		if( troop.getErrCode()!=null && troop.getErrCode().equals("112") ){
				%>
					<div style="color:#fff; background-color:red;">Warning: another user is currently logged into this account or you have no permissions. To continue, please logout and login again.  (This will cause the other user to be logged out.) error 112-another user</div>
				<%
				troop.setRefresh(true);
				return;
			}
		
	
	
	
	%>
	
	


