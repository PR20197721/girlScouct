<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<div id="newLocationModal">



<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask_custom_extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/alex.js"></script>


<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>
<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/alex/screen.css">

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
	

/*
$.validator.setDefaults({
	submitHandler: function() {
		//alert("submitted!");
		createNewCustActivity();
	}
	
});
*/


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


			
			
<form class="cmxform" id="signupForm">
	
	<h2>Add an Activity</h2>
	<a class="closeText" href="#" onclick="$('#gsModal').dialog('close')">Return to Plan</a>
	<div class="sectionBar">Create a Custom Activity</div>
	<div id="newCustActivity_err" style="color:red;"></div>
	
	
	
        <div class="row">
                <div class="small-6 columns">
			<font color="red">*</font>
			
			 <input type="text" name="newCustActivity_name" id="newCustActivity_name" value="" style="width:200px;" placeholder="Name of Activity" />
		</div>
		
		
                <div class="small-6 columns">
			Date: ex:05/07/2014<input type="text"  id="newCustActivity_date" name="newCustActivity_date" style="width:160px;"/>
                </div>  
                <div class="small-6 columns">
			Start Time: ex: 18:15
			<input type="text" id="newCustActivity_startTime" name="newCustActivity_startTime" value="<%=org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_START_TIME_MIN %>" style="width:100px;" required />
			<select id="newCustActivity_startTime_AP"> <option value="am">am</option> <option value="pm">pm</option></select> </div>  
                <div class="small-6 columns">
			End Time: ex: 09:10<input type="text" id="newCustActivity_endTime" name="newCustActivity_endTime" value="<%=org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_END_TIME_HOUR+":"+org.girlscouts.vtk.models.api.VTKConfig.CALENDAR_END_TIME_MIN %>"  style="width:100px;" required/>
			<select id="newCustActivity_endTime_AP"><option value="am">am</option><option value="pm">pm</option></select>
                </div> 
	</div>
        <div class="row">
                <div class="small-12 columns">
			Location Name <input type="text" id="newCustActivity_locName" value="" style="width:100px;"/>
		</div>
                <div class="small-12 columns">
			Location Address <input type="text" id="newCustActivity_locAddr" value="" style="width:100px;"/>
                </div>
	</div>
        <div class="row">
                <div class="small-16 columns">
			<textarea id="newCustActivity_txt" rows="4" cols="5"  style="width:300px;"></textarea>
                </div>
                <div class="small-8 columns">
                
                
              <div style="background-color:red;">Cost: <input type="text" id="newCustActivity_cost" value=""/></div>
			<input type="button" value="Add Activity" id="newCustActivity" />
			
                </div>
        </div>
       
	 </form>
	 
	 
	 
	 
	 
	 
	 
	 
	 <form>
        <div class="sectionBar">Add activity from the Council Calendar<</div>
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
