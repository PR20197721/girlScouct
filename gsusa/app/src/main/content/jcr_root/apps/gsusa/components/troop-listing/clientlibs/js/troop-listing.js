$(document).ready(function() {
    var map;
    var geocoder;
    var zip;
    var boothDetails;
    var numPerPage = $("#booth-finder-details").data("num-per-page");
    LoadGoogle();
    // Get zip from param
    zip = getParameterByName('zip');
    // Get zip from hash
    zip = (function(zip) {
        var hash = window.location.hash;
        if (hash.indexOf('#') == 0) {
            hash = hash.substring(1);
        }
        var zipRegex = /[0-9]{5}/;
        return zipRegex.test(hash) ? hash : zip;
    })(zip);

    if (zip == undefined) {
        // TODO: error: zip not found.
    } else {
        var radius,date,sortBy;
        //accessing boothFinderFilterObj from session and getting the set filters
        if(sessionStorage.boothFinderFilterObj){
            var boothFinderFilterObj = JSON.parse(sessionStorage.boothFinderFilterObj);
            if(boothFinderFilterObj){
                radius = boothFinderFilterObj['radius'];
                date = boothFinderFilterObj['date']
                sortBy = boothFinderFilterObj['sortBy'];
            }
        }
        if (!radius) radius = 500;
        if (!date) date = 60;
        if (!sortBy) sortBy = 'distance';
        //Code for Troop Listing, creating new parameter as

        boothFinder = new BoothFinder("/content/dam/gsusa/api/booth_list_merged.asp", zip, radius, date, sortBy, numPerPage /*numPerPage*/ );
        boothFinder.getResult();
    }
});


function BoothFinder(url, zip, radius, date, sortBy, numPerPage) {
    this.url = url;
    this.zip = zip;
    this.radius = radius;
    this.date = date;
    this.sortBy = sortBy;
    this.numPerPage = numPerPage;

    this.page = 1;
}

BoothFinder.prototype.getResult = function() {
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
        success: BoothFinder.prototype.processResult.bind(this)
    });
}

BoothFinder.prototype.processResult = function(result) {
    var council = result.council;
    var booths = result.booths;
    // Add zip to environment
    result = result || {};
    result.env = result.env || {};
    result.env.zip = this.zip;
    var templateId;

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

//function getUpdatedFilterResult
function getUpdatedFilterResult(event){
    var boothFinderFilterObj = {};
    var zip = $('input[name="zip"]').val();

    if(event.target.name=="radius"){
        boothFinderFilterObj["radius"] = event.target.value;
        boothFinderFilterObj["sortBy"] = $('select[name="sortBy"]').val();
        boothFinderFilterObj["date"] = $('select[name="date"]').val();
    }else if(event.target.name=="date"){
        boothFinderFilterObj["date"] = event.target.value;
        boothFinderFilterObj["radius"] = $('select[name="radius"]').val();
        boothFinderFilterObj["sortBy"] = $('select[name="sortBy"]').val();
    } else if(event.target.name=="sortBy"){
        boothFinderFilterObj["sortBy"] = event.target.value;
        boothFinderFilterObj["radius"] = $('select[name="radius"]').val();
        boothFinderFilterObj["date"] = $('select[name="date"]').val();
    }
    boothFinderFilterObj["zip"] = zip;
    sessionStorage.setItem('boothFinderFilterObj', JSON.stringify(boothFinderFilterObj));
    window.location.reload();
}
