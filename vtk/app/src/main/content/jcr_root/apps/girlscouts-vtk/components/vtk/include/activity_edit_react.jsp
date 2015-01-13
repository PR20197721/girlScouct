
<%
Activity activity = (Activity)planView.getYearPlanComponent();
%>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>

<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>

<script type="text/javascript"
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript"
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>

<script>
$(function() {
	$( "#newCustActivity_date" ).datepicker({minDate: 0});
  });
  
  
jQuery(function($){
	
	$("#newCustActivity_date").inputmask("mm/dd/yyyy", {});
	
	$('#newCustActivity_date').datepicker({minDate: 0});
	
	$("#newCustActivity_startTime").inputmask("h:s", {});
	$("#newCustActivity_endTime").inputmask("h:s", {});
	$("#newCustActivity_cost").maskMoney({thousands:''});
	});
	
$.validator.addMethod('time', function(value, element, param) {
    return value == '' || value.match(/^([01][0-9]|2[0-3]):[0-5][0-9]$/);
}, 'Enter a valid time: hh:mm');

$.validator.addMethod('currency', function(value, element, regexp) {
    var re = /^\d{1,9}(\.\d{1,2})?$/;
    return this.optional(element) || re.test(value);
}, '');


$().ready(function() {	
	$("#signupForm").validate({	
		rules: {
			newCustActivity_name: {
				required: true,
				minlength: 2
			},
			newCustActivity_startTime:{
				required:true,
				minlength: 5,
				time: true
			},
			newCustActivity_endTime:{
				required:true,
				minlength: 5,
				time: true
			},
			newCustActivity_cost:{
				required:false,
				minlength: 4,
				currency:true
			},
			newCustActivity_date:{
				required:true,
				minlength:8,
				date:true
			}
			
		},
		messages: {
			newCustActivity_name: {
				required: "Please enter a Name",
				minlength: "Your Name must consist of at least 2 characters"
			},
			newCustActivity_startTime:{
				required: "Please enter a Start time",
				minlength: "Valid format HH:mm"
			},
			newCustActivity_endTime:{
				required: "Please enter a End time",
				minlength: "Valid format HH:mm"
			},
			newCustActivity_cost:{
				required: "Please enter a valid amount. Default 0.00",
				minlength: "Valid format 0.00"
			},
			newCustActivity_date:{
				required: "Please enter valid start date",
				minlength: "Valid format MM/dd/yyyy"
			}
		}
	});

 
});

function saveActivity(){
    if ($('#signupForm').valid()) {
    	if(!timeDiff()){ return false;}
    	editNewCustActivity('<%=activity.getUid()%>');
    }
    else {
      alert("The form has one or more errors.  Please update the form and try again.");
    }
	}
	
$('#newCustActivity1').click(function() {
    if ($('#signupForm').valid()) {
    	if(!timeDiff()){ return false;}
    	editNewCustActivity('<%=activity.getUid()%>');
		} else {
			alert("Invalid.Fix it");
		}
	});

	function timeDiff() {
		var date = document.getElementById("newCustActivity_date").value;
		var startTime = document.getElementById("newCustActivity_startTime").value;
		var endTime = document.getElementById("newCustActivity_endTime").value;
		var newCustActivity_startTime_AP = document
				.getElementById("newCustActivity_startTime_AP").value;
		var newCustActivity_endTime_AP = document
				.getElementById("newCustActivity_endTime_AP").value;

		if (!Date.parse(new Date(date + " " + startTime + " "
				+ newCustActivity_startTime_AP))) {
			alert("Invalid Start Date,time. 12hr format: " + date + " "
					+ startTime + " " + newCustActivity_startTime_AP);
			return false;
		}
		if (!Date.parse(new Date(date + " " + endTime + " "
				+ newCustActivity_endTime_AP))) {
			alert("Invalid End Date,time. 12hr format: " + date + " " + endTime
					+ " " + newCustActivity_endTime_AP);
			return false;
		}

		if ((new Date(date + " " + startTime + " "
				+ newCustActivity_startTime_AP) - new Date(date + " " + endTime
				+ " " + newCustActivity_endTime_AP)) >= 0) {
			alert("StartTime after/equal EndTime");
			return false;
		} else
			return true;

	}
	
	function closeMe(){ $('.ui-dialog-content').dialog('close'); }
