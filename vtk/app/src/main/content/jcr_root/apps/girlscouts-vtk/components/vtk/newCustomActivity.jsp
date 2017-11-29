<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<script>

function disabledButton(boolean){
  if(boolean){
    $('#view_activities_button').addClass('inactive-button');
  }else{
    $('#view_activities_button').removeClass('inactive-button');
     
  }
}






function _checkInput(){
  return $('#sch_keyword').val().length >= 3;
}


function _checkCalendar(){

  return $('#sch_startDate').val() &&  $('#sch_endDate').val();
  // if($('#sch_startDate').val() &&  $('#sch_endDate').val()){
  //   disabledButton(false);
  // }else{

  //   disabledButton(true)
  // }
}


function inputLogic(){

  console.log((_checkInput() && !_checkCalendar()),(_checkCalendar() && _checkInput()))
  return (_checkInput() && !_checkCalendar()) || (_checkCalendar() && _checkInput()) || (($('#sch_keyword').val().length == 0 || $('#sch_keyword').val() == "") && _checkCalendar()) ; 
}




  function doChkSubmitValid(){

  	
  	//var x= document.getElementById("newCustActivity_date").value;
  	//var y =document.getElementById("newCustActivity_name").value;
  	
  	if ($('#signupForm').valid()) {


  		if(!timeDiff()){ return false;}
  		
  		document.getElementById("newCustActivity").disabled=false;
  		}
  	/*
  	if( $.trim(x) == '' || $.trim(y) =='' ){
  		
  		document.getElementById("newCustActivity").disabled=true;
  	}else{
  		
  		document.getElementById("newCustActivity").disabled=false;
  	}
  	*/
  		
  }

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
    newCustActivity_locName:{
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
    newCustActivity_locName: {
    		    	  required: "Please enter a Location Name",
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
  			showError("The form has one or more errors.  Please update and try again.", "#createActivitySection .errorMsg");
  		}
  });

  function timeDiff(){
  	var date= document.getElementById("newCustActivity_date").value;
  	var startTime = document.getElementById("newCustActivity_startTime").value;
  	var endTime = document.getElementById("newCustActivity_endTime").value;
  	var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
  	var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;
  	var locName= document.getElementById("newCustActivity_locName").value;

  	if( $.trim(locName) == '' ){
  		var thisMsg="Missing Location Name";
  		showError(thisMsg, "#pickActivitySection .errorMsg");
  		return false;
  	}
  		
  	
  	if(!Date.parse( new Date( date +" " + startTime +" "+newCustActivity_startTime_AP) )) {
  		var thisMsg = "Invalid Start Date,time. 12hr format: "+date +" " + startTime +" "+newCustActivity_startTime_AP;
  		showError(thisMsg, "#pickActivitySection .errorMsg");
  		return false;
  	}
  	if(!Date.parse( new Date( date +" " + endTime +" "+newCustActivity_endTime_AP) )) {
  		var thisMsg = "Invalid End Date,time. 12hr format: "+date +" " + endTime +" "+newCustActivity_endTime_AP;
                  showError(thisMsg, "#pickActivitySection .errorMsg");
  		return false;
  	}



  	if( (new Date(date +" "+ startTime+ " "+newCustActivity_startTime_AP) - new Date( date +" " + endTime +" "+newCustActivity_endTime_AP) ) >=0 ) {
  		var thisMsg = "StartTime after/equal EndTime";
                  showError(thisMsg, "#pickActivitySection .errorMsg");
  		return false;
  	} else {
  		return true;
  	}
  }
</script>  

<div class="header clearfix">
  <%
  boolean isWarning = false;
  String instruction = "Add an Activity";
  if (isWarning) {
  %>
    <span class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></span>
  <% } %>
  <h3 class="columns small-21"><%= instruction %></h3>
  <a class="close-reveal-modal columns small-3" onclick="closeModalPage()"  ><i class="icon-button-circle-cross"></i></a>
</div>

