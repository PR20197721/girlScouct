function displayWebResource(path) {
	path = "/content/girlscouts-vtk/en/resources.html/web" + path;
	$.get(path, function(data){
		$('<div>' + data + '</div>').dialog();
	});
}