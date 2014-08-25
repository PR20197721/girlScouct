<%@page import="org.codehaus.jackson.map.ObjectMapper"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<%!
	double convertObjectToDouble(Object o) {
		Double parsedDouble = 0.00d;
		if (o != null) {
			try{
				String preParsedCost = ((String) o).replaceAll(",", "").replaceAll(" ", "");
				parsedDouble = Double.parseDouble(preParsedCost);
			} catch (NumberFormatException npe) {
				// do nothing -- leave cost at 0.00
			} catch (ClassCastException cce) {
				// doo nothing -- leave cost at 0.00
			}catch(Exception e){
				// print error
				e.printStackTrace();
			}
		}
		return parsedDouble;
	}
	java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
%>
<%

System.err.println("CONTROOLER "+ (request.getParameter("editMeetingName")==null ));
Enumeration parameterList = request.getParameterNames();
while( parameterList.hasMoreElements() )
{
  String sName = parameterList.nextElement().toString();
  System.err.println("</br/>"+ sName +" :" + request.getParameter(sName));
}


if( request.getParameter("isMeetingCngAjax") !=null){
	meetingUtil.changeMeetingPositions( user, request.getParameter("isMeetingCngAjax") );
}else if( request.getParameter("newCustActivity") !=null ){
	
	double cost= convertObjectToDouble(request.getParameter("newCustActivity_cost"));
	activityDAO.createActivity(user, new Activity( 
		request.getParameter("newCustActivity_name"), 
		request.getParameter("newCustActivity_txt"),
		dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime") +" " +request.getParameter("newCustActivity_startTime_AP")), 
		dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime") +" "+ request.getParameter("newCustActivity_endTime_AP")), 
		request.getParameter("newCustActivityLocName"), request.getParameter("newCustActivityLocAddr"),
		cost )
	);
}else if( request.getParameter("buildSched") !=null ){
	try{
		calendarUtil.createSched(user, 
			request.getParameter("calFreq"), 
			new org.joda.time.DateTime(dateFormat4.parse(request.getParameter("calStartDt") +" "+ request.getParameter("calTime") +
			" "+ request.getParameter("calAP"))), request.getParameter("exclDt"));
	}catch(Exception e){
		e.printStackTrace();
	}
	
}else if( request.getParameter("addYearPlanUser") !=null ){
	userDAO.selectYearPlan(  user, request.getParameter("addYearPlanUser"), request.getParameter("addYearPlanName"));
}else if( request.getParameter("addLocation") !=null ){
	boolean isLoc=false;
	if( user.getYearPlan().getLocations()!=null && request.getParameter("name")!=null ) {
		for(int i=0;i< user.getYearPlan().getLocations().size();i++) {
			if( user.getYearPlan().getLocations().get(i).getName().equals(request.getParameter("name")) ) {
				isLoc=true;
				out.println("Location already exists");
			}
		}
	}
	if(!isLoc) {
		locationUtil.setLocation(user, 
		new Location(request.getParameter("name"),
		request.getParameter("address"), request.getParameter("city"), 
		request.getParameter("state"), request.getParameter("zip") ));
	}
	if( user.getYearPlan().getLocations().size()==1 ){
		locationUtil.setLocationAllMeetings(user, user.getYearPlan().getLocations().get(0).getPath());
	}else if( !isLoc ){
		locationUtil.setLocationAllEmpty( user,request.getParameter("name") );
	}
}else if( request.getParameter("rmLocation") !=null ){
	locationDAO.removeLocation(user, request.getParameter("rmLocation"));
}else if( request.getParameter("newCustAgendaName") !=null ){
	
	meetingUtil.createCustomAgenda(user, 
			request.getParameter("name"), request.getParameter("newCustAgendaName"), 
			Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ), request.getParameter("txt") );

}else if( request.getParameter("setLocationToAllMeetings") !=null ){
	locationUtil.setLocationAllMeetings(user, request.getParameter("setLocationToAllMeetings") );
}else if( request.getParameter("updSched") !=null ){
	calendarUtil.updateSched(user, request.getParameter("meetingPath"), 
		request.getParameter("time"), request.getParameter("date"), request.getParameter("ap"), 
		request.getParameter("isCancelledMeeting"), Long.parseLong( request.getParameter("currDt") ));
}else if( request.getParameter("rmCustActivity") !=null ){
	meetingUtil.rmCustomActivity (user, request.getParameter("rmCustActivity") );
}else if( request.getParameter("chnLocation") !=null ){
	locationUtil.changeLocation(user, request.getParameter("chnLocation"), request.getParameter("newLocPath"));
}else if( request.getParameter("cngMeeting") !=null ){ //change Meeting
	meetingUtil.swapMeetings(user, request.getParameter("fromPath"), request.getParameter("toPath"));
}else if( request.getParameter("addMeeting") !=null ){ //add Meeting
	meetingUtil.addMeetings(user,  request.getParameter("toPath"));
}else if( request.getParameter("isActivityCngAjax") !=null ){ //activity shuffle
	meetingUtil.rearrangeActivity( user, request.getParameter("mid"), request.getParameter("isActivityCngAjax"));
}else if( request.getParameter("rmAgenda") !=null ){
	meetingUtil.rmAgenda(user, request.getParameter("rmAgenda") , request.getParameter("mid")  );
}else if( request.getParameter("editAgendaDuration") !=null ){
	meetingUtil.editAgendaDuration(user, Integer.parseInt(request.getParameter("editAgendaDuration")), 
		request.getParameter("aid"),
		request.getParameter("mid"));
}else if( request.getParameter("revertAgenda") !=null ){
	meetingUtil.reverAgenda(user,  request.getParameter("mid") );
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
	  emailUtil.sendMeetingReminder(user, emr);
}else if( request.getParameter("loginAs")!=null){ //troopId
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
	User new_user= userDAO.getUser( 
		"/vtk/"+newTroop.getCouncilCode()+
    		"/"+newTroop.getTroopId()+"/users/"+
		user.getApiConfig().getUserId()+"_"+  request.getParameter("loginAs") );
	if( new_user==null ){
		new_user = new User( "/vtk/"+newTroop.getCouncilCode()+ "/"+newTroop.getTroopId()+"/users/", user.getApiConfig().getUserId()+"_"+  request.getParameter("loginAs") );
	}
	new_user.setTroop(newTroop );
	new_user.setApiConfig(user.getApiConfig());
	new_user.setSfTroopId( new_user.getTroop().getTroopId() );
	new_user.setSfUserId( new_user.getApiConfig().getUserId() );
	new_user.setSfTroopName( new_user.getTroop().getTroopName() );  
	session.setAttribute("VTK_user", new_user);
	session.putValue("VTK_planView_memoPos", null);
}else if( request.getParameter("addAsset")!=null){
	org.girlscouts.vtk.models.Asset asset = new org.girlscouts.vtk.models.Asset(request.getParameter("addAsset"));
	new UserDAOImpl().addAsset( user ,  request.getParameter("meetingUid"),   asset);
}else if( request.getParameter("testAB")!=null){
	userDAO.updateUser(user);
	//getMeeting(girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan1/meetings/meeting1");
	//meetingDAO.getMeeting("/content/girlscouts-vtk/meetings/myyearplan/brownie/B14OG01");
}else if( request.getParameter("addAids")!=null){
	if( request.getParameter("assetType").equals("AID")){
		meetingUtil.addAids(user, request.getParameter("addAids"), request.getParameter("meetingId"), java.net.URLDecoder.decode(request.getParameter("assetName") ) );
	}else{
		meetingUtil.addResource(user, request.getParameter("addAids"), request.getParameter("meetingId"), java.net.URLDecoder.decode(request.getParameter("assetName") ) );
	}
}else if( request.getParameter("rmAsset")!=null){
	meetingUtil.rmAsset(user, request.getParameter("rmAsset"), request.getParameter("meetingId"));
}else if( request.getParameter("previewMeetingReminderEmail") !=null ){
	  String email_to_gp =request.getParameter("email_to_gp");
	  String email_to_sf =request.getParameter("email_to_sf");
	  String email_to_tv =request.getParameter("email_to_tv");
	  String cc = request.getParameter("email_cc");
	  String subj = request.getParameter("email_subj");
	  String html = request.getParameter("email_htm"); 
	  String meetingId= request.getParameter("mid");
	  EmailMeetingReminder emr=null;
	  if( user.getSendingEmail() !=null ){
		  emr= user.getSendingEmail();
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

	  user.setSendingEmail(emr);
}else if( request.getParameter("sendMeetingReminderEmail") !=null ){ //view smpt
  
	EmailMeetingReminder emr=null;
	if( user.getSendingEmail() !=null ) {
		emr= user.getSendingEmail();
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
	  
	//TODO LOG JCR
	  
	//REMOVE EMR
	emr=null;
	user.setSendingEmail(null);
}else if( request.getParameter("bindAssetToYPC") !=null ){
	String assetId = request.getParameter("bindAssetToYPC");
	String ypcId = request.getParameter("ypcId");
	String assetDesc = java.net.URLDecoder.decode(request.getParameter("assetDesc"));
	String assetTitle = java.net.URLDecoder.decode(request.getParameter("assetTitle"));
	java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
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
			
			userDAO.updateUser(user);
			return;
		}
	}
	
	java.util.List<Activity> activities = user.getYearPlan().getActivities();
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
				
				userDAO.updateUser(user);
				return;
			}
		}
	}
}else if( request.getParameter("editCustActivity") !=null ){
	
	java.util.List<Activity> activities= user.getYearPlan().getActivities();
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
	
	userDAO.updateUser(user);
}else if( request.getParameter("srch") !=null ){
	try{
		java.util.Date startDate = null, endDate= null;
		if(request.getParameter("startDate") !=null && !request.getParameter("startDate").equals("")) {
			startDate = new java.util.Date(request.getParameter("startDate"));
		}
		if(request.getParameter("endDate") !=null && !request.getParameter("endDate").equals("")) {
			endDate = new java.util.Date(request.getParameter("endDate"));
		}
		java.util.List activities= meetingDAO.searchA1( user,  request.getParameter("lvl"), request.getParameter("cat") ,
			request.getParameter("keywrd"),
			startDate, endDate,
			request.getParameter("region")
		);
		session.putValue("vtk_search_activity", activities);
	}catch(Exception e){
		e.printStackTrace();
	}
   
}else if( request.getParameter("newCustActivityBean") !=null ){
	java.util.List <org.girlscouts.vtk.models.Activity> activities =  (java.util.List <org.girlscouts.vtk.models.Activity>)session.getValue("vtk_search_activity");
	for(int i=0;i<activities.size();i++){
		if( activities.get(i).getUid().equals( request.getParameter("newCustActivityBean") )){
			activityDAO.createActivity(user, activities.get(i) );
			break;
		}
	}
	
}else if( request.getParameter("id") !=null ){
	//out.println( request.getParameter("newvalue") );
	System.err.println("MID: "+request.getParameter("mid"));
	
	java.util.List<MeetingE> meetings= user.getYearPlan().getMeetingEvents();
	for(MeetingE m: meetings){
		if( m.getUid().equals( request.getParameter("mid") ))
		{
		
			
			Meeting custM = m.getMeetingInfo();
			
		
			
			
			if( request.getParameter("id").equals("editMeetingName")){
				custM.setName( request.getParameter("newvalue") );
			}else if( request.getParameter("id").equals("editMeetingDesc")){
				//custM.set( request.getParameter("newvalue") );
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
			//create custom meeting
			System.err.println(0 +" :"  + m.getRefId());
			
			if( !m.getRefId().contains("_") )
			    meetingDAO.createCustomMeeting(user, m, custM);
			else{
				System.err.println(1);
				meetingDAO.updateCustomMeeting(user, m, custM);
				System.err.println(2);
			}
			
			 out.println( request.getParameter("newvalue") );
			}catch(Exception e){e.printStackTrace();}
			
			break;
		}
	}
	
}else if( request.getParameter("test") !=null ){
	
	//System.err.println( "TEST: "+request.getParameter("test") );
	
	ObjectMapper mapper = new ObjectMapper();
	out.println(mapper.writeValueAsString(user));
	
	//System.err.println( "User json: "+mapper.writeValueAsString(user) );


/*
	out.print("["+ 
	 "{"+
	  "\"age\": 13, \"id\": \"motorola-defy-with-motoblur\", \"name\": \"Motorola DEFY\u2122 with MOTOBLUR\u2122\", \"snippet\": \"Are you ready for everything life throws your way?\""+	
	 "}"+
	"]");
	*/		
			  
			  
	
}else if( request.getParameter("editMtLogo") !=null ){
	
	System.err.println("EDITING MEETING LOGO...");
	/*
	java.util.List<MeetingE> meetings= user.getYearPlan().getMeetingEvents();
	for(MeetingE m: meetings){
		if( m.getUid().equals( request.getParameter("mid") ))
		{
			
			Meeting custM = m.getMeetingInfo();
			custM.setName( request.getParameter("newvalue") );
					
			//create custom meeting
			 meetingDAO.createCustomMeeting(user, m, custM);
			
			 out.println( request.getParameter("newvalue") );
			
			break;
		}
	}
	*/
	
}else{
	//TODO throw ERROR CODE
}
%>
