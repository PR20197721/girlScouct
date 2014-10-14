<%@page import="org.codehaus.jackson.map.ObjectMapper"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>


<%

String vtkErr="";

if( request.getParameter("isMeetingCngAjax") !=null){
//	/gscontroller/vtk/crud/meetingOrder POST {operation:UPDATE , isMeetingCngAjax: [2,4,6,31]}
	meetingUtil.changeMeetingPositions( user, troop, request.getParameter("isMeetingCngAjax") );
}else if( request.getParameter("newCustActivity") !=null ){
	yearPlanUtil.createActivity(user, troop, new Activity( 
		request.getParameter("newCustActivity_name"), 
		request.getParameter("newCustActivity_txt"),
		dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime") +" " +request.getParameter("newCustActivity_startTime_AP")), 
		dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime") +" "+ request.getParameter("newCustActivity_endTime_AP")), 
		request.getParameter("newCustActivityLocName"), request.getParameter("newCustActivityLocAddr"),
		convertObjectToDouble(request.getParameter("newCustActivity_cost")) )
	);
}else if( request.getParameter("buildSched") !=null ){
//	/gscontroller/vtk/crud/createSchedule POST {operation: CREATE, calFreq: 'daily', calStartDt: '9/23/14', calTime: '10:46 EST'}
	try{
		calendarUtil.createSched(user, troop, 
			request.getParameter("calFreq"), 
			new org.joda.time.DateTime(dateFormat4.parse(request.getParameter("calStartDt") +" "+ request.getParameter("calTime") +
			" "+ request.getParameter("calAP"))), request.getParameter("exclDt"));

	}catch(Exception e){
		e.printStackTrace();
	}
	
}else if( request.getParameter("addYearPlanUser") !=null ){
	troopUtil.selectYearPlan( user,  troop, request.getParameter("addYearPlanUser"), request.getParameter("addYearPlanName"));

}else if( request.getParameter("addLocation") !=null ){
	
	locationUtil.addLocation(user, troop, new Location(request.getParameter("name"),
			request.getParameter("address"), request.getParameter("city"), 
			request.getParameter("state"), request.getParameter("zip") ));
	/*
	boolean isLoc=VtkUtil.isLocation(troop.getYearPlan().getLocations(), request.getParameter("name")); //false;

	if(!isLoc) {
		locationUtil.setLocation(user, troop, 
		new Location(request.getParameter("name"),
		request.getParameter("address"), request.getParameter("city"), 
		request.getParameter("state"), request.getParameter("zip") ));
	}
	if( troop.getYearPlan().getLocations().size()==1 ){
		locationUtil.setLocationAllMeetings( user, troop, troop.getYearPlan().getLocations().get(0).getPath());
	}else if( !isLoc ){
		
		locationUtil.setLocationAllEmpty(user, troop,request.getParameter("name") );
	}
	*/
	
}else if( request.getParameter("rmLocation") !=null ){
	// /gscontroller/vtk/crud/location POST {operation: DELETE, locationId: 12345}
	locationUtil.removeLocation(user, troop, request.getParameter("rmLocation"));
}else if( request.getParameter("newCustAgendaName") !=null ){
	
	meetingUtil.createCustomAgenda(user, troop, 
			request.getParameter("name"), request.getParameter("newCustAgendaName"), 
			Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ), request.getParameter("txt") );

}else if( request.getParameter("setLocationToAllMeetings") !=null ){
	// /gscontroller/vtk/crud/updateAllMeetingLocations POST {operation: UPDATE, locationId: 12345}
	locationUtil.setLocationAllMeetings(user, troop, request.getParameter("setLocationToAllMeetings") );
}else if( request.getParameter("updSched") !=null ){
	calendarUtil.updateSched(user, troop, request.getParameter("meetingPath"), 
		request.getParameter("time"), request.getParameter("date"), request.getParameter("ap"), 
		request.getParameter("isCancelledMeeting"), Long.parseLong( request.getParameter("currDt") ));
}else if( request.getParameter("rmCustActivity") !=null ){
	meetingUtil.rmCustomActivity (user, troop, request.getParameter("rmCustActivity") );
}else if( request.getParameter("chnLocation") !=null ){
	locationUtil.changeLocation(user, troop, request.getParameter("chnLocation"), request.getParameter("newLocPath"));
}else if( request.getParameter("cngMeeting") !=null ){ //change Meeting
	meetingUtil.swapMeetings(user, troop, request.getParameter("fromPath"), request.getParameter("toPath"));
}else if( request.getParameter("addMeeting") !=null ){ //add Meeting
	meetingUtil.addMeetings(user, troop,  request.getParameter("toPath"));
}else if( request.getParameter("isActivityCngAjax") !=null ){ //activity shuffle
	meetingUtil.rearrangeActivity(user,  troop, request.getParameter("mid"), request.getParameter("isActivityCngAjax"));

}else if( request.getParameter("rmAgenda") !=null ){
	// /gscontroller/vtk/crud/removeAgenda POST {operation: DELETE, agendaId: 12345}
	meetingUtil.rmAgenda(user, troop, request.getParameter("rmAgenda") , request.getParameter("mid")  );
}else if( request.getParameter("editAgendaDuration") !=null ){
	// /gscontroller/vtk/crud/agenda POST {operation: update, action: updateDuration, duration: 10}
	meetingUtil.editAgendaDuration(user, troop, Integer.parseInt(request.getParameter("editAgendaDuration")), 
		request.getParameter("aid"),
		request.getParameter("mid"));
}else if( request.getParameter("revertAgenda") !=null ){
	// /gscontroller/vtk/crud/agenda POST {operation: UPDATE, action: revert, agendaId: 123}
	// /gscontroller/vtk/action/revertAgenda POST {agendaId: 123}
	meetingUtil.reverAgenda(user, troop,  request.getParameter("mid") );
}else if( request.getParameter("sendMeetingReminderEmail_SF") !=null ){ //view SalesForce
	  String email_to_gp =request.getParameter("email_to_gp");
	  String email_to_sf =request.getParameter("email_to_sf");
	  String email_to_tv =request.getParameter("email_to_tv");
	  String cc = request.getParameter("email_to_cc");
	  String subj = request.getParameter("email_subj");
	  String html = request.getParameter("email_htm"); 
	  System.out.println("contrHTML: "+ html);
	  
	  EmailMeetingReminder emr = new EmailMeetingReminder(null, null, cc, subj, html);
	  emr.setEmailToGirlParent(email_to_gp);
	  emr.setEmailToSelf(email_to_sf);
	  emr.setEmailToTroopVolunteer(email_to_tv);
	  emailUtil.sendMeetingReminder(troop, emr);
}else if( request.getParameter("loginAs")!=null){ //troopId
	// /gscontroller/vtk/action/changeTroop POST {operation: UPDATE, locationId: 12345}

	troopUtil.reLogin(user, troop, request.getParameter("loginAs"), session);
/*
	if(request.getParameter("loginAs")==null || request.getParameter("loginAs").trim().equals("") ){
		System.err.println("loginAs invalid.abort");
		return;
	}
	
	troops = user.getApiConfig().getTroops();
	session.setAttribute("USER_TROOP_LIST", troops);

	org.girlscouts.vtk.salesforce.Troop newTroop = null;
	for(int i=0;i<troops.size();i++) {
		if( troops.get(i).getTroopId().equals(request.getParameter("loginAs"))) {
			newTroop= troops.get(i);
		}
	}
	
	Troop new_troop= troopUtil.getTroop(user, newTroop.getCouncilCode()+"", request.getParameter("loginAs") );
	
	
	if( new_troop==null ){
			new_troop= troopUtil.createTroop(user, ""+newTroop.getCouncilCode(), request.getParameter("loginAs"));
	
	}
	new_troop.setTroop(newTroop );
	new_troop.setSfTroopId( new_troop.getTroop().getTroopId() );
	new_troop.setSfUserId( user.getApiConfig().getUserId() );
	new_troop.setSfTroopName( new_troop.getTroop().getTroopName() );  
	new_troop.setSfTroopAge(new_troop.getTroop().getGradeLevel());
	new_troop.setSfCouncil(new_troop.getTroop().getCouncilCode()+"" );
	
	//logout multi troop
	troopUtil.logout(user, troop);
	
	session.setAttribute("VTK_troop", new_troop);	
	session.putValue("VTK_planView_memoPos", null);
	new_troop.setCurrentTroop( session.getId() );
	troopUtil.updateTroop(user, new_troop);
	*/
}else if( request.getParameter("addAsset")!=null){
	org.girlscouts.vtk.models.Asset asset = new org.girlscouts.vtk.models.Asset(request.getParameter("addAsset"));
	troopUtil.addAsset(user,  troop ,  request.getParameter("meetingUid"),   asset);
}else if( request.getParameter("testAB")!=null){
	
	java.util.Set<Integer> myPermissionTokens = new HashSet<Integer>();
	troop.getTroop().setPermissionTokens(myPermissionTokens);
	boolean isUsrUpd= false;
	try{
		isUsrUpd = troopUtil.updateTroop(user, troop) ;	
	}catch(IllegalAccessException iae){iae.printStackTrace();}
	if(!isUsrUpd)
		vtkErr+= vtkErr.concat("Warning: You last change was not saved.");
	
}else if( request.getParameter("addAids")!=null){
	if( request.getParameter("assetType").equals("AID")){
		meetingUtil.addAids(user, troop, request.getParameter("addAids"), request.getParameter("meetingId"), java.net.URLDecoder.decode(request.getParameter("assetName") ) );
	}else{
		meetingUtil.addResource(user, troop, request.getParameter("addAids"), request.getParameter("meetingId"), java.net.URLDecoder.decode(request.getParameter("assetName") ) );
	}
}else if( request.getParameter("rmAsset")!=null){
	meetingUtil.rmAsset(user, troop, request.getParameter("rmAsset"), request.getParameter("meetingId"));
}else if( request.getParameter("previewMeetingReminderEmail") !=null ){
	  String email_to_gp =request.getParameter("email_to_gp");
	  String email_to_sf =request.getParameter("email_to_sf");
	  String email_to_tv =request.getParameter("email_to_tv");
	  String cc = request.getParameter("email_cc");
	  String subj = request.getParameter("email_subj");
	  String html = request.getParameter("email_htm"); 
	  String meetingId= request.getParameter("mid");
	  EmailMeetingReminder emr=null;
	  if( troop.getSendingEmail() !=null ){
		  emr= troop.getSendingEmail();
		  emr.setCc(cc);
		  emr.setSubj(subj);
		  emr.setHtml(html);
	  }else{
	  	  emr = new EmailMeetingReminder(null, null, cc, subj, html);
	  }
	  emr.setEmailToGirlParent(email_to_gp);
	  emr.setEmailToSelf(email_to_sf);
	  emr.setEmailToTroopVolunteer(email_to_tv);
	  emr.setMeetingId(meetingId);
	  
	  String addAid= request.getParameter("addAid");
	  String rmAid=  request.getParameter("rmAid");
	  
	  java.util.List<Asset> aids = emr.getAssets();
	  if( addAid!=null ){
		  aids= aids==null ? new java.util.ArrayList<Asset>() : aids;
		  Asset aid = new Asset();
		  aid.setRefId(addAid);
		  aids.add( aid );
		  emr.setAssets(aids);
	  }
	  
	  if( rmAid!=null){
		  for(int i=0;i<aids.size();i++)
			  if( aids.get(i).getRefId().equals( rmAid))
		  		aids.remove(i);
	  }

	  troop.setSendingEmail(emr);
}else if( request.getParameter("sendMeetingReminderEmail") !=null ){ //view smpt
// /gscontroller/vtk/action/sendMeetingReminderEmail parameters  
	EmailMeetingReminder emr=null;
	if( troop.getSendingEmail() !=null ) {
		emr= troop.getSendingEmail();
	}
	if( emr.getCc()==null || emr.getCc().equals("")) {
		emr.setCc("ayakobovich@northpointdigital.com");
	}
	String html = emr.getHtml();
	html+="<br/>Aids Included:";
	if( emr!=null ){
		java.util.List<Asset> eAssets = emr.getAssets();
		if( eAssets!=null) {
			for(int i=0;i<eAssets.size();i++){
				html += "<br/><a href=\""+eAssets.get(i).getRefId()+"\">"+eAssets.get(i).getRefId()+ "</a>";
			}
		}
	}
	emr.setHtml( html);
	org.girlscouts.vtk.ejb.Emailer emailer = sling.getService(org.girlscouts.vtk.ejb.Emailer.class);
	emailer.test(emr);
	  
	
	emr=null;
	troop.setSendingEmail(null);
}else if( request.getParameter("bindAssetToYPC") !=null ){
	
	vtkErr = troopUtil.bindAssetToYPC(user, troop, request.getParameter("bindAssetToYPC"), 
			request.getParameter("ypcId"), request.getParameter("assetDesc"), request.getParameter("assetTitle"));
	
	/*
	String assetId = request.getParameter("bindAssetToYPC");
	String ypcId = request.getParameter("ypcId");
	String assetDesc = java.net.URLDecoder.decode(request.getParameter("assetDesc"));
	String assetTitle = java.net.URLDecoder.decode(request.getParameter("assetTitle"));
	java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
	for(int i=0;i<meetings.size();i++){
		if( meetings.get(i).getUid().equals( ypcId)){
			Asset asset = new Asset();
			asset.setIsCachable(false);
			asset.setRefId(assetId);
			asset.setDescription(assetDesc);
			asset.setTitle(assetTitle);
			
			java.util.List<Asset> assets = meetings.get(i).getAssets();
			assets = assets ==null ? new java.util.ArrayList() : assets;
			
			assets.add(asset);
			
			meetings.get(i).setAssets(assets);
			
			boolean isUsrUpd= troopUtil.updateTroop(user, troop);
			
			if(!isUsrUpd)
				vtkErr+= vtkErr.concat("Warning: You last change was not saved.");
			
			
			return;
		}
	}
	
	java.util.List<Activity> activities = troop.getYearPlan().getActivities();
	if( activities!=null) {
		for(int i=0;i<activities.size();i++){
			if( activities.get(i).getUid().equals( ypcId)){
				Asset asset = new Asset();
				asset.setIsCachable(false);
				asset.setRefId(assetId);
				asset.setDescription(assetDesc);
				asset.setTitle(assetTitle);
				
				java.util.List<Asset> assets = activities.get(i).getAssets();
				assets = assets ==null ? new java.util.ArrayList() : assets;
				
				assets.add(asset);
				
				activities.get(i).setAssets(assets);
				
				boolean isUsrUpd = troopUtil.updateTroop(user, troop);
				if(!isUsrUpd)
					vtkErr+= vtkErr.concat("Warning: You last change was not saved.");
				
				return;
			}
		}
	}
	*/
}else if( request.getParameter("editCustActivity") !=null ){
	
	java.util.List<Activity> activities= troop.getYearPlan().getActivities();
	Activity activity= null; 
	for(int i=0;i<activities.size();i++)
		if(activities.get(i).getUid().equals(request.getParameter("editCustActivity")) )
			{activity = activities.get(i); break;}	
    double cost= convertObjectToDouble(request.getParameter("newCustActivity_cost"));
	activity.setCost(cost);
	activity.setContent(request.getParameter("newCustActivity_txt"));
	activity.setDate( dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime") +" " +request.getParameter("newCustActivity_startTime_AP") ));
	activity.setEndDate(dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime") +" "+ request.getParameter("newCustActivity_endTime_AP")));
	activity.setName(request.getParameter("newCustActivity_name"));
	activity.setLocationName(request.getParameter("newCustActivityLocName"));
	activity.setLocationAddress(request.getParameter("newCustActivityLocAddr"));
	
	boolean isUsrUpd = troopUtil.updateTroop(user, troop);
	if(!isUsrUpd)
		vtkErr+= vtkErr.concat("Warning: You last change was not saved.");
	
	
}else if( request.getParameter("srch") !=null ){
	try{
		java.util.Date startDate = null, endDate= null;
		if(request.getParameter("startDate") !=null && !request.getParameter("startDate").equals("")) {
			startDate = new java.util.Date(request.getParameter("startDate"));
		}
		if(request.getParameter("endDate") !=null && !request.getParameter("endDate").equals("")) {
			endDate = new java.util.Date(request.getParameter("endDate"));
		}
		java.util.List activities= yearPlanUtil.searchA1(user, troop,  request.getParameter("lvl"), request.getParameter("cat") ,
			request.getParameter("keywrd"),
			startDate, endDate,
			request.getParameter("region")
		);
		session.putValue("vtk_search_activity", activities);
	}catch(Exception e){
		e.printStackTrace();
	}
   
}else if( request.getParameter("newCustActivityBean") !=null ){
	yearPlanUtil.createCustActivity(user, troop, (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity"), request.getParameter("newCustActivityBean"));
	/*
	java.util.List <org.girlscouts.vtk.models.Activity> activities =  (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
	for(int i=0;i<activities.size();i++){
		if( activities.get(i).getUid().equals( request.getParameter("newCustActivityBean") )){	
			yearPlanUtil.createActivity(user, troop, activities.get(i) );
			break;
		}
	}
	*/
	
}else if(request.getParameter("isAltered") !=null){ //on yearPlan cng
	
	out.println( yearPlanUtil.isYearPlanAltered(user, troop) );

	/*
	if( troop.getYearPlan()!=null ){
		if( troop.getYearPlan().getAltered()!=null && troop.getYearPlan().getAltered().equals("true") ){
		
			out.println("true");return;
		}
	}
	out.println("false");return;
	*/
}else if(request.getParameter("admin_login")!=null ){
	
	if( session.getValue("VTK_ADMIN") ==null ){
		String u= request.getParameter("usr");
		String p= request.getParameter("pswd");
		if( u.equals("admin") && p.equals("icruise123") )
			session.putValue("VTK_ADMIN", u);
	}
	response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.home.html");
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}else if( request.getParameter("id") !=null ){

	java.util.List<MeetingE> meetings= troop.getYearPlan().getMeetingEvents();
	for(MeetingE m: meetings){
		if( m.getUid().equals( request.getParameter("mid") ))
		{
			Meeting custM = m.getMeetingInfo();			
			if( request.getParameter("id").equals("editMeetingName")){
				custM.setName( request.getParameter("newvalue") );
			}else if( request.getParameter("id").equals("editMeetingDesc")){
				java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  custM.getMeetingInfo();
				meetingInfoItems.put("meeting short description", new JcrCollectionHoldString(request.getParameter("newvalue")));
			}else if( request.getParameter("id").equals("editMeetingOverview")){
				java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  custM.getMeetingInfo();
				meetingInfoItems.put("overview", new JcrCollectionHoldString(request.getParameter("newvalue")));
			}else if( request.getParameter("id").equals("editMeetingActivity")){
				java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  custM.getMeetingInfo();
				meetingInfoItems.put("detailed activity plan", new JcrCollectionHoldString(request.getParameter("newvalue")));
			}else if( request.getParameter("id").equals("editMeetingMaterials")){
				java.util.Map<String, JcrCollectionHoldString> meetingInfoItems=  custM.getMeetingInfo();
				meetingInfoItems.put("materials", new JcrCollectionHoldString(request.getParameter("newvalue")));
			}
			
			
			try{
				if( !m.getRefId().contains("_") )
				    yearPlanUtil.createCustomMeeting(user, troop, m, custM);
				else{
					yearPlanUtil.updateCustomMeeting(user, troop, m, custM);				
				}				
				out.println( request.getParameter("newvalue") );
			}catch(Exception e){e.printStackTrace();}
			
			break;
		}
	}
	
}else if( request.getParameter("test") !=null ){
	

	ObjectMapper mapper = new ObjectMapper();
	out.println(mapper.writeValueAsString(troop));
	
	
			  
			  
	
}else if( request.getParameter("editMtLogo") !=null ){
	
	System.err.println("EDITING MEETING LOGO...");
	
}else if(request.getParameter("updateCouncilMilestones") !=null){
	
	String councilId= request.getParameter("cid");
	
	java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones( councilId ) ;
	for(int i=0;i<milestones.size();i++){
		
		Milestone m = milestones.get(i);
		String blurb= request.getParameter("blurb" + i);
		String date = request.getParameter("date" + i);
		
		m.setBlurb(blurb);
		m.setDate( new java.util.Date(date) );
		
	}
	
	yearPlanUtil.saveCouncilMilestones(milestones);
	response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
	
}else if(request.getParameter("createCouncilMilestones") !=null){
	
	String councilId= request.getParameter("cid");
	java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones( councilId ) ;
	
	Milestone m= new Milestone();
	m.setBlurb(request.getParameter("blurb"));
	m.setDate( new java.util.Date( request.getParameter("date")  ) );
	milestones.add(m);
			
	response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
	
}else if(request.getParameter("removeCouncilMilestones") !=null){
	
	
	java.util.List<Milestone> milestones = troop.getYearPlan().getMilestones();
	for(int i=0;i<milestones.size();i++){
		
		Milestone m = milestones.get(i);
		if( m.getUid().equals(request.getParameter("removeCouncilMilestones")) ){
			milestones.remove(m);
			
			
			boolean isUsrUpd =troopUtil.updateTroop(user, troop);
			if(!isUsrUpd)
				vtkErr+= vtkErr.concat("Warning: You last change was not saved.");
			
			
			response.sendRedirect("/content/girlscouts-vtk/en/vtk.admin.milestones.html");
			return;
		}
	}

}else if(request.getParameter("resetCal") !=null){

	calendarUtil.resetCal(user, troop);
	out.println("Cal reset");

}else if( request.getParameter("chngPermis") !=null){

	switch( Integer.parseInt(request.getParameter("chngPermis") )){
	  case 2 :
		troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_GUEST_PERMISSIONS));	
		break;
	  case 11:
			
			troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_LEADER_PERMISSIONS));			
			break;
	  case 12:
		  
		  troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_MEMBER_2G_PERMISSIONS));
		  break;
	  case 13:
		  troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_MEMBER_1G_PERMISSIONS));		  
		  break;
		  
	  case 14:
		  troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_MEMBER_NO_TROOP_PERMISSIONS ));
		  break;
		  
	  case 15:
		  troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_MEMBER_TROOP_PERMISSIONS ));
		  break;
		  
	   default:
		   troop.getTroop().setPermissionTokens(Permission.getPermissionTokens( Permission.GROUP_GUEST_PERMISSIONS));	
			
		break;
	}
	
}else{
	//TODO throw ERROR CODE
}






%>
