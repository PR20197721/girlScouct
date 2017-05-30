function rmCustActivity12(x){
	
	
	 var r = confirm("You are about to delete activity!");
	    if (r != true) {
	      return;
	    }

	$.ajax({
		cache: false,
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
		type: 'POST',
		data: { 
			act:'RemoveCustomActivity',
			rmCustActivity:x,
			a:Date.now()
		},
		success: function(result) {
			
			vtkTrackerPushAction('RemoveCustomActivity');
			document.location="/content/girlscouts-vtk/en/vtk.plan.html";
		}
	});
	//document.location="/content/girlscouts-vtk/en/vtk.plan.html";
}

function viewMeetingLibrary(meetingPath,x){
	$( "#meetingLibraryView" ).load( "/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath="+meetingPath+"&xx="+x,function( html ) {
		$(function() {
                        $( "#meetingLibraryView" ).dialog({
                                width:920,
                                modal:true,
                                dialogClass:"modalWrap"
                        });
		});
	});
}


function addCustAgenda(){
	$(function() {
		$( "#newMeetingAgenda").dialog({
			width:920,
			modal:true,
			dialogClass:"modalWrap"
		});

	});

}


<!-- TODO: change js name -->
function createCustAgendaItem1(mid, time, mPath){
	var newCustAgendaName = document.getElementById("newCustAgendaName").value;
	if( $.trim(newCustAgendaName)==''){alert("Please fill agenda name"); return false;}
	var newCustAgendaDuration= document.getElementById("newCustAgendaDuration").value;
	var createCustAgendaTxt = document.getElementById("newCustAgendaTxt").value;
	var urlPath =mPath +"&duration="+newCustAgendaDuration+"&name="+ newCustAgendaName+"&startTime="+time+"&txt="+createCustAgendaTxt ;
	$.ajax({
		url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=CreateCustomAgenda&newCustAgendaName="+urlPath,
		cache: false
	}).done(function( html ) {
		vtkTrackerPushAction('CreateCustomAgenda');
		document.location="/content/girlscouts-vtk/en/vtk.planView.html?elem="+mid;
		
	});
}

//no reload
function createCustAgendaItem2(mid, time, mPath){
	

	
	var newCustAgendaName = document.getElementById("newCustAgendaName").value;
	if( $.trim(newCustAgendaName)=='' || $.trim(newCustAgendaName)=='Enter Agenda Item Name'){alert("Please fill agenda name"); return false;}
	var newCustAgendaDuration= document.getElementById("newCustAgendaDuration").value;
	
	if( newCustAgendaDuration<1){alert('Invalid Duration');return false;}
	var createCustAgendaTxt = document.getElementById("newCustAgendaTxt").value;
	if( $.trim(createCustAgendaTxt)=='' || $.trim(createCustAgendaTxt)=='Description')
	{createCustAgendaTxt='';}
	
	var urlPath =mPath +"&duration="+newCustAgendaDuration+"&name="+ newCustAgendaName+"&startTime="+time+"&txt="+createCustAgendaTxt ;
	
	$.ajax({
		url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=CreateCustomAgenda&newCustAgendaName="+urlPath,
		cache: false
	}).done(function( html ) {
		//document.location="/content/girlscouts-vtk/en/vtk.planView.html?elem="+mid;
		//location.reload("true");
		
		window.location.reload(true);
		vtkTrackerPushAction('CreateCustomAgenda');
		$('#modal_popup').foundation('reveal', 'close');
	});
}

function showIt(x){
	$( "#"+x ).show();
}

function getNewActivitySetup() {
	var toRet="";
	var liTags = document.getElementById ("sortable").getElementsByTagName ("li");
	for (var i = 0; i < liTags.length; i++) {
		toRet+=  liTags[i].value +"," ;
	}
	return toRet.substring(0, toRet.length-1);
}

