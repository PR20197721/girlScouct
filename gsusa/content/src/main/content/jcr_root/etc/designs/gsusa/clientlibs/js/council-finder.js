$CQ(function() {
	$CQ(".zipSearch").submit( searchZip(event) );
	$CQ(".stateSearch").submit( searchState(event) );
	$CQ(" .searchCouncilCode").submit( searchCouncilCode(event) );
	
});

function searchZip( ev ){
	var $zip = ev.data.zip;
	var baseURL = ev.data.url;
	var $id = ev.data.div-id;
	$CQ.getJSON(baseURL + '.zip.json', outputResult(data, $id));
}

function searchState( ev ){
	var $state = ev.data.state;
	var baseURL = ev.data.url;
	var $id = ev.data.div-id;
	$CQ.getJSON(baseURL + '.state.json', outputResult(data, $id));
}

function searchCouncilCode( ev ){
	var $council = ev.data.council-code;
	var baseURL = ev.data.url;
	var $id = ev.data.div-id;
	$CQ.getJSON(baseURL + '.council.json', outputResult(data, $id));
}

function outputResult( data, $id ) {
	var $html = [];
	html.push("<p>Results:</p>");
	$.each(data.resultsArray, function(i, value){
		
	});
}