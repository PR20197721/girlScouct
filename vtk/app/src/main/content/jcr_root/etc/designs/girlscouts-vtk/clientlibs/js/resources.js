function displayWebResource(path) {
	path = "/content/girlscouts-vtk/en/resources/web/" + path;
	$.get(path, function(data){
		$('<div>' + data + '</div>').dialog();
	});
}