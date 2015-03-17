function loadMeetings(){
	var url = '/content/girlscouts-vtk/controllers/vtk.meetingInclude.html';
	$("#yearPlanMeetings").load(url,resizeWindow);
}	

function x(planId, planPath, confirmMsg, planName) {	
	console.log(1);
	if( confirmMsg!=null && confirmMsg!='' ){
		console.log(2);
		if( !confirm(confirmMsg) ){
			return;
		}else{
			x1_1(planPath, planName);
		}
    }else{
    	console.log(3);
    	$.ajax({
    		url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=isAltered&isAltered=chk",
    		cache: false
    	}).done(function( html ) {
    		html= $.trim(html)  		
    		if(html=='true'){
    			if( !confirm("Are You Sure? You will lose customizations that you have made") ){
    				return;
    			}else{
    				x1_1(planPath, planName);
    			}
    		}else{
    			x1_1(planPath, planName);
    		}
    	});
    	
    }
	/*
	console.log(4);
	
	$.ajax({
		url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=SelectYearPlan&addYearPlanUser="+planPath+"&addYearPlanName="+ planName,
		cache: false
	}).done(function( html ) {
		//loadMeetings();
		location.reload();
	});
	*/
}

function x1_1(planPath, planName){
	$.ajax({
		url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=SelectYearPlan&addYearPlanUser="+planPath+"&addYearPlanName="+ planName,
		cache: false
	}).done(function( html ) {
		//loadMeetings();
		if( html !=null && $.trim(html)!="" )
			{alert( $.trim(html)); return; }
		location.reload();
	});
}


function testIt(){
	var s =  document.getElementById("sortable");
	s.sortable=true
}

function getNewMeetingSetup() {
	var toRet="";
	var liTags = document.getElementById ("sortable123").getElementsByTagName ("li");
	for (var i = 0; i < liTags.length; i++) {
		if( liTags[i].value !=0)
			toRet+=  liTags[i].value +"," ; 
		
	}
	return toRet.substring(0, toRet.length-1);
}

function doUpdMeeting(newVals){
	//var newVals = getNewMeetingSetup();
	
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=ChangeMeetingPositions&isMeetingCngAjax='+ newVals, // JQuery loads serverside.php
		data: '', // Send value of the clicked button

		dataType: 'html', // Choosing a JSON datatype

	}).done(function( html ) { loadMeetings();});
		
		
}

function reloadMeeting(){
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.meetingInclude.html?isRefresh=true', // JQuery loads serverside.php
		data: '', // Send value of the clicked button
		dataType: 'html', // Choosing a JSON datatype
		success: function (data) { 
		},
		error: function (data) { 
		}
	});
}

function newActivity(){
        loadModalPage('/content/girlscouts-vtk/controllers/vtk.newCustomActivity.html', false, null, true, false);
}

function addExistActivity(activityId){
	$("#addExistActivity_err_"+activityId).load("/content/girlscouts-vtk/controllers/vtk.controller.html?addExistActivity="+activityId);
}

function newLocCal(){
	loadModalPage('/content/girlscouts-vtk/controllers/vtk.locations.html', false, null, true, false);
}

function closeModalPage() {
        try {
                $("#gsModal").dialog( "close");
        } catch (e) {
                console.log(e);
        }
}
function resetModalPage() {
        $("#gsModal").css({overflow: 'inherit'});
}

function loadModalPage(link, showTitle, title, fullPageScroll, print) {
		
	resetModalPage();

        $( "#gsModal" ).load(link, function( response, status, xhr ) {
    	
                if ( status == "error" ) {
               	
                        var msg = "Sorry but there was an error: ";
                        $( "#error" ).html( msg + xhr.status + " " + xhr.statusText );
                }else{
               	
                	loadModal("#gsModal", showTitle, title, fullPageScroll, print);
                }
        });
}

