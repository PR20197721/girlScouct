//Global variables
var numPerPage = $("#troop-listing-details").data("num-per-page");
var showOneLink = $("#troop-listing-config").data("show-one-link");
var supportAnotherTroop = $("#troop-listing-config").data("support-another-troop");
var cookieButtonColor = $("#troop-listing-config").data("get-cookie-button-color");
var cookieButtonHoverColor = $("#troop-listing-config").data("hover-button-color");
var cookieButtonTextColor = $("#troop-listing-config").data("text-color");
var cookieButtonText = $("#troop-listing-config").data("text");

var anotherTroopButtonColor = $("#troop-listing-config").data("another-troop-button-color");
var anotherTroopHoverButtonColor = $("#troop-listing-config").data("another-troop-hover-button-color");
var anotherTroopTextColor = $("#troop-listing-config").data("another-troop-text-color");
var anotherTroopText = $("#troop-listing-config").data("another-troop-text");

var oneLinkCount = $("#troop-listing-config").data("one-link-count");
var troopListingApiURL = $("#troop-listing-config").data("troop-listing-api-url") || "/includes/cookie/trooplink_list_merged.asp";
var troopListingLookupApiURL = $("#troop-listing-config").data("troop-listing-lookup-api-url") || "/includes/cookie/trooplink_detail_lookup.asp";

//Creating a Global Object to pass on config values.
var troopListingConfigObj = {};
troopListingConfigObj["cookieButtonColor"] = cookieButtonColor;
troopListingConfigObj["cookieButtonHoverColor"] = cookieButtonHoverColor;
troopListingConfigObj["cookieButtonTextColor"] = cookieButtonTextColor;
troopListingConfigObj["cookieButtonText"] = cookieButtonText;
troopListingConfigObj["showOneLink"] = showOneLink;
troopListingConfigObj["supportAnotherTroop"] = supportAnotherTroop;
troopListingConfigObj["anotherTroopButtonColor"] = anotherTroopButtonColor;
troopListingConfigObj["anotherTroopHoverButtonColor"] = anotherTroopHoverButtonColor;
troopListingConfigObj["anotherTroopTextColor"] = anotherTroopTextColor;
troopListingConfigObj["anotherTroopText"] = anotherTroopText;

$(document).ready(function() {
  var troopListingZip;
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
    var troopListingRadius, troopListingDate, troopListingSortBy;
    //accessing troopListingFilterObj from session and getting the set filters
    if (sessionStorage.troopListingFilterObj) {
      var troopListingFilterObj = JSON.parse(sessionStorage.troopListingFilterObj);
      if (troopListingFilterObj) {
        troopListingRadius = troopListingFilterObj['troopListingRadius'];
        troopListingDate = troopListingFilterObj['troopListingDate']
        troopListingSortBy = troopListingFilterObj['troopListingSortBy'];
      }
    }
    if (!troopListingRadius) troopListingRadius = 5000;
    if (!troopListingDate) troopListingDate = 60;
    if (!troopListingSortBy) troopListingSortBy = 'distance';
    //Code for Troop Listing, creating new parameter as

    troopListing = new TroopListing(troopListingApiURL, troopListingZip, troopListingRadius, troopListingDate, troopListingSortBy, numPerPage /*numPerPage*/ );
    troopListing.getResult();
  }

  registerClickOfRegisterButton();
});


function TroopListing(url, troopListingZip, troopListingRadius, troopListingDate, troopListingSortBy, numPerPage) {
  this.url = url;
  this.zip = troopListingZip;
  this.radius = troopListingRadius;
  this.date = troopListingDate;
  this.sortBy = troopListingSortBy;
  this.numPerPage = numPerPage;
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

  $.ajax({
    url: this.url,
    dataType: "json",
    data: data,
    success: TroopListing.prototype.processResult.bind(this)
  });
}

