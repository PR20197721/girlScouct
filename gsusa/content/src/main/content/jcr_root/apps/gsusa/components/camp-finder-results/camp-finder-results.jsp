<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page session="false" %>

<%-- Template for not found --%>
<script id="template-notfound" type="text/x-handlebars-template">
	<cq:include script="not-found.jsp" />
</script>

<%-- Template for invalid zip --%>
<script id="template-invalidzip" type="text/x-handlebars-template">
	<cq:include script="invalid-zip.jsp" />
</script>

<%-- Template for camp list --%>
<script id="template-camps" type="text/x-handlebars-template">
	<cq:include script="camp-list.jsp" />
</script>

<%-- Template for more booths --%>
<script id="template-more-camps" type="text/x-handlebars-template">
	<cq:include script="camp-list-more.jsp" />
</script>

<%-- Intro --%>
<%= properties.get("intro", "") %>

<div class="camp-results">
    <form class="camp-finder-options sort-form clearfix">
        <div class="clearfix">
            <section class="radius">
                <label>Radius:</label>
                <select name="radius" onchange="getCampResults()">
                    <option value="1">1 miles</option>
                    <option value="5">5 miles</option>
                    <option value="10">10 miles</option>
                    <option value="15">15 miles</option>
                    <option value="25">25 miles</option>
                    <option value="50">50 miles</option>
                    <option value="100">100 miles</option>
                    <!-- default -->
                    <option value="250" selected>250 miles</option>
                    <option value="500">500 miles</option>
                </select>
            </section>
            <section class="duration">
                <label>Duration:</label>
                <select name="duration" onchange="getCampResults()">
                    <!-- default -->
                    <option value="all" selected>All</option>
                    <option value="less">Less than 1 week</option>
                    <option value="week">1 week</option>
                    <option value="long">More than 1 week</option>
                </select>
            </section>
            <section  class="grade">
                <label>Grade:</label>
                <select name="grade" onchange="getCampResults()">
                    <!-- default -->
                    <option value="all" selected>All</option>
                    <option value="k">Grade K</option>
                    <option value="1">Grade 1</option>
                    <option value="2">Grade 2</option>
                    <option value="3">Grade 3</option>
                    <option value="4">Grade 4</option>
                    <option value="5">Grade 5</option>
                    <option value="6">Grade 6</option>
                    <option value="7">Grade 7</option>
                    <option value="8">Grade 8</option>
                    <option value="9">Grade 9</option>
                    <option value="10">Grade 10</option>
                    <option value="11">Grade 11</option>
                    <option value="12">Grade 12</option>
                    <option value="adult">Adult</option>
                </select>
            </section>
            <section  class="date">
                <label>Start Date:</label>
                <input type="text" class="dp-calendar form-control hide-for-touch" id="start-desktop" data-language="my-lang" placeholder="mm/dd/yyyy" data-date-format="mm/dd/yyyy" data-position="bottom center">
                <input type="date" class="show-for-touch" id="start-touch" data-language="my-lang" name="startDate" placeholder="mm/dd/yyyy" data-date-format="mm/dd/yyyy" data-position="bottom center" onchange="getCampResults()">
            </section>
            <section  class="date">
                <label>End Date:</label>
                <input type="text" class="dp-calendar form-control hide-for-touch" id="end-desktop" data-language="my-lang" placeholder="mm/dd/yyyy" data-date-format="mm/dd/yyyy" data-position="bottom center">
                <input type="date" class="show-for-touch" id="end-touch" data-language="my-lang" name="endDate" placeholder="mm/dd/yyyy" data-date-format="mm/dd/yyyy" data-position="bottom center" onchange="getCampResults()">
            </section>
            <section  class="sort">
                <label>Sort by:</label>
                <select name="sortBy" onchange="getCampResults()">
                    <!-- default -->
                    <option value="distance" selected>Distance</option>
                    <option value="date">Date</option>
                </select>
            </section>
        </div>
    </form>
</div>

<%-- Placeholder for the actual render --%>
<div id="camp-finder-result" class="camp-results"></div>

