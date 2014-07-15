<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/calendarElem.jsp -->
<%
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());

String elem = request.getParameter("elem");
java.util.Date date = new java.util.Date( Long.parseLong(elem));
MeetingE meeting = (MeetingE)sched.get(date);
String AP = "AM";
if( FORMAT_AMPM.format(date).toUpperCase().equals("PM")){
	AP="PM";
}

boolean isCancelMeeting= false;
if( meeting != null && meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
	isCancelMeeting=true;
}
%>       
<%=   	meetingDAO.getMeeting(  meeting.getRefId() ).getName() %>
<div id="locMsg"></div>
<div  style="padding:40px;">
	Change Date: <input type="text" value="<%= FORMAT_MMddYYYY.format(date) %>" id="cngDate0"  />
	Change Time: <input tyle="text" id="cngTime0" value="<%= FORMAT_hhmm.format(date) %>"/>
	<select id="cngAP0">
		<option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>pm</option> 
		<option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>am</option>
	</select>
	<input type="checkbox" id="isCancellMeeting0" <%=isCancelMeeting == true ? "CHECKED" : "" %>/>Cancel Meeting
	<input type="button" value="save" onclick="updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>')"/>
	<input type="button" value="cancel" onclick="loadCalMng()"/>
</div>

<script>
$(function() {
    $( "#cngDate0" ).datepicker({minDate: 0});
  });
</script>
