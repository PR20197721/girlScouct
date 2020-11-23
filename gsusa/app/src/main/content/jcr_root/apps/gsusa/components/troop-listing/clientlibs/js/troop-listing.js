$(document).ready(function() {
    var map;
    var geocoder;
    var troopListingZip;
    var boothDetails;
    var numPerPage = $("#troop-listing-details").data("num-per-page");
    var showOneLink = $("#troop-listing-config").data("show-one-link");
    var supportAnotherTroop = $("#troop-listing-config").data("support-another-troop");
    LoadGoogle();
    // Get zip from param
    troopListingZip = getParameterByName('zip');
    // Get zip from hash
    troopListingZip = (function(troopListingZip) {
        var hash = window.location.hash;
        if (hash.indexOf('#') == 0) {
            hash = hash.substring(1);
        }
        var zipRegex = /[0-9]{5}/;
        return zipRegex.test(hash) ? hash : troopListingZip;
    })(troopListingZip);

    if (troopListingZip == undefined) {
        // TODO: error: zip not found.
    } else {
        var troopListingRadius,troopListingDate,troopListingSortBy;
        //accessing troopListingFilterObj from session and getting the set filters
        if(sessionStorage.troopListingFilterObj){
            var troopListingFilterObj = JSON.parse(sessionStorage.troopListingFilterObj);
            if(troopListingFilterObj){
                troopListingRadius = troopListingFilterObj['troopListingRadius'];
                troopListingDate = troopListingFilterObj['troopListingDate']
                troopListingSortBy = troopListingFilterObj['troopListingSortBy'];
            }
        }
        if (!troopListingRadius) troopListingRadius = 5000;
        if (!troopListingDate) troopListingDate = 60;
        if (!troopListingSortBy) troopListingSortBy = 'distance';
        //Code for Troop Listing, creating new parameter as

        troopListing = new TroopListing("/content/dam/gsusa/api/trooplisting.asp", troopListingZip, troopListingRadius, troopListingDate, troopListingSortBy, numPerPage /*numPerPage*/, showOneLink, supportAnotherTroop );
        troopListing.getResult();
    }
});


function TroopListing(url, troopListingZip, troopListingRadius, troopListingDate, troopListingSortBy, numPerPage , showOneLink, supportAnotherTroop) {
    this.url = url;
    this.zip = troopListingZip;
    this.radius = troopListingRadius;
    this.date = troopListingDate;
    this.sortBy = troopListingSortBy;
    this.numPerPage = numPerPage;
    this.showOneLink = showOneLink;
    this.supportAnotherTroop = supportAnotherTroop;
    this.page = 1;
}

TroopListing.prototype.getResult = function() {
    var data = {
        z: this.zip,
        r: this.radius,
        d: this.date,
        t: this.sortBy,
        s: (this.page - 1) * this.numPerPage + 1,
        m: this.numPerPage + 1, // Plus 1 to see if there are more results
        f: 'Website' // call is made from website
    };

    var gaparam = getParameterByName('utm_campaign');
    if (gaparam) {
        data.GSCampaign = gaparam;
    }
    gaparam = getParameterByName('utm_medium');
    if (gaparam) {
        data.GSMedium = gaparam;
    }
    gaparam = getParameterByName('utm_source');
    if (gaparam) {
        data.GSSource = gaparam;
    }

    $.ajax({
        url: this.url,
        dataType: "json",
        data: data,
        success: TroopListing.prototype.processResult.bind(this)
    });
}

TroopListing.prototype.processResult = function(result) {
    //Sorting the result to get the nearest first.
    result.Troops = result.Troops.sort(function(a, b){return a.Distance-b.Distance});
    var troops = result.Troops;
    // Add zip to environment
    result = result || {};
    result.env = result.env || {};
    result.env.zip = this.zip;
    if (troops.length != 0) {
        // there are troops available for given filter search
        var min = Math.min(troops.length, this.numPerPage); // length - 1 to omit the "more" one
        for (var troopIndex = 0; troopIndex < min; troopIndex++) {
             var troop = troops[troopIndex];
                if(troop.StoreURL != null && (troop.StoreURL.includes("http://") || troop.StoreURL.includes("https://"))){
                troop.Location = "<a href=\""+troop.StoreURL+"\" target=\"_blank\">"+troop.TroopName+"</a>";
                troop.detailsText = "Get Cookies";
                troop.City = troop.City;
                troop.Distance = troop.Distance;
                troop.State = troop.State;
                troop.ZipCode = troop.ZipCode;
                troop.DateEnd = troop.DateEnd;
                troop.visitBoothUrl = troop.StoreURL;
            }
            console.log(troop)
        }
        //if showOneLink is true then return only the first result set.
        if(this.showOneLink){
          // Remove "more" items
          troops.splice(1);
        }
        var templatePathID = 'template-troop-listing';
        var html = Handlebars.compile($('#' + templatePathID).html())(result);
        $('#troop-listing-result').html(html);

        formDataReset();
    }

}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}