function loadModal(divSelector, showTitle, title, fullPageScroll, print) {
	
	var wWidth = $(window).width();
	var wHeight = $(window).height();
	var dWidth = wWidth * 1; //this will make the dialog 80% of the
	var dHeight = wHeight * 1;
	var tHeight = $(window).height() 
	if (dWidth > 960) {
		dWidth = 960; // max-width: 60em;
	}
	var dialog = null;
	if (print) {
		title += "<span class=\"printIcon\"><a href=\"#\" onclick=\"printDiv('gsModal')\"><img src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/print-icon.png\" width=\"22\" height=\"22\" alt=\"print icon\"/></a></span>";
	}
	if (fullPageScroll) {
		dialog = $( divSelector ).dialog({
			dialogClass: "modalWrap",
			modal: true,
			show: 375,
			minWidth:dWidth,
			maxWidth: dWidth,
			width:dWidth,
			open: function() {
				$('.scroll').css('max-height' , ($(window).height()-75)+'px');
				$("body").css({ overflow: 'hidden' });
				// $(".modalWrap").css({
				// 	'max-height': $(window).height() + 'px !important',
				// 	'height': $(window).height() + 'px !important'
				// 	});
				if (!showTitle) {
					$(".ui-dialog-titlebar").hide();
				} else {
					$("span.ui-dialog-title").html(title);
					$(".ui-dialog-titlebar").show();
				}
					
			},
			close: function() {
				$("body").css({ overflow: 'inherit' });
			}
		});
	} else {
		dialog = $( divSelector ).dialog({
			modal:true,
			dialogClass:"modalWrap",
			show:375,
			open: function() {
				if (!showTitle) {
					$(".ui-dialog-titlebar").hide();
				} else {
					$("span.ui-dialog-title").html(title);
					$(".ui-dialog-titlebar").show();
				}
				$("body").css({ overflow: 'hidden' });
			},
			close: function() {
				$("body").css({ overflow: 'inherit' });
			}
		});
	}
}
function yesPlan(){
  if(document.getElementById('yearPlanMeetings').style.display=='none' ){
    document.getElementById('yearPlanMeetings').style.display='block';
    document.getElementById('yearPlanSelection').style.display='none';  
    document.getElementById('showHideReveal').innerHTML='VIEW YEAR PLAN LIBRARY';
    // document.getElementById('arrowDirection').innerHTML='&#9660;';
    $('#showHideReveal').toggleClass('open').addClass('close');
    $("#empty-yp-directions").show();
  }else{
    document.getElementById('yearPlanMeetings').style.display='none';
    document.getElementById('yearPlanSelection').style.display='block';
    document.getElementById('showHideReveal').innerHTML='YEAR PLAN LIBRARY';
    // document.getElementById('arrowDirection').innerHTML='&#9650;';
    $('#showHideReveal').removeClass('close').addClass('open');
    $("#empty-yp-directions").hide();
  }
}
function addLocation(){
	
	//NEEDS to be BACK vtk-global.js? showError(null, "#locationEdit .errorMsg");	
	var  name = document.getElementById("loc_name").value;
	
	if( $.trim(name) =='' ){
		//showError("Please enter a location", "#locationEdit .errorMsg"); //js missing from VTK2
		alert("Please enter a location");
		return false;
	}
	
	var  address = document.getElementById("loc_address").value;	
	var  city = document.getElementById("loc_city").value;
	var  state = document.getElementById("loc_state").value;
	var  zip = document.getElementById("loc_zip").value;
	
	
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'AddLocation',
			addLocation:true,
			name:name,
			address:address,
			city:city,
			state:state,
			zip:zip,
			a:Date.now()
		},
		success: function(result) {	
			if($.trim(result)!=''){ alert( $.trim(result) ) ;}
			 loadLocMng();
			//document.getElementById("err").innerHtml=result;
			$("#addLocationForm").trigger("reset");
			
		}
	});
}

function updSched(i, meetingPath, currDt){
	var date = document.getElementById("cngDate"+i).value;
	var time = document.getElementById("cngTime"+i).value;
	var ap = document.getElementById("cngAP"+i).value;
	var isCancelled = document.getElementById("isCancellMeeting"+i).checked;
	var urlParam =  "meetingPath="+meetingPath+
		"&date="+date+
		"&time="+time+
		"&ap="+ap+
		"&currDt="+currDt+
		"&isCancelledMeeting="+ isCancelled;

	$( "#locMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?act=UpdateSched&updSched=true&"+urlParam, function( response, status, xhr ) {
		if ( status != "error" ) { }else{ }  
	});

}

