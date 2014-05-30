
<%@page import="java.util.Calendar"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.ejb.*, org.girlscouts.vtk.dao.*" %>

<%
	ActivityDAO activityDAO = sling.getService(ActivityDAO.class);
	UserDAO activityDAO = sling.getService(UserDAO.class);
	LocationDAO activityDAO = sling.getService(ActivityDAO.class);

if( request.getParameter("isMeetingCngAjax") !=null){
	
	MeetingUtil.changeMeetingPositions( (User) session.getValue("VTK_user"),
			request.getParameter("isMeetingCngAjax") );
	
	
	
}else if( request.getParameter("newCustActivity") !=null ){
	
	
		new ActivityDAOImpl().createActivity(
				(User) session.getValue("VTK_user"),
				new Activity( 
			request.getParameter("newCustActivity_name"), 
			request.getParameter("newCustActivity_txt"),
			dateFormat5.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime")), 
			dateFormat5.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime"))  )
				);
	
	
	
	
}else if( request.getParameter("buildSched") !=null ){

	new CalendarUtil().createSched((User) session.getValue("VTK_user"), 
			request.getParameter("calFreq"), 
			new org.joda.time.DateTime(dateFormat4.parse(request.getParameter("calStartDt") +" "+ request.getParameter("calTime") +
					" "+ request.getParameter("calAP"))), request.getParameter("exclDt"));
	
	
}else if( request.getParameter("addYearPlanUser") !=null ){
	
	new UserDAOImpl().selectYearPlan(  (User) session.getValue("VTK_user"), request.getParameter("addYearPlanUser"));
	
	
}else if( request.getParameter("addLocation") !=null ){
	
	LocationUtil.setLocation((User) session.getValue("VTK_user"), 
			 new Location(request.getParameter("name"),
						request.getParameter("address"), request.getParameter("city"), 
						request.getParameter("state"), request.getParameter("zip") ));
	
}else if( request.getParameter("rmLocation") !=null ){

	new LocationDAOImpl().removeLocation((User) session.getValue("VTK_user"), request.getParameter("rmLocation"));
	
	
	
}else if( request.getParameter("newCustAgendaName") !=null ){

	System.err.println( "new custagendaName");
	MeetingUtil.createCustomAgenda((User) session.getValue("VTK_user"), 
			request.getParameter("name"), request.getParameter("newCustAgendaName"), 
			Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ) );
	
	
	
}else if( request.getParameter("setLocationToAllMeetings") !=null ){
	
	new LocationUtil().setLocationAllMeetings((User) session.getValue("VTK_user"), 
			request.getParameter("setLocationToAllMeetings") );
	
}else if( request.getParameter("updSched") !=null ){
	
	CalendarUtil.updateSched((User)session.getValue("VTK_user"), request.getParameter("meetingPath"), 
			request.getParameter("time"), request.getParameter("date"), request.getParameter("ap"), 
			request.getParameter("isCancelledMeeting"), Long.parseLong( request.getParameter("currDt") ));
	
	
	
}else if( request.getParameter("rmCustActivity") !=null ){
	
	
	MeetingUtil.rmCustomActivity ((User)session.getValue("VTK_user"), request.getParameter("rmCustActivity") );
	 
	 
	
}else if( request.getParameter("chnLocation") !=null ){
	
	LocationUtil.changeLocation((User)session.getValue("VTK_user"), request.getParameter("chnLocation"), request.getParameter("newLocPath"));
	
	
}else if( request.getParameter("cngMeeting") !=null ){ //change Meeting
	
	MeetingUtil.swapMeetings((User)session.getValue("VTK_user"), request.getParameter("fromPath"), request.getParameter("toPath"));

}else if( request.getParameter("addMeeting") !=null ){ //add Meeting
	
	MeetingUtil.addMeetings((User)session.getValue("VTK_user"),  request.getParameter("toPath"));


}else if( request.getParameter("isActivityCngAjax") !=null ){ //activity shuffle
	
	MeetingUtil.rearrangeActivity( (User)session.getValue("VTK_user"), request.getParameter("mid"), request.getParameter("isActivityCngAjax"));

}else if( request.getParameter("rmAgenda") !=null ){

	MeetingUtil.rmAgenda((User)session.getValue("VTK_user"), request.getParameter("rmAgenda") , request.getParameter("mid")  );
	
}else if( request.getParameter("editAgendaDuration") !=null ){
	MeetingUtil.editAgendaDuration((User)session.getValue("VTK_user"), Integer.parseInt(request.getParameter("editAgendaDuration")), 
			request.getParameter("aid"),
			request.getParameter("mid"));
}else{
	
	//TODO throw ERROR CODE
	
}

%>




<%!
   java.text.SimpleDateFormat dateFormat5 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm");
   java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
  
   

%>