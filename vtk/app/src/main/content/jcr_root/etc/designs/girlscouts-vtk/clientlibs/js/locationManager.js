function rmLocation(locationName){

	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			rmLocation:locationName,
			a:Date.now()
		},
		success: function(result) {
		$("#locList").load("/content/girlscouts-vtk/en/vtk.locationManage.html?rand="+Date.now());
		}
	});
}

function applyLocToAllMeetings(locationPath){
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			setLocationToAllMeetings:locationPath,
			a:Date.now()
		},
		success: function(result) {
			document.location="/content/girlscouts-vtk/en/vtk.plan.html";
		}
	});
}

function updLocations(locationPath,idName){
	var av =  document.getElementsByName(idName);
	var addon="|";
	for (e=0;e<av.length;e++) {
		if (av[e].checked==true) {
			addon+=av[e].value+"|";
		}
	}
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			chnLocation:addon,
			newLocPath:locationPath,
			a:Date.now()
		},
		success: function(result) {
			document.location="/content/girlscouts-vtk/en/vtk.plan.html";
		}
	});
}

function showLocationManager(){
	$("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand="+Date.now());
}
