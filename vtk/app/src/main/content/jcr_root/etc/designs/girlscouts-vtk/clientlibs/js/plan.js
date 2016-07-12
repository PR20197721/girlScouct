function loadMeetings() {
    var url = '/content/girlscouts-vtk/controllers/vtk.meetingInclude.html';
    $("#yearPlanMeetings").load(url, resizeWindow);
}

function x(planId, planPath, confirmMsg, planName) {

    if (confirmMsg != null && confirmMsg != '') {

        if (!confirm(confirmMsg)) {
            return;
        } else {
            x1_1(planPath, planName);
        }
    } else {

        $.ajax({
            url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=isAltered&isAltered=chk",
            cache: false
        }).done(function (html) {
            html = $.trim(html)
            if (html == 'true') {
                if (!confirm("Are You Sure? You will lose customizations that you have made")) {
                    return;
                } else {
                    x1_1(planPath, planName);
                }
            } else {
                x1_1(planPath, planName);
            }
            vtkTrackerPushAction('ChangeYearPlan');
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

function x1_1(planPath, planName) {
    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=SelectYearPlan&addYearPlanUser=" + planPath + "&addYearPlanName=" + planName,
        cache: false
    }).done(function (html) {
        //loadMeetings();
        if (html != null && $.trim(html) != "") {
            alert($.trim(html));
            return;
        }
        vtkTrackerPushAction('CreateYearPlan');
        location.reload();
    });
}


function testIt() {
    var s = document.getElementById("sortable");
    s.sortable = true
}

function getNewMeetingSetup() {
    var toRet = "";
    var liTags = document.getElementById("sortable123").getElementsByTagName("li");
    for (var i = 0; i < liTags.length; i++) {
        if (liTags[i].value != 0)
            toRet += liTags[i].value + ",";

    }
    return toRet.substring(0, toRet.length - 1);
}

function doUpdMeeting(newVals) {
    //var newVals = getNewMeetingSetup();

    var x = $.ajax({ // ajax call starts
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=ChangeMeetingPositions&isMeetingCngAjax=' + newVals, // JQuery loads serverside.php
        data: '', // Send value of the clicked button

        dataType: 'html', // Choosing a JSON datatype

    }).done(function (html) {

        vtkTrackerPushAction('ChangeMeetingPosition');
        loadMeetings();
    });

}

function reloadMeeting() {
    var x = $.ajax({ // ajax call starts
        url: '/content/girlscouts-vtk/controllers/vtk.meetingInclude.html?isRefresh=true', // JQuery loads serverside.php
        data: '', // Send value of the clicked button
        dataType: 'html', // Choosing a JSON datatype
        success: function (data) { },
        error: function (data) { }
    });
}

function newActivity() {
    loadModalPage('/content/girlscouts-vtk/controllers/vtk.newCustomActivity.html', false, null, true, false);
}

function addExistActivity(activityId) {
    $("#addExistActivity_err_" + activityId).load("/content/girlscouts-vtk/controllers/vtk.controller.html?addExistActivity=" + activityId);
}

function newLocCal() {
    loadModalPage('/content/girlscouts-vtk/controllers/vtk.locations.html', false, null, true, false);
}

function newLocCal_withTarget(tabIndex) {
    closeModalPage();
    loadModalPage('/content/girlscouts-vtk/controllers/vtk.locations.html?tabIndex=location', false, null, true, false);
}

function closeModalPage() {
    try {
        $("#gsModal").dialog("close");
    } catch (e) {
        console.log(e);
    }
}

function resetModalPage() {
    $("#gsModal").css({ overflow: 'inherit' });
}

function loadModalPage(link, showTitle, title, fullPageScroll, print) {

    resetModalPage();

    $("#gsModal").load(link, function (response, status, xhr) {

        if (status == "error") {

            var msg = "Sorry but there was an error: ";
            $("#error").html(msg + xhr.status + " " + xhr.statusText);
        } else {

            loadModal("#gsModal", showTitle, title, fullPageScroll, print);
        }
    });
}

function loadModal(divSelector, showTitle, title, fullPageScroll, print) {

    var wWidth = $(window).width();
    var wHeight = $(window).height();
    // var dWidth = wWidth * .8; //this will make the dialog 80% of the
    var dHeight = wHeight * 1;
    var dWidth = '920';
    var tHeight = $(window).height();
    var dialog = null;
    if (print) {
        title += "<span class=\"printIcon\"><a href=\"#\" onclick=\"printDiv('gsModal')\"><img src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/print-icon.png\" width=\"22\" height=\"22\" alt=\"print icon\"/></a></span>";
    }
    if (fullPageScroll) {
        dialog = $(divSelector).dialog({
            dialogClass: "modalWrap",
            modal: true,
            show: 375,
            // maxWidth: '920',
            // width: '100%',
            minWidth: dWidth,
            maxWidth: '920px',
            width: '100%',
            open: function () {
                $('.scroll').css('max-height', ($(window).height() - 75) + 'px');
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
                if (navigator.userAgent.match(/(msie\ [0-9]{1})/i)) {
                    if (navigator.userAgent.match(/(msie\ [0-9]{1})/i)[0].split(" ")[1] == 9) {
                        $('select').css('background-image', 'none');
                        placeholder_IE9();
                    }
                }
            },
            close: function () {
                $("body").css({ overflow: 'inherit' });
            }
        });
    } else {
        dialog = $(divSelector).dialog({
            modal: true,
            dialogClass: "modalWrap",
            show: 375,
            maxWidth: dWidth,
            open: function () {
                if ($(window).width() > 920) {
                    $('.vtk-body .ui-dialog.modalWrap').css({ 'max-width': $(window).width() / 2, 'width': '100%', 'left': ($(window).width() - dWidth) / 2 });
                } else {
                    $('.vtk-body .ui-dialog.modalWrap').css({ 'max-width': $(window).width() / 2, 'width': '100%', 'left': '0 !important' });
                }
                if (!showTitle) {
                    $(".ui-dialog-titlebar").hide();
                } else {
                    $("span.ui-dialog-title").html(title);
                    $(".ui-dialog-titlebar").show();
                }
                $("body").css({ overflow: 'hidden' });
                placeholder_IE9();
            },
            close: function () {
                $("body").css({ overflow: 'inherit' });
            }
        });
    }
}
//add placeholder to the forms in the popups for IE9.
function placeholder_IE9() {
    $('select').css('background-image', 'none');

    function add() {
        if ($(this).val() === '') {
            $(this).val($(this).attr('placeholder')).addClass('placeholder');
        }
    }

    function remove() {
        if ($(this).val() === $(this).attr('placeholder')) {
            $(this).val('').removeClass('placeholder');
        }
    }

    // Create a dummy element for feature detection
    if (!('placeholder' in $('<input>')[0])) {

        // Select the elements that have a placeholder attribute
        $('input[placeholder], textarea[placeholder]').blur(add).focus(remove).each(add);

        // Remove the placeholder text before the form is submitted
        $('form').submit(function () {
            $(this).find('input[placeholder], textarea[placeholder]').each(remove);
        });
    }
}

function yesPlan() {
    if (document.getElementById('yearPlanMeetings').style.display == 'none') {
        document.getElementById('yearPlanMeetings').style.display = 'block';
        document.getElementById('yearPlanSelection').style.display = 'none';
        document.getElementById('showHideReveal').innerHTML = 'SEE YEAR PLAN LIBRARY';
        // document.getElementById('arrowDirection').innerHTML='&#9660;';
        $('#showHideReveal').toggleClass('open').addClass('close');
        $("#empty-yp-directions").show();
    } else {
        document.getElementById('yearPlanMeetings').style.display = 'none';
        document.getElementById('yearPlanSelection').style.display = 'block';
        document.getElementById('showHideReveal').innerHTML = 'YEAR PLAN LIBRARY';
        // document.getElementById('arrowDirection').innerHTML='&#9650;';
        $('#showHideReveal').removeClass('close').addClass('open');
        $("#empty-yp-directions").hide();
    }
}

function addLocation() {

    //NEEDS to be BACK vtk-global.js? showError(null, "#locationEdit .errorMsg");
    var name = document.getElementById("loc_name").value;
    if ($.trim(name) == '' || $.trim(name) == 'Location Name') {
        //showError("Please enter a location", "#locationEdit .errorMsg"); //js missing from VTK2
        alert("Please enter a location");
        return false;
    }

    var address = document.getElementById("loc_address").value;
    if ($.trim(address) == '' || $.trim(address) == 'Location Address') {
        address = "";
    }
    var city = document.getElementById("loc_city").value;
    var state = document.getElementById("loc_state").value;
    var zip = document.getElementById("loc_zip").value;
    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
        type: 'POST',
        data: {
            act: 'AddLocation',
            addLocation: true,
            name: name,
            address: address,
            city: city,
            state: state,
            zip: zip,
            a: Date.now()
        },
        success: function (result) {
            if ($.trim(result) != '') { alert($.trim(result)); }
            loadLocMng();
            //document.getElementById("err").innerHtml=result;
            $("#addLocationForm").trigger("reset");
            vtkTrackerPushAction('AddLocation');
        }
    });
}