function repositionActivity(meetingPath,newVals ){
	//-var newVals = getNewActivitySetup();
console.log(1);	
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RearrangeActivity&mid='+meetingPath+'&isActivityCngAjax='+ newVals, // JQuery loads serverside.php
		data: '', // Send value of the clicked button
		dataType: 'html', // Choosing a JSON datatype
		success: function (data) { 
			//-location.reload();
			vtkTrackerPushAction('ChangeAgenda');
		},
		error: function (data) { 
		}
	});
}

function editAgenda(x){
	$(function() {
                $( "#"+x ).dialog({
                        width:920,
                        modal:true,
                        dialogClass:"modalWrap"
                });
	});
}

function rmAgenda(id, mid){

	var isRm = confirm("Remove Agenda?");
	if( !isRm ) return false;
	
	
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RemoveAgenda&rmAgenda='+id+'&mid='+mid, // JQuery loads serverside.php
		data: '', // Send value of the clicked button
		dataType: 'html', // Choosing a JSON datatype
		success: function (data) { 
			vtkTrackerPushAction('RemoveAgenda');
			location.reload();
		},
		error: function (data) { 
			
		}
	});
	
}

function durEditActiv(duration, activPath, meetingPath){
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=EditAgendaDuration&editAgendaDuration='+duration+'&aid='+activPath+'&mid='+meetingPath, // JQuery loads serverside.php
		data: '', // Send value of the clicked button
		dataType: 'html', // Choosing a JSON datatype
		success: function (data) { 
			vtkTrackerPushAction('ChangeAgenda');
			location.reload();
		},
		error: function (data) { 

		}
	});
}

function revertAgenda(mid) {
	console.log("MID: "+mid); 
	
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RevertAgenda&revertAgenda=true&mid='+ mid, // JQuery loads serverside.php
		data: '', // Send value of the clicked button
		dataType: 'html', // Choosing a JSON datatype
		success: function (data) { 
			vtkTrackerPushAction('ChangeAgenda');
			location.reload();
		},
		error: function (data) { 

		}
	});
}

function previewMeetingReminderEmail(mid,template){
	var email_to_gp = document.getElementById("email_to_gp").checked;
	//var email_to_sf = document.getElementById("email_to_sf").checked;
	var email_to_tv = document.getElementById("email_to_tv").checked;
	var email_cc = document.getElementById("email_to_cc").value;
	var email_subj = document.getElementById("email_subj").value;
	var email_htm = document.getElementById("email_htm").value;

	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			previewMeetingReminderEmail: true,
			mid: mid,
			email_to_gp :email_to_gp,
			//email_to_sf:email_to_sf,
			email_to_tv:email_to_tv,
			email_cc:email_cc,
			email_subj:email_subj,
			mid:mid,
			email_htm: email_htm,
			template:template
		},
		success: function(result) {
			sendMeetingReminderEmail();
		}
	});
	return;
}

function sendMeetingReminderEmail(){
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			sendMeetingReminderEmail: true,
		},
		success: function(result) {
			location.reload('true');
		    $('.ui-dialog-titlebar').css('display', 'none');
			$('#after-sent').dialog('open');
			$('.ui-dialog').css('z-index', 300);
		    setTimeout(function() {
		    	$('#added').dialog('close');
		    }, 10000);

		}
	});
	return;
}



function openClose(div){
	if( document.getElementById(div).style.display=='none' )
		document.getElementById(div).style.display='block';
	else
		document.getElementById(div).style.display='none';
	
}



function openClose1(div1, div2){
	if( document.getElementById(div1).style.display=='none' ){
		document.getElementById(div1).style.display='block';
		document.getElementById(div2).style.display='none';	
	}else {
		document.getElementById(div1).style.display='none';
		document.getElementById(div2).style.display='none';	
	}
}

