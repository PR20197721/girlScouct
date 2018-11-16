<%@page import="java.net.URLEncoder,
	java.text.SimpleDateFormat,
	org.apache.commons.lang3.time.FastDateFormat,
	org.girlscouts.vtk.models.Troop,
	org.girlscouts.vtk.auth.permission.*,
	org.girlscouts.vtk.utils.VtkUtil,
	org.apache.commons.lang3.time.FastDateFormat,
	org.apache.sling.runmode.RunMode"%><%!
    // put all static in util classes
	java.text.NumberFormat FORMAT_CURRENCY = java.text.NumberFormat.getCurrencyInstance();
    java.text.DecimalFormat FORMAT_COST_CENTS = new java.text.DecimalFormat( "#,##0.00");
	boolean isCachableContacts=false;

	// Feature set toggles
	boolean SHOW_BETA = false; // controls feature for all users -- don't set this to true unless you know what I'm talking about
	String SHOW_VALID_SF_USER_FEATURE = "showValidSfUser";

	String SESSION_FEATURE_MAP = "sessionFeatureMap"; // session attribute to hold map of enabled features
	String[] ENABLED_FEATURES = new String[] {SHOW_VALID_SF_USER_FEATURE};
%><% 
	String relayUrl="";//sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("idpSsoTargetUrl") +"&RelayState="+sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("baseUrl");
	boolean isMultiUserFullBlock = true;
// Why so heavy?  Do we need to load all services here or maybe on demand is better?
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
	
	
	// Why is this here if it say's not to use?
	//dont use
	final TroopDAO troopDAO = sling.getService(TroopDAO.class);
	//final org.girlscouts.vtk.helpers.ConfigManager configManager = sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class);
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
		    
		    if( session.getAttribute("fatalError")!=null ){
		    	%><div id="panelWrapper" class="row meeting-detail content">
                 <div class="columns large-20 large-centered">
                	<%@include file="vtkError.jsp" %>
		    	 </div>
		    	</div><%
		    }else{
				    %>
				    <div id="panelWrapper" class="row meeting-detail content">
		             <div class="columns large-20 large-centered">
		                <p>
		                   Your session has timed out.  Please refresh this page and login.
		                 </p>
		             </div>
		            </div>
					<% 
		    }
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
	
	if((apiConfig.getTroops() == null
			|| apiConfig.getTroops().size() <= 0
			|| (apiConfig.getTroops().get(0).getType() == 1)) ){
			int vtkSignupYear = VtkUtil.getCurrentGSYear() + 1;
			%>
			<div id="panelWrapper" class="row meeting-detail content">
			<div class="columns large-20 large-centered">
			    <%@include file="vtkError.jsp" %>
			    
			    <p>We're sorry you're having trouble logging into the VTK. We're here to help!</p>
			    <p>
			    	The Volunteer Toolkit is a digital planning tool currently available for active Troop Leaders and Co-Leaders. To access the VTK, please ensure you have an active <%= vtkSignupYear %> membership. If you're a parent, please make sure your daughter has an active <%= vtkSignupYear %> membership record as well and that she's part of an active troop.
			    </p>
			    <p>
			    	Need help? click Contact Us at the top of the page to connect with the customer service team and we'll get it sorted out.
			    </p>
			    <p>
			    	Thank you!
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
	}

    //if (troop == null || troop.isRefresh() || troopUtil.isUpdated(troop)) {
	if (troop == null || troop.isRefresh() ) {
		if (troop != null && troop.isRefresh() && troop.getErrCode() != null && !troop.getErrCode().equals("")) {
			errMsg = troop.getErrCode();
		}
		
	    org.girlscouts.vtk.salesforce.Troop prefTroop = null;
		if (apiConfig.getTroops() != null && apiConfig.getTroops().size() > 0) {
		  prefTroop = apiConfig.getTroops().get(0);
		}
	  
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
						
	 
						for (int ii = 0; ii < apiConfig.getTroops().size(); ii++) {
							String gradeLevel = apiConfig.getTroops().get(ii).getGradeLevel();
							
							if (gradeLevel != null && gradeLevel.equals(cookies[i].getValue())) {
								prefTroop = apiConfig.getTroops().get(ii);
								break theCookie;
							}
						}
					}
				}
			}
		}

		try{
		   if(!(apiConfig.getUser().isAdmin() && prefTroop.getTroopId().equals("none"))) {

			   troop = troopUtil.getTroop(user, "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());

		   }
		} catch (org.girlscouts.vtk.utils.VtkException ec ){
%>  <div id="panelWrapper" class="row meeting-detail content">
              <p class="errorNoTroop" style="padding:10px;color: #009447; font-size: 14px;">
                 <%=ec.getMessage() %> 
                 <br/>Please notify Girlscouts VTK support
              </p>
          </div>
<%
            return;
		}catch(IllegalAccessException ex){
			ex.printStackTrace();
			%><span class="error">Sorry, you have no access to view year plan.</span><%
			return;
		}
		
		
	    if (troop == null ) {
	        try{
	        
	            troop = troopUtil.createTroop(user,  "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());
            }catch(org.girlscouts.vtk.utils.VtkException e){
%><div id="panelWrapper" class="row meeting-detail content">
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
		session.setAttribute("VTK_troop", troop);
	}
	
	java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = (java.util.List<org.girlscouts.vtk.salesforce.Troop>) session.getAttribute("USER_TROOP_LIST");
	if (session.getAttribute("USER_TROOP_LIST") == null) {
		troops = user.getApiConfig().getTroops();
		session.setAttribute("USER_TROOP_LIST", troops);
	}
	//check valid cache url /myvtk/
	if( !VtkUtil.isValidUrl( user,  troop, request.getRequestURI() , councilMapper.getCouncilName(troop.getSfCouncil())) ) {
	    response.setStatus(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
	    return;
	}

	RunMode runModeService = sling.getService(RunMode.class);
	String apps[] = new String[] {"prod"}; 
	String prodButDontTrack[] = new String[]{"gspreview"};
	
	if( runModeService.isActive(apps) && !runModeService.isActive(prodButDontTrack)){ 
	    String footerScript ="<script>window['ga-disable-UA-2646810-36'] = false; vtkInitTracker('"+troop.getSfTroopName()+"', '"+troop.getSfTroopId() +"', '"+user.getApiConfig().getUser().getSfUserId()+"', '"+ troop.getSfCouncil() +"', '"+ troop.getSfTroopAge()+"', '" + (troop.getYearPlan() == null ? "" : troop.getYearPlan().getName()) + "'); vtkTrackerPushAction('View'); showSelectedDemoTroop('"+troop.getSfTroopAge()+"');</script>";
	    request.setAttribute("footerScript", footerScript);
	}else{
		String footerScript ="<script>window['ga-disable-UA-2646810-36'] = true; showSelectedDemoTroop('"+troop.getSfTroopAge()+"')</script>";
	    request.setAttribute("footerScript", footerScript);
	}
	request.setAttribute("vtk-request-user",user);
	request.setAttribute("vtk-request-troop",troop);
%>                  