function updSched(i, meetingPath, currDt) {
    var date = document.getElementById("cngDate" + i).value;
    var time = document.getElementById("cngTime" + i).value;
    var ap = document.getElementById("cngAP" + i).value;
    var isCancelled = document.getElementById("isCancellMeeting" + i).checked;
    var urlParam = "meetingPath=" + meetingPath +
        "&date=" + date +
        "&time=" + time +
        "&ap=" + ap +
        "&currDt=" + currDt +
        "&isCancelledMeeting=" + isCancelled;

    $("#locMsg").load("/content/girlscouts-vtk/controllers/vtk.controller.html?act=UpdateSched&updSched=true&" + urlParam, function (response, status, xhr) {
        if (status != "error") { vtkTrackerPushAction('UpdateSched'); } else { }
    });

}

function buildSched() {

    var calStartDt = document.getElementById("calStartDt").value;

    var orgDt = "0";
    if (document.getElementById("orgDt") != null)
        orgDt = document.getElementById("orgDt").value;

    var calAP = document.getElementById("calAP").value;
    var calFreq = document.getElementById("calFreq");

    var z = calFreq.options[calFreq.selectedIndex].text;
    var calTime = document.getElementById("calTime").value;
    if ($.trim(calTime) == '') {
        alert("Time field empty");
        return;
    }

    var stringToParse = calStartDt + " " + calTime;
    var dateString = stringToParse.match(/\d{2}\/\d{2}\/\d{4}\s+\d{2}:\d{2}/) + " " + calAP;
    var dt = new Date(dateString);



    if (isNaN(dt)) {
        alert("Invalid date/time. Valid date format MM/dd/yyyy  Valid time format HH:mm");
        return;
    }


    var minExpDate = new Date();
    minExpDate.setMinutes(minExpDate.getMinutes() + 30);

    /*
    var maxExpDate = new Date();
    maxExpDate.setDate ( maxExpDate.getDate() +  730);

    if( dt > maxExpDate ){
    	alert("You cannot select a date after "+moment(maxExpDate).format('MM/DD/YYYY h:mm a')+" since the YP won't fit into the actual calendar year");
    	return;
    }
    */

    if (new Date(dt) <= minExpDate) {
        alert("You cannot select a date in the past to reschedule the meetings. Please type or select a date in the future.");
        return;
    }

    var _level = "";
    var levels = document.getElementsByName('exclDt');
    for (var i = 0; i < levels.length; i++) {
        if (levels[i].checked)
            _level += levels[i].value + ",";
    }


    viewProposedSched(calStartDt, calAP, z, calTime, _level, orgDt);
    vtkTrackerPushAction('ChangeSchedule');
}

function buildSchedContr(calStartDt, calAP, z, calTime, _level, orgDt) {


    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
        type: 'POST',
        data: {
            act: 'CreateSchedule',
            buildSched: true,
            calStartDt: calStartDt,
            calAP: calAP,
            calFreq: z,
            calTime: calTime,
            exclDt: _level,
            orgDt: orgDt
        },
        success: function (result) {
            //-loadCalMng();
            //	location.reload();
            vtkTrackerPushAction('CreateSchedule');
            location.reload(true);
        }
    });
}

function viewProposedSched(calStartDt, calAP, z, calTime, _level, orgDt) {

    var toRet = false;
    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
        type: 'POST',

        data: {
            viewProposedSched: 'true',
            calStartDt: calStartDt,
            calAP: calAP,
            calFreq: z,
            calTime: calTime,
            exclDt: _level,
            orgDt: orgDt

        },
        success: function (result) {
            //console.log( $.trim(result) );
            //proposedSchedConfirm($.trim(result));

            if ($.trim(result) <= 0) {
                alert('All of the scheduled meetings fall outside of the current troop year.  Please change the meeting date and/or frequency and try again.');
                return;
            }

            //			 toRet= confirm("One or more of the meetings fall outside of the troop year. Changing meeting frequency will result in "+ $.trim(result)+" schedule meetings. Are you sure you would like to continue");
            toRet = confirm("The Year Plan for a troop ends on June 30th. The date you selected will result in " + $.trim(result) + " meetings. Would you like to continue?");
            //console.log(toRet);


            if (toRet) {
                buildSchedContr(calStartDt, calAP, z, calTime, _level, orgDt);
            }
        }
    });

    //return toRet;
}

