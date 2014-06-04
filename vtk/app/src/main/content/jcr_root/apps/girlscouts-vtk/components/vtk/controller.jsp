
<%@page import="java.util.Calendar"%>
<%@page import="org.joda.time.LocalDate"%>
<%@ page import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.ejb.*, org.girlscouts.vtk.dao.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

<%
	HttpSession session = request.getSession();

	ActivityDAO activityDAO = sling.getService(ActivityDAO.class);
	UserDAO activityDAO = sling.getService(UserDAO.class);
	LocationDAO activityDAO = sling.getService(ActivityDAO.class);
	CalendarUtil calendarUtil = sling.getService(CalendarUtil.class);
	LocationUtil locationUtil = sling.getService(LocationUtil.class);
	MeetingUtil meetingUtil = sling.getService(MeetingUtil.class);

if( request.getParameter("isMeetingCngAjax") !=null){
	
	MeetingUtil.changeMeetingPositions( (User) session.getValue("VTK_user"),
			request.getParameter("isMeetingCngAjax") );
	
	
	
}else if( request.getParameter("newCustActivity") !=null ){
	
	
		activityDAO.createActivity(
				(User) session.getValue("VTK_user"),
				new Activity( 
			request.getParameter("newCustActivity_name"), 
			request.getParameter("newCustActivity_txt"),
			dateFormat5.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_startTime")), 
			dateFormat5.parse(request.getParameter("newCustActivity_date") +" "+request.getParameter("newCustActivity_endTime"))  )
				);
	
	
	
	
}else if( request.getParameter("buildSched") !=null ){

	calendarUtil.createSched((User) session.getValue("VTK_user"), 
			request.getParameter("calFreq"), 
			new org.joda.time.DateTime(dateFormat4.parse(request.getParameter("calStartDt") +" "+ request.getParameter("calTime") +
					" "+ request.getParameter("calAP"))), request.getParameter("exclDt"));
	
	
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
			Integer.parseInt(request.getParameter("duration")), Long.parseLong( request.getParameter("startTime") ) );
	
	
	
}else if( request.getParameter("setLocationToAllMeetings") !=null ){
	
	locationUtil().setLocationAllMeetings((User) session.getValue("VTK_user"), 
			request.getParameter("setLocationToAllMeetings") );
	
}else if( request.getParameter("updSched") !=null ){
	
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
}else{
	
	//TODO throw ERROR CODE
	
}

%>




<%!
   java.text.SimpleDateFormat dateFormat5 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm");
   java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
  
   

%>