function buildSched(){

	var calStartDt = document.getElementById("calStartDt").value;

	var orgDt = "0";
	if( document.getElementById("orgDt")!=null )
		orgDt= document.getElementById("orgDt").value;

	var calAP = document.getElementById("calAP").value;
	var calFreq = document.getElementById("calFreq");
	var z =calFreq.options[calFreq.selectedIndex].text;
	var calTime = document.getElementById("calTime").value;
	if( $.trim(calTime) =='') {alert("Time field empty");return;}
	
	var stringToParse = calStartDt +" " +calTime ;
	var dateString    = stringToParse.match(/\d{2}\/\d{2}\/\d{4}\s+\d{2}:\d{2}/) +" "+calAP;
	var dt            = new Date(dateString);
	
	
	
	if( isNaN(dt) ){
		alert("Invalid date/time");
		return;
	}
	
	
	var minExpDate = new Date();
	minExpDate.setMinutes ( minExpDate.getMinutes() + 30 );
	
	/*
	var maxExpDate = new Date();
	maxExpDate.setDate ( maxExpDate.getDate() +  730);
	
	if( dt > maxExpDate ){
		alert("You cannot select a date after "+moment(maxExpDate).format('MM/DD/YYYY h:mm a')+" since the YP won't fit into the actual calendar year");
		return;
	}
	*/
	
	if( new Date(dt) <= minExpDate )
	{
		
			alert("You cannot select a date in the past to reschedule the meetings. Please type or select a date in the future.");
			return;
	}

	
	
	
	
	var _level="";
	var levels = document.getElementsByName('exclDt');
	for (var i=0; i < levels.length; i++){ 
		if (levels[i].checked)
			_level+= levels[i].value +",";
	}
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'CreateSchedule',
			buildSched:true,
			calStartDt:calStartDt ,
			calAP:calAP,
			calFreq:z,
			calTime:calTime,
			exclDt:_level,
			orgDt:orgDt
		},
		success: function(result) {
			//-loadCalMng();
		//	location.reload();
			location.reload(true);
		}
	});
}

function rmCustActivity(x){
	
	$( "#locMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?act=RemoveCustomActivity&rmCustActivity="+x, function( response, status, xhr ) {
		if ( status != "error" ) {
			location.reload();
		}else{
			alert("Sorry.  Unable to remove activity: " + status);
		}
	});
}

function createNewCustActivity(){	
	var newCustActivity_name = document.getElementById("newCustActivity_name").value;
	if( $.trim(newCustActivity_name) =='' ){alert("Please fill 'Name' field"); return false;}
	var newCustActivity_date = document.getElementById("newCustActivity_date").value;
	
	var aDate = new Date(newCustActivity_date);

	if( !Date.parse(aDate) ||  aDate < new Date() )
		{alert( "Invalid start date. Date must be after todays date." ); return false;}
	
	var newCustActivity_startTime = document.getElementById("newCustActivity_startTime").value;
	var newCustActivity_endTime = document.getElementById("newCustActivity_endTime").value;
	var newCustActivity_txt = document.getElementById("newCustActivity_txt").value;
	var newCustActivityLocName = document.getElementById("newCustActivity_locName").value;
	var newCustActivityLocAddr = document.getElementById("newCustActivity_locAddr").value;
	var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
	var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;
	var newCustActivity_cost = document.getElementById("newCustActivity_cost").value;
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			act:'CreateActivity',
			newCustActivity:true,
			newCustActivity_name:newCustActivity_name ,
			newCustActivity_date:newCustActivity_date ,
			newCustActivity_startTime: newCustActivity_startTime ,
			newCustActivity_endTime:newCustActivity_endTime ,
			newCustActivityLocName: newCustActivityLocName ,
			newCustActivityLocAddr: newCustActivityLocAddr ,
			newCustActivity_txt: newCustActivity_txt,
			newCustActivity_startTime_AP:newCustActivity_startTime_AP,
			newCustActivity_endTime_AP:newCustActivity_endTime_AP,
			newCustActivity_cost:newCustActivity_cost,
			a:Date.now()
		},
		success: function(result) {
			location.reload();
		}
	});
}


