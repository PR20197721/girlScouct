var troopListing = $("#troop-listing-config").data("troop-listing");
var filterDistancePerMiles = $("#filter-distance-per-miles").attr("data") || "500";
var boothListingApiURL = $("#booth-listing-api-url").attr("data") || "/cookiesapi/booth_list_merged.asp";
var boothListingLookupApiURL = $("#booth-listing-lookup-api-url").attr("data") || "/includes/cookie/booth_detail_lookup.asp";
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
        //GSAWDO-2 , if troop listing component is active in DOM (based on campaign ON/OFF time authored in component), then we have to set default values of troop listing and booth finder both to
        //radius = 5000, date =  2 months (60), sortBy = distance
        var radius, date, sortBy;
        radius = getParameterByName('radius');
        date = getParameterByName('date');
        sortBy = getParameterByName('sortBy');

        if(troopListing && !( radius && date && sortBy )){
			radius = filterDistancePerMiles;
	        date = 60; 
	        sortBy = 'distance';
        }else{
			if (!radius) radius = filterDistancePerMiles;
	        if (!date) date = 60;
	        if (!sortBy) sortBy = 'distance';
        }

        boothFinder = new BoothFinder(boothListingApiURL, zip, radius, date, sortBy, numPerPage /*numPerPage*/ );
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
        data.GACampaign = gaparam;
    }
    gaparam = getParameterByName('utm_medium');
    if (gaparam) {
        data.GAMedium = gaparam;
    }
    gaparam = getParameterByName('utm_source');
    if (gaparam) {
        data.GASource = gaparam;
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
    if (!council.CouncilCode) { // Council Code not found. Council does not exist.
        templateId = 'notfound';
    } else if (booths.length != 0) {
        templateId = 'booths';
        council.shoudShowContactUsFormAfterListing = council.PreferredPath.toLowerCase() == 'path2';
        var nearestDistance = Number.MAX_VALUE;
        this.shouldHideMoreButton = booths.length <= this.numPerPage;
        var min = Math.min(booths.length, this.numPerPage); // length - 1 to omit the "more" one
        for (var boothIndex = 0; boothIndex < min; boothIndex++) {
            var booth = booths[boothIndex];
            // Add index field
            booth.ID = boothIndex;
            booth.detailsText = "View Details";
            // Add zip field to booth. "View Detail" needs this info.
            booth.queryZip = this.zip;
            if (Number(booth.Distance) < nearestDistance) {
                nearestDistance = Number(booth.Distance);
            }
            if(booth.Address1 != null && (booth.Address1.includes("http://") || booth.Address1.includes("https://"))){
                var fixedUrl = booth.Address1;
                booth.Location = "<a href=\""+fixedUrl+"\" target=\"_blank\">"+booth.Location+"</a>";
                booth.Address1 = "";
                booth.Address2 = "";
                booth.detailsText = "Get Cookies";
                booth.visitBoothUrl = fixedUrl;
            }
            console.log(booth)
        }

        // Remove "more" items
        booths.splice(min, booths.length - min);

        result.env.nearestDistance = nearestDistance;
    } else {
        templateId = result.council.PreferredPath.toLowerCase(); // e.g. path1
        if (templateId == 'path1' || templateId == 'path2') {
            setTimeout(function() {
				//GSAWDO-2 , if troop listing component is active in DOM (based on campaign ON/OFF time authored in component), then we have to set default values of troop listing and booth finder both to
		        //radius = 5000, date =  2 months (60), sortBy = distance
		        var radiusEmpty, dateEmpty, sortByEmpty;
		        radiusEmpty = getParameterByName('radius');
		        dateEmpty = getParameterByName('date');
		        sortByEmpty = getParameterByName('sortBy');
                if (!radiusEmpty) radiusEmpty = filterDistancePerMiles;
                if (!dateEmpty) dateEmpty = 60;
                if (!sortByEmpty) sortByEmpty = 'distance'
                $('select[name="radius"]').val(radiusEmpty);
                $('select[name="date"]').val(dateEmpty);
                $('select[name="sortBy"]').val(sortByEmpty);
                $('#emptyForm').show();
                 
            }, 1000);
        }
    }

    // "Contact local council" form data
    result.contactBanner = {
        btn: "Contact Your Local Council",
        title: "Cookies are Here!",
        desc: "Enter your info below and girls from the " + council.CouncilName + " will contact you to help you place your cookie order."
    }

    // Calculate days left
    var daysLeft = moment(council.CookieSaleStartDate, 'M/D/YYYY').diff(moment(), 'days') + 1;
    result.council.DaysLeft = daysLeft;
    result.council.DaysLeftStr = daysLeft + ' day';
    result.council.DaysLeftStrUpper = daysLeft + ' Day';
    if (daysLeft !== 1) {
        result.council.DaysLeftStr += 's';
        result.council.DaysLeftStrUpper += 's';
    }


    if (this.page == 1) {
        if (templateId == 'booths') {
            // if templateId is booths, the council is either path 1 or path 2
            // display both template-path1/2 (here) and template-booths (below)
            var templatePathID = 'template-' + council.PreferredPath.toLowerCase();
            var html = Handlebars.compile($('#' + templatePathID).html())(result);
            $('#booth-finder-result').html(html);
        }

        // this part is diplayed in all case scenarios besides booth finder show more
        // in case there are booths, it compiles template-booths
        // if there are no booths, it compiles template-path4, template-path5, notfound
        var templateDOMId = 'template-' + templateId; // template-path1; <-- Actually, it is template-booths
        var html = Handlebars.compile($('#' + templateDOMId).html())(result);
        $('#booth-finder-result').append(html);
    } else {
        var templateDOMId = 'template-more-booths';
        var html = Handlebars.compile($('#' + templateDOMId).html())(result);
        $('.booth-finder .show-more').before(html);
    }

    if (templateId == 'booths') {
        // Bind "View Details" buttons
        $('.booth-finder .viewmap.button').on('click', function() {
            var booth = JSON.parse($(this).attr('data'));
            if(booth.detailsText == "Get Cookies"){
                window.open(booth.visitBoothUrl);
            }else {
                var data = $.param(booth) + '&' +
                    'fbTitle=' + encodeURIComponent($('#share-map-FBTitle').attr('data')) + '&' +
                    'fbDesc=' + encodeURIComponent($('#share-map-FBDesc').attr('data')) + '&' +
                    'tweet=' + encodeURIComponent($('#share-map-Tweet').attr('data')) + '&' +
                    'shareImgPath=' + encodeURIComponent($('#share-map-FBImgPath').attr('data'));
                console.log(booth);

                $('#modal_booth_item_map').foundation('reveal', 'open', {
                    url: '' + $("#booth-finder-details").data('res-path') + '.booth-detail.html',
                    cache: false,
                    processData: false,
                    data: data
                });
                $(document).on('opened.fndtn.reveal', '[data-reveal]', function () {
                    boothDetails = JSON.parse($('#boothDetailInfo').attr('data-booth'));
                    var functionName = 'postToFeed' + boothDetails.uniqueID;
                    //booth-detail and share-modal script -- start
                    var scriptTag = document.createElement("script");
                    scriptTag.type = "text/javascript"
                    scriptTag.src = "//connect.facebook.net/en_US/all.js";
                    scriptTag.async = true;
                    document.getElementsByTagName("head")[0].appendChild(scriptTag);
                    scriptTag.onload = initFB;
                    scriptTag.onreadystatechange = function () {
                        if (this.readyState == 'complete' || this.readyState == 'loaded') initFB();
                    }

                    window[functionName] = function () {

                        // calling the API ...
                        var obj = {
                            method: 'feed',
                            link: window.location.href,
                            name: '' + boothDetails.facebookTitle + '',
                            picture: location.host + '' + boothDetails.imgPath + '',
                            caption: 'WWW.GIRLSCOUTS.ORG',
                            description: '' + boothDetails.facebookDesc + ''
                        };

                        function callback(response) {
                        }

                        FB.ui(obj, callback);
                    }

                });
                $('.off-canvas-wrap').addClass('noprint');
                //GSDO-1024 :multiple-gmaps-api-call :Start
                $(document).on('closed.fndtn.reveal', '[data-reveal]', function () {
                    //removes gmaps traces from DOM.
                    if (window.google !== undefined && google.maps !== undefined) {
                        delete google.maps;
                        $('script').each(function () {
                            if (this.src.indexOf('googleapis.com/maps') >= 0 ||
                                this.src.indexOf('maps.gstatic.com') >= 0 ||
                                this.src.indexOf('earthbuilder.googleapis.com') >= 0) {
                                $(this).remove();
                            }
                        });
                    }
                })
            }
            //GSDO-1024 :multiple-gmaps-api-call :End

            //Lookup Call
            var value = JSON.parse($(this).attr("data"));
            var data = {
                l : value.Location,
                d : value.DateStart,
                z : value.ZipCode,
                s : "Website",
                cn : getParameterByName('utm_campaign'),
                cm : getParameterByName('utm_medium'),
                cs : getParameterByName('utm_source'),
                a1 : value.Address1,
                a2 : value.Address2
            }

            $.ajax({
                url: boothListingLookupApiURL,
                dataType: "json",
                data: data,
                success: function(data) {
                if (data) {
                    console.log('Redirecting from BoothFinder');
                } else {
                    console.log('Error occured in redirecting');
                }
                }
            });
        });

        if (this.page == 1) {
            // Reset form values

			//GSAWDO-2 , if troop listing component is active in DOM (based on campaign ON/OFF time authored in component), then we have to set default values of troop listing and booth finder both to
	        //radius = 5000, date =  2 months (60), sortBy = distance
	        var radius, date, sortBy;
	        radius = getParameterByName('radius');
	        date = getParameterByName('date');
	        sortBy = getParameterByName('sortBy');
	        
            if (!radius) radius = filterDistancePerMiles;
            if (!date) date = 60;
            if (!sortBy) sortBy = 'distance';
            $('select[name="radius"]').val(radius);
            $('select[name="date"]').val(date);
            $('select[name="sortBy"]').val(sortBy);
	        

            // Bind click more
            $('.booth-finder #more').on('click', function() {
                boothFinder.getResult();
            });
        }
    }

    // Share dialog
    var showShareDialog = $('#share-showShareDialog').attr('data') == 'true';
    const shareModelEle = $("#booth-finder-result").find(".share-modal");
    if (showShareDialog && shareModelEle && shareModelEle.length ===0) {
        var shareModalHtml = Handlebars.compile($('#template-sharemodal').html())({
            buttonCaption: "SHARE WITH YOUR FRIENDS",
            header: $('#share-shareDialogHeader').attr('data'),
            desc: $('#share-shareDialogDescription').attr('data'),
            tweet: $('#share-shareDialogTweet').attr('data'),
            imageFilePath: $('#share-shareDialogImagePath').attr('data'),
            url: window.location.href,
            uniqueID: Math.floor((Math.random() * 1000) + 1),
            facebookId: $("#booth-finder-details").data('fb-id')
        });
        $('#booth-finder-result').append(shareModalHtml);
    }

    // Setup contact local council form
    setupContactLocalCouncilForm();

    if (council.CouncilAbbrName) {
        var boothForm = $('form.contactlocalcouncil');
        var hiddenCouncilNameInput = boothForm.find('input[name="CouncilAbbrName"]');
        if (hiddenCouncilNameInput.length < 1) {
            hiddenCouncilNameInput = $('<input>').attr('type', 'hidden').attr('name', 'CouncilAbbrName');
            hiddenCouncilNameInput.val(council.CouncilAbbrName);
            hiddenCouncilNameInput.appendTo(boothForm);
        } else {
            hiddenCouncilNameInput.val(council.CouncilAbbrName);
        }
    }

    // Increase page count
    this.page++;

    // Hide "more" link if there is no more result
    if (this.shouldHideMoreButton) {
        $('.booth-finder #more').hide();
    }

    // Attach ajax to all council links
    var cname = council.CouncilName;
    var cdata = {
        z: this.zip
    };

    $('a:contains(' + cname + ')').on('click', function() {
        $.ajax({
            url: "/cookiesapi/booth_list_link_visits.asp",
            data: cdata,
            datatype: "json"
        });
    });
    // Reset foundation again since new tags are added.
    $(document).foundation();
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