function proposedSchedConfirm(numberOfMeetings) {
    $("#pcs_dialog-confirm").html("Are you sure you want to continue? New number of meetings will be " + numberOfMeetings);

    // Define the Dialog and its properties.
    $("#pcs_dialog-confirm").dialog({
        resizable: false,
        modal: true,
        title: false,
        height: 250,
        width: 400,
        buttons: {
            "Go ahead, reschedule": function () {
                $(this).dialog('close');

            },
            "Cancel": function () {
                $(this).dialog('close');

            }
        },
        create: function (event, ui) {
            $(".ui-dialog-titlebar.ui-widget-header").hide();
        }

    });
}

function rmCustActivity(x) {

    $("#locMsg").load("/content/girlscouts-vtk/controllers/vtk.controller.html?act=RemoveCustomActivity&rmCustActivity=" + x, function (response, status, xhr) {
        if (status != "error") {
            location.reload();
        } else {
            alert("Sorry.  Unable to remove activity: " + status);
        }
    });
    vtkTrackerPushAction('RemoveActivity');
}

function createNewCustActivity() {



    var newCustActivity_name = document.getElementById("newCustActivity_name").value;
    if ($.trim(newCustActivity_name) == '' || $.trim(newCustActivity_name) == 'Activity Name') {
        alert("Please fill 'Name' field");
        return false;
    }
    var newCustActivity_date = document.getElementById("newCustActivity_date").value;

    var aDate = new Date(newCustActivity_date);

    if (!Date.parse(aDate) || aDate < new Date()) {
        alert("Invalid start date. Date must be after todays date.");
        return false;
    }

    var newCustActivity_startTime = document.getElementById("newCustActivity_startTime").value;
    var newCustActivity_endTime = document.getElementById("newCustActivity_endTime").value;
    var newCustActivity_txt = document.getElementById("newCustActivity_txt").value;
    if ($.trim(newCustActivity_txt) == '' || $.trim(newCustActivity_txt) == 'Activity Description') { newCustActivity_txt = ''; }


    var newCustActivityLocName = document.getElementById("newCustActivity_locName").value;
    if ($.trim(newCustActivityLocName) == '' || $.trim(newCustActivityLocName) == 'Location Name') {
        alert("Please fill 'Location Name' field");
        return false;
    }

    var newCustActivityLocAddr = document.getElementById("newCustActivity_locAddr").value;
    if ($.trim(newCustActivityLocAddr) == '' || $.trim(newCustActivityLocAddr) == 'Location Address') {
        alert("Please fill 'Location Address' field");
        return false;
    }

    var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
    var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;
    var newCustActivity_cost = document.getElementById("newCustActivity_cost").value;

    if (document.getElementById("newCustActivity")) {
        document.getElementById("newCustActivity").disabled = true;
    }

    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            act: 'CreateActivity',
            newCustActivity: true,
            newCustActivity_name: newCustActivity_name,
            newCustActivity_date: newCustActivity_date,
            newCustActivity_startTime: newCustActivity_startTime,
            newCustActivity_endTime: newCustActivity_endTime,
            newCustActivityLocName: newCustActivityLocName,
            newCustActivityLocAddr: newCustActivityLocAddr,
            newCustActivity_txt: newCustActivity_txt,
            newCustActivity_startTime_AP: newCustActivity_startTime_AP,
            newCustActivity_endTime_AP: newCustActivity_endTime_AP,
            newCustActivity_cost: newCustActivity_cost,
            a: Date.now()
        },
        success: function (result) {
            location.reload();
            vtkTrackerPushAction('CreateActivity');
        }
    });
}


function editNewCustActivity(activityUid) {
    var newCustActivity_name = document.getElementById("newCustActivity_name").value;
    if ($.trim(newCustActivity_name) == '') {
        alert("Please fill 'Name' field");
        return false;
    }
    var newCustActivity_date = document.getElementById("newCustActivity_date").value;
    var newCustActivity_startTime = document.getElementById("newCustActivity_startTime").value;
    var newCustActivity_endTime = document.getElementById("newCustActivity_endTime").value;
    var newCustActivity_txt = document.getElementById("newCustActivity_txt").value;
    if ($.trim(newCustActivity_txt) == 'Activity Description') { newCustActivity_txt = ''; }
    var newCustActivityLocName = document.getElementById("newCustActivity_locName").value;
    var newCustActivityLocAddr = document.getElementById("newCustActivity_locAddr").value;
    var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
    var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;
    var newCustActivity_cost = document.getElementById("newCustActivity_cost").value;

    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            act: 'EditCustActivity',
            editCustActivity: activityUid,
            newCustActivity_name: newCustActivity_name,
            newCustActivity_date: newCustActivity_date,
            newCustActivity_startTime: newCustActivity_startTime,
            newCustActivity_endTime: newCustActivity_endTime,
            newCustActivityLocName: newCustActivityLocName,
            newCustActivityLocAddr: newCustActivityLocAddr,
            newCustActivity_txt: newCustActivity_txt,
            newCustActivity_startTime_AP: newCustActivity_startTime_AP,
            newCustActivity_endTime_AP: newCustActivity_endTime_AP,
            newCustActivity_cost: newCustActivity_cost,

            a: Date.now()
        },
        success: function (result) {
            //location.reload();
            //var x= new Date(newCustActivity_date + " "+ newCustActivity_startTime +" "+newCustActivity_startTime_AP);
            //alert(x);
            //-self.location="/content/girlscouts-vtk/en/vtk.planView.html?elem="+new Date(newCustActivity_date + " "+ newCustActivity_startTime +" "+newCustActivity_startTime_AP).getTime();
            vtkTrackerPushAction('ChangeCustomActivity');
        }
    });
}

