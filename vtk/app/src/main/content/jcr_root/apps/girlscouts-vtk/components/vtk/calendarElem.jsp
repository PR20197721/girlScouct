<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/calendarElem.jsp -->













<%
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());

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
<h2><%=   	yearPlanUtil.getMeeting( user, meeting.getRefId() ).getName() %></h2>
<div id="locMsg"></div>
<div class="modifyCalendarDate">
<form id="frmCalElem">
	<label for"cngDate0">Change Date</label>
	<br/><input type="text" value="<%= FORMAT_MMddYYYY.format(date) %>" id="cngDate0"  name="cngDate0" class="date calendarField"/>
	<span  id="cngDate0ErrMsg"></span>
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
	<!--  <input type="button" value="save" onclick="updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>')" class="button linkButton"/> -->	
	<input type="button" value="save" id="saveCalElem" class="button linkButton"/>
	
	<input type="button" value="cancel" onclick="loadCalMng()" class="button linkButton"/>
</form>
</div>
<script>
$(function() {
    $( "#cngDate0" ).datepicker({minDate: 0});
  });
</script>





<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>

<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>


<script>

function doChkSubmitValid(){
	

	if ($('#frmCalElem').valid()) {
		
		if(!timeDiff()){ return false;}
		
		document.getElementById("newCustActivity").disabled=false;
		}
	
		
}

$(function() {
		$("#cngDate0").inputmask("mm/dd/yyyy", {});
		$('#cngDate0').datepicker({minDate: 0});

		$("#newCustActivity_startTime").inputmask("h:s", {});
		$("#newCustActivity_endTime").inputmask("h:s", {});
		
});

$.validator.addMethod('time', function(value, element, param) {
		return value == '' || value.match(/^([01][0-9]|2[0-3]):[0-5][0-9]$/);
		}, 'Enter a valid time: hh:mm');



$().ready(function() {

		$("#frmCalElem").validate({	
rules: {


cngDate0:{
required:true,
	 minlength:8,
	 date:true
		     }

},
messages: {

newCustActivity_date:{
required: "Please enter valid start date",
	  minlength: "Valid format mm/dd/yyyy"
		     }
	  }
});


});



$('#saveCalElem').click(function() {
		if ($('#frmCalElem').valid()) {
		if(!timeDiff()){ return false;}
		updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>');
		}
		else {
			showError("The form has one or more errors.  Please update and try again.", "#createActivitySection .errorMsg");
		}
		});


function timeDiff(){
	var date= document.getElementById("cngDate0").value;
	
return true;
}
</script> 