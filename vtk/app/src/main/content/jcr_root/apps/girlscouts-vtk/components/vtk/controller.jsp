<%@page import="java.util.Calendar"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.ejb.*, org.girlscouts.vtk.dao.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
	ActivityDAO activityDAO = sling.getService(ActivityDAO.class);
	LocationDAO locationDAO = sling.getService(LocationDAO.class);
	CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
	LocationUtil locationUtil = sling.getService(LocationUtil.class);
	MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
	EmailUtil emailUtil = sling.getService(EmailUtil.class);

if( request.getParameter("isMeetingCngAjax") !=null){
	
	//System.err.println("contr : meeting rearg");
	meetingUtil.changeMeetingPositions( user, request.getParameter("isMeetingCngAjax") );
	
	
	
}else if( request.getParameter("newCustActivity") !=null ){
	
	double cost=0.00;
	try{cost= Double.parseDouble(request.getParameter("newCustActivity_cost").replace(",","") );}catch(Exception e){e.printStackTrace();}
	
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
	}catch(Exception e){e.printStackTrace();}
	
}else if( request.getParameter("addYearPlanUser") !=null ){
	
	userDAO.selectYearPlan(  user, request.getParameter("addYearPlanUser"),
			request.getParameter("addYearPlanName"));
	
	
}else if( request.getParameter("addLocation") !=null ){
	
	
	
		
	    locationUtil.setLocation(user, 
			 new Location(request.getParameter("name"),
						request.getParameter("address"), request.getParameter("city"), 
						request.getParameter("state"), request.getParameter("zip") ));
	
	    
	    if( user.getYearPlan().getLocations().size()==1 ){
			
		    locationUtil.setLocationAllMeetings(user, user.getYearPlan().getLocations().get(0).getPath());
		}
	
	    
	    
}else if( request.getParameter("rmLocation") !=null ){

	locationDAO.removeLocation(user, request.getParameter("rmLocation"));
	
	
	
}else if( request.getParameter("newCustAgendaName") !=null ){

	meetingUtil.createCustomAgenda(user, 
			request.getParameter("name"), request.getParameter("newCustAgendaName"), 
			Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ), request.getParameter("txt") );
	
	
	
}else if( request.getParameter("setLocationToAllMeetings") !=null ){
	
	locationUtil.setLocationAllMeetings(user, 
			request.getParameter("setLocationToAllMeetings") );
	
}else if( request.getParameter("updSched") !=null ){
	
	//System.err.println("updSched");
	//System.err.println("UpdCal: "+request.getParameter("date") +" " + request.getParameter("time") +" " + request.getParameter("ap"));
	
	
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
	  
	  //System.err.println("EMAILINGGGGGGG");
	
	  String email_to_gp =request.getParameter("email_to_gp");
	  String email_to_sf =request.getParameter("email_to_sf");
	  String email_to_tv =request.getParameter("email_to_tv");
	  String cc = request.getParameter("email_to_cc");
	  String subj = request.getParameter("email_subj");
	  String html = request.getParameter("email_htm"); 
	  
	  System.err.println("contrHTML: "+ html);
	  
	  EmailMeetingReminder emr = new EmailMeetingReminder(null, null, cc, subj, html);
	  emr.setEmailToGirlParent(email_to_gp);
	  emr.setEmailToSelf(email_to_sf);
	  emr.setEmailToTroopVolunteer(email_to_tv);
	  emailUtil.sendMeetingReminder(user, emr);
	  
}else if( request.getParameter("loginAs")!=null){ //troopId
	if(request.getParameter("loginAs")==null || request.getParameter("loginAs").trim().equals("") ){System.err.println("loginAs invalid.abort");return;}
	
	java.util.List<org.girlscouts.vtk.salesforce.Troop> troops = user.getApiConfig().getTroops();
	org.girlscouts.vtk.salesforce.Troop newTroop = null;
	for(int i=0;i<troops.size();i++)
		if( troops.get(i).getTroopId().equals(request.getParameter("loginAs")))
			newTroop= troops.get(i);
	
	User new_user= userDAO.getUser( 
			"/vtk/"+newTroop.getCouncilCode()+
    		"/"+newTroop.getTroopName()+"/users/"+
		user.getApiConfig().getUserId()+"_"+  request.getParameter("loginAs") );

	if( new_user==null ){
		
		new_user = new User(
				 "/vtk/"+newTroop.getCouncilCode()+
	        		"/"+newTroop.getTroopName()+"/users/",
				user.getApiConfig().getUserId()+"_"+  request.getParameter("loginAs") );
	}
	
	new_user.setTroop(newTroop );
	
	
    new_user.setApiConfig(user.getApiConfig());
    new_user.setSfTroopId( new_user.getTroop().getTroopId() );
    new_user.setSfUserId( new_user.getApiConfig().getUserId() );
    new_user.setSfTroopName( new_user.getTroop().getTroopName() );  
    session.putValue("VTK_user", new_user);
}else if( request.getParameter("addAsset")!=null){
	
	org.girlscouts.vtk.models.Asset asset = new org.girlscouts.vtk.models.Asset(request.getParameter("addAsset"));
	
	//System.err.println("*** "+request.getParameter("meetingUid") );
	new UserDAOImpl().addAsset( user ,  request.getParameter("meetingUid"),   asset);
	
	
}else if( request.getParameter("testAB")!=null){
	userDAO.updateUser(user);
}else if( request.getParameter("addAids")!=null){
	meetingUtil.addAids(user, request.getParameter("addAids"), request.getParameter("meetingId"), request.getParameter("assetName") );
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
	 
	  System.err.println("--- "+ addAid +" : "+ rmAid);
	  
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
	  if( user.getSendingEmail() !=null )
		  emr= user.getSendingEmail();
	 
	  if( emr.getCc()==null || emr.getCc().equals(""))
	 	 emr.setCc("ayakobovich@northpointdigital.com");
	 
	  
	  String html = emr.getHtml();
		html+="<br/>Aids Included:";
			
			
			if( emr!=null ){
				java.util.List<Asset> eAssets = emr.getAssets();
				if( eAssets!=null)
					for(int i=0;i<eAssets.size();i++){
						html += "<br/><a href=\""+eAssets.get(i).getRefId()+"\">"+eAssets.get(i).getRefId()+ "</a>";
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
	System.err.println("*** "+ assetId +" : "+ ypcId +" : "+ assetDesc);
	java.util.List<MeetingE> meetings = user.getYearPlan().getMeetingEvents();
	for(int i=0;i<meetings.size();i++){
		if( meetings.get(i).getUid().equals( ypcId)){
			
			
			//System.err.println("Found meetin");
			Asset asset = new Asset();
			asset.setIsCachable(false);
			asset.setRefId(assetId);
			asset.setDescription(assetDesc);
			
			java.util.List<Asset> assets = meetings.get(i).getAssets();
			assets = assets ==null ? new java.util.ArrayList() : assets;
			
			assets.add(asset);
			
			meetings.get(i).setAssets(assets);
			
			userDAO.updateUser(user);
			return;
		}
	}
	
	java.util.List<Activity> activities = user.getYearPlan().getActivities();
	for(int i=0;i<activities.size();i++){
		if( activities.get(i).getUid().equals( ypcId)){
			
			Asset asset = new Asset();
			asset.setIsCachable(false);
			asset.setRefId(assetId);
			asset.setDescription(assetDesc);
			
			java.util.List<Asset> assets = activities.get(i).getAssets();
			assets = assets ==null ? new java.util.ArrayList() : assets;
			
			assets.add(asset);
			
			activities.get(i).setAssets(assets);
			
			userDAO.updateUser(user);
			return;
		}
	}
	
	
	
	
	
}else if( request.getParameter("editCustActivity") !=null ){
	
	java.util.List<Activity> activities= user.getYearPlan().getActivities();
	Activity activity= null; 
	for(int i=0;i<activities.size();i++)
		if(activities.get(i).getUid().equals(request.getParameter("editCustActivity")) )
		{activity = activities.get(i); break;}
	
	double cost=0.00;
	try{cost= Double.parseDouble(request.getParameter("newCustActivity_cost") );}catch(Exception e){e.printStackTrace();}
	
	activity.setCost(cost);
	activity.setContent(request.getParameter("newCustActivity_txt"));
	activity.setDate( dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime") +" " +request.getParameter("newCustActivity_startTime_AP") ));
	activity.setEndDate(dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime") +" "+ request.getParameter("newCustActivity_endTime_AP")));
	activity.setName(request.getParameter("newCustActivity_name"));
	activity.setLocationName(request.getParameter("newCustActivityLocName"));
	activity.setLocationAddress(request.getParameter("newCustActivityLocAddr"));
	
	userDAO.updateUser(user);
	

}else{
	//TODO throw ERROR CODE
	
}

%>




<%!
   //java.text.SimpleDateFormat dateFormat5 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm");
   java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
  
   

%>
