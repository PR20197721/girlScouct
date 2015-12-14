<%@page import="java.util.Map"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

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
	</script>
<%
	request.setAttribute("gsusa-component-booth-finder-index", null);
}
%>

<%-- Template for booths if the list is not empty --%>
<script id="template-booths" type="text/x-handlebars-template">
	<cq:include script="booth-list.jsp" />
</script>

<%-- Template for not found --%>
<script id="template-notfound" type="text/x-handlebars-template">
	<cq:include script="not-found.jsp" />
</script>

<%-- Placeholder for the actual render --%>
<div id="booth-finder-result"></div> 

<script>
var zip;

// Get zip from hash
zip = function(zip){
	var hash = windows.location.hash;
	if (hash.indexOf('#') == 0) {
		hash = hash.substring(1);
	}
	var zipRegex = /[0-9]{5}/;
	return zipRegex.test(hash) : hash : zip;
})();

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
	var result = { "council": { "CouncilCode":"218", "CouncilName":"Girl Scouts of Central Maryland, Inc", "CouncilAbbrName":"Central Maryland", "CouncilCityStateZip":"Baltimore, MD 21215-3247", "CouncilURL":"http://www.gscm.org", "CookieSaleStartDate":"9/11/2015", "CookieSaleEndDate":"12/7/2015", "PreferredPath":"Path4", "CookiePageURL":"http://www.gscm.org/cookies/gs-cookies/", "CookieSaleContact_Email":"cookies@gscm.org"}, "booths": [ { "Distance":"8.9", "Location":"Patterson Park, Great Lantern Festival & Parade", "TroopName":"262", "DateStart":"12/14/2015", "DateEnd":"12/14/2015", "TimeOpen":"1:30 PM", "TimeClose":"3:30 PM", "Address1":"Patterson Park Pulaski Monument on Eastern & Linwood Avenues", "Address2":"", "City":"Baltimore", "State":"MD", "ZipCode":"21224"}, { "Distance":"54.0", "Location":"Dollar Tree", "TroopName":"6164", "DateStart":"3/15/2016", "DateEnd":"", "TimeOpen":"1:00 PM", "TimeClose":"4:00 PM", "Address1":"14120 Lee Highway", "Address2":"", "City":"Centreville", "State":"VA", "ZipCode":"20120"}, { "Distance":"54.0", "Location":"Dollar Tree", "TroopName":"1040", "DateStart":"3/15/2016", "DateEnd":"", "TimeOpen":"4:00 PM", "TimeClose":"7:00 PM", "Address1":"14120 Lee Highway", "Address2":"", "City":"Centreville", "State":"VA", "ZipCode":"20120"}]};
	var council = result.council;
	var booths = result.booths;
	
	var templateId;
	if (!council.CouncilCode) { // Council Code not found. Council does not exist.
		templateId = 'notfound';
	} else if (booths.length !== 0) {
		templateId = 'booths';
	} else {
		var preferredPath = result.council.PreferredPath;
		templateId = 'template-' + preferredPath.toLowerCase(); // template-path1;
	}
	var html = Handlebars.compile($('#' + templateId).html())(result);
	$('#booth-finder-result').html(html);
}

var boothFinder;
if (zip == undefined) {
	// TODO: error: zip not found.
} else {
	boothFinder = new BoothFinder(zip, 25 /*radius*/, 60 /*date*/, 'distance' /*distance*/, 50/*numPerPage*/);
	boothFinder.getResult();
}
</script>