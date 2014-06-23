<%@page import="java.util.Iterator"%>
<%@ page import="org.girlscouts.vtk.models.user.*,org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/calendarElem.jsp -->
<a href="/content/girlscouts-vtk/en/vtk.html">Return to Plan</a>
<%
MeetingDAO meetingDAO = sling.getService(MeetingDAO.class);
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());

String elem = request.getParameter("elem");
java.util.Date date = new java.util.Date( Long.parseLong(elem));
MeetingE meeting = (MeetingE)sched.get(date);
String AP = "AM";
if( fmtAP.format(date).toUpperCase().equals("PM")){
	AP="PM";
}

boolean isCancelMeeting= false;
if( meeting != null && meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
	isCancelMeeting=true;
}
%>       
<h1>F3 Manage Calendar</h1>
<%=   	meetingDAO.getMeeting(  meeting.getRefId() ).getName() %>
<div id="locMsg"></div>
<div  style="padding:40px; background-color:gray; border:1px solid red;">
	Change Date: <input type="text" value="<%= fmtDate.format(date) %>" id="cngDate0" onclick="dtPicker('cngDate0')" />
	Change Time: <input tyle="text" id="cngTime0" value="<%= fmtHr1.format(date) %>"/>
	<select id="cngAP0">
		<option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>pm</option> 
		<option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>am</option>
	</select>
	<input type="checkbox" id="isCancellMeeting0" <%=isCancelMeeting == true ? "CHECKED" : "" %>/>Cancel Meeting
	<input type="button" value="save" onclick="updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>')"/>
	<input type="button" value="cancel" onclick="loadCalMng()"/>
</div>