<div class="tabs-wrapper scroll">
  <dl class="tabs" data-tab>
    <dd id="createActivityTab" class="active manageCalendarTab"><a href="#" onclick="toggleSection('create')">Custom Activity</a></dd>
    <dd id="pickActivityTab" class="manageCalendarTab" ><a href="#" onclick="toggleSection('pick')">Council Activity</a></dd>
  </dl>
  <div class="modalBody tabs-content">
    <div class="row">
      <div class="small-24 medium-24 large-24 columns">
        <div id="createActivitySection">
          <form class="cmxform" id="signupForm">
         <!--    <div class="sectionBar">Create a Custom Activity</div> -->
            <div class="errorMsg error"></div>

            <div class="row">
              <div class="small-24 large-12 medium-12 columns">
                <input type="text" placeholder="Activity Name" name="newCustActivity_name" id="newCustActivity_name" value="" onchange="doChkSubmitValid()"/>
                <!-- <span style="color:red;">*</span> -->
              </div>
              <div class="small-21 large-3 medium-3 columns date">
                <input type="text" id="newCustActivity_date" name="newCustActivity_date" placeholder="mm/dd/yyyy" class="date calendarField" onchange="doChkSubmitValid()"/><!-- <span style="color:red;">*</span> -->
              </div>
              <div class="large-1 columns medium-1 small-3 date">
                <label for="newCustActivity_date"><i class="icon-calendar"></i></label>
              </div>
              <div class="small-16 medium-2 large-2 columns">
                <input type="text" id="newCustActivity_startTime" placeholder="Start Time" name="newCustActivity_startTime" value="<%=org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN %>" required class="time" />
                <!-- <span style="color:red;">*</span> -->
              </div>
              <div class="small-8 medium-2 large-2 columns">
                <select id="newCustActivity_startTime_AP" class="ampm">
                  <option value="pm">PM</option>
                  <option value="am">AM</option>
                </select>
              </div>
              <div class="small-16 medium-2 large-2 columns">
                <input type="text" placeholder="End Time" id="newCustActivity_endTime" name="newCustActivity_endTime" value="<%=org.girlscouts.vtk.models.VTKConfig.CALENDAR_END_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_END_TIME_MIN %>"  required class="time"/>
                <!-- <span style="color:red;">*</span> -->
              </div>
              <div class="small-8 medium-2 large-2 columns">
                <select id="newCustActivity_endTime_AP" class="ampm">
                  <option value="pm">PM</option>
                  <option value="am">AM</option>
                </select>
              </div>
            </div><!--/row-->

            <div class="row">
            
              <div class="small-24 medium-12 large-12 columns">
                <input type="text" name="newCustActivity_locName" id="newCustActivity_locName" placeholder="Location Name" value="" onchange="doChkSubmitValid()"/>
              <!--   <span style="color:red;">*</span> -->
              </div>
              <div class="small-24 medium-12 large-12 columns"><input type="text" id="newCustActivity_locAddr" value="" placeholder="Location Address" /></div>
            </div><!--/row-->

            <div class="row">
              <div class="small-24 medium-12 large-12 columns"><input type="text" id="newCustActivity_cost" value="" placeholder="Cost" /></div>
              <div class="small-24 medium-12 large-12 columns">
                <textarea id="newCustActivity_txt" rows="4" cols="5" placeholder="Activity Description"></textarea>
              </div>
            </div><!--/row-->

            <input class="button right" type="button" value="Add Activity" id="newCustActivity"  disabled/>
          </form>
        </div><!--/create activity-->

        <div id="pickActivitySection">
          <form id="schFrm">
            <!-- <div class="sectionBar" id="activitySearchLabel">Add activity from the Council Calendar</div> -->
            <div class="errorMsg error"></div>
              <%
              SearchTag search = yearPlanUtil.searchA(user, troop,  ""+troop.getTroop().getCouncilCode());
              java.util.Map<String, String> levels = search.getLevels();
              java.util.Map<String, String> categories =search.getCategories();
              java.util.Map<String, String> region =search.getRegion();
              %>
            <div class="row">

              <div class="small-24 medium-12 large-12 columns">
                <label for="sch_keyword" ACCESSKEY="f">Find Activity by:</label>
                <div class="looking-glass"><input type="text" id="sch_keyword" placeholder="Keywords" value="" onKeyUp="return submitenter(this,event)" /></div>
              </div>

              <!-- <div class="small-24 medium-6 large-6 columns">
                <label for="sch_region" ACCESSKEY="g">Region</label>
                <select type="hidden" id="sch_region">
                  <option value="">Select Region</option>
                  <% java.util.Iterator itr2= region.keySet().iterator();
                  while( itr2.hasNext() ){
                  String str=(String) itr2.next();
                  %>
                  <option value="<%= str %>"><%= str %></option>
                  <% } %>
                </select>
              </div> -->
              <div class="columns large-12 medium-12 small-24 end date">
                <label id="dateTitle" ACCESSKEY="r">Date</label>
                <div class="small-21 large-9 medium-9 columns">
                  <input type="text" id="sch_startDate" onchange="checkCalendar"  value="" placeholder="From" class="date calendarField"/>
                </div>
                <div class="large-3 columns medium-3 small-3">
                  <label for="sch_startDate"><i class="icon-calendar"></i></label>
                </div>
                <div class="small-21 large-9 medium-9 columns">
                  <input type="text" id="sch_endDate" onchange="checkCalendar" value="" placeholder="To" class="date calendarField"/>
                </div>
                <div class="large-3 columns medium-3 small-3">
                  <label for="sch_endDate"><i class="icon-calendar"></i></label>
                </div>
              </div>
            </div>
            <p id ="dateErrorBox"></p>

            <div class="row">
              <div class="columns small-24">
                <label for="sch_lvl" ACCESSKEY="p">Program Level</label>
                <ul class="small-block-grid-2 large-block-grid-6 medium-block-grid-3 formCheckboxes">
                  <% java.util.Iterator itr1= levels.keySet().iterator();
                    int i=0;
                    while( itr1.hasNext() ){
                    i++;
                    String str=(String) itr1.next();
                    %>
                    <li><input type="checkbox" name="sch_lvl" id="sch_lvl_<%=i %>" value="<%= str %>"/>
                    <label for="sch_lvl_<%=i %>"><p><span><%= levels.get(str) %></span></p></label></li>
                  <% } %>
                </ul>
              </div>
            </div>

            <div class="row">
              <div class="columns small-24">
                <label for="sch_cats" ACCESSKEY="i">Categories</label>
                <ul class="small-block-grid-2 large-block-grid-6 medium-block-grid-3 formCheckboxes">
                  <% java.util.Iterator itr= categories.keySet().iterator();
                  i=0;
                  while( itr.hasNext() ){
                  i++;
                  String str=(String) itr.next();
                  %>
                  <li>
                    <input type="checkbox" name="sch_cats" id="sch_cats_<%=i %>" value="<%= str %>"/>
                    <label class="tty" for="sch_cats_<%=i %>"><p><span><%= categories.get(str) %></span></p></label>
                  </li>
                  <% } %>
                <ul>
              </ul>
            
            <%if(apiConfig.isDemoUser()){%>  
              <p style="color:orange;text-align: right; width:100%">Council activities are not available in the demo at this time.</p>
            <%}%>
            
            </div>

            <input id="view_activities_button" type="button" value="View Activities" onclick='searchActivities()' class="button btn right inactive-button"/>

            <div id="searchResults"></div>
          </form>
        </div><!--/pickActivitySection-->
      </div><!--/small-24-->
    </row>
  </div><!--/modalBody-->
