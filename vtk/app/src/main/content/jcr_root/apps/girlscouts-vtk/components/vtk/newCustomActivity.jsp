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
	  minlength: "Valid format mm/dd/yyyy"
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
        <div class="small-4 medium-2 large-2 columns">
                <div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
        </div>
        <div class="small-16 medium-20 large-20 columns">
<%
        } else {
%>
        <div class="small-20 medium-22 large-22 columns">
<%
        }
%>
                <span class="instruction"><%= instruction %></span>

        </div>
        <div class="small-4 medium-2 large-2 columns">
                <a class="right" href="#" onclick="$('#gsModal').dialog('close')"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/close-small.png" width="20" height="20" border="0" align="right"></a>
        </div>
</div>
<div class="row modalNav">
        <ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2 specifyDates">
                <li id="createActivityTab" class="active manageCalendarTab"><a href="#" onclick="toggleSection('create')">Custom Activity</a></li>
                <li id="pickActivityTab" class="manageCalendarTab"><a href="#" onclick="toggleSection('pick')">Council Activity</a></li>
        </ul>
</div>
<div class="row modalBody">
        <div class="small-24 columns">
                <div id="createActivitySection">
<form class="cmxform" id="signupForm">
<div class="sectionBar">Create a Custom Activity</div>
<div id="newCustActivity_err" style="color:red;"></div>
<div class="row">
        <div class="small-24 medium-5 large-4 columns"><label for="newCustActivity_name" ACCESSKEY="n">Activity Name</label></div>
        <div class="small-24 medium-7 large-8 columns"><input type="text" name="newCustActivity_name" id="newCustActivity_name" value=""/></div>
        <div class="small-24 medium-5 large-4 columns"><label for="newCustActivity_date" ACCESSKEY="d">Date</label></div>
        <div class="small-24 medium-7 large-8 columns">
		<input type="text" id="newCustActivity_date" name="newCustActivity_date" placeholder="mm/dd/yyyy" class="date calendarField"/>
	</div>
</div>
<div class="row">
	<div class="small-24 medium-5 large-4 columns"><label for="newCustActivity_startTime" ACCESSKEY="1">Start Time</label></div>
	<div class="small-16 medium-4 large-4 columns">
		<input type="text" id="newCustActivity_startTime" name="newCustActivity_startTime" value="<%=org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN %>" required class="time" />
	</div>
        <div class="small-8 medium-3 large-4 columns">
                <select id="newCustActivity_startTime_AP" class="ampm">
                        <option value="pm">PM</option>
			<option value="am">AM</option>
		</select>
	</div>
	<div class="small-24 medium-5 large-4 columns"><label for="newCustActivity_endTime" ACCESSKEY="2">End Time</label></div>
	<div class="small-16 medium-4 large-4 columns">
		<input type="text" id="newCustActivity_endTime" name="newCustActivity_endTime" value="<%=org.girlscouts.vtk.models.VTKConfig.CALENDAR_END_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_END_TIME_MIN %>"  required class="time"/>
	</div>
        <div class="small-8 medium-3 large-4 columns">
                <select id="newCustActivity_endTime_AP" class="ampm">
                        <option value="pm">PM</option>
			<option value="am">AM</option>
		</select>
	</div>
</div>
<div class="row">
        <div class="small-24 medium-6 large-4 columns"><label for="newCustActivity_locName" ACCESSKEY="l">Location Name</label></div>
        <div class="small-24 medium-6 large-8 columns"><input type="text" id="newCustActivity_locName" value="" /></div>
        <div class="small-24 medium-6 large-4 columns"><label for="newCustActivity_locAddr" ACCESSKEY="a">Location Address</label></div>
        <div class="small-24 medium-6 large-8 columns"><input type="text" id="newCustActivity_locAddr" value="" /></div>
</div>
<div class="row">
        <div class="small-24 medium-5 large-4 columns"><label for="newCustActivity_cost" ACCESSKEY="c">Cost</label></div>
        <div class="small-24 medium-7 large-8 columns"><input type="text" id="newCustActivity_cost" value=""/></div>
        <div class="hide-for-small medium-12 large-12 columns">&nbsp;</div>
</div>
<div class="row">
        <div class="small-24 medium-5 large-4 columns"><label for="newCustActivity_txt" ACCESSKEY="t">Description</label></div>
        <div class="small-24 medium-19 large-20 columns"><textarea id="newCustActivity_txt" rows="4" cols="5" ></textarea></div>
</div>
<br/><br/><input type="button" value="Add Activity" id="newCustActivity" />
</form>

                </div>
                <div id="pickActiviySection">
<form id="schFrm">
<div class="sectionBar">Add activity from the Council Calendar</div>

<%

SearchTag search = meetingDAO.searchA();
java.util.Map<String, String> levels = search.getLevels();
java.util.Map<String, String> categories =search.getCategories();
java.util.Map<String, String> region =search.getRegion();