</script>

<div id="editCustActiv" style="display: none;">
	<form class="cmxform" id="signupForm">
		<div class="sectionBar">Edit Custom Activity</div>
		<div id="newCustActivity_err" style="color: red;"></div>
		<div class="row">
			<div class="large-6 medium-12 small-24 columns">
				<font color="red">*</font> <input type="text"
					name="newCustActivity_name" id="newCustActivity_name"
					value="<%=activity.getName()%>" placeholder="Name of Activity" />
			</div>
			<div class="large-6 medium-12 small-24 columns">
				Date: ex:05/07/2014<input type="text" name="newCustActivity_date"
					id="newCustActivity_date"
					value="<%=FORMAT_MMddYYYY.format(activity.getDate())%>"
					style="width: 160px;" />
			</div>
			<div class="large-6 medium-12 small-24 columns">
				Start Time: <input type="text" name="newCustActivity_startTime"
					id="newCustActivity_startTime"
					value="<%=FORMAT_hhmm_AMPM.format(activity.getDate())%>"
					style="width: 100px;" /> <select id="newCustActivity_startTime_AP">

					<option value="am"
						<%=FORMAT_AMPM.format(activity.getDate()).toUpperCase()
					.trim().equals("AM") ? "SELECTED" : ""%>>am</option>
					<option value="pm"
						<%=FORMAT_AMPM.format(activity.getDate()).toUpperCase()
					.trim().equals("PM") ? "SELECTED" : ""%>>pm</option>
				</select>
			</div>
			<div class="large-6 medium-12 small-24 columns">
				End Time: <input type="text" id="newCustActivity_endTime"
					value="<%=FORMAT_hhmm_AMPM.format(activity.getEndDate())%>"
					style="width: 100px;" /> <select id="newCustActivity_endTime_AP">
					<option value="am"
						<%=FORMAT_AMPM.format(activity.getEndDate()).toUpperCase()
					.trim().equals("AM") ? "SELECTED" : ""%>>am</option>
					<option value="pm"
						<%=FORMAT_AMPM.format(activity.getEndDate()).toUpperCase()
					.trim().equals("PM") ? "SELECTED" : ""%>>pm</option>
				</select>
			</div>
		</div>
		<div class="row">
			<div class="small-24 medium-12 large-12 columns">
				Location Name <input type="text" id="newCustActivity_locName"
					value="<%=activity.getLocationName()%>" style="width: 100px;" />
			</div>
			<div class="small-24 medium-12 large-12 columns">
				Location Address <input type="text" id="newCustActivity_locAddr"
					value="<%=activity.getLocationAddress()%>" style="width: 100px;" />
			</div>
		</div>
		<div class="row">
			<div class="small-24 medium-12 large-12 columns">
				Cost: <input type="text" name="newCustActivity_cost"
					id="newCustActivity_cost"
					value="<%=FORMAT_COST_CENTS.format(activity.getCost())%>" />
			</div>
			<div class="large-12 medium-12 small-24 columns">
				<textarea id="newCustActivity_txt" rows="4" cols="5" style="width: 300px;"><%=activity.getContent()%></textarea>
			</div>
		</div>
		<div class="row">
			<div class="small-24 large-24 medium-24 columns centered-table">
				<input class="button linkButton" type="button" value="Save"
					id="newCustActivity1" onclick="saveActivity()" />
					 <input	class="button linkButton" type="button" value="Cancel" onclick="closeMe()" />
		</div>
		</div>
	</form>
</div>