function updateAttendAchvm(mid){
	
	var attend = getCheckedCheckboxesFor('attendance');
	var achn = getCheckedCheckboxesFor('achievement');
	//var UpdAttendance= document.getElementById('UpdAttendance');
	//var mid= document.getElementById('mid');
	console.log( "attend: "+ attend);
	console.log( "achv: "+ achn);
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'UpdAttendance',
			mid:mid,
			attendance:attend,
			achievement:achn
		},
		success: function(result) {
			console.log("closing...");
			vtkTrackerPushAction('ChangeAttendance');
			$('#modal_popup').foundation('reveal', 'close');
			location.reload();
		}
	});
	return;
	
}

function getCheckedCheckboxesFor(checkboxName) {
	var t="";
    var checkboxes = document.querySelectorAll('input[name="' + checkboxName + '"]:checked'), values = [];
    Array.prototype.forEach.call(checkboxes, function(el) {
        //values.push(el.value);
    	t+= el.value +",";
    });
    return t;//values;
}


function getEventImg( eventPath){

    $.ajax({
        cache: false,
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: { 
            'getEventImg':'EventImg',
            path: eventPath,
            a:Date.now()
        },
        success: function(result) {
           
           document.getElementById("activ_img").innerHTML = $.trim(result);
           return result;
        }
    });
    return '';
}


function isSafary(){
	var ua = navigator.userAgent.toLowerCase(); 
	  if (ua.indexOf('safari') != -1) { 
	    if (ua.indexOf('chrome') > -1) {
	      return false; // Chrome
	    } else {
	     return true; // Safari
	    }
	  }
	  return false;
}

function rmMeetingHref(mPath, mDate, ageGroup, meetingName) {

    var rmMeeting = document.getElementById("rmMeeting");
    var rmMeetingSmall = document.getElementById("rmMeetingSmall");
    if (rmMeeting != null) {
        rmMeeting.innerHTML = "<a href=\"#\" onclick=\"rmMeetingWithConf( mPath, mDate, ageGroup, meetingName )\">delete meeting</a>";
    }
    if (rmMeetingSmall != null) {
        mMeetingSmall.innerHTML = "<a href=\"#\" onclick=\"rmMeetingWithConf( mPath, mDate, ageGroup, meetingName )\">delete meeting</a>";
    }

}

function rmMeetingWithConf(mPath, mDate, ageGroup, meetingName){
	
	var isRm = confirm('Are you sure you want to delete the '+ ageGroup+' meeting, "'+meetingName+'" from your Year Plan?');
	if( isRm ){
		rmMeetingSingle(  mDate, mPath );
	}
}



function rmMeetingSingle(rmDate, mid) {
alert("rmMeetingSingle...");
    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            act: 'RmMeeting',
            rmDate: rmDate,
            mid: mid,
            a: Date.now()
        },
        success: function(result) {

            vtkTrackerPushAction('RemoveMeeting');
            self.location='/content/girlscouts-vtk/en/vtk.html';
        }
    });
}


var getDataIfModified;
(function() {
	var BASE_PATH = '/vtk-data';
    var eTags = {};
    function _getTroopDataToken() {
    	// Ref: https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
    	// Get cookie: troopDataToken
    	var hash = document.cookie.replace(/(?:(?:^|.*;\s*)troopDataToken\s*\=\s*([^;]*).*$)|^.*$/, "$1");
    	return hash;
    }
    
    
    function _getDataIfModified(path, that, success) {
    	var url = BASE_PATH + '/' + _getTroopDataToken() + '/' + path;
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data, textStatus, jqXHR){
                var eTag = jqXHR.getResponseHeader("ETag");
                if (eTag) {
                    eTags[url] = eTag;
                }
                if (success) {
                    success.apply(this, arguments);
                }
            }.bind(that),
            beforeSend: function(request) {
                if (eTags[url]) {
                    request.setRequestHeader('If-None-Match', eTags[url]);
                }
            }
        });
    };
    getDataIfModified = _getDataIfModified;
})();
