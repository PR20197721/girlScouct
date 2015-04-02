<%@page	import="org.girlscouts.vtk.models.Troop, org.girlscouts.vtk.auth.permission.*, org.girlscouts.vtk.utils.VtkUtil"%>
<%!java.text.SimpleDateFormat FORMAT_MMddYYYY = new java.text.SimpleDateFormat("MM/dd/yyyy");
	java.text.SimpleDateFormat FORMAT_hhmm_AMPM = new java.text.SimpleDateFormat("hh:mm a");
	java.text.SimpleDateFormat FORMAT_hhmm = new java.text.SimpleDateFormat("hh:mm");
	java.text.SimpleDateFormat FORMAT_AMPM = new java.text.SimpleDateFormat("a");
	java.text.SimpleDateFormat FORMAT_MONTH = new java.text.SimpleDateFormat("MMM");
	java.text.SimpleDateFormat FORMAT_DAY_OF_MONTH = new java.text.SimpleDateFormat("d");
	java.text.SimpleDateFormat FORMAT_MONTH_DAY = new java.text.SimpleDateFormat("MMM d");
	java.text.SimpleDateFormat FORMAT_MMM_dd_hhmm_AMPM = new java.text.SimpleDateFormat("MMM dd hh:mm a");
	java.text.SimpleDateFormat FORMAT_MMMM_dd_hhmm_AMPM = new java.text.SimpleDateFormat("MMMM dd hh:mm a");
	java.text.SimpleDateFormat FORMAT_MEETING_REMINDER = new java.text.SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
	java.text.SimpleDateFormat FORMAT_MMM_dd_yyyy_hhmm_AMPM = new java.text.SimpleDateFormat( "MMM dd yyyy hh:mm a");
	java.text.SimpleDateFormat FORMAT_CALENDAR_DATE = new java.text.SimpleDateFormat( "MMM dd, yyyy hh:mm a");
	java.text.NumberFormat FORMAT_CURRENCY = java.text.NumberFormat.getCurrencyInstance();
	java.text.DecimalFormat FORMAT_COST_CENTS = new java.text.DecimalFormat( "#,##0.00");
	java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
	java.text.SimpleDateFormat fmr_ddmm = new java.text.SimpleDateFormat("M/d");
	java.text.SimpleDateFormat fmt_yyyyMMdd = new java.text.SimpleDateFormat("yyyy-MM-dd");
	
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

	String SHOW_BETA_FEATURE = "showBeta"; // request parameter to control feature per user session
	String SHOW_FINANCE_FEATURE = "showFinance"; 
	String SHOW_PARENT_FEATURE = "showParent";
	String SHOW_ADMIN_FEATURE = "showCouncilAdmin";
	String SHOW_PERMISSION_FEATURE = "showPermission";

	String SESSION_FEATURE_MAP = "sessionFeatureMap"; // session attribute to hold map of enabled features
	String[] ENABLED_FEATURES = new String[] {SHOW_BETA_FEATURE, SHOW_FINANCE_FEATURE, SHOW_PARENT_FEATURE, SHOW_ADMIN_FEATURE, SHOW_PERMISSION_FEATURE};

