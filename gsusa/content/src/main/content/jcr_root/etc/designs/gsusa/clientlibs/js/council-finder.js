$CQ(function() {
	$CQ(".zipSearch").submit( searchZip(event) );
	$CQ(".stateSearch").submit( searchState(event) );
	$CQ(" .searchCouncilCode").submit( searchCouncilCode(event) );
	
});

function searchZip( ev ){
	var zip = ev.data.zip;
	var baseURL = ev.data.url;
	var id = ev.data.div-id;
	$CQ.getJSON(baseURL + '.zip.json', outputResult(data, id));
}

function searchState( ev ){
	var state = ev.data.state;
	var baseURL = ev.data.url;
	var id = ev.data.div-id;
	$CQ.getJSON(baseURL + '.state.json', outputResult(data, id));
}

function searchCouncilCode( ev ){
	var council = ev.data.council-code;
	var baseURL = ev.data.url;
	var id = ev.data.div-id;
	$CQ.getJSON(baseURL + '.council.json', outputResult(data, id));
}

function outputResult( data, id ) {
	var html = [];
	html.push("<p>Results:</p>");
	$CQ.each(data.resultsArray, function(i, value){
		html.push(
		"<p><strong>", 
		value.councils[i].councilFullName, 
		"</strong></p><p>",
		value.councils[i].city,
		", ",
		value.councils[i].state,
		" ",
		value.councils[i].zipcode,
		"</p><p>",
		value.councils[i].phone,
		"</p><p>E-mail: ",
		value.councils[i].email,
		"</p><p>Web Site: ",
		value.councils[i].website,
		"</p><p>Facebook: ",
		value.councils[i].facebook,
		"</p><p>Twitter: ",
		value.councils[i].twitter,
		"</p><p><a class=\"finder-button\" href=\"",
		value.councils[i].joinUrl,
		"\">Join</a><a class=\"finder-button\" href=\"",
		value.councils[i].volunteerUrl,
		"\">Volunteer</a><a class=\"finder-button\" href=\"",
		value.councils[i].onlineRegistrationUrl,
		"\">Online Registration</a></p><br/>");
	});
	$CQ( id ).html(html.join(""));
}