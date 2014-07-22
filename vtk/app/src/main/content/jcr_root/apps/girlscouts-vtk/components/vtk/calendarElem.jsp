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
<h2><%=   	meetingDAO.getMeeting(  meeting.getRefId() ).getName() %></h2>
<div id="locMsg"></div>
<div class="modifyCalendarDate">
<form>
	<label for"cngDate0">Change Date</label>
	<br/><input type="text" value="<%= FORMAT_MMddYYYY.format(date) %>" id="cngDate0"  name="cngDate0" class="date calendarField"/>
	<br/><label for"cngTime0">Change Time</label>
	<br/><input type="text" id="cngTime0" value="<%= FORMAT_hhmm.format(date) %>" name="cngDate0" class="date"/>
	<select id="cngAP0" name="cngAP0" class="ampm">
		<option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>PM</option> 
		<option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>AM</option>
	</select>

	<br/>
        <input type="checkbox" id="isCancellMeeting0" name="isCancellMeeting0" <%=isCancelMeeting == true ? "CHECKED" : "" %>/>&nbsp;<label for"isCancellMeeting0">Cancel Meeting</label>
	<br/>
	<hr/>
	<input type="button" value="save" onclick="updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>')" class="button linkButton"/>
	<input type="button" value="cancel" onclick="loadCalMng()" class="button linkButton"/>
</form>
</div>
<script>
$(function() {
    $( "#cngDate0" ).datepicker({minDate: 0});
  });
</script>