%>
<%
	boolean isMultiUserFullBlock = true;
	final CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
	final LocationUtil locationUtil = sling.getService(LocationUtil.class);
	final MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
	final EmailUtil emailUtil = sling.getService(EmailUtil.class);
	final YearPlanUtil yearPlanUtil = sling.getService(YearPlanUtil.class);
	final TroopUtil troopUtil = sling.getService(TroopUtil.class);
	final UserUtil userUtil = sling.getService(UserUtil.class);
	final FinanceUtil financeUtil = sling.getService(FinanceUtil.class);
	final SessionFactory sessionFactory = sling.getService(SessionFactory.class);
	final ContactUtil contactUtil = sling.getService(ContactUtil.class);
	
	//dont use
	final TroopDAO troopDAO = sling.getService(TroopDAO.class);
	
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
			out.println("Your session has timed out.  Please refresh this page and login.");
			return;
		}
	} catch (ClassCastException cce) {
		session.invalidate();
		log.error("ApiConfig class cast exception -- probably due to restart.  Logging out user.");
		out.println("Your session has timed out.  Please login.");
		return;
	}
	if (apiConfig.getTroops() == null
			|| apiConfig.getTroops().size() <= 0
			|| (apiConfig.getTroops().get(0).getType() == 1)) {
		out.println("Council Code: "
				+ apiConfig.getTroops().get(0).getCouncilCode());
		out.println("<span class='error'>Sorry, this user is not part of a valid GirlScouts' campaign. Please ask your council admin for SalesForce access.</span>");
		return;
	}

	
	user = ((org.girlscouts.vtk.models.User) session
			.getAttribute(org.girlscouts.vtk.models.User.class
					.getName()));
	user.setSid(session.getId());

	String errMsg = null;
	Troop troop = (Troop) session.getValue("VTK_troop");
	
	
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
				
		   troop = troopUtil.getTroop(user, "" + prefTroop.getCouncilCode(), prefTroop.getTroopId());

		   //load troop contacts
		   //-java.util.List<Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), prefTroop.getTroopId() );
		   
		   
		   
		}catch(IllegalAccessException ex){
			%><span class="error">Sorry, you have no access to view year plan</span><%
			return;
		}
		
		
		if (troop == null) {
			troop = troopUtil.createTroop(user, 
					"" + prefTroop.getCouncilCode(),
					prefTroop.getTroopId());
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
//	if (false){//troop.getSfUserId().equals("005Z0000002OBPiIAO")) {
	if ((SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) && sessionFeatures.contains(SHOW_PERMISSION_FEATURE)) {
%>
<form action="/content/girlscouts-vtk/controllers/vtk.controller.html">
	<div class="small-18 medium-18 large-18 columns">
		<select name="chngPermis">
			<option value="<%=PermissionConstants.GROUP_LEADER%>">
				Leader-
				<%=PermissionConstants.GROUP_LEADER%></option>
			<option value="<%=PermissionConstants.GROUP_MEMBER_2G%>">GROUP_MEMBER_2G_PERMISSIONS</option>
			<option value="<%=PermissionConstants.GROUP_MEMBER_1G%>">GROUP_MEMBER_1G_PERMISSIONS</option>
			<option value="<%=PermissionConstants.GROUP_MEMBER_NO_TROOP%>">GROUP_MEMBER_NO_TROOP_PERMISSIONS</option>
			<option value="<%=PermissionConstants.GROUP_MEMBER_TROOP%>">GROUP_MEMBER_TROOP_PERMISSIONS</option>
			<option value="<%=PermissionConstants.GROUP_GUEST%>">GROUP_GUEST_PERMISSIONS</option>
		</select>
	</div>
	<div class="small-6 medium-6 large-6 columns">
		<input type="submit" value="Change my permission" class="button" />
	</div>
</form>
<%
	}
if( false ){//troop!=null && troop.getYearPlan()!=null){
	String footerScript = "<script>$( document ).ready(function() {setTimeout(function(){expiredcheck('"+session.getId()+"','"+troop.getYearPlan().getPath()+"');},20000);});</script>";
	request.setAttribute("footerScript", footerScript);
}

if(true){//userUtil.hasPermission(troop, Permission.PERMISSION_REMOVE_MEETING_ID)){
    //String footerScript ="<script>(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)})(window,document,'script','//www.google-analytics.com/analytics.js','ga');ga('create', 'UA-61431888-1', 'auto');ga('set', 'dimension1', '"+troop.getSfTroopName()+"');ga('set', 'dimension2', '"+troop.getSfTroopId() +"');ga('set', 'dimension3', '"+user.getApiConfig().getUser().getSfUserId()+"');ga('send', 'pageview');</script>";
    String footerScript ="<script>vtkInitTracker('"+troop.getSfTroopName()+"', '"+troop.getSfTroopId() +"', '"+user.getApiConfig().getUser().getSfUserId()+"');vtkTrackerPushAction('sessionjsp');</script>";
    request.setAttribute("footerScript", footerScript);
}

%>