//booth-detail-script - Start
function LoadGoogle() {
    if (typeof google != 'undefined' && google && google.load) {
        google.load("maps", "3", {
            callback: initMap
        });
    } else {
        setTimeout(LoadGoogle, 30);
    }
}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8,
        center: {
            lat: -0.000,
            lng: 150.000
        }
    });

    geocoder = new google.maps.Geocoder;
    setTimeout(function() {
        doIt();
    }, 1000);
}

function doIt() {
    codeAddress(map, geocoder);
    google.maps.event.trigger(map, 'resize');
    google.maps.event.trigger(map, 'center');

    // to correct gray box issue on chrome and chrome mobile
    $('#map').css('visibility', 'hidden');
    // $('#map').css('overflow','auto');
    setTimeout(function() {
        $('#map').css('position', 'absolute');
        setTimeout(function() {
            $('#map').css('position', 'relative');
            $('#map').css('visibility', 'visible');
        }, 500);
    }, 500);

}

function codeAddress(resultsMap, geocoder) {
    var address = "" + boothDetails.address + "";
    var latitude = "" + boothDetails.latitudeData + "";
    var longitude = "" + boothDetails.longitudeData + "";
    if (latitude.length > 0 && longitude.length > 0) {
        var myLatLng = {
            "lat": parseFloat(parseFloat(latitude).toFixed(6)),
            "lng": parseFloat(parseFloat(longitude).toFixed(6))
        };
        var marker = new google.maps.Marker({
            map: resultsMap,
            zoom: 8,
            position: myLatLng
        });
        resultsMap.setCenter(myLatLng);

    } else {

        geocoder.geocode({
            'address': address
        }, function(results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                var marker = new google.maps.Marker({
                    map: resultsMap,
                    zoom: 8,
                    position: results[0].geometry.location
                });
                resultsMap.setCenter(results[0].geometry.location);

                // save the lat/long
                var formData = {
                    "a1": "" + boothDetails.address1 + "",
                    "z": "" + boothDetails.addressZip + "",
                    "lat": results[0].geometry.location.lat().toFixed(6),
                    "long": results[0].geometry.location.lng().toFixed(6)
                };
                console.log(formData);
                $.ajax({
                    method: "POST",
                    url: "/cookiesapi/booth_list_geo_insert.asp",
                    data: formData
                });

            } else {
                console.log('Geocode was not successful for the following reason: ' + status);
            }
        });
    }

    map.addListener('click', function() {
        var addressEncode = encodeURI("" + boothDetails.address + "");
        window.open("http://maps.google.com/maps/dir/" + boothDetails.zip + "/" + addressEncode + "");
    });
}


function initFB() {
    FB.init({
        appId: "" + boothDetails.facebookId + "",
        status: true,
        cookie: true
    });
}
//booth-detail-script - End

//function getUpdatedFilterResult, this gets called when someone change any filter value.
function getUpdatedTroopListingFilterResult(event){
    var troopListingFilterObj = {};
    var troopListingZip = $('input[name="troopListingZip"]').val();

    if(event.target.name=="troopListingRadius"){
        troopListingFilterObj["troopListingRadius"] = event.target.value;
        troopListingFilterObj["troopListingSortBy"] = $('select[name="troopListingSortBy"]').val();
        troopListingFilterObj["troopListingSortBy"] = $('select[name="troopListingSortBy"]').val();
    }else if(event.target.name=="troopListingDate"){
        troopListingFilterObj["troopListingDate"] = event.target.value;
        troopListingFilterObj["troopListingRadius"] = $('select[name="troopListingRadius"]').val();
        troopListingFilterObj["troopListingSortBy"] = $('select[name="troopListingSortBy"]').val();
    } else if(event.target.name=="troopListingSortBy"){
        troopListingFilterObj["troopListingSortBy"] = event.target.value;
        troopListingFilterObj["troopListingRadius"] = $('select[name="troopListingRadius"]').val();
        troopListingFilterObj["troopListingDate"] = $('select[name="troopListingDate"]').val();
    }
    troopListingFilterObj["troopListingZip"] = troopListingZip;
    sessionStorage.setItem('troopListingFilterObj', JSON.stringify(troopListingFilterObj));
    window.location.reload();
}

function formDataReset(){
            // Reset form values
            //accessing troopListingFilterObj from session and getting the set filters
            var troopListingFilterObj = JSON.parse(sessionStorage.troopListingFilterObj);
            var troopListingRadius = troopListingFilterObj['troopListingRadius'];
            var troopListingDate = troopListingFilterObj['troopListingDate']
            var troopListingSortBy = troopListingFilterObj['troopListingSortBy'];
            if (!troopListingRadius) troopListingRadius = 5000;
            if (!troopListingDate) troopListingDate = 60;
            if (!troopListingSortBy) troopListingSortBy = 'distance'
            $('select[name="troopListingRadius"]').val(troopListingRadius);
            $('select[name="troopListingDate"]').val(troopListingDate);
            $('select[name="troopListingSortBy"]').val(troopListingSortBy);

            // Bind click more
            $('.troop-listing #more').on('click', function() {
                troopListing.getResult();
            });
}