TroopListing.prototype.processResult = function(result) {
  var troops = result.booths;
  if (troops == null && this.page == 1) {
    var templatePathID = 'template-troop-listing';
    var html = Handlebars.compile($('#' + templatePathID).html())(result);
    $('#troop-listing-result').html(html);
    var templatePathID = 'template-notfound';
    html = Handlebars.compile($('#' + templatePathID).html())(result);
    $('#troop-listing-result').append(html);
  } else if (troops && troops.length != 0) {

    //logic for Randomizing the 0 mile records so as to give a fair chance to every troop.
    //checking end index of zero Mile troops, as start will always be 0
    var zeroMileEndIndex = 0;
    for (var troopIndex = 0; troopIndex < troops.length; troopIndex++) {
        if(troops[troopIndex].Distance == 0 ){
            zeroMileEndIndex++;
        }
    }
	troops = shuffleZeroMilesTroop(troops,zeroMileEndIndex);

    /*
    //Sorting the result to get the nearest first.
    troops = troops.sort(function(a, b) {
      return a.Distance - b.Distance
    });*/
    // Add zip to environment
    result = result || {};
    result.env = result.env || {};
    result.env.zip = this.zip;

    this.shouldHideMoreButton = troops.length <= this.numPerPage;
    // there are troops available for given filter search
    var min = Math.min(troops.length, this.numPerPage); // length - 1 to omit the "more" one
    for (var troopIndex = 0; troopIndex < min; troopIndex++) {
      
      var troop = troops[troopIndex];
      if (troop.StoreURL != null && (troop.StoreURL.includes("http://") || troop.StoreURL.includes("https://"))) {
        troop.detailsText = "Get Cookies";
        troop.City = troop.City;
        troop.Distance = troop.Distance;
        troop.State = troop.State;
        troop.ZipCode = troop.ZipCode;
        troop.DateEnd = troop.DateEnd;

        //GSAWDO-84 Detect "utm_campaign", "utm_medium", "utm_source" from query parameters, and if present, append it to the Troop Link StoreURL
	    var utmCampaign = getParameterByName('utm_campaign');
	    var utmMedium = getParameterByName('utm_medium');
	    var utmSource = getParameterByName('utm_source');
	    //updating the StroreLink to append utm parameters
	    var troopStoreUrl = troop.StoreURL;
	    if(troopStoreUrl){
	        if(troopStoreUrl.indexOf('?') == -1){
	            if(utmCampaign && utmMedium && utmSource){
	                troopStoreUrl = troopStoreUrl+"?utm_campaign="+utmCampaign+"&utm_medium="+utmMedium+"&utm_source="+utmSource;
	            }
	        }else{
                if(utmCampaign && utmMedium && utmSource){
	                troopStoreUrl = troopStoreUrl+"&utm_campaign="+utmCampaign+"&utm_medium="+utmMedium+"&utm_source="+utmSource;
	            }
	        }
	    }
        troop.Location = "<a href=\"" + troopStoreUrl + "\" target=\"_blank\">" + troop.TroopName + "</a>";
        troop.visitBoothUrl = troopStoreUrl;
      }
    }

    // Remove "more" items
      troops.splice(min, troops.length - min);
    if (this.page == 1) {
      var templatePathID = 'template-troop-listing';
      var html = Handlebars.compile($('#' + templatePathID).html())(result);
      $('#troop-listing-result').html(html);

      // Bind click on more, this code is kept here, cause page counter 1 will only come once and this will not get register multiple times.
      $('.troop-listing #more').on('click', function() {
       $(".troop-listing .row.details").last().css({ 'border-bottom' : 'solid 1px #e1e1e1'});
       troopListing.getResult();
      });

    } else {
      var templateDOMId = 'template-more-troop-listing';
      var html = Handlebars.compile($('#' + templateDOMId).html())(result);
      $('.troop-listing .show-more').before(html);
    }
    // Hide "more" link if there is no more result
    if (this.shouldHideMoreButton) {
        $('.troop-listing #more').hide();
    }

    applyTroopListingConfigChanges(result);
	applySupportAnotherTroopConfig();
  }
  this.page++;
  //CALL TO HIDE THE BOTTOM BORDER OF THE LAST BUTTON
  fixLastResultBottomBorder();
}

