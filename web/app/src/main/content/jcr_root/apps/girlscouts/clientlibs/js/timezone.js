// Read "timezone" property from the council branch.
// Returns an array of supported time zones in this format:
// {"timezone": "US/Central", "label": "CST"}
// label can be empty if there is only one time zone in the array.
girlscouts.functions.getTimezones = function() {
	var defaultTimezone = 'US/Eastern';
	var timezoneStr;
    
	var url = window.location.pathname;
	var path = CQ.shared.HTTP.getPath(url);
	
	// New event from scaffolding page
	var regexScaffolding = /\/etc\/scaffolding\/[^/]+\/event/; //e.g. /etc/scaffolding/gsnetx/event
	if (regexScaffolding.test(path)) {
		var targetPathProperty = regexScaffolding.exec(path) + '/jcr:content/cq:targetPath';
		var response = CQ.shared.HTTP.get(targetPathProperty);
		if (response.status == 200) {
			path = response.body;
		}
	}
	
	// Get the council path
	var regex = /^\/content\/[^/]+\/[^/]+/; // e.g. /content/girlscouts-prototype/en
	if (regex.test(path)) {
		// Read the "timezone" property from the council
		var timezoneProperty = regex.exec(path) + '/jcr:content/timezone';
		var response = CQ.shared.HTTP.get(timezoneProperty);
		if (response.status == 200) {
			timezoneStr = response.body;
		} else {
			timezoneStr = defaultTimezone;
		}
	} else {
		timezoneStr = defaultTimezone;
	}
	
	// Retrieved property is in this format:
	// US/Central:CST,US/Eastern/EST
	// or if there is only one time zone, without label
	// US/Central 
	var timezoneStrElems = timezoneStr.split(',');
	var timezones = new Array();
	for (var i = 0; i < timezoneStrElems.length; i++) {
		var result = timezoneStrElems[i].split(':'); // US/Central:CST
		timezones.push({
			timezone: result[0],
			label: result.length >= 2 ? result[1] : '' 
		});
	}
	
	return timezones;
};