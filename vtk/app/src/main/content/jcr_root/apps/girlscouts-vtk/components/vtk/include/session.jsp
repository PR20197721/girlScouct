<%@page	import="java.text.SimpleDateFormat,
                org.apache.commons.lang3.time.FastDateFormat,
                org.girlscouts.vtk.models.Troop,
                org.girlscouts.vtk.auth.permission.*,
                org.girlscouts.vtk.utils.VtkUtil,
                org.apache.commons.lang3.time.FastDateFormat,
                org.apache.sling.runmode.RunMode"%>
<%!

	java.text.NumberFormat FORMAT_CURRENCY = java.text.NumberFormat.getCurrencyInstance();
    java.text.DecimalFormat FORMAT_COST_CENTS = new java.text.DecimalFormat( "#,##0.00");
    
	boolean isCachableContacts=false;
	
	public boolean hasPermission(Troop troop, int permissionId) {
		java.util.Set<Integer> myPermissionTokens = troop.getTroop().getPermissionTokens();
		if (myPermissionTokens != null && myPermissionTokens.contains(permissionId)) {
			return true;
		}
		return false;
	}

	// Feature set toggles
	boolean SHOW_BETA = false; // controls feature for all users -- don't set this to true unless you know what I'm talking about


	String SESSION_FEATURE_MAP = "sessionFeatureMap"; // session attribute to hold map of enabled features
	String[] ENABLED_FEATURES = new String[] {};
