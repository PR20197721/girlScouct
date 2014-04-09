// Define namespaces
girlscouts = {};
girlscouts.components = {};
girlscouts.functions = {};

girlscouts.functions.createPath = function(path, type, prop) {
	if (!type) type = "cq:Page";
	var conf = {
		type: 'POST',
		url: "/apps/girlscouts/wcm/components/path-creator.html", 
		data: {"path": path, "type" : type},
		async: false
	};
	if (prop) {
		conf.data.prop = prop;
	}

	$.ajax(conf);
};