function searchActivity() {

    var existActivSFind = document.getElementById("existActivSFind").value;
    var existActivSMon = document.getElementById("existActivSMon").value;
    var existActivSYr = document.getElementById("existActivSYr").value;
    var existActivSDtFrom = document.getElementById("existActivSDtFrom").value;
    var existActivSDtTo = document.getElementById("existActivSDtTo").value;
    var existActivSReg = document.getElementById("existActivSReg").value;
    var _level = "";
    var levels = document.getElementsByName('existActivSLevl');
    for (var i = 0; i < levels.length; i++) {
        if (levels[i].checked)
            _level += levels[i].value + ",";
    }
    var _cat = "";
    var levels = document.getElementsByName('existActivSCat');
    for (var i = 0; i < levels.length; i++) {
        if (levels[i].checked)
            _cat += levels[i].value + ",";
    }
    var urlParam = "&existActivSFind=" + existActivSFind +
        "&existActivSMon=" + existActivSMon +
        "&existActivSYr=" + existActivSYr +
        "&existActivSDtFrom=" + existActivSDtFrom +
        "&existActivSDtTo=" + existActivSDtTo +
        "&existActivSReg=" + existActivSReg + "&existActivSLevl=" + _level +
        "&existActivSCat=" + existActivSCat;
    $("#listExistActivity").load("/content/girlscouts-vtk/controllers/vtk.controller.html?searchExistActivity=true" + urlParam);

}

$('#plan_hlp_hrf').click(function () {
    $('#plan_hlp').dialog();
    return false;
});

function loadLocMng() {
    $("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand=" + Date.now());
}

function loadCalMng() {
    $("#calMng").load("/content/girlscouts-vtk/controllers/vtk.calendar.html?rand=" + Date.now());

}

function manageCalElem(elem) {
    $("#calMng").load("/content/girlscouts-vtk/controllers/vtk.calendarElem.html?elem=" + elem + "&rand=" + Date.now());
}

function relogin() {
    var elem = document.getElementById("reloginid").value;
    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            act: 'ReLogin',
            loginAs: elem,
            a: Date.now()
        },
        success: function (result) {
            vtkTrackerPushAction('ChangeTroop');
            document.location = "/content/girlscouts-vtk/en/vtk.plan.html";
        }
    });
}


function bindAssetToYPC(assetId, ypcId) {

    var assetDesc = document.getElementById("assetDesc").value;
    var custasset = document.getElementById("custasset").value;
    if ($.trim(custasset) == '') {
        alert('Please select file to upload');
        return false;
    }
    if ($.trim(assetDesc) == '') {
        alert('Please enter name of asset');
        return false;
    }
    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            act: 'BindAssetToYPC',
            bindAssetToYPC: assetId,
            ypcId: ypcId,
            assetDesc: assetDesc,
            a: Date.now()
        },
        success: function (result) {
            vtkTrackerPushAction('AddAsset');
        }
    });

}


function doMeetingLib(isMsgConf) {
    if (!isMsgConf) {

        if (!confirm("This action will create a meeting outside of the current GirlScouts school year. Would you like to proceed?")) {
            return;
        }
    }
    loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html', false, null, true, false);
}

function doHelp(isSched) {
    loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html', false, null, true, false);
}


function doEditActivity(x) {

    loadModal("#" + x, true, "Edit Activity", false);
}

function printDiv(x) {
    var divToPrint = document.getElementById(x);
    var popupWin = window.open('', '_blank', 'width=300,height=300');
    popupWin.document.open();
    popupWin.document.write('<html><body onload="window.print()">' + divToPrint.innerHTML + '</html>');
    popupWin.document.close();
}

function showAlterYearPlanStartDate(fromDate, mCountUpd) {
    // temporary fix until more permanent refactoring
    closeModalPage();
    loadModalPage('/content/girlscouts-vtk/controllers/vtk.locations.html?alterYPStartDate=' + fromDate + '&mCountUpd=' + mCountUpd, false, null, true, false);
}


function expiredcheck(ssId, ypId) {

    $.ajax({
        url: "/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=" + ssId + "&ypid=" + ypId + "&d=",
        dataType: 'json',
        cache: false
    }).done(function (obj) {
        //console.log("**"+html+"**");
        //var obj = jQuery.parseJSON(html );
        console.log("/content/girlscouts-vtk/en/vtk.expiredcheck.json?sid=" + ssId + "&ypid=" + ypId + "&d=");
        console.log("*** " + (obj.yp_cng == 'true'));

        if (obj.yp_cng == 'true') {
            //alert("reloading...");

            var myUrl = window.location.href;
            if (window.location.href.indexOf("reload=data") != -1) {
                ;
            } else if (window.location.href.indexOf("?") != -1) {
                //window.location.replace(window.location.href + "&reload=data")
                myUrl = window.location.href + "&reload=data";
            } else {
                //window.location.replace(window.location.href + "?reload=data")
                myUrl = window.location.href + "?reload=data";
            }
            //alert(myUrl);
            //if(true)return;
            //window.location.reload();
            window.location.href = myUrl;

        }
        setTimeout(function () { expiredcheck(ssId, ypId); }, 20000);

    });



}



//tmp need to replace with original
function showError(x, y) { }

function rmTroopInfo() {

    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            isRmTroopImg: 'true',
            a: Date.now()
        },
        success: function (result) {
            location.reload();
        }
    });



}


function rmMeeting(rmDate, mid) {

    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
        type: 'POST',
        data: {
            act: 'RmMeeting',
            rmDate: rmDate,
            mid: mid,
            a: Date.now()
        },
        success: function (result) {

            vtkTrackerPushAction('RemoveMeeting');
            location.reload();
        }
    });
}

function councilRpt(troopId, cid) {
    console.log(troopId);

    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?isAdminRpt=true&cid=" + cid + "&ypPath=" + troopId,
        cache: false
    }).done(function (html) {
        councilRpt_updateTroopName(html);
    });
}

function councilRpt_updateTroopName(input) {


    var lines = input.split('\n');
    var output = '';
    $.each(lines, function (key, line) {
        var parts = line.split(';');
        for (var i = 0; i < parts.length; i++) {
            output += parts[i] + '; \n';
        }
    });

    output = "<script> function test(){" + output + " } test(); </" + "" + "script>";
    $(output).appendTo('body');

}

