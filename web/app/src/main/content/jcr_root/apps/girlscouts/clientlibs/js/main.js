// Define namespaces
girlscouts = {};
girlscouts.components = {};
girlscouts.functions = {};

girlscouts.functions.createPath = function(path, type) {
	if (!type) type = "cq:Page";
	$.ajax({
		type: 'POST',
		url: "/apps/girlscouts/wcm/components/path-creator.html", 
		data: {"path": path, "type" : type},
		async: false
	});
};