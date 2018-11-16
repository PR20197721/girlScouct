function rmLocation(locationName){
	
        showError(null, "#locationEdit .errorMsg");
     
        
     if(!confirm('Are you sure you want to delete this location?') ){
    	 return false;
     }   
        
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: {
			act:'RemoveLocation',
			rmLocation:locationName,
			a:Date.now()
		},
		success: function(result) {
			vtkTrackerPushAction('RemoveLocation');
			$("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand="+Date.now());
		}
	});
}

function applyLocToAllMeetings(locationPath){
	$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		type: 'POST',
		data: { 
			act:'SetLocationAllMeetings',
			setLocationToAllMeetings:locationPath,
			a:Date.now()
		},
		success: function(result) {
			alert("Location successfully modified.");
			vtkTrackerPushAction('ChangeLocation');
			$("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand="+Date.now());
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
			act:'ChangeLocation',
			chnLocation:addon,
			newLocPath:locationPath,
			a:Date.now()
		},
		success: function(result) {
			alert("Location successfully modified.");
			$("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand="+Date.now());
			
		}
	});
}

function showLocationManager(){
	$("#locList").load("/content/girlscouts-vtk/controllers/vtk.locationManage.html?rand="+Date.now());
}
