<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

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
$(function() {
		$( "#newCustActivity_date" ).datepicker({minDate: 0});
		});


jQuery(function($){

		$("#newCustActivity_date").inputmask("mm/dd/yyyy", {});

		$('#newCustActivity_date').datepicker({minDate: 0});

		$("#newCustActivity_startTime").inputmask("h:s", {});
		$("#newCustActivity_endTime").inputmask("h:s", {});
		$("#newCustActivity_cost").maskMoney();
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
required:true,
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



$('#newCustActivity').click(function() {
		if ($('#signupForm').valid()) {
		if(!timeDiff()){ return false;}
		createNewCustActivity();
		}
		else {
		alert("Invalid.Fix it");
		}
		});


function timeDiff(){
	var date= document.getElementById("newCustActivity_date").value;
	var startTime = document.getElementById("newCustActivity_startTime").value;
	var endTime = document.getElementById("newCustActivity_endTime").value;
	var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
	var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;


	if(!Date.parse( new Date( date +" " + startTime +" "+newCustActivity_startTime_AP) )) {alert("Invalid Start Date,time. 12hr format: "+date +" " + startTime +" "+newCustActivity_startTime_AP);return false;}
	if(!Date.parse( new Date( date +" " + endTime +" "+newCustActivity_endTime_AP) )) {alert("Invalid End Date,time. 12hr format: "+date +" " + endTime +" "+newCustActivity_endTime_AP);return false;}



	if( (new Date(date +" "+ startTime+ " "+newCustActivity_startTime_AP) - new Date( date +" " + endTime +" "+newCustActivity_endTime_AP) ) >=0 )
	{alert("StartTime after/equal EndTime"); return false;}
	else 
		return true;

}
</script>  

<style>



.ui-datepicker .ui-datepicker-prev,
	.ui-datepicker .ui-datepicker-next {

border:5px solid #000;
	}

</style>


<div class="row modalHeader">
<%
        boolean isWarning=false;
        String instruction = "Add an Activity";
        if (isWarning) {
%>
        <div class="small-2 columns">
                <div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
        </div>
        <div class="small-20 columns">
<%
        } else {
%>
        <div class="small-22 columns">
<%
        }
%>
                <span class="instruction"><%= instruction %></span>

        </div>
        <div class="small-2 columns">
                <a class="right" href="#" onclick="$('#gsModal').dialog('close')"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
        </div>
</div>
<div class="row modalNav">
        <div id="createActivityTab" class="small-12 columns active">
                <a href="#" onclick="toggleSection('create')">Create Activity</a>
        </div>
        <div id="pickActivityTab" class="small-12 columns">
                <a href="#" onclick="toggleSection('pick')">Pick Activity</a>
        </div>
</div>
<div class="row modalBody">
        <div class="small-24 columns">
                <div id="createActivitySection">
<form class="cmxform" id="signupForm">

<div class="sectionBar">Create a Custom Activity</div>
<div id="newCustActivity_err" style="color:red;"></div>



<div class="row">
        <div class="small-6 columns">
                <font color="red">*</font><input type="text" name="newCustActivity_name" id="newCustActivity_name" value="" placeholder="Name of Activity" />
        </div>
        <div class="small-6 columns"> Date: ex:05/07/2014<input type="text"  id="newCustActivity_date" name="newCustActivity_date" /> </div>
        <div class="small-6 columns">
                Start Time
                <input type="text" id="newCustActivity_startTime" name="newCustActivity_startTime" value="<%=org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN %>" required />
                <select id="newCustActivity_startTime_AP"> <option value="am">am</option> <option value="pm">pm</option></select> </div>
                <div class="small-6 columns">
                End Time<input type="text" id="newCustActivity_endTime" name="newCustActivity_endTime" value="<%=org.girlscouts.vtk.models.VTKConfig.CALENDAR_END_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_END_TIME_MIN %>"  required/>
                <select id="newCustActivity_endTime_AP"><option value="am">am</option><option value="pm">pm</option></select>
        </div>
</div>

<div class="row">
        <div class="small-12 columns"> Location Name <input type="text" id="newCustActivity_locName" value="" /> </div>
        <div class="small-12 columns"> Location Address <input type="text" id="newCustActivity_locAddr" value="" /> </div>
</div>
<div class="row">
        <div class="small-24 columns"> Description <textarea id="newCustActivity_txt" rows="4" cols="5" ></textarea> </div>
</div>
<div class="row">
        <div class="small-12 columns">
                Cost <input type="text" id="newCustActivity_cost" value=""/>
	</div>
        <div class="small-12 columns">
                <input type="button" value="Add Activity" id="newCustActivity" />
        </div>
</div>
</form>

                </div>
                <div id="pickActiviySection">
<form>
<div class="sectionBar">Add activity from the Council Calendar</div>
<p>Find Activity by:</p>
<input type="text" value="" id="existActivSFind"/>
<br/>Month and Year
<select id="existActivSMon"><option value="01">Jan</option> <option value="02">Feb</option></select>
<select id="existActivSYr"><option value="2014">2014</option> <option value="2015">2015</option></select>
<br/>Date Range
<input type="text" id="existActivSDtFrom" />
<input type="text"  id="existActivSDtTo"  />

<br/>Region
<select id="existActivSReg"><option value="region1">Region1</select>

<br/>Program Level
<input type="checkbox" value="1" name="existActivSLevl"/>1
<input type="checkbox" value="2" name="existActivSLevl"/>2
<input type="checkbox" value="3" name="existActivSLevl"/>3
<input type="checkbox" value="4" name="existActivSLevl"/>4
<input type="checkbox" value="5" name="existActivSLevl"/>5
<input type="checkbox" value="6" name="existActivSLevl"/>6

<br/>Categories
<input type="checkbox" value="1" name="existActivSCat"/>1
<input type="checkbox" value="2" name="existActivSCat"/>2
<input type="checkbox" value="3" name="existActivSCat"/>3
<input type="checkbox" value="4" name="existActivSCat"/>4
<input type="checkbox" value="5" name="existActivSCat"/>5
<input type="checkbox" value="6" name="existActivSCat"/>6

<br/><input type="button" value="View Activity" onclick="searchActivity()" />

<div id="listExistActivity"></div>


</form>
                </div>
        </div>
</div>

<script>
        function toggleSection(section) {
                $("#createActivityTab").removeClass("active");
                $("#pickActivityTab").removeClass("active");
                $("#createActivitySection").hide();
                $("#pickActiviySection").hide();
                if (section == "pick") {
                        $("#pickActivityTab").addClass("active");
                        $("#pickActiviySection").show();
                } else if (section == "create")  {
                        $("#createActivityTab").addClass("active");
                        $("#createActivitySection").show();
                }
        }
</script>