//loaded in vtkFooter jsp
function vtkCreateTracker() {

    (function (i, s, o, g, r, a, m) {
        i['GoogleAnalyticsObject'] = r;
        i[r] = i[r] || function () {
            (i[r].q = i[r].q || []).push(arguments)
        }, i[r].l = 1 * new Date();
        a = s.createElement(o), m = s.getElementsByTagName(o)[0];
        a.async = 1;
        a.src = g;
        m.parentNode.insertBefore(a, m)
    })
        (window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');
    ga('create', 'UA-2646810-36', 'auto', { 'name': 'vtkTracker' });

}

function vtkInitTracker(tName, tId, uId, cId, tAge, ypn) {
    //var newTracker = ga.getByName('vtkTracker');
    //vtkCreateTracker();
    ga('vtkTracker.set', 'dimension1', tName);
    ga('vtkTracker.set', 'dimension2', tId);
    ga('vtkTracker.set', 'dimension3', uId);
    ga('vtkTracker.set', 'dimension7', cId);
    ga('vtkTracker.set', 'dimension8', tAge);
    ga('vtkTracker.set', 'dimension9', ypn);
}

function vtkTrackerPushAction(vAction) {
    $(document).ready(function () {
        ga('vtkTracker.send', 'pageview', {
            dimension4: vAction
        });
    });


}


function getRelogin() {
    console.log('relogin')
    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?printTroopReloginids=true",
        cache: false
    }).done(function (html) {
        printRelogin(html);
    });
}


function printRelogin(reloginSelect) {
    var reloginDiv = document.getElementById("relogin");
    if (reloginDiv != null && reloginDiv != null) {
        reloginDiv.innerHTML = reloginSelect;
    }
}

function chgYearPlan(planId, planPath, confirmMsg, planName, isYearPlan, yearPlanName) {
    if (isYearPlan) {
        if (planName === yearPlanName) {
            confirmMsg = "Are you sure to reset the yearplan?";
        }
    }
    x(planId, planPath, confirmMsg, planName);
};

function chgCustYearPlan(planId, planPath, confirmMsg, planName, isYearPlan, yearPlanName) {
    if (isYearPlan) {
        if (planName === yearPlanName) {
            confirmMsg = "Are you sure to reset the yearplan?";
        }
    }


    $('#modal_custom_year_plan').foundation('reveal', 'open', {
        url: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_custom_year_plan.html",

        success: function (data) {
            var min_height = $('#sortable1').height() - 71;
            $("#sortable2").css('min-height', min_height);
        }
    });
};

function getCngYearPlan() {
    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?printCngYearPlans=true",
        cache: false
    }).done(function (html) {
        printYearPlans(html);
    });
}


function printYearPlans(reloginSelect) {
    var reloginDiv = document.getElementById("cngYearPlan");
    if (reloginDiv != null && reloginDiv != null) {
        reloginDiv.innerHTML = reloginSelect;
    }
}

function replaceMeetingHref(mPath, mDate) {

    var replaceMeeting = document.getElementById("replaceMeeting");
    var replaceMeetingSmall = document.getElementById("replaceMeetingSmall");
    if (replaceMeeting != null) {
        replaceMeeting.innerHTML = "<a href=\"#\" onclick=\"loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath=" + mPath + "&xx=" + mDate + "', false, null, true)\">replace this meeting</a>";
    }
    if (replaceMeetingSmall != null) {
        replaceMeetingSmall.innerHTML = "<a href=\"#\" onclick=\"loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath=" + mPath + "&xx=" + mDate + "', false, null, true)\">replace this meeting</a>";
    }

}


function loadNav(activeTab) {
    loadTabNav(activeTab);
    //		loadUNav(activeTab);


    window.setTimeout(function () { checkIsLoggedIn(); }, 10000);

    if (activeTab != null && activeTab == 'myTroop') {
        vtkTrackerPushAction('ViewTroop');
    }

    if (activeTab != null && activeTab == 'reports') {
        vtkTrackerPushAction('ViewReport');
    }

    if (activeTab != null && activeTab == 'finances') {
        vtkTrackerPushAction('ViewFinances');
    }


}

/*

	function loadUNav(activeTab){

	    $.ajax({
	        url: "/content/girlscouts-vtk/controllers/vtk.include.utility_nav.html?activeTab="+activeTab+ getElem(),
	        cache: false
	    }).done(function( html ) {
	        var vtkNav = document.getElementById("vtkNav");
	        vtkNav.innerHTML =html;
	    })
	}

*/

function loadTabNav(activeTab) {

    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.include.tab_navigation.html?activeTab=" + activeTab + getElem(),
        cache: false
    }).done(function (html) {
        var vtkNav = document.getElementById("fullNav");
        vtkNav.innerHTML = html;
        getRelogin();

        if ($('.tabs dd').length == 6) {
            $('.tabs dd').css('width', '100%');
        }

    })
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function getElem() {
    var elem = getParameterByName('elem');
    if (elem != null && elem != '')
        return "&elem=" + elem;

    return "";
}

var isLoggedIn = false;

function resetIsLoggedIn() {
    console.log("reset logged in to false...");
    isLoggedIn = false;
}

function setLoggedIn() {
    console.log("set login true");
    isLoggedIn = true;
}

function checkIsLoggedIn() {

    if (document.getElementById("myframe") == null) {
        console.log("Skipping because iframe not ready.")
        return;
    }
    console.log("checking isLoggedin..." + isLoggedIn);
    if (!isLoggedIn) {
        doVtkLogout();
    }
}

function displayErrMsg(errMsgPlaceHldr) {

    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.include.vtkError.html",
        cache: false
    }).done(function (html) {
        var vtkErrMsgHld = document.getElementById(errMsgPlaceHldr);
        if (vtkErrMsgHld != null) {
            vtkErrMsgHld.innerHTML = html;
        }
    })
}

function doVtkLogout() {
    var isLoginAgain = confirm("Your session has expired. Would you like to login again?");
    window.parent.location = "/content/girlscouts-vtk/controllers/auth.sfauth.html?action=signout&isVtkLogin=" + isLoginAgain;
}

function rmVtkErrMsg(errMsgId) {

    $.ajax({
        url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
        type: 'POST',
        data: {
            vtkErrMsgId: errMsgId,
            act: 'RemoveVtkErrorMsg',
            a: Date.now()
        },

        success: function (result) { },
        error: function (XMLHttpRequest, textStatus, errorThrown) { }
    }),

        rmVtkErrMsg_disableView(errMsgId);
}

function rmVtkErrMsg_disableView(errMsgId) {
    var msgPlanceHldr = document.getElementById("_vtkErrMsgId_" + errMsgId);
    if (msgPlanceHldr != null) {
        msgPlanceHldr.style.display = 'none';
    }
}


function showSelectedDemoTroop(troopAge) {
    $(function () {
        if (typeof gsusa !== "undefined") {
            if (gsusa.component && gsusa.component.dropDown && troopAge != undefined && troopAge != '') {
                gsusa.component.dropDown('#vtk-dropdown-1', { local: true }, troopAge);
            }
        }

    });
}



