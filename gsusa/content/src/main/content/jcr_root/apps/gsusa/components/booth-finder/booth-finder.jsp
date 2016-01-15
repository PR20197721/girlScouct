<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page session="false" %>

<%-- Placeholder for the actual render --%>
<div id="booth-finder-result" class="booth-finder"></div> 

<%
// Templates
// Templates for path 1 to path 5.
for (int pathIndex = 1; pathIndex <= 5; pathIndex++) {
	if (pathIndex == 3) {continue;} // Skip obsolete path3.
	String pathIndexStr = Integer.toString(pathIndex);
	request.setAttribute("gsusa-component-booth-finder-index", pathIndexStr);
%>
	<script id="template-path<%= pathIndexStr %>" type="text/x-handlebars-template">
		<cq:include script="pathn.jsp" />

        <div id="share-showShareDialog" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShowShareDialog", "")) %>" />
        <div id="share-shareDialogHeader" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogHeader", "")) %>" />
        <div id="share-shareDialogDescription" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogDescription", "")) %>" />
        <div id="share-shareDialogTweet" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogTweet", "")) %>" />
        <div id="share-shareDialogImagePath" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogImagePath", "")) %>" />

<%
        if (pathIndex == 2) {
            %><cq:include path="contact-banner" resourceType="gsusa/components/contact-banner"/><%
        }
%>
	</script>

<%
	boolean isShowShareDialog = properties.get("path" + pathIndexStr + "ShowShareDialog", "false").equalsIgnoreCase("true"); // e.g. path1ShowShareDialog
	request.setAttribute("gsusa-component-booth-finder-index", null);
}
%>

<%-- Template for not found --%>
<script id="template-notfound" type="text/x-handlebars-template">
	<cq:include script="not-found.jsp" />
</script>

<%-- Template for booths if the list is not empty --%>
<script id="template-booths" type="text/x-handlebars-template">
	<cq:include script="booth-list.jsp" />
	<div id="share-map-FBTitle" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapFBTitle", "")) %>" />
	<div id="share-map-FBDesc" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapFBDesc", "")) %>" />
	<div id="share-map-Tweet" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapTweet", "")) %>" />
	<div id="share-map-FBImgPath" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapFBImgPath", "")) %>" />
</script>

<%-- Template for more booths --%>
<script id="template-more-booths" type="text/x-handlebars-template">
	<cq:include script="booth-list-more.jsp" />
</script>

<%-- Template for share modal --%>
<script id="template-sharemodal" type="text/x-handlebars-template">
	<cq:include script="share-modal.jsp" />
</script>

