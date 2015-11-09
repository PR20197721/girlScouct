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
if( VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, date).toUpperCase().equals("PM")){
	AP="PM";
}
boolean isCancelMeeting= false;
if( meeting != null && meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
	isCancelMeeting=true;
}
java.util.List <MeetingE>meetingsToCancel = meetingUtil.getMeetingToCancel(user, troop);

%>     
 
<h5><strong><%=yearPlanUtil.getMeeting(user, troop, meeting.getRefId() ).getName() %></strong></h5>

<div id="locMsg"></div>

	<div class="modifyCalendarDate clearfix">
		<div class="column large-10 medium-10 small-24">
			<input type="radio" value="change" id="cngRadio" CHECKED name="change" /><label for="cngRadio"><p>Change Date / Time</p></label>
			<form id="frmCalElem">
				<p><strong>Change Date:</strong></p>
				<span>Select today's date or any future date</span>
				<div id="datepicker"></div>
				<input type="hidden" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY,date) %>" id="cngDate0"  name="cngDate0" class="date calendarField" />
				<p><strong>Change Time:</strong></p>
				<section class='row clearfix'>
					<div class="column small-6">
						<input type="text" id="cngTime0" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm,date) %>" name="cngDate0"  />
					</div>
					<div class="columm small-6 left">
						<select id="cngAP0" name="cngAP0" class="ampm">
							<option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>PM</option> 
							<option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>AM</option>
						</select>
					</div>
				</section>
			</form>
			<span  id="cngDate0ErrMsg"></span>
		</div>

		<div class="column small-24 large-10 medium-10">
			<input type="radio" value="cancel" id="cclRadio" name="cancel" /><label for="cclRadio"><p>Cancel Meeting</p></label>
			<p>Select meeting plan you would like to cancel:</p>
			<form id="frmCalElem_1">
				<select id="meeting_select">	
				 	<%
				 	    for(int i=0;i<meetingsToCancel.size();i++) {%>
						 	<option value="<%=meetingsToCancel.get(i).getRefId()%>" <%=meetingsToCancel.get(i).getRefId()==meeting.getRefId()? "SELECTED" : "" %>><%= meetingsToCancel.get(i).getMeetingInfo().getName()%></option>		 				 	   
						<% }
				 	%>	 	
			 	</select>
			 </form>
		</div>
	</div>
	<input type="button" value="save" id="saveCalElem" class="button btn right">
	<div id="dialog-confirm"></div>

<script>
$(function() {
	$( "#datepicker" ).datepicker({
		  defaultDate: new Date ('<%=date%>'),
		  minDate: 0,
		  onSelect: function(dateText, inst) { 
		      var dateAsString = dateText; //the first parameter of this function
		      var dateAsObject = $(this).datepicker( 'getDate' ); //the getDate method
		      
		      document.getElementById("cngDate0").value =dateAsString;
		      
		   }
	});
});
</script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
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
		$('#cclRadio').change(function(){
			if($('#cclRadio').prop('checked')){
				$("#cngRadio").prop('checked', false);
				}
		});
		$('#cngRadio').change(function(){
			if($('#cngRadio').prop('checked')){
				$("#cclRadio").prop('checked', false);
				}
		});

});


$('#saveCalElem').click(function() {

	if($('#cclRadio').prop('checked')){
		   fnOpenNormalDialog();
	}else if($("#cngRadio").prop("checked")){

		if ($('#frmCalElem').valid()) {
		
			if(!timeDiff()){ return false;}
			
			  updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>');
			}
			else {
				showError("The form has one or more errors.  Please update and try again.", "#createActivitySection .errorMsg");
			}

	}
	function timeDiff(){
		return true;
	}
});

if (navigator.userAgent.match(/(msie\ [0-9]{1})/i)[0].split(" ")[1] == 9) {
  $('select').css('background-image', 'none');
}
function fnOpenNormalDialog() {
    $("#dialog-confirm").html("Are you sure you want to cancel the meeting? This will remove the meeting from the calendar and you will have <%=(sched.size()-1)%> meetings instead of <%=sched.size()%> meetings this year.");

    // Define the Dialog and its properties.
    $("#dialog-confirm").dialog({
        resizable: false,
        modal: true,
        title: false,
        height: 250,
        width: 400,
        buttons: {
        "Go ahead, cancel the meeting": function () {
            $(this).dialog('close');
            var r = $("#meeting_select option:selected").val();
            rmMeeting('<%=date.getTime()%>',r);
        },
            "Return to Specify Dates and Locations": function () {
            $(this).dialog('close');
            newLocCal();
        }
        },
        create: function (event, ui) {
        	$(".ui-dialog-titlebar.ui-widget-header").hide();
    		}
    });
}
</script> 
