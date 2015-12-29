<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page session="false" %>

<%-- Placeholder for the actual render --%>
<div id="camp-finder-result" class="camp-results"></div> 

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

CampFinder.prototype.getResult = function() {
	// TODO: Other filters are not implemented yet.
    var data = {
		z: this.zip,
		//r: this.radius,
		d: this.duration,
		//d: this.duration
		//g: this.grade,
		//sd: this.startDate,
		//ed: this.endDate,
		//t: this.sortBy,
		//s: this.page * this.numPerPage,
		//m: this.numPerPage + 1 // Plus 1 to see if there are more results
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
		// TODO: change it back after test
		//dataType: "json",
		data: data,
		success: CampFinder.prototype.processResult.bind(this)
	});
}

CampFinder.prototype.processResult = function(campResult) {
	// TODO: test result
	campResult = [{"Council Name":"Girl Scouts of North-Central Alabama","Camp Name":"Mountain\\Camp Cottaquilla","Location":"Anniston, AL","ZipCode":"36201","Description":"Located in the foothills of the Appalachian Mountains near the highest point in Alabama, Camp Cottaquilla offers sprawling hardwood forests, soaring mountains, miles of hiking trails, and 1,800 acres to explore. A small lake near the center of the camp provides the ideal location for girls to try canoeing, kayaking, paddle boarding, and fishing, and the swimming pool offers a welcome reprieve from the summer\'s heat. There\'s also archery, riflery, low ropes, \"nature nook,\" and campfire circle. Camp Cottaquilla is ACA accredited.","SeasonDates":"May 31 - June 12","SessionLength":"1 or 2 weeks","Fee":"$349 - $359 per week","Grades":"All","Website":"http:\/\/www.girlscoutsnca.org","Email1":"lelliott@girlscoutsnca.org","Email2":"","Phone1":"","Phone2":"","Distance":"0"},{"Council Name":"Girl Scouts of North-Central Alabama","Camp Name":"Camp Coleman","Location":"Trussville, AL","ZipCode":"35173","Description":"Nestled along the banks of the winding Cahaba River near Trussville, Alabama, Camp Coleman features 140 acres of woods, meadows, and trails. The camp provides an instructional horseback riding program, low and high ropes courses, indoor climbing wall, swimming pool, nature center, canoeing, kayaking, archery, riflery, and more.","SeasonDates":"May 26 - June 26","SessionLength":"1 or 2 weeks","Fee":"day camp, $125 per week; resident camp, $339 - $399 per week","Grades":"All","Website":"http:\/\/www.girlscoutsnca.org","Email1":"lelliott@girlscoutsnca.org","Email2":"hello@example.net","Phone1":"123-123-1234 ext 1234","Phone2":"456-456-4567 ext 7890","Distance":"43.5"},{"Council Name":"Girl Scout Council of the Florida Panhandle","Camp Name":"Kugelman\/Campus","Location":"Lillian, AL","ZipCode":"36549","Description":"Located just outside of Pensacola, Florida, on Perdido Bay, Kugelman Campus offers weeklong, half-week, and weekend camping programs for girls. Girls attending camp have the opportunity to work on Girl Scout Journeys and earn badges and leadership awards. Lodging at Kugelman Campus is dormitory style with central heat and air conditioning.","SeasonDates":"July 5 - 19","SessionLength":"Half week, Full week and Weekend","Fee":"$75 - $600 per session","Grades":"entering 1st - 12th","Website":"http:\/\/issuu.com\/gscfp\/docs\/camp_brochure__2015","Email1":"Camp@gscfp.org","Email2":"","Phone1":"888-271-8779","Phone2":"","Distance":"245.6"}];
		
	var camps = campResult;
	
	// Add zip to environment
	var result = {};
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

		result.camps = camps;
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
			$('#camp-finder-result #more').on('click', function(){
				campFinder.getResult();
			});
		}
	} 
	
	// Increase page count
	this.page++;

	// Hide "more" link if there is no more result
	if (this.shouldHideMoreButton) {
		$('#camp-finder-result #more').hide();
	}

    $('.camp-results .more-section section').hide();
    $(".camp-results .more-section .read-more").on("click", function (e){
      $(this).siblings('section').slideToggle();
      $(this).html(($(this).text() == 'Read More') ? 'Read Less' : 'Read More');
      $(this).toggleClass('on');
      e.preventDefault();
    });
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

		// TODO: User # for testing. Correct URL will be: /campsapi/ajax_camp_results.asp
		campFinder = new CampFinder("/content/gsusa/en.html", zip, radius, duration, grade, startDate, endDate, sortBy, <%= properties.get("numPerPage", 50)%>/*numPerPage*/);
		campFinder.getResult();
	}
});
</script>