function editNewCustActivity(activityUid){
	var newCustActivity_name = document.getElementById("newCustActivity_name").value;
	if( $.trim(newCustActivity_name) =='' ){alert("Please fill 'Name' field"); return false;}
	var newCustActivity_date = document.getElementById("newCustActivity_date").value;
	var newCustActivity_startTime = document.getElementById("newCustActivity_startTime").value;
	var newCustActivity_endTime = document.getElementById("newCustActivity_endTime").value;
	var newCustActivity_txt = document.getElementById("newCustActivity_txt").value;
	var newCustActivityLocName = document.getElementById("newCustActivity_locName").value;
	var newCustActivityLocAddr = document.getElementById("newCustActivity_locAddr").value;
	var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
	var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;
	var newCustActivity_cost = document.getElementById("newCustActivity_cost").value;
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			act:'EditCustActivity',
			editCustActivity:activityUid,
			newCustActivity_name:newCustActivity_name ,
			newCustActivity_date:newCustActivity_date ,
			newCustActivity_startTime: newCustActivity_startTime ,
			newCustActivity_endTime:newCustActivity_endTime ,
			newCustActivityLocName: newCustActivityLocName ,
			newCustActivityLocAddr: newCustActivityLocAddr ,
			newCustActivity_txt: newCustActivity_txt,
			newCustActivity_startTime_AP:newCustActivity_startTime_AP,
			newCustActivity_endTime_AP:newCustActivity_endTime_AP,
			newCustActivity_cost:newCustActivity_cost,
			
			a:Date.now()
		},
		success: function(result) {
			//location.reload();
			//var x= new Date(newCustActivity_date + " "+ newCustActivity_startTime +" "+newCustActivity_startTime_AP);
			//alert(x);
			//-self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem="+new Date(newCustActivity_date + " "+ newCustActivity_startTime +" "+newCustActivity_startTime_AP).getTime(); 
		}
	});
}

function searchActivity(){
	var existActivSFind = document.getElementById("existActivSFind").value;
	var existActivSMon  = document.getElementById("existActivSMon").value;
	var existActivSYr = document.getElementById("existActivSYr").value;
	var existActivSDtFrom = document.getElementById("existActivSDtFrom").value;
	var existActivSDtTo= document.getElementById("existActivSDtTo").value;
	var existActivSReg= document.getElementById("existActivSReg").value;
	var _level="";
	var levels = document.getElementsByName('existActivSLevl');
	for (var i=0; i < levels.length; i++){ 
		if (levels[i].checked)
			_level+= levels[i].value +",";
	}
	var _cat="";
	var levels = document.getElementsByName('existActivSCat');
	for (var i=0; i < levels.length; i++){ 
		if (levels[i].checked)
			_cat+= levels[i].value +",";
	}
	var urlParam="&existActivSFind="+existActivSFind+
		"&existActivSMon="+existActivSMon+
		"&existActivSYr="+existActivSYr+
		"&existActivSDtFrom="+existActivSDtFrom+
		"&existActivSDtTo="+existActivSDtTo+
		"&existActivSReg="+existActivSReg
		"&existActivSLevl="+_level+
		"&existActivSCat="+existActivSCat;
	$("#listExistActivity").load("/content/girlscouts-vtk/controllers/vtk.controller.html?searchExistActivity=true"+ urlParam);
}

$('#plan_hlp_hrf').click(function() {
	$('#plan_hlp').dialog();
	return false;
});

function loadLocMng(){
	$("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand="+Date.now());
}       
function loadCalMng(){
	$("#calMng").load("/content/girlscouts-vtk/controllers/vtk.calendar.html?rand="+Date.now());
	
}

function manageCalElem(elem ){
	$("#calMng").load("/content/girlscouts-vtk/controllers/vtk.calendarElem.html?elem="+ elem + "&rand="+Date.now());
}

function relogin(){
	var elem = document.getElementById("reloginid").value;
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			act:'ReLogin',
			loginAs:elem,
			a:Date.now()
		},
		success: function(result) {
			document.location="/content/girlscouts-vtk/en/vtk.plan.html";
		}
	});
}


