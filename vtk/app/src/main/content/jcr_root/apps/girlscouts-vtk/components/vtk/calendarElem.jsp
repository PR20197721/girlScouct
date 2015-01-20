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
java.util.List <MeetingE>meetingsToCancel = meetingUtil.getMeetingToCancel(user, troop);

%>     
 
<h5><strong><%=yearPlanUtil.getMeeting(user, meeting.getRefId() ).getName() %></strong></h5>
<div id="locMsg"></div>

<div class="clearfix">
	<div class="modifyCalendarDate">
		<div class="column small-10">
		<input type="radio" value="change" id="cngRadio"><strong> Change Date / Time</strong>
		<form id="frmCalElem">
			<p><strong>Change Date</strong></p>
			<div id="datepicker"></div>
			<input type="hidden" value="<%= FORMAT_MMddYYYY.format(date) %>" id="cngDate0"  name="cngDate0" class="date calendarField"/>
			<p><strong>Change Time</strong></p>
			<section class='row clearfix'>
				<div class="column small-4">
					<input type="text" id="cngTime0" value="<%= FORMAT_hhmm.format(date) %>" name="cngDate0" class="date inline"/>
				</div>
				<div class="columm small-4 left">
					<select id="cngAP0" name="cngAP0" class="ampm">
						<option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>PM</option> 
						<option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>AM</option>
					</select>
				</div>
				</div>
			</section>
		</form>
		<span  id="cngDate0ErrMsg"></span>
	</div>
		<div class="column small-10 push-2">
		 <input type="radio" value="cancel" id="cclRadio"><strong> Cancel Meeting</strong>
		 <span>Select meeting plan you would  like to cancel:</span>
		 <form id="frmCalElem_1">
		 	<select id="meeting_select">	
		 	<%
		 	    for(int i=0;i<meetingsToCancel.size();i++) {%>
				 	<option value="<%=meetingsToCancel.get(i).getRefId()%>" <%=meetingsToCancel.get(i).getRefId()==meeting.getRefId()? "SELECTED" : "" %>><%= meetingsToCancel.get(i).getMeetingInfo().getName()%></option>		 				 	   
				<% }
		 	%>	 	
		 	</select>
			<!--  <input type="checkbox" id="isCancellMeeting0" name="isCancellMeeting0" <%=isCancelMeeting == true ? "CHECKED" : "" %>/>
			 <label for="isCancellMeeting0">Cancel Meeting</label> -->
			 <!--  <input type="button" value="save" onclick="updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>')" class="button linkButton"/> -->	
		<!-- 	 <input type="button" value="cancel" onclick="loadCalMng()" class="button btn"/> -->
		 </form>
		</div>
	</div>
	<input type="button" value="save" id="saveCalElem" class="button btn right"/>
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
	  $( "#datepicker" ).datepicker({
	  	minDate: 0
	  });

		$("#frmCalElem").validate({	
			rules: {
				cngDate0: {
					required:true,
					 minlength:8,
					 date:true
				}
			},
			messages: {
					newCustActivity_date: {
					required: "Please enter valid start date",
					minlength: "Valid format mm/dd/yyyy"
					}
				}
		});
});

$('#saveCalElem').click(function() {
	if($('#cclRadio').prop('checked')){
		var r = $("#meeting_select option:selected").val();
  	  	rmMeeting('<%=date.getTime()%>',r);
	}
	else if($("#cngRadio").prop("checked")){
		if ($('#frmCalElem').valid()) {
			if(!timeDiff()){ return false;}
			  updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>');
			}
			else {
				showError("The form has one or more errors.  Please update and try again.", "#createActivitySection .errorMsg");
			}
	}
});
function timeDiff(){
	var date= document.getElementById("cngDate0").value;
	return true;
}
</script> 
