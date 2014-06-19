
<%@page import="java.util.Calendar"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.ejb.*, org.girlscouts.vtk.dao.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%

//System.err.println("controller");




	HttpSession session = request.getSession();

	ActivityDAO activityDAO = sling.getService(ActivityDAO.class);
	UserDAO userDAO = sling.getService(UserDAO.class);
	LocationDAO locationDAO = sling.getService(LocationDAO.class);
	CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
	LocationUtil locationUtil = sling.getService(LocationUtil.class);
	MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);
	EmailUtil emailUtil = sling.getService(EmailUtil.class);
//System.err.println("controller");






if( request.getParameter("isMeetingCngAjax") !=null){
	
	//System.err.println("contr : meeting rearg");
	meetingUtil.changeMeetingPositions( (User) session.getValue("VTK_user"),
			request.getParameter("isMeetingCngAjax") );
	
	
	
}else if( request.getParameter("newCustActivity") !=null ){
	
	
		activityDAO.createActivity(
				(User) session.getValue("VTK_user"),
				new Activity( 
			request.getParameter("newCustActivity_name"), 
			request.getParameter("newCustActivity_txt"),
			dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime") +" " +request.getParameter("newCustActivity_startTime_AP")), 
			dateFormat4.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime") +" "+ request.getParameter("newCustActivity_endTime_AP")), 
			request.getParameter("newCustActivityLocName"), request.getParameter("newCustActivityLocAddr"))
				);
	
	
	
	
}else if( request.getParameter("buildSched") !=null ){

	try{
	calendarUtil.createSched((User) session.getValue("VTK_user"), 
			request.getParameter("calFreq"), 
			new org.joda.time.DateTime(dateFormat4.parse(request.getParameter("calStartDt") +" "+ request.getParameter("calTime") +
					" "+ request.getParameter("calAP"))), request.getParameter("exclDt"));
	}catch(Exception e){e.printStackTrace();}
	
}else if( request.getParameter("addYearPlanUser") !=null ){
	
	userDAO.selectYearPlan(  (User) session.getValue("VTK_user"), request.getParameter("addYearPlanUser"));
	
	
}else if( request.getParameter("addLocation") !=null ){
	
	locationUtil.setLocation((User) session.getValue("VTK_user"), 
			 new Location(request.getParameter("name"),
						request.getParameter("address"), request.getParameter("city"), 
						request.getParameter("state"), request.getParameter("zip") ));
	
}else if( request.getParameter("rmLocation") !=null ){

	locationDAO.removeLocation((User) session.getValue("VTK_user"), request.getParameter("rmLocation"));
	
	
	
}else if( request.getParameter("newCustAgendaName") !=null ){

	meetingUtil.createCustomAgenda((User) session.getValue("VTK_user"), 
			request.getParameter("name"), request.getParameter("newCustAgendaName"), 
			Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ), request.getParameter("txt") );
	
	
	
}else if( request.getParameter("setLocationToAllMeetings") !=null ){
	
	locationUtil.setLocationAllMeetings((User) session.getValue("VTK_user"), 
			request.getParameter("setLocationToAllMeetings") );
	
}else if( request.getParameter("updSched") !=null ){
	
	System.err.println("updSched");
	System.err.println("UpdCal: "+request.getParameter("date") +" " + request.getParameter("time") +" " + request.getParameter("ap"));
	
	
	calendarUtil.updateSched((User)session.getValue("VTK_user"), request.getParameter("meetingPath"), 
			request.getParameter("time"), request.getParameter("date"), request.getParameter("ap"), 
			request.getParameter("isCancelledMeeting"), Long.parseLong( request.getParameter("currDt") ));
	
	
	
}else if( request.getParameter("rmCustActivity") !=null ){
	
	
	meetingUtil.rmCustomActivity ((User)session.getValue("VTK_user"), request.getParameter("rmCustActivity") );
	 
	 
	
}else if( request.getParameter("chnLocation") !=null ){
	
	locationUtil.changeLocation((User)session.getValue("VTK_user"), request.getParameter("chnLocation"), request.getParameter("newLocPath"));
	
	
}else if( request.getParameter("cngMeeting") !=null ){ //change Meeting
	
	meetingUtil.swapMeetings((User)session.getValue("VTK_user"), request.getParameter("fromPath"), request.getParameter("toPath"));

}else if( request.getParameter("addMeeting") !=null ){ //add Meeting
	
	meetingUtil.addMeetings((User)session.getValue("VTK_user"),  request.getParameter("toPath"));


}else if( request.getParameter("isActivityCngAjax") !=null ){ //activity shuffle
	
	meetingUtil.rearrangeActivity( (User)session.getValue("VTK_user"), request.getParameter("mid"), request.getParameter("isActivityCngAjax"));

}else if( request.getParameter("rmAgenda") !=null ){

	meetingUtil.rmAgenda((User)session.getValue("VTK_user"), request.getParameter("rmAgenda") , request.getParameter("mid")  );
	
}else if( request.getParameter("editAgendaDuration") !=null ){
	meetingUtil.editAgendaDuration((User)session.getValue("VTK_user"), Integer.parseInt(request.getParameter("editAgendaDuration")), 
			request.getParameter("aid"),
			request.getParameter("mid"));
}else if( request.getParameter("revertAgenda") !=null ){
	
	meetingUtil.reverAgenda((User)session.getValue("VTK_user"),  request.getParameter("mid") );
	
}else if( request.getParameter("sendMeetingReminderEmail") !=null ){
	  
	System.err.println("EMAILINGGGGGGG");
	
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
	  
	  User user = (User)session.getValue("VTK_user");
	  user.setApiConfig((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
	  
	  
	  emailUtil.sendMeetingReminder((User)session.getValue("VTK_user"), emr);
	  
}else if( request.getParameter("loginAs")!=null){ //troopId
	if(request.getParameter("loginAs")==null || request.getParameter("loginAs").trim().equals("") ){System.err.println("loginAs invalid.abort");return;}
	System.err.println("REgloginL: "+request.getParameter("loginAs"));
	User curr_user = (User)session.getValue("VTK_user");
	User new_user= userDAO.getUser( curr_user.getApiConfig().getUserId() +"_"+ request.getParameter("loginAs") );
	if( new_user==null )
		 new_user = new User(curr_user.getApiConfig().getUserId()+"_"+ request.getParameter("loginAs") );
	
	java.util.List <org.girlscouts.vtk.salesforce.Troop> troops = curr_user.getApiConfig().getTroops();
	for(int i=0;i<troops.size();i++){
			if( troops.get(i).getTroopId().equals( request.getParameter("loginAs"))){
				new_user.setTroop(troops.get(i) );
			}
	}
    new_user.setApiConfig(curr_user.getApiConfig());
    session.putValue("VTK_user", new_user);
	
}else{
	
	//TODO throw ERROR CODE
	
}

%>




<%!
   //java.text.SimpleDateFormat dateFormat5 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm");
   java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
  
   

%>