</div><!--/tabs-wrapper-->
<script>
 $( "#newCustActivity_date" ).datepicker();
  function toggleSection(section) {
    $("#createActivityTab").removeClass("active");
    $("#pickActivityTab").removeClass("active");
    $("#createActivitySection").hide();
    $("#pickActivitySection").hide();
    if (section == "pick") {
            $("#pickActivityTab").addClass("active");
            $("#pickActivitySection").show();
    } else if (section == "create")  {
            $("#createActivityTab").addClass("active");
            $("#createActivitySection").show();
    }
}
function submitenter(myfield,e) {
  // debugger;
	var keycode;

disabledButton(!inputLogic());
	
  if (window.event) {
		keycode = window.event.keyCode;
	} else if (e) {
		keycode = e.which;
	} else {
		return true;
	}

	if (keycode == 13) {
		searchActivities();
		return false;
	} else {
		return true;
	}
}

  $('#sch_startDate').datepicker({minDate: 0,
  onSelect: function(){
    disabledButton(!inputLogic());
  },
  beforeShowDay: function(d) {
      if($('#sch_endDate').val() == "" || $('#sch_endDate').val() == undefined){
  		return [true, "","Available"]; 
      }

      var dateString = (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear();


      if(+(new Date(dateString)) <= +(new Date($('#sch_endDate').val()))){
  		return [true, "","Available"];
      }

  	return [false, "","unAvailable"]; 

      }});
  $('#sch_endDate').datepicker({minDate: 0,
     onSelect: function(){
    disabledButton(!inputLogic());
  },
  beforeShowDay: function(d) {


    if($('#sch_startDate').val() == "" || $('#sch_startDate').val() == undefined){
		return [true, "","Available"]; 
    }

    var dateString = (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear();



    if(+(new Date(dateString)) >= +(new Date($('#sch_startDate').val()))){
		return [true, "","Available"];
    }

	return [false, "","unAvailable"]; 

    }});

function checkAll(x) {
		var container ="";
	   var arrMarkMail = document.getElementsByName(x);
	   for (var i = 0; i < arrMarkMail.length; i++) {
	     if(arrMarkMail[i].checked)
	    	 container += arrMarkMail[i].value +"|";
	   }
	   return container;  
}

function searchActivities() {
	showError(null, "#pickActivitySection .errorMsg");
	var  keywrd = $.trim(document.getElementById("sch_keyword").value);
	if( keywrd.length>0 && keywrd.length<3  ){
		var thisMsg = "Min 3 character search for keyword: "+ keywrd;
                showError(thisMsg, "#pickActivitySection .errorMsg");
		return false;
	}
	
	var lvl=  $.trim(checkAll('sch_lvl'));
	var cat=  $.trim(checkAll('sch_cats'));
	var startDate = $.trim(document.getElementById("sch_startDate").value);
	var endDate = $.trim(document.getElementById("sch_endDate").value);
	// var region = document.getElementById("sch_region").value;
	
	
	
	if(!isDate(startDate) && startDate != ''){setError("Invalid Start Date");return false;}
    if(!isDate(endDate) && endDate!=''){setError("Invalid End Date");return false;}
	
	if( startDate != '' && endDate=='' ) {
		
        setError("Missing End Date");

		return false; 
	}
	if( startDate =='' && endDate!='' ) {
		
        setError("Missing Start Date");
		return false;
	}
    if( new Date(startDate) > new Date(endDate) ) {
		
        setError("The End Date cannot be less than Start Date"); 
		return false;
	}
	if( keywrd=='' && lvl=='' && cat =='' && startDate=='' && endDate=='' /*&& region==''*/ ){
		var thisMsg = "Please select search criteria.";
                showError(thisMsg, "#pickActivitySection .errorMsg");
		return false;
	}
	
    document.getElementById("dateTitle").style.fontWeight = "normal";
    document.getElementById("dateTitle").style.color = "#FF0000";
    document.getElementById("dateErrorBox").innerHTML = "";
    document.getElementById("dateTitle").style.color = "";
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'Search',
			srch:true,
			keywrd:keywrd,
			lvl:lvl,
			cat:cat,
			startDate:startDate,
			endDate:endDate,
			region:'',
			a:Date.now()
		},
		success: function(result) {
			$("#searchResults").load('/content/girlscouts-vtk/controllers/vtk.searchActivity.html');
			vtkTrackerPushAction('SearchActivities');
		}
	});
}