function cngYear(yr) {

    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?cngYear=" + yr,
        cache: false
    }).done(function (html) {
        relogin();

    });
}


function resetYear() {

    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?cngYearToCurrent=true",
        cache: false
    }).done(function (html) {
        relogin();

    });
}


//Notes ===
var ModalVtk = (function () {

    function Modal() {

        var $main_modal_wrap, $main_modal, $gray_modal = [];

        function init() {
            var $a = $('<div class="vtk-js-modal_wrap"><div class="vtk-js-modal" style=""><div class="vtk-js-modal_head"><div class="vtk-js-modal_title"></div></div><div class="vtk-js-modal_body"></div></div>');
            var $b = $('<div class="vtk-gray-modal" style=""></div></div>');
            $('body').append($a, $b);


            $main_modal_wrap = $('.vtk-js-modal_wrap');
            $main_modal = $('.vtk-js-modal');
            $gray_modal = $('.vtk-gray-modal');

        }
        //<div class="vtk-js-modal_description"></div><div vtk-js-modal_body_actions></div>

        function _getDimensions() {
            return {
                width: $(window).width(),
                height: $(window).height()
            }
        }

        function _applySize() {
            $('.vtk-gray-modal').css(_getDimensions());
        };

        function _centerModal() {
            $('.vtk-js-modal_wrap').animate({ 'top': ($(window).innerHeight() / 2) - 150 + 'px' });
        }

        function _preOpen(open) {
            if (open) {

                _applySize();
                $main_modal.show();
                $gray_modal.show();

            } else {

                $main_modal.hide();
                $gray_modal.hide();
            }
        }

        function _clean() {
            $('.vtk-js-modal_title').html('');
            $('.vtk-js-modal_body').html('');
        }

        function close() {
            _clean();
            _preOpen(false);

        }

        function alert(msg, desc) {

            _preOpen(true);
            _centerModal();

            $('.vtk-js-modal_title').html(msg);

            $('.vtk-js-modal_body').html('<div class="vtk-js-modal_description">' + desc + '</div><div class="vtk-js-modal_body_actions"><div class="vtk-js-modal_button_action vtk-js-modal_ok_action" >Ok</div></div>')

            $('.vtk-js-modal_ok_action').on('click', close);

        }

        function confirm(msg, desc, okCallBack, cancelCallBack) {
            _preOpen(true);
            _centerModal();




            $('.vtk-js-modal_title').html(msg);
            $('.vtk-js-modal_body').html('<div class="vtk-js-modal_description">' + desc + '</div><div class="vtk-js-modal_body_actions"><div class="vtk-js-modal_button_action vtk-js-modal_ok_action">Ok</div><div class="vtk-js-modal_button_action vtk-js-modal_cancel_action">Cancel</div></div>')

            $('.vtk-js-modal_ok_action').on('click', okCallBack);
            $('.vtk-js-modal_cancel_action').on('click', cancelCallBack);



        }

        $(window).resize(function () {
            _applySize();
        })
        return {
            init: init,
            close: close,
            alert: alert,
            confirm: confirm
        }
    }

    return Modal;
})();

    var modal = new ModalVtk();

function ajaxConnection(ajaxOptions) {
    return $.ajax(ajaxOptions);
}

function addNote(mid) {
    var msg = $('.input-content').html();
    var msgl = $('.input-content').text().length;

    var data = {
        addNote: "true",
        message: msg,
        mid: mid,
        a: Date.now()
    }

    if (msgl <= 500) {
        ajaxConnection({
            url: "/content/girlscouts-vtk/controllers/vtk.controller.html",
            cache: false,
            type: 'POST',
            data: data
        })
            .fail(function (err) {
                console.log(err)
            })
            .success(function (d) {
                initNotes.getNotes(data.mid,userLoginId);
                $('.input-content').html('');
                $('.add-note-detail').slideUp();
            })

            .done(function (html) {
            });
    } else {
        modal.alert('warning', 'Message should be less 500 characters')
    }


}

function rmNote(nid) {

    return ajaxConnection({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html",
        cache: false,
        type: 'POST',
        data: {
            rmNote: "true",
            nid: nid,
            a: Date.now()
        }
    });
}

function editNote(nid, msg) {

    return ajaxConnection({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html",
        cache: false,
        type: 'POST',
        data: {
            editNote: "true",
            nid: nid,
            msg: msg,
            a: Date.now()
        }
    });
}

