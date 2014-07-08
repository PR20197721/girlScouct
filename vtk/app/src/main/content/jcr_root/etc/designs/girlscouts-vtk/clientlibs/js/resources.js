function displayResource(type, path) {
	path = '/content/girlscouts-vtk/en/resources.html/' + type + path;
	$.get(path, function(data){
		$('<div>' + data + '</div>').dialog();
	});
}