function bindAssetToYPC(assetId, ypcId){
	
	var assetDesc = document.getElementById("assetDesc").value;
	 var custasset = document.getElementById("custasset").value;
	 if( $.trim(custasset)=='' ){alert('Please select file to upload');return false;}
	 if( $.trim(assetDesc)=='' ){alert('Please enter name of asset');return false;}
	  $.ajax({
			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
			type: 'POST',
			data: { 
				act:'BindAssetToYPC',
				bindAssetToYPC:assetId,
				ypcId:ypcId,
				assetDesc:assetDesc,
				a:Date.now()
			},
			success: function(result) {
				
			}
		});
	
}


function doMeetingLib(){
	loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html', false, null, true, false);
}

function doHelp(isSched){
	loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html', false, null, true, false);
}


function doEditActivity(x){
	
	loadModal( "#"+x, true, "Edit Activity", false);
}

function printDiv(x) {
        var divToPrint = document.getElementById(x);
        var popupWin = window.open('', '_blank', 'width=300,height=300');
        popupWin.document.open();
        popupWin.document.write('<html><body onload="window.print()">' + divToPrint.innerHTML + '</html>');
        popupWin.document.close();
}
function showAlterYearPlanStartDate(fromDate, mCountUpd){
	// temporary fix until more permanent refactoring
	closeModalPage();
	loadModalPage('/content/girlscouts-vtk/controllers/vtk.locations.html?alterYPStartDate='+ fromDate+'&mCountUpd='+ mCountUpd, false, null, true, false);
}


function expiredcheck(ssId, ypId){
	
		$.ajax({
    		url: "/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid="+ssId+"&ypid="+ypId+"&d=",
    		dataType: 'json',
    		cache: false
    	}).done(function( obj ) {
    		//console.log("**"+html+"**");
    		//var obj = jQuery.parseJSON(html );
    		console.log("/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid="+ssId+"&ypid="+ypId+"&d=");
    		console.log("*** "+ (obj.yp_cng == 'true') );
    		
    		if( obj.yp_cng == 'true'  ){
    			//alert("reloading...");
    		
    			var myUrl = window.location.href;
    			if(window.location.href.indexOf("reload=data") !=-1 ){
    					;
    			}else if( window.location.href.indexOf("?") !=-1){
    				//window.location.replace(window.location.href + "&reload=data")
    				myUrl = window.location.href + "&reload=data";
    			}else{
    				//window.location.replace(window.location.href + "?reload=data")
    				myUrl = window.location.href + "?reload=data";
    		    }
    			//alert(myUrl);
    			//if(true)return;
    			//window.location.reload();
    			window.location.href= myUrl;
    			
    		}
    		setTimeout(function(){ expiredcheck(ssId, ypId);},20000);
    		
    	});
	
		
			
	}
	
	
	
	//tmp need to replace with original
function showError(x,y){}

function rmTroopInfo(){
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			isRmTroopImg:'true',
			a:Date.now()
		},
		success: function(result) {
			location.reload();
		}
	});
	
	
	
}


function rmMeeting( rmDate, mid){
	
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
		type: 'POST',
		data: { 
			act:'RmMeeting',
			rmDate: rmDate,
			mid: mid,			
			a:Date.now()
		},
		success: function(result) {
			location.reload();
		}
	});
}

function councilRpt(troopId, cid){
	console.log( troopId );   
	   
	   $.ajax({
	        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?isAdminRpt=true&cid="+cid+"&ypPath="+troopId ,
	        cache: false
	    }).done(function( html ) {
	    	councilRpt_updateTroopName( html );
	    });
	}
	
	function councilRpt_updateTroopName(input){
		
	
		var lines = input.split('\n');
		var output = '';
		$.each(lines, function(key, line) {
		    var parts = line.split(';');
		    for(var i=0;i<parts.length;i++){
		     output +=  parts[i] + '; \n';
		     }
		});
		
		output = "<script> function test(){" + output +" } test(); </"+""+"script>";
		$(output).appendTo('body');
	
	}