%>
<form>
<div class="row">
        <div class="small-24 medium-6 large-6 columns"><label for="sch_keyword" ACCESSKEY="f">Find Activity by</label></div>
        <div class="small-24 medium-18 large-18 columns"><input type="text" id="sch_keyword" value="" onKeyPress="return submitenter(this,event)"/></div>
</div>
<div class="row">
        <div class="small-12 medium-6 large-6 columns"><label for="sch_startDate" ACCESSKEY="r">Date Range</label></div>
        <div class="small-6 medium-6 large-6 columns"><input type="text" id="sch_startDate"  value="" class="date calendarField"/></div>
        <div class="small-6 medium-6 large-6 columns"><input type="text" id="sch_endDate"  value="" class="date calendarField"/></div>
        <div class="hide-for-small medium-6 large-6 columns">&nbsp;</div>
</div>
<div class="row">
        <div class="small-24 medium-8 large-6 columns"><label for="sch_region" ACCESSKEY="g">Region</label></div>
        <div class="small-24 medium-16 large-18 columns">
		<select id="sch_region">
			<option value="">Select Region</option>
<% java.util.Iterator itr2= region.keySet().iterator();
	while( itr2.hasNext() ){
		String str=(String) itr2.next();
%>
	<option value="<%= str %>"><%= str %></option>
<% } %>
		</select>
	</div>
</div>
<br/>
<div class="row">
        <div class="small-24 medium-8 large-6 columns"><label for="sch_lvl" ACCESSKEY="p">Program Level</label></div>
        <div class="small-24 medium-16 large-18 columns">
        <ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-3 formCheckboxes">

		<% java.util.Iterator itr1= levels.keySet().iterator();
int i=0;
while( itr1.hasNext() ){
i++;
String str=(String) itr1.next();
%>
	<li><input type="checkbox" name="sch_lvl" id="sch_lvl_<%=i %>" value="<%= str %>"/>&nbsp;<label for="sch_lvl_<%=i %>"><%= str %></label></li>
<% } %>
	</ul>
        </div>  
</div>
<br/>
<div class="row">
        <div class="small-24 medium-8 large-6 columns"><label for="sch_cats" ACCESSKEY="i">Categories</label></div>
        <div class="small-24 medium-16 large-18 columns">
        <ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-3 formCheckboxes">
		<% java.util.Iterator itr= categories.keySet().iterator();
i=0;
	while( itr.hasNext() ){
i++;
		String str=(String) itr.next();
%>
        <li><input type="checkbox" name="sch_cats" id="sch_cats_<%=i %>" value="<%= str %>"/>&nbsp;<label for="sch_cats_<%=i %>"><%= str %></label></li>
<% } %>
	<ul>
        </div>
</div>

<br/><input type="button" value="View Activities" onclick='src11()' />
</form>

<div id="searchResults"></div>
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
function submitenter(myfield,e) {
	var keycode;
	if (window.event) {
		keycode = window.event.keyCode;
	} else if (e) {
		keycode = e.which;
	} else {
		return true;
	}

	if (keycode == 13) {
		src11();
		return false;
	} else {
		return true;
	}
}

$('#sch_startDate').datepicker({minDate: 0});
$('#sch_endDate').datepicker({minDate: 0});

function checkAll(x) {
	
		var container ="";
	   var arrMarkMail = document.getElementsByName(x);
	   for (var i = 0; i < arrMarkMail.length; i++) {
	     if(arrMarkMail[i].checked)
	    	 container += arrMarkMail[i].value +"|";
	   }
	   return container;
	   
	}
	
	
	
function src11(){
	
	var  keywrd = $.trim(document.getElementById("sch_keyword").value);
	if( keywrd.length>0 && keywrd.length<3  ){alert("Min 3 character search for keyword: "+ keywrd);return false;}
	
	var lvl=  $.trim(checkAll('sch_lvl'));
	var cat=  $.trim(checkAll('sch_cats'));
	var startDate = $.trim(document.getElementById("sch_startDate").value);
	var endDate = $.trim(document.getElementById("sch_endDate").value);
	var region = $.trim(document.getElementById("sch_region").value);
	
	if( startDate != '' && endDate=='' )
		{alert('Missing end date');return false; }
	if( startDate =='' && endDate!='' )
		{alert('Missing start date');return false;}
	
	if( keywrd=='' && lvl=='' && cat =='' && startDate=='' && endDate=='' && region=='' ){
		alert("Please select search criteria.");
		return false;
	}
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			srch:true,
			keywrd:keywrd,
			lvl:lvl,
			cat:cat,
			startDate:startDate,
			endDate:endDate,
			region:region,
			a:Date.now()
		},
		success: function(result) {
			$("#searchResults").load('/content/girlscouts-vtk/controllers/vtk.searchActivity.html');
		}
	});
	
}
</script>