var initNotes = (function (global, modal, $) {
    var globalMid,userLoginId;

    var utility = {
        compileTemplate: function (template) {
            //Create the Dom Element assing the the class and event
            function createElement(element, detail) {
                var domElement = document.createElement(element);


                if (detail.data) {
                    for (var data in detail.data) {
                        domElement.setAttribute('data-' + data, detail.data[data]);
                    }
                }

                if (detail.class) {
                    domElement.className = detail.class;
                }
                if (detail.text) {
                    domElement.appendChild(document.createTextNode(detail.text));
                }

                if (detail.html) {
                    domElement.innerHTML = detail.html;
                }

                if (detail.component) {
                    for (var component in detail.component) {
                        domElement.appendChild(detail.component[component].render());
                    }
                }

                if (detail.style) {
                    var style = "";
                    for (var sty in detail.style) {
                        if (sty && detail.style[sty]) {
                            style += sty + ':' + detail.style[sty] + ';'
                        }

                    }

                    domElement.setAttribute('style', style);
                }


                if (detail.attr) {
                    for (var attr in detail.attr) {
                        domElement.setAttribute(attr, detail.attr[attr]);
                    }
                }



                if (detail.events) {
                    for (var event in detail.events) {
                        domElement.addEventListener(event, detail.events[event]);
                    }
                }



                return domElement;
            }
            //recursive function that interate the Json object
            function interator(object, parentDom) {
                var objElem;
                for (var element in object) {

                    if (/[a-zA-Z]*-[0-9]*/g.test(element)) {
                        objElem = element.split('-')[0]
                    } else {
                        objElem = element;
                    }

                    var currentElement = createElement(objElem, object[element]);
                    if (object[element].child) {
                        interator(object[element].child, currentElement);
                    }

                    if (parentDom) {
                        parentDom.appendChild(currentElement);
                    }
                }
                return currentElement;
            }
            //return the dom with child and event listerners
            return interator(template);
        },
        alertButton: function (msg, okCallback, cancelCallback) {
            var x;
            if (confirm(msg) == true) {
                x = okCallback();
            } else {
                x = cancelCallback();
            }
        }
    }
    var view = {
        actions: global.actions,
        state: '',
        noteFocus: function (e) {
        },
        noteEditable: function (element, boolean) {

            if (boolean) {
                element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content')
                    .attr({
                        'contenteditable': true,
                        'style': 'background-color:lightgray'
                    })
                    .focus();
            } else {
                element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content')
                    .attr({
                        'contenteditable': false,
                        'style': 'background-color:white'
                    })
            }
        },
        deleteNote: function (e) {
            e.preventDefault();

            modal.confirm('Warning', 'Are you sure you want to delete this note', function () {
                rmNote($(e.target).parents('li').data('uid'))
                    .fail(function (err) {
                        console.log('error', err)
                    })
                    .success(function () {
                        checkQuantityNotes($('.vtk-notes_item').length)
                        getNotes(globalMid,userLoginId);
                    }).done(function () {
                        modal.close();
                    });
            }, function () {
                modal.close();
            })
        },
        editNotelocal: function (e) {
            e.preventDefault();
            e.stopPropagation();

            var nid = $(e.target).parents('li').data('uid');
            var element = $(e.target).parents('li');

            editor.render(element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_container'));

            $('li.vtk-note_item').removeClass('shadow');
            element.addClass('shadow');

            var originalMessage = element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content').html();

            counter.methods.render(element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_container'));

            element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content').on('keyup', function (e) {


                var l, memory;
                memory = element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content')[0].innerHTML;
                l = memory.length;

                counter.methods.textChange(element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content'));
            });

            view.state = originalMessage;
            var saveButton = $('li[data-uid="' + nid + '"]').children('.vtk-note_detail').children('.vtk-note_actions').children('.save-note');

            saveButton.show();

            view.noteEditable(element, true);

            element.children('.row').children('.vtk-note_content').on('click', function (e) {
                e.stopPropagation();
            });

            $('.save-note').on('click', function (e) {
                e.stopPropagation();

            });

            $('.format-buttom').on('click', function (e) {
                e.stopPropagation();
            })

            $('.vtk-js-modal_wrap').on('click', function (e) {
                e.stopPropagation();
            });

            $('.vtk-gray-modal').on('click', function (e) {
                e.stopPropagation();
            })

            $('.vtk-js-modal_button_action').on('click', function (e) {
                e.stopPropagation();
            })

            $(this).parents('li').children('.vtk-note_wrap_content').on('click', function (e) {
                e.stopPropagation();
            })

            $(document).on('click', function (e) {
                e.stopPropagation();

                modal.confirm('Warning', 'Changes in Notes will be Erase', function () {
                    element.children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content').html(originalMessage);
                    counter.methods.destroy();
                    view.noteEditable(element, false);
                    saveButton.hide();

                    element.children('.row').children('.vtk-note_content').off('click');
                    $('.save-note').off('click');
                    $(document).off('click');
                    $('.format-buttom').off('click');
                    $('.vtk-js-modal_wrap').off('click');
                    $('.vtk-gray-modal').off('click');
                    $('.vtk-js-modal_button_action').off('click');
                    $(this).parents('li').children('.vtk-note_wrap_content').off('click');



                    editor.destroy();

                    modal.close();

                }, function () {
                    view.noteEditable(element, true);
                    saveButton.show();

                    modal.close();

                });

            })
        },
        updateNote: function (e) {
            e.preventDefault();
            e.stopPropagation();
            var nid = $(e.target).parents('li').data('uid');
            var message = $(e.target).parents('li').children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content').html();
            var character = $(e.target).parents('li').children('.vtk-note_wrap_content').children('.row').children('.vtk-note_content').text();
            var saveButton = $('li[data-uid="' + nid + '"]').children('.vtk-note_detail').children('.vtk-note_actions').children('.save-note');




            if (view.state !== message) {
                if (character.length < 500) {
                    editNote(nid, message)
                        .fail(function (err) {
                            console.log(err);
                        })
                        .success(function () {
                            modal.alert("Warning", "Your Note Was Edited");

                            getNotes(globalMid,userLoginId);


                            // view.noteEditable($(e.target).parents('li'), false)

                            // saveButton.hide();

                        }).done(function () {
                            editor.destroy();
                            counter.methods.destroy();
                        })
                } else {
                    modal.alert('Warning', 'Need to Write a text less than 500 chacracter')
                }
            } else {
                modal.alert("Warning", 'Need to make a change in order to save');
            }



        },
        getPermisionOfEdit: function (note) {

            if (userLoginId === note.createdByUserId) {
                return {
                    'button': {
                        child: {
                            i: {
                                class: "icon-pencil"
                            }
                        },
                        // text: 'Edit ',
                        class: 'vtk-note-edit-button',

                        events: {
                            click: this.editNotelocal
                        }
                    },

                    'button-1': {
                        child: {
                            i: {
                                class: "icon-trash-delete-remove"
                            }
                        },



                        data: {
                            uid: note.uid

                        },




                        // text: 'Delete ',

                        events: {
                            click: this.deleteNote
                        }
                    },

                    'button-2': {
                        child: {
                            i: {
                                class: "icon-save-floppy"
                            }
                        },


                        data: {
                            uid: note.uid

                        },

                        class: 'save-note',


                        style: {
                            display: 'none'
                        },




                        // text: 'Save ',

                        events: {
                            click: this.updateNote
                        }
                    }
                }
            } else {
                return {}
            }
        },
        newNote: function (note) {
            var date = new Date(note.createTime);
            var dateArray = date.toString().split(' ');
            var dateString = dateArray[1] + '/' + dateArray[2] + '/' + dateArray[3];
            var timeString = dateArray[4] + " " + dateArray[6];


            var template = {
                li: {
                    data: {
                        uid: note.uid
                    },
                    events: {
                        click: this.noteFocus
                    },
                    class: 'vtk-note_item row',
                    child: {

                        div: {
                            class: 'vtk-note_wrap_content small-24 medium-18 columns',
                            child: {
                                div: {
                                    class: 'row',
                                    child: {
                                        div: {
                                            class: "vtk-note_content small-24 columns",
                                            html: note.message
                                        }
                                    }
                                },
                                'div-0': {
                                    class: 'row',
                                    child: {
                                        'div': {
                                            class: "vtk-note_container small-24 columns"
                                        }
                                    }

                                }
                            }
                        },
                        'div-1': {
                            class: "vtk-note_detail small-24 medium-6  columns",

                            child: {
                                'p': {
                                    class: '',
                                    child: {
                                        strong: {
                                            text: note.createdByUserName
                                        }
                                    }

                                },
                                'p-1': {
                                    class: '',
                                    text: dateString
                                },
                                'p-2': {
                                    class: '',
                                    text: timeString
                                },
                                'span': {
                                    class: 'vtk-note_actions',
                                    child: this.getPermisionOfEdit(note)
                                }
                            }
                        }
                        // div: {
                        //     class: "vtk-note_content small-24 medium-18 columns",
                        //     html: note.message
                        // },
                        // 'div-0': {
                        //     class: "vtk-note_detail small-24 medium-6  columns",

                        //     child: {
                        //         'p': {
                        //             class: '',
                        //             child: {
                        //                 strong: {
                        //                     text: note.createdByUserName
                        //                 }
                        //             }

                        //         },
                        //         'p-1': {
                        //             class: '',
                        //             text: dateString
                        //         },
                        //         'p-2': {
                        //             class: '',
                        //             text: timeString
                        //         },
                        //         'span': {
                        //             class: 'vtk-note_actions',
                        //             child: this.actions(note)
                        //         }
                        //     }
                        // },
                        // 'div-1': {
                        //     class: 'vtk-note_container',

                        // }
                    }
                }
            }


            return template;
        }
    }
    var editor = {
        $el: '',

        applyFormat: function (e) {
            console.log(e);
            e.preventDefault();
            var command = $(this).data('command');

            if (command == 'h1' || command == 'h2' || command == 'p') {
                document.execCommand('formatBlock', false, command);
            }
            if (command == 'forecolor' || command == 'backcolor') {
                document.execCommand($(this).data('command'), false, $(this).data('value'));
            }
            if (command == 'createlink' || command == 'insertimage') {
                url = prompt('Enter the link here: ', 'http:\/\/');
                document.execCommand($(this).data('command'), false, url);
            } else document.execCommand($(this).data('command'), false, null);
        },
        template: function () {
            return {
                div: {
                    class: 'format',
                    child: {
                        a: {
                            attr: {
                                href: '#'
                            },
                            class: 'format-buttom format-bold',
                            data: {
                                command: 'bold'
                            },
                            events: {
                                click: editor.applyFormat
                            },
                            text: 'B'
                        },
                        'a-1': {
                            attr: {
                                href: '#'
                            },
                            class: 'format-buttom format-italic',
                            data: {
                                command: 'italic'
                            },
                            events: {
                                click: editor.applyFormat
                            },
                            text: 'i'
                        },

                        'a-3': {
                            attr: {
                                href: '#'
                            },
                            class: 'format-buttom format-p',
                            data: {
                                command: 'p'
                            },
                            events: {
                                click: editor.applyFormat
                            },
                            text: 'P'
                        },
                        'a-4': {
                            attr: {
                                href: '#'
                            },
                            class: 'format-buttom format-h',
                            data: {
                                command: 'h1'
                            },
                            events: {
                                click: editor.applyFormat
                            },
                            text: 'H1'
                        },
                        'a-5': {
                            attr: {
                                href: '#'
                            },
                            class: 'format-buttom format-h',
                            data: {
                                command: 'h2'
                            },
                            events: {
                                click: editor.applyFormat
                            },
                            text: 'H2'
                        }
                    }

                }
            }
        },
        render: function (element) {

            var el = utility.compileTemplate(editor.template());

            editor.$el = $(el);
            if (element) {
                element.append(editor.$el);
            } else {
                return el;
            }


        },

        destroy: function () {
            editor.$el.remove();
        }

    }
    var counter = {
        template: {
            counter: function (number) {
                return {
                    div: {
                        class: 'vtk-word-counter',
                        text: number,
                    }
                }
            },
            format: {
                ul: {
                    child: {
                        li: {

                        },
                        'li-1': {

                        },
                        'li-2': {

                        }
                    }
                }
            }
        },
        methods: {
            el: '',

            checkerF: function (element) {
                var l = 500 - element.text().length;
                var style = {};
                var element;

                if (l >= 500 && l <= 301) {
                    style = {
                        color: 'green',
                        opacity: 0.4

                    }
                } else if (l >= 150 && l <= 300) {
                    style = {
                        color: 'orange',
                        opacity: 0.7

                    }
                } else if (l < 150) {
                    style = {
                        color: 'red',
                        opacity: 1

                    }
                }
                $(counter.methods.el).css(style);
                return l;
            },
            render: function (element) {
                 counter.methods.el = utility.compileTemplate(counter.template.counter(''));
                if (element) {
                 element.append(counter.methods.el);
                 counter.methods.textChange(element.parents('.vtk-note_wrap_content').children('.row').children('.vtk-note_content'))
                } else {
                    return counter.methods.el;

                }



            },
            textChange: function (element) {
                $(counter.methods.el).text(counter.methods.checkerF(element));
            },
            destroy: function () {
                $(counter.methods.el).remove();
            }
        }
    }

    function checkQuantityNotes(number) {
        if (number <= 25) {
            $('.add-notes-area').show();
            $('.you-reach-25').hide();
        } else {
            $('.add-notes-area').hide();
            $('.you-reach-25').show();
        }
    }

    function interateNotes(notes) {
        var node = $('.vtk-notes_list_container');

        node.html('');

        notes.forEach(function (note, idx) {
            node.append(utility.compileTemplate(view.newNote(note)))
        });


        checkQuantityNotes(notes.length);
    }

    function getNotes(mid,auid) {

        globalMid = mid;
        if (auid) {
            userLoginId = auid;
        }


        $.ajax({
            url: "/content/girlscouts-vtk/controllers/vtk.controller.html",
            cache: false,
            type: 'GET',
            dataType: 'json',

            data: {
                getNotes: "true",
                mid: mid,

                a: Date.now()
            }

        }).success(function (json) {

            interateNotes(json);

        }).fail(function (err) {

        }).done(function (html) {



        });
    }

    $(function () {
        var editormain = Object.create(editor);
        var countermain = Object.create(counter);


        $('.add-note-detail').append(utility.compileTemplate({
            div: {
                class: 'container',
                component: {
                    editor: editormain,
                    counter:countermain.methods
                },
                // html: countermain.methods.reder()

            }
        }))

        countermain.methods.textChange($('.input-content'));

            $('.input-content').on('keyup', function (e) {
                var l, memory;
                memory = $(this)[0].innerHTML;
                l = memory.length;

                countermain.methods.textChange($('.input-content'));
            });


        $('.add-note').on('click', function (e) {
            $('.add-note-detail').stop().slideToggle();
            });


         modal.init();
    });

    return {
        getNotes: getNotes,
        addNote: addNote
    };

})(this, modal, $);