function setError(errorMessage) {
  document.getElementById("dateErrorBox").innerHTML = errorMessage;
  document.getElementById("dateTitle").style.color = "#FF0000";
  document.getElementById("dateErrorBox").style.color = "#FF0000";
  document.getElementById("dateErrorBox").style.fontSize = "small";
  document.getElementById("dateErrorBox").style.fontWeight = "bold";
  document.getElementById("dateTitle").style.fontWeight = "bold";
  document.getElementById("activitySearchLabel").scrollIntoView();
}


function isDate(txtDate) {
    var currVal = txtDate;
    if(currVal == '')
        return false;
    
    var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/; //Declare Regex
    var dtArray = currVal.match(rxDatePattern); // is format OK?
    
    if (dtArray == null) 
        return false;
    
    //Checks for mm/dd/yyyy format.
    dtMonth = dtArray[1];
    dtDay= dtArray[3];
    dtYear = dtArray[5];        
    
    if (dtMonth < 1 || dtMonth > 12) 
        return false;
    else if (dtDay < 1 || dtDay> 31) 
        return false;
    else if ((dtMonth==4 || dtMonth==6 || dtMonth==9 || dtMonth==11) && dtDay ==31) 
        return false;
    else if (dtMonth == 2) 
    {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay> 29 || (dtDay ==29 && !isleap)) 
                return false;
    }
    return true;
}
</script>
