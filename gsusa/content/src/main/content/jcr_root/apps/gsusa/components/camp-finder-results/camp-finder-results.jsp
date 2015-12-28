<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page session="false" %>

<%-- Placeholder for the actual render --%>
<div id="camp-finder-result" class="camp-results"></div> 

// Templates
<%-- Template for not found --%>
<script id="template-notfound" type="text/x-handlebars-template">
	<cq:include script="not-found.jsp" />
</script>

<%-- Template for camp list --%>
<script id="template-camps" type="text/x-handlebars-template">
	<cq:include script="camp-list.jsp" />
</script>

<%-- Template for more booths --%>
<script id="template-more-camps" type="text/x-handlebars-template">
	<cq:include script="camp-list-more.jsp" />
</script>

<script>
function CampFinder(url, zip, radius, duration, grade, startDate, endDate, sortBy, numPerPage) {
	this.url = url;
	this.zip = zip;
	this.radius = radius;
	this.duration = duration;
	this.grade = grade;
	this.startDate = startDate;
	this.endDate = endDate;
	this.sortBy = sortBy;
	this.numPerPage = numPerPage;
	
	this.page = 1;
}

BoothFinder.prototype.getResult = function() {
    var data = {
		z: this.zip,
		r: this.radius,
		d: this.duration,
		g: this.grade,
		sd: this.startDate,
		ed: this.endDate,
		t: this.sortBy,
		s: this.page * this.numPerPage,
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
		success: CampFinder.prototype.processResult.bind(this)
	});
}

CampFinder.prototype.processResult = function(campResult) {
	var camps = campResult;
	
	// Add zip to environment
	result = result || {};
	result.env = result.env || {};
	result.env.zip = this.zip;
	
	var templateId;
	if (camps.length != 0) {
		templateId = 'camps';
		
		this.shouldHideMoreButton = camps.length <= this.numPerPage;
		var min = Math.min(camps.length, this.numPerPage); // length - 1 to omit the "more" one
		for (var campIndex = 0; campIndex < min; campIndex++) { 
			var camp = camps[campIndex];
			// Add Council Name and zip field to camp. "View Detail" needs this info.
			camp.queryZip = this.zip;
		}
		
		// Remove "more" items
		camps.splice(min, camps.length - min);
	} else {
		templateId = 'notfound';
	}

	if (this.page == 1) {
		var templateDOMId = 'template-' + templateId; // template-path1;
		var html = Handlebars.compile($('#' + templateDOMId).html())(result);
		$('#camp-finder-result').html(html);
	} else {
		var templateDOMId = 'template-more-camps';
		var html = Handlebars.compile($('#' + templateDOMId).html())(result);
		$('.camp-finder .show-more').before(html);
	}
	
	if (templateId == 'camps') {
		// Bind "View Details" buttons
		$('.readmore.button').on('click', function(){
			// TODO: bind view detail button
		});
		
		if (this.page == 1) {
			// Reset form values
			var radius = getParameterByName('radius');
			var duration = getParameterByName('duration');
			var grade = getParameterByName('grade');
			var startDate = getParameterByName('startDate');
			var endDate = getParameterByName('endDate');
			var sortBy = getParameterByName('sortBy');
			if (!radius) radius = 250;
			if (!duration) date = 'all';
			if (!grade) sortBy = 'all';
			//TODO: if (!startDate) radius = 25;
			//TODO: if (!endDate) date = 60;
			if (!sortBy) sortBy = 'distance'
			$('select[name="radius"]').val(radius);
			$('select[name="duration"]').val(duration);
			$('select[name="grade"]').val(grade);
			$('select[name="startDate"]').val(startDate);
			$('select[name="endDate"]').val(endDate);
			$('select[name="sortBy"]').val(sortBy);
			
			// Bind click more
			$('.camp-finder-result #more').on('click', function(){
				campFinder.getResult();
			});
		}
	} 
	
	// Increase page count
	this.page++;

	// Hide "more" link if there is no more result
	if (this.shouldHideMoreButton) {
		$('.camp-finder-result #more').hide();
	}

	// Reset foundation again since new tags are added.
	// TODO: do we need this here?
	//$(document).foundation();
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
		var duration = getParameterByName('duration');
		var grade = getParameterByName('grade');
		var startDate = getParameterByName('startDate');
		var endDate = getParameterByName('endDate');
		var sortBy = getParameterByName('sortBy');
		if (!radius) radius = 250;
		if (!duration) date = 'all';
		if (!grade) sortBy = 'all';
		//TODO: if (!startDate) radius = 25;
		//TODO: if (!endDate) date = 60;
		if (!sortBy) sortBy = 'distance'

		campFinder = new CampFinder("/cookiesapi/ajax_camp_results.asp", zip, radius, duration, grade, startDate, endDate, sortBy, <%= properties.get("numPerPage", 50)%>/*numPerPage*/);
		campFinder.getResult();
	}
});
</script>

<%! 
public String escapeDoubleQuotesAddCouncil(String str) {
	return str.replaceAll("\"", "\\\"").replaceAll("\\{\\{", "{{escapeDoubleQuotes council.").replaceAll("#", "%23");	
}
%>