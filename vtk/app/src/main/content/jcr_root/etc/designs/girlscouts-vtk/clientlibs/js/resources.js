function displayHtmlResource(path) {
	$.get(path, function(data){
		$('<div>' + data + '</div>').dialog();
	});
}