<script>
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
		m: this.numPerPage + 1 // Plus 1 to see if there are more results
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
	if (!council.CouncilCode) { // Council Code not found. Council does not exist.
		templateId = 'notfound';
	} else if (booths.length != 0) {
		templateId = 'booths';
		
		var nearestDistance = Number.MAX_VALUE;
		this.shouldHideMoreButton = booths.length <= this.numPerPage;
		var min = Math.min(booths.length, this.numPerPage); // length - 1 to omit the "more" one
		for (var boothIndex = 0; boothIndex < min; boothIndex++) { 
			var booth = booths[boothIndex];
			// Add index field
			booth.ID = boothIndex;
			// Add zip field to booth. "View Detail" needs this info.
			booth.queryZip = this.zip;
			
			if (Number(booth.Distance) < nearestDistance) {
				nearestDistance = Number(booth.Distance);
			}
		}
		
		// Remove "more" items
		booths.splice(min, booths.length - min);

		result.env.nearestDistance = nearestDistance;
	} else {
		templateId = result.council.PreferredPath.toLowerCase(); // e.g. path1
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
		var templateDOMId = 'template-' + templateId; // template-path1;
		var html = Handlebars.compile($('#' + templateDOMId).html())(result);
		$('#booth-finder-result').html(html);
	} else {
		var templateDOMId = 'template-more-booths';
		var html = Handlebars.compile($('#' + templateDOMId).html())(result);
		$('.booth-finder .show-more').before(html);
	}
	
	if (templateId == 'booths') {
		// Bind "View Details" buttons
		$('.viewmap.button').on('click', function(){
	        var data = $.param(JSON.parse($(this).attr('data'))) + '&' + 
	        	'fbTitle=' + encodeURIComponent($('#share-map-FBTitle').attr('data')) + '&' +
	        	'fbDesc=' + encodeURIComponent($('#share-map-FBDesc').attr('data')) + '&' +
	        	'tweet=' + encodeURIComponent($('#share-map-Tweet').attr('data')) + '&' +
	        	'shareImgPath=' + encodeURIComponent($('#share-map-FBImgPath').attr('data'));
	
		    $('#modal_booth_item_map').foundation('reveal', 'open', {
	            url: '<%= resource.getPath() %>.booth-detail.html',
	            cache:false,
	            processData: false,
	            data: data
	        });
	        $('.off-canvas-wrap').addClass('noprint');
		});
		
		if (this.page == 1) {
			// Reset form values
			var radius = getParameterByName('radius');
			var date = getParameterByName('date');
			var sortBy = getParameterByName('sortBy');
			if (!radius) radius = 25;
			if (!date) date = 60;
			if (!sortBy) sortBy = 'distance'
			$('select[name="radius"]').val(radius);
			$('select[name="date"]').val(date);
			$('select[name="sortBy"]').val(sortBy);
			
			// Bind click more
			$('.booth-finder #more').on('click', function(){
				boothFinder.getResult();
			});
		}
	} 
	
	// Share dialog
	var showShareDialog = $('#share-showShareDialog').attr('data') == 'true';
	if (showShareDialog) {
		var shareModalHtml = Handlebars.compile($('#template-sharemodal').html())({
			buttonCaption: "SHARE WITH YOUR FRIENDS",
			header: $('#share-shareDialogHeader').attr('data'),
			desc: $('#share-shareDialogDescription').attr('data'),
			tweet: $('#share-shareDialogTweet').attr('data'),
			imageFilePath: $('#share-shareDialogImagePath').attr('data'),
			url: window.location.href,
			uniqueID: Math.floor((Math.random() * 1000) + 1),
			facebookId: <%= currentSite.get("facebookId", "") %>
		});
		$('#booth-finder-result').append(shareModalHtml);
	}
	
	// Setup contact local council form
	setupContactLocalCouncilForm();

	// Increase page count
	this.page++;

	// Hide "more" link if there is no more result
	if (this.shouldHideMoreButton) {
		$('.booth-finder #more').hide();
	}

	// Reset foundation again since new tags are added.
	$(document).foundation();
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

$(document).ready(function(){
	var zip;
	// Get zip from hash
	zip = (function(zip){
		var hash = window.location.hash;
		if (hash.indexOf('#') == 0) {
			hash = hash.substring(1);
		}
		var zipRegex = /[0-9]{5}/;
		return zipRegex.test(hash) ? hash : zip;
	})();

	if (zip == undefined) {
		// TODO: error: zip not found.
	} else {
		var radius = getParameterByName('radius');
		var date = getParameterByName('date');
		var sortBy = getParameterByName('sortBy');
		if (!radius) radius = 25;
		if (!date) date = 60;
		if (!sortBy) sortBy = 'distance';
		
		boothFinder = new BoothFinder("/cookiesapi/booth_list.asp", zip, radius, date, sortBy, <%= properties.get("numPerPage", 50)%>/*numPerPage*/);
		boothFinder.getResult();
	}
});
</script>

<%! 
public String escapeDoubleQuotesAddCouncil(String str) {
	return str.replaceAll("\"", "\\\"").replaceAll("\\{\\{", "{{escapeDoubleQuotes council.");
}
%>