<script>
$(function() {
	var navTitles = {
        days: 'MM <i>yyyy</i>',
        months: 'yyyy',
        years: 'yyyy1 - yyyy2'
    };
	
    $("#start-desktop").datepicker({
      navTitles: navTitles,
      onSelect: function (fd, date) {
        var dateStr = $('#start-desktop').val();
        if (dateStr) {
        	$('#start-touch').val(moment($('#start-desktop').val(), 'MM/DD/YYYY').format('YYYY-MM-DD'));
        	$('#end-desktop').data('datepicker').update('minDate', date);
        } else {
        	$('#start-touch').val('');
        	$('#end-desktop').data('datepicker').update('minDate', null);
        }
        getCampResults();
      },
      autoClose: true
    });

    $("#end-desktop").datepicker({
      navTitles: navTitles,
      onSelect: function (fd, date) {
        var dateStr = $('#end-desktop').val();
        if (dateStr) {
        	$('#end-touch').val(moment($('#end-desktop').val(), 'MM/DD/YYYY').format('YYYY-MM-DD'));
        	$('#start-desktop').data('datepicker').update('maxDate', date);
        } else {
        	$('#end-touch').val('');
        	$('#start-desktop').data('datepicker').update('maxDate', null);
        }
        getCampResults();
      },
      autoClose: true
    });
});

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
    var data = {
		z: this.zip,
		d: this.radius,
		cd: this.duration,
		g: this.grade,
		t: this.sortBy,
		csd: this.startDate,
		ced: this.endDate,
		s: (this.page - 1) * this.numPerPage + 1,
		m: this.numPerPage + 1 // Plus 1 to see if there are more results
    };
    
    if (this.startDate) {
    	data.csd = this.startDate;
    }
    if (this.endDate) {
    	data.ced = this.endDate;
    }

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

    if (this.page == 1) {
		$('#camp-finder-result').html('');
    }

	$.ajax({
		url: this.url,
		dataType: "json",
		data: data,
		success: CampFinder.prototype.processResult.bind(this)
	});
}

CampFinder.prototype.processResult = function(campResult) {
	var result = {};

	var camps;
	if (Array.isArray(campResult)) {
		camps = campResult;
	} else {
		// The API returns a council object instead of an array if no camp is found.
		camps = [];
		result.council = campResult;
	}

	// Add zip to environment
	result.env = result.env || {};
	result.env.zip = this.zip;

	var templateId;
	if (camps.length != 0) {
		templateId = 'camps';

		this.shouldHideMoreButton = camps.length <= this.numPerPage;
		var min = Math.min(camps.length, this.numPerPage); // length - 1 to omit the "more" one
		for (var campIndex = 0; campIndex < min; campIndex++) {
			var camp = camps[campIndex];
			// Process distance
			if (camp.Distance == '1') {
				camp.Distance = "1 mile";
			} else {
				camp.Distance += ' miles';
			}

			// Process emails
			var origEmails = camp.Email.split(/,\s+/);
			var emails = "";
			for (var emailIndex = 0; emailIndex < origEmails.length; emailIndex++) {
				var email = origEmails[emailIndex];
				email = email.replace(/([^\s]+@[^\s]+\.[^\s]+)/, '<a href="mailto:$1">$1</a>');
				emails = emails + email + ', ';
			}
			// if (emails.endsWith(', ')) {  Safari on iOS 8.1 does not support endsWith
			if (emails.lastIndexOf(', ') == emails.length - 2) {
				emails = emails.substring(0, emails.length - 2);
			}
			camp.Emails = emails;
		}

		// Remove "more" items
		camps.splice(min, camps.length - min);

		result.camps = camps;
	} else {
		if (result.council && result.council.CouncilName) {
			templateId = 'notfound';
		} else {
			templateId = 'invalidzip';
		}
	}

	if (this.page == 1) {
		var templateDOMId = 'template-' + templateId; // template-path1;
		var html = Handlebars.compile($('#' + templateDOMId).html())(result);
		$('#camp-finder-result').html(html);
	} else {
		var templateDOMId = 'template-more-camps';
		var html = Handlebars.compile($('#' + templateDOMId).html())(result);
		$('.camp-finder-results .show-more').before(html);
	}

	if (templateId == 'camps') {
		if (this.page == 1) {
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

    $(".camp-results .more-section .read-more").unbind().on("click", function (e){
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

function getCampResults() {
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
		var radius = $('select[name="radius"]').val();
		var duration = $('select[name="duration"]').val();
		var grade = $('select[name="grade"]').val();
		var startDate = $('input[name="startDate"]').val();
		var endDate = $('input[name="endDate"]').val();
		var sortBy = $('select[name="sortBy"]').val();

		if (!radius) radius = 250;
		if (!duration) duration = 'all';
		if (!grade) grade = 'all';
		if (!sortBy) sortBy = 'distance'

		campFinder = new CampFinder("/campsapi/ajax_camp_results.asp", zip, radius, duration, grade, startDate, endDate, sortBy, <%= properties.get("numPerPage", 50)%>/*numPerPage*/);
		campFinder.getResult();
	}
}

$(document).ready(function() {
	getCampResults();
});
</script>