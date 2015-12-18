<%@page import="java.util.Map"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>


<%-- TODO: only for authoring mode --%>
#####$

<%-- Placeholder for the actual render --%>
<div id="booth-finder-result"></div> 

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

        <div id="share-showShareDialog" data="<%= escapeDoubleQuotes(properties.get("path" + pathIndex + "ShowShareDialog", "")) %>" />
        <div id="share-shareDialogHeader" data="<%= escapeDoubleQuotes(properties.get("path" + pathIndex + "ShareDialogHeader", "")) %>" />
        <div id="share-shareDialogDescription" data="<%= escapeDoubleQuotes(properties.get("path" + pathIndex + "ShareDialogDescription", "")) %>" />
        <div id="share-shareDialogTweet" data="<%= escapeDoubleQuotes(properties.get("path" + pathIndex + "ShareDialogTweet", "")) %>" />
        <div id="share-shareDialogImagePath" data="<%= escapeDoubleQuotes(properties.get("path" + pathIndex + "ShareDialogImagePath", "")) %>" />
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
	<div id="share-map-FBTitle" data="<%= escapeDoubleQuotes(properties.get("mapFBTitle", "")) %>" />
	<div id="share-map-FBDesc" data="<%= escapeDoubleQuotes(properties.get("mapFBDesc", "")) %>" />
	<div id="share-map-Tweet" data="<%= escapeDoubleQuotes(properties.get("mapTweet", "")) %>" />
	<div id="share-map-FBImgPath" data="<%= escapeDoubleQuotes(properties.get("mapFBImgPath", "")) %>" />
</script>

<%-- Template for share modal --%>
<script id="template-sharemodal" type="text/x-handlebars-template">
	<cq:include script="share-modal.jsp" />
</script>

<script>
function BoothFinder(zip, radius, date, sortBy, numPerPage) {
	this.zip = zip;
	this.radius = radius;
	this.date = date;
	this.sortBy = sortBy;
	this.numPerPage = numPerPage;
	
	this.page = 0;
}

BoothFinder.prototype.getResult = function() {
	// TODO: now testing. Ajax skipped.
	// TODO: calculate days left and add it to the council.
	//var result = { "council": { "CouncilCode":"218", "CouncilName":"Girl Scouts of Central Maryland, Inc", "CouncilAbbrName":"Central Maryland", "CouncilCityStateZip":"Baltimore, MD 21215-3247", "CouncilURL":"http://www.gscm.org", "CookieSaleStartDate":"9/11/2015", "CookieSaleEndDate":"12/7/2015", "PreferredPath":"Path4", "CookiePageURL":"http://www.gscm.org/cookies/gs-cookies/", "CookieSaleContact_Email":"cookies@gscm.org"}, "booths": [ { "Distance":"8.9", "Location":"Patterson Park, Great Lantern Festival & Parade", "TroopName":"262", "DateStart":"12/14/2015", "DateEnd":"12/14/2015", "TimeOpen":"1:30 PM", "TimeClose":"3:30 PM", "Address1":"Patterson Park Pulaski Monument on Eastern & Linwood Avenues", "Address2":"", "City":"Baltimore", "State":"MD", "ZipCode":"21224"}, { "Distance":"54.0", "Location":"Dollar Tree", "TroopName":"6164", "DateStart":"3/15/2016", "DateEnd":"", "TimeOpen":"1:00 PM", "TimeClose":"4:00 PM", "Address1":"14120 Lee Highway", "Address2":"", "City":"Centreville", "State":"VA", "ZipCode":"20120"}, { "Distance":"54.0", "Location":"Dollar Tree", "TroopName":"1040", "DateStart":"3/15/2016", "DateEnd":"", "TimeOpen":"4:00 PM", "TimeClose":"7:00 PM", "Address1":"14120 Lee Highway", "Address2":"", "City":"Centreville", "State":"VA", "ZipCode":"20120"}]};
	var result = { "council": { "CouncilCode":"218", "CouncilName":"Girl Scouts of Central Maryland, Inc", "CouncilAbbrName":"Central Maryland", "CouncilCityStateZip":"Baltimore, MD 21215-3247", "CouncilURL":"http://www.gscm.org", "CookieSaleStartDate":"9/11/2015", "CookieSaleEndDate":"12/7/2015", "PreferredPath":"Path5", "CookiePageURL":"http://www.gscm.org/cookies/gs-cookies/", "CookieSaleContact_Email":"cookies@gscm.org"}, "booths": []};
	//var result = {"council": {}, "booths": []};
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
		for (var boothIndex = 0; boothIndex < result.booths.length; boothIndex++) {
			var booth = booths[boothIndex];
			// Add index field
			booth.ID = boothIndex;
			// Add Council Name and zip field to booth. "View Detail" needs this info.
			booth.CouncilName = result.council.CouncilName;
			booth.queryZip = this.zip;
			
			if (Number(booth.Distance) < nearestDistance) {
				nearestDistance = Number(booth.Distance);
			}
		}
		result.env.nearestDistance = nearestDistance;
	} else {
		templateId = result.council.PreferredPath.toLowerCase(); // e.g. path1
	}

	// Needed for "View Detail" data
	Handlebars.registerHelper('json', function(context) {
	    return JSON.stringify(context);
	});
	Handlebars.registerHelper('escapeDoubleQuotes', function(context) {
		if (typeof context == 'string') {
	    	return context.replace(/"/g, '\\\"');
		}
		return '';
	});
	
	// "Contact local council" form data
	result.contactBanner = {
		btn: "Contact Your Local Council",
		title: "Cookies are Here!",
		desc: "Enter your info below and girls from the " + council.name + " will contact you to help you place your cookie order."
	}
	
	var templateDOMId = 'template-' + templateId; // template-path1;
	var html = Handlebars.compile($('#' + templateDOMId).html())(result);

	$('#booth-finder-result').html(html);
	
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
	} 
	
	// Share dialog
	var showShareDialog = $('#share-showShareDialog').attr('data') == 'true';
	if (showShareDialog) {
		var shareModalHtml = Handlebars.compile($('#template-sharemodal').html())({
			buttonCaption: "SHARE WITH YOUR FRIENDS",
			header: $('#share-shareDialogHeader').attr('data'),
			desc: $('#share-shareDialogDescription').attr('data'),
			tweet: $('#share-shareDialogTweet').attr('data'),
			modFilePath: $('#share-shareDialogImagePath').attr('data')
		});
		$('#booth-finder-result').append(shareModalHtml);
	}

	// Reset foundation again since new tags are added.
	$(document).foundation();
}

var boothFinder;
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
		boothFinder = new BoothFinder(zip, 25 /*radius*/, 60 /*date*/, 'distance' /*distance*/, 50/*numPerPage*/);
		boothFinder.getResult();
	}
});
</script>

<%! 
public String escapeDoubleQuotes(String str) {
	return str.replaceAll("\"", "\\\"").replaceAll("\\{\\{", "{{escapeDoubleQuotes ");	
}
%>