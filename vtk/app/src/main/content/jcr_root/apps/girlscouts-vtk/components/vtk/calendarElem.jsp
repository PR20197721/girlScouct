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
		<%@include file="include/yearPlanCal.jsp"%>

		<%@include file="include/cancelMeeting.jsp"%>
		
		<%@include file="include/combineMeetings.jsp"%>
		
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
