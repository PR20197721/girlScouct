$(document).ready(function() {

    $("#eventSearchSubmit").on('click', function(){
        $(".event-search-facets").find("form").submit();
    });
    $("#advSearch").on('click', function(){
        var ref = $("#advSearch").find("a").attr("href");
        var searchInput = $(".event-search-facets").find("input");
        var searchVal = searchInput.val();
        if(searchVal == searchInput.attr('placeholder')){
            searchVal = "";
        }
        if(searchVal !== ""){
            //GSWP-2056-used indexOf to support IE
            if(ref.indexOf("?search=") === -1){
                ref = ref + "?search=" + searchVal;
            }else{
               ref = ref.replace(ref.substring(ref.indexOf("?search=")), "?search=" + searchVal);
            }
             $("#advSearch").find("a").attr("href", ref)
        }
    });

    //advanced search
    var placeholder = $("#keywordInput").attr("placeholder");
    if (typeof placeholder !== 'undefined' && placeholder !== false) {
        	//GSWP-2056-used indexOf to support IE
            if (placeholder.indexOf('Keywords') === -1) {
                $("#keywordInput").val(placeholder);
            }
    }

    //advanced search

    $("#smplSearch").on('click', function() {
        var ref = $("#smplSearch").attr("href");
        if ($(".event-search-facets").find("input[name=q]").val() !== "") {
            //GSWP-2056-used indexOf to support IE
            if (ref.indexOf("?search=") === -1) {
                ref = ref + "?search=" + $(".event-search-facets").find("input[name=q]").val();
            } else {
                ref = ref.replace(ref.substring(ref.indexOf("?search=")), "?search=" + $(".event-search-facets").find("input[name=q]").val());
            }
            $("#smplSearch").attr("href", ref);
        }


    });

});

function toggleWhiteArrow() {
    $('#events-display').toggle();
    if ($('#whiteArrowImg').attr('src') == "/etc/designs/girlscouts-usa-green/images/white-right-arrow.png") {
        $('#whiteArrowImg').attr('src', "/etc/designs/girlscouts-usa-green/images/white-down-arrow.png");
    } else {
        $('#whiteArrowImg').attr('src', "/etc/designs/girlscouts-usa-green/images/white-right-arrow.png");
    }
}

$(function() {

    $("#startdtRange").datepicker({
        minDate: 0,
        beforeShowDay: function(d) {


            if ($('#enddtRange').val() == "" || $('#enddtRange').val() == undefined) {
                return [true, "", "Available"];
            }

            var dateString = (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear();

            //alert((new Date(dateString) < new Date($('#sch_endDate').val())) + " " + dateString + "<" + $('#sch_endDate').val());

            if (+(new Date(dateString)) <= +(new Date($('#enddtRange').val()))) {
                return [true, "", "Available"];
            }

            return [false, "", "unAvailable"];

        }
    });

    $("#enddtRange").datepicker({
        minDate: 0,
        beforeShowDay: function(d) {


            if ($('#startdtRange').val() == "" || $('#startdtRange').val() == undefined) {
                return [true, "", "Available"];
            }

            var dateString = (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear();

            //alert((new Date(dateString) < new Date($('#sch_endDate').val())) + " " + dateString + "<" + $('#sch_endDate').val());

            if (+(new Date(dateString)) >= +(new Date($('#startdtRange').val()))) {
                return [true, "", "Available"];
            }

            return [false, "", "unAvailable"];

        }
    });

});

function validateForm() {


    if ($('#enddtRange').val() == "" && $('#startdtRange').val() == "") {
        return true;
    }

    if (!isDate($('#enddtRange').val())) {
        displayError("Invalid End Date");
        return false;
    }

    if (!isDate($('#startdtRange').val())) {
        displayError("Invalid Start Date");
        return false;
    }


    if (new Date($('#enddtRange').val()) < new Date($('#startdtRange').val())) {
        displayError("End Date cannot be less than Start Date");

        return false;
    } else {
        document.getElementById("dateErrorBox").innerHTML = "";
        return true;
    }

}

function displayError(errorMessage) {
    document.getElementById("dateErrorBox").innerHTML = errorMessage;
    document.getElementById("dateTitle").style.color = "#FF0000";
    document.getElementById("dateErrorBox").style.color = "#FF0000";
    document.getElementById("dateErrorBox").style.fontSize = "x-small";
    document.getElementById("dateErrorBox").style.fontWeight = "bold";
    document.getElementById("eventsTitle").scrollIntoView();

}


function isDate(txtDate) {
    var currVal = txtDate;
    if (currVal == '')
        return false;

    var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/; //Declare Regex
    var dtArray = currVal.match(rxDatePattern); // is format OK?

    if (dtArray == null)
        return false;

    //Checks for mm/dd/yyyy format.
    dtMonth = dtArray[1];
    dtDay = dtArray[3];
    dtYear = dtArray[5];

    if (dtMonth < 1 || dtMonth > 12)
        return false;
    else if (dtDay < 1 || dtDay > 31)
        return false;
    else if ((dtMonth == 4 || dtMonth == 6 || dtMonth == 9 || dtMonth == 11) && dtDay == 31)
        return false;
    else if (dtMonth == 2) {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay > 29 || (dtDay == 29 && !isleap))
            return false;
    }
    return true;
}