function getParameterByName(name) {
  name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
  var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
  return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function applyTroopListingConfigChanges() {
  //if showOneLink is true then return only the first result set.
  if (troopListingConfigObj["showOneLink"]) {
    // Hide "extra " items from the list
    var troopListingItems = $(".troop-listing .details");
    for (var i = oneLinkCount; i < troopListingItems.length; i++) {
      $(troopListingItems[i]).hide();
    }
    //remove the bottom border of the last visible stuff on screen.
    $(troopListingItems[oneLinkCount-1]).css({ 'border-bottom' : '0px'});
  }

  // updating text,text color,hover color,color  for troop listing item button, if author has authored it
  var troopListingItemButton = $(".troop-listing .details .button");
  for (var i = 0; i < troopListingItemButton.length; i++) {
    // updating text,text color,hover color,color  for support another troop button, if author has authored it
    if (troopListingConfigObj["cookieButtonText"] != null) {
      $(troopListingItemButton[i]).text(troopListingConfigObj["cookieButtonText"]);
    }
    if (troopListingConfigObj["cookieButtonColor"] != null) {
      $(troopListingItemButton[i]).css("background-color", troopListingConfigObj["cookieButtonColor"]);
    }
    //not sure how it works
    if (troopListingConfigObj["cookieButtonHoverColor"] != null) {
      $(troopListingItemButton[i]).mouseenter(function() {
        $(this).css("background-color", troopListingConfigObj["cookieButtonHoverColor"]);
      })
      $(troopListingItemButton[i]).mouseleave(function() {
        $(this).css("background-color", troopListingConfigObj["cookieButtonColor"]);
      })
    }
    if (troopListingConfigObj["cookieButtonTextColor"] != null) {
      $(troopListingItemButton[i]).css("color", troopListingConfigObj["cookieButtonTextColor"]);
    }
  }

}

function applySupportAnotherTroopConfig() {
  //hide of supportAntherTroop section if supportAnotherTroop is not checked
  if (troopListingConfigObj["supportAnotherTroop"] && troopListingConfigObj["showOneLink"]) {
    $(".troop-listing .show-more").addClass("hide");
    $(".supportAnotherTroopSection").removeClass("hide");
  }else if(troopListingConfigObj["showOneLink"]){
    $(".troop-listing .show-more").addClass("hide");
  }
  // updating text,text color,hover color,color  for support another troop button, if author has authored it
  if (troopListingConfigObj["anotherTroopText"] != null) {
    $(".troop-listing #supportAnotherTroopButton").text(troopListingConfigObj["anotherTroopText"]);
  }
  if (troopListingConfigObj["anotherTroopTextColor"] != null) {
    $(".troop-listing #supportAnotherTroopButton").css("color", troopListingConfigObj["anotherTroopTextColor"]);
  }
  //not sure how it works
  if (troopListingConfigObj["anotherTroopHoverButtonColor"] != null) {
    $(".troop-listing #supportAnotherTroopButton").mouseenter(function() {
      $(this).css("background-color", troopListingConfigObj["anotherTroopHoverButtonColor"]);
    })
    $(".troop-listing #supportAnotherTroopButton").mouseleave(function() {
      $(this).css("background-color", troopListingConfigObj["anotherTroopButtonColor"]);
    })
  }
  if (troopListingConfigObj["anotherTroopButtonColor"] != null) {
    $(".troop-listing #supportAnotherTroopButton").css("background-color", troopListingConfigObj["anotherTroopButtonColor"]);
  }

  // Bind click on supportAnotherTroopButton, if clicked we need to hide this button and show the rest of the result, along with load more.
  $('.troop-listing #supportAnotherTroopButton').on('click', function() {
    $(".supportAnotherTroopSection").addClass("hide");
    $(".troop-listing .row.details")[oneLinkCount-1]
    troopListingConfigObj["showOneLink"] = false;
    troopListingConfigObj["supportAnotherTroop"] = false;
    var troopListingItems = $(".troop-listing .details");
    $(troopListingItems[oneLinkCount-1]).css({ 'border-bottom' : 'solid 1px #e1e1e1'});
    for (var i = 1; i < troopListingItems.length; i++) {
      $(troopListingItems[i]).show();
    }
    fixLastResultBottomBorder();
    $(".troop-listing .show-more").removeClass("hide");
  });


}

function fixLastResultBottomBorder(){
	$(".troop-listing .row.details").last().css({ 'border-bottom' : '0px'});
}

function shuffleZeroMilesTroop(troops, zeroMileLastIndex) {
  var temp, index;
  var troopLength = troops.length;
  // While there are elements in the object
  while (troopLength > 0) {
    // Pick a random index in between 0 and zeroMileLastIndex
    index = Math.floor(Math.random() * (zeroMileLastIndex));
    // Decrease zeroMileLastIndex by 1
    troopLength--;
    // And swap the last element with it
    temp = troops[0];
    troops[0] = troops[index];
    troops[index] = temp;
  }
  return troops;
}

function registerClickOfRegisterButton(){
  $('.troop-listing .troopRegisterButton, .troop-listing .troopRegisterLink').on('click', function() {
      var item;
      if ($(this).attr("class") === "troopRegisterLink") {
		item = $(this).parents(".row.details").find(".troopRegisterButton");;
      } else {
		item = $(this);
      }
	  var value = JSON.parse(item.attr("data")); 
      var visitURL = value.visitBoothUrl;
        var data = {
          t : value.TroopName,
          u : value.StoreURL,
          d : value.DateEnd,
          z : value.ZipCode,
          s : "Website",
          cn : getParameterByName('utm_campaign'),
          cm : getParameterByName('utm_medium'),
          cs : getParameterByName('utm_source')
        }

      $.ajax({
        url: troopListingLookupApiURL,
        dataType: "json",
        data: data,
        success: function(data) {
          if (data) {
            console.log('Redirecting from trooplisting');
          } else {
            console.log('Error occured in redirecting');
          }
        }
      });

  });
}