%>
<% 

	boolean isMultiUserFullBlock = true;
	final CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
	final LocationUtil locationUtil = sling.getService(LocationUtil.class);
	final MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
	final YearPlanUtil yearPlanUtil = sling.getService(YearPlanUtil.class);
	final TroopUtil troopUtil = sling.getService(TroopUtil.class);
	final UserUtil userUtil = sling.getService(UserUtil.class);
	final FinanceUtil financeUtil = sling.getService(FinanceUtil.class);
	final SessionFactory sessionFactory = sling.getService(SessionFactory.class);
	final ContactUtil contactUtil = sling.getService(ContactUtil.class);
	final ConnectionFactory connectionFactory = sling.getService(ConnectionFactory.class);
	final VtkUtil vtkUtil = sling.getService(VtkUtil.class);
	final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
	
	
	
	//dont use
	final TroopDAO troopDAO = sling.getService(TroopDAO.class);
	final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
	org.girlscouts.vtk.helpers.CouncilMapper councilMapper = sling.getService(org.girlscouts.vtk.helpers.CouncilMapper.class);
	User user=null;
	
	HttpSession session = request.getSession();

	int timeout = session.getMaxInactiveInterval();
	response.setHeader("Refresh", timeout + "; URL = /content/girlscouts-vtk/en/vtk.logout.html");

	if (session.getAttribute(SESSION_FEATURE_MAP) == null) {
		session.setAttribute(SESSION_FEATURE_MAP, new HashSet<String>());
	}
	Set sessionFeatures = (Set) session.getAttribute(SESSION_FEATURE_MAP);
	for (String enabledFeature: ENABLED_FEATURES) {
		if (request.getParameter(enabledFeature) != null) {
			String thisFeatureValue = ((String) request.getParameter(enabledFeature)).trim().toLowerCase();
			if ("true".equals(thisFeatureValue) || "yes".equals(thisFeatureValue) || "1".equals(thisFeatureValue)) {
				if (!sessionFeatures.contains(enabledFeature)) {
					sessionFeatures.add(enabledFeature);
				}
			} else if ("false".equals(thisFeatureValue) || "no".equals(thisFeatureValue) || "0".equals(thisFeatureValue)) {
				if (sessionFeatures.contains(enabledFeature)) {
					sessionFeatures.remove(enabledFeature);
				}
			}
		}
	}
	
	org.girlscouts.vtk.auth.models.ApiConfig apiConfig = null;
	try {
		if (session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()) != null) {
			apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig) session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
		} else {
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			out.println("Your session has timed out.  Please refresh this page and login.");
			return;
		}
	} catch (ClassCastException cce) {
		session.invalidate();
		log.error("ApiConfig class cast exception -- probably due to restart.  Logging out user.");
        try {
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception setStatusException) { setStatusException.printStackTrace(); }
		out.println("Your session has timed out.  Please login.");
		return;
	}
	
	//if(!apiConfig.getUser().isAdmin() && (apiConfig.getTroops() == null
			
	if((apiConfig.getTroops() == null
			|| apiConfig.getTroops().size() <= 0
			|| (apiConfig.getTroops().get(0).getType() == 1)) ){
		
		//out.println("Council Code: "+ apiConfig.getTroops().get(0).getCouncilCode());
			%>
			<div id="panelWrapper" class="row meeting-detail content">
			<div class="columns large-20 large-centered">
			    <p>
			       The Volunteer Toolkit is a digital planning tool currently available for Troop Leaders and Co-Leaders. Parents can access it in the fall, and other troop volunteer roles will have access later on. For questions, click Contact Us at the top of the page.
			        </p>
			        <p>
			        Stay tuned! 
			    </p>
			    </div>
			</div>
			
			<%
return;
	}

	
	user = ((org.girlscouts.vtk.models.User) session
			.getAttribute(org.girlscouts.vtk.models.User.class
					.getName()));
	user.setSid(session.getId());
   
	
    String errMsg = null;
	Troop troop = (Troop) session.getValue("VTK_troop");
	
	//NO PARENTS ALLOWED!!!!!
	boolean  allowParentAccess= Boolean.parseBoolean(configManager.getConfig("allowParentAccess"));
	if( !allowParentAccess && troop!=null && troop.getTroop()!=null && troop.getTroop().getRole()!=null && troop.getTroop().getRole().toUpperCase().trim().equals("PA" ))
	{
		   %>
		<div id="panelWrapper" class="row meeting-detail content">
               <div class="columns large-20 large-centered">
                <p>
                   The Volunteer Toolkit is a digital planning tool currently available for Troop Leaders and Co-Leaders. Parents can access it in the fall, and other troop volunteer roles will have access later on. For questions, click Contact Us at the top of the page.
                    </p>
                    <p>
                    Stay tuned! 
                </p>
                </div>
        </div>
		<%
        return;
	}
	
	
	
	
	
	if( request.getParameter("showGamma")!=null && request.getParameter("showGamma").equals("true")){
	     troop.getTroop().getPermissionTokens().add( PermissionConstants.PERMISSION_VIEW_FINANCE_ID);
	     troop.getTroop().getPermissionTokens().add( PermissionConstants.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID);
	     troop.getTroop().getPermissionTokens().add( PermissionConstants.PERMISSION_VIEW_REPORT_ID);
	     troop.getTroop().getPermissionTokens().add( PermissionConstants.PERMISSION_EDIT_FINANCE_ID );
	     troop.getTroop().getPermissionTokens().add( PermissionConstants.PERMISSION_EDIT_FINANCE_FORM_ID);
	     session.setAttribute("showGamma", "true");
	} else if( request.getParameter("showGamma")!=null && request.getParameter("showGamma").equals("false")){
        troop.getTroop().getPermissionTokens().remove( PermissionConstants.PERMISSION_VIEW_FINANCE_ID);
        troop.getTroop().getPermissionTokens().remove( PermissionConstants.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID);
        troop.getTroop().getPermissionTokens().remove( PermissionConstants.PERMISSION_VIEW_REPORT_ID);
        troop.getTroop().getPermissionTokens().remove( PermissionConstants.PERMISSION_EDIT_FINANCE_ID );
        troop.getTroop().getPermissionTokens().remove( PermissionConstants.PERMISSION_EDIT_FINANCE_FORM_ID);
        session.setAttribute("showGamma", null);
	}else if( false) {// session.getAttribute("showGamma")==null ){
		
		//disable REPORT &  finances
	    try{
	        if( user.getApiConfig().getUser().isAdmin() && troop!=null && troop.getTroop()!=null && troop.getTroop().getPermissionTokens()!=null){      
	            troop.getTroop().getPermissionTokens().remove( Permission.PERMISSION_VIEW_REPORT_ID);
	            troop.getTroop().getPermissionTokens().remove( Permission.PERMISSION_VIEW_FINANCE_ID);
	        }
	    }catch(Exception e){e.printStackTrace();}
		
	}
	
	//Needs for front yp page. ajax/multi call to session.jsp. Not always happens.
	if(  troop != null && !troop.isRefresh() && !userUtil.isCurrentTroopId_NoRefresh(troop,user.getSid() ) &&
			session.getAttribute("isReloadedWindow")!=null ){
	
			troop.setRefresh(true);
	}
	session.removeAttribute( "isReloadedWindow"); //rm after pull
	
	if(request.getParameter("reload")!=null){troop.setRefresh(true);}

	
	    //if (troop == null || troop.isRefresh() || troopUtil.isUpdated(troop)) {
		if (troop == null || troop.isRefresh() ) {

			if (troop != null && troop.isRefresh() && troop.getErrCode() != null && !troop.getErrCode().equals(""))
				errMsg = troop.getErrCode();
		
	
	  org.girlscouts.vtk.salesforce.Troop prefTroop = apiConfig.getTroops().get(0);
	  
	  if( troop!=null){
		  for (int ii = 0; ii < apiConfig.getTroops().size(); ii++){
		 	if( apiConfig.getTroops().get(ii).getTroopId().equals(troop.getSfTroopId())){ 
		 			prefTroop = apiConfig.getTroops().get(ii);
		 			break;
	  		}
		  }
	  }else{
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			theCookie: for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals("vtk_prefTroop")) {
					for (int ii = 0; ii < apiConfig.getTroops().size(); ii++)
						if (apiConfig.getTroops().get(ii)
								.getGradeLevel()
								.equals(cookies[i].getValue())) {
							prefTroop = apiConfig.getTroops().get(ii);
							break theCookie;
						}

				}

			}
		}
	 }
	 
	
		try{
		   if( apiConfig.getUser().isAdmin() && prefTroop.getTroopId().equals("none"))
			   ;
		   else
				   troop = troopUtil.getTroop(user, "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());

		   //load troop contacts
		   //-java.util.List<Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), prefTroop.getTroopId() );
		   
		   
		} catch (org.girlscouts.vtk.utils.VtkException ec ){
			%>
            <div id="panelWrapper" class="row meeting-detail content">
              <p class="errorNoTroop" style="padding:10px;color: #009447; font-size: 14px;">
                 <%=ec.getMessage() %> 
                 <br/>Please notify Girlscouts VTK support
              </p>
          </div>
            <%
            return;
		}catch(IllegalAccessException ex){
			%><span class="error">Sorry, you have no access to view year plan</span><%
			return;
		}
		
		
		if (troop == null ) {
			
		  try{
			troop = troopUtil.createTroop(user, 
					"" + prefTroop.getCouncilCode(),
					prefTroop.getTroopId());
		  }catch(org.girlscouts.vtk.utils.VtkException e){
			  %>
			  
			  <div id="panelWrapper" class="row meeting-detail content">
			    <p class="errorNoTroop" style="padding:10px;color: #009447; font-size: 14px;">
			       <%=e.getMessage() %> 
			       <br/>Please notify Girlscouts VTK support
			    </p>
			</div>
<%
			           
			  e.printStackTrace();
			  return;
			  }
		  }
		
		
		
		
		troop.setTroop(prefTroop);
		troop.setSfTroopId(troop.getTroop().getTroopId());
		troop.setSfUserId( user.getApiConfig().getUserId() ); //troop.getApiConfig().getUserId());
		troop.setSfTroopName(troop.getTroop().getTroopName());
		troop.setSfTroopAge(troop.getTroop().getGradeLevel());
		troop.setSfCouncil(troop.getTroop().getCouncilCode() + "");

		/*
		if (troop != null && troop.getYearPlan() != null
				&& troop.getYearPlan().getActivities() != null
				&& troop.getYearPlan().getActivities().size() > 0) {
			yearPlanUtil.checkCanceledActivity(user, troop);
		}
		*/
		session.setAttribute("VTK_troop", troop);
	}

	java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = (java.util.List<org.girlscouts.vtk.salesforce.Troop>) session
			.getAttribute("USER_TROOP_LIST");
	if (session.getAttribute("USER_TROOP_LIST") == null) {
		troops = user.getApiConfig().getTroops();
		session.setAttribute("USER_TROOP_LIST", troops);
	}

	if ((errMsg != null && errMsg.equals("111"))
			|| (troop.getErrCode() != null && troop.getErrCode()
					.equals("111"))) {
%>
<!--Warning:  Another user is logged in with this user id.  If you have logged in to the Volunteer Toolkit on another device or desktop, please logout and login again. error 111-in db -->
<%
	}

	/*
	if (isMultiUserFullBlock
			&& troop != null
			&& troop.getYearPlan() != null
			&& !userUtil.isCurrentTroopId(troop,
					troop.getCurrentTroop())) {
%><div style="color: #fff; background-color: red;">Warning:  Another user is logged in with this user id.  If you have logged in to the Volunteer Toolkit on another device or desktop, please logout and login again.111.1</div>
<%
		troop.setRefresh(true);
		return;
	}
*/
	if ((errMsg != null && errMsg.equals("112"))
			|| (troop.getErrCode() != null && troop.getErrCode().equals("112"))
	    	) {
%>
<div style="color: #fff; background-color: red;">
One of your co-leaders is currently making changes in the Volunteer Toolkit for your troop.  When the updates are completed, you will be able to update the Volunteer Toolkit.
</div>
<%
		troop.setRefresh(true);
	}

if( false ){//troop!=null && troop.getYearPlan()!=null){
	String footerScript = "<script>$( document ).ready(function() {setTimeout(function(){expiredcheck('"+session.getId()+"','"+troop.getYearPlan().getPath()+"');},20000);});</script>";
	request.setAttribute("footerScript", footerScript);
}

RunMode runModeService = sling.getService(RunMode.class);
String apps[] = new String[1];
apps[0]="prod";
if( runModeService.isActive(apps) ){ 
    String footerScript ="<script>window['ga-disable-UA-2646810-36'] = false; vtkInitTracker('"+troop.getSfTroopName()+"', '"+troop.getSfTroopId() +"', '"+user.getApiConfig().getUser().getSfUserId()+"');vtkTrackerPushAction('View');</script>";
    request.setAttribute("footerScript", footerScript);
}else{
	String footerScript ="<script>window['ga-disable-UA-2646810-36'] = true;</script>";
    request.setAttribute("footerScript", footerScript);
}



%>


