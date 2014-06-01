function rmLocation(locationName){
		
		 $("#locMsg1").load("/content/girlscouts-vtk/controllers/vtk.include.controller.html?rmLocation="+locationName);
		 
	}
	
	function applyLocToAllMeetings(locationPath){
		
		 $("#locMsg1").load("/content/girlscouts-vtk/controllers/vtk.include.controller.html?setLocationToAllMeetings="+locationPath);
		 
	}
	
	
	function updLocations(locationPath,idName){
		console.log( idName )
		   var av =  document.getElementsByName(idName);
		
		
		var addon="|";
		for (e=0;e<av.length;e++) {
			  if (av[e].checked==true) {
			   addon+=av[e].value+"|";
			   }
			  }
		
		//$("#locMsg1").load("/content/girlscouts-vtk/controllers/vtk.include.controller.html?chnLocation="+addon+"&newLocPath="+locationPath);
		 
		
		 var x =$.ajax({ // ajax call starts
	          url: "/content/girlscouts-vtk/controllers/vtk.include.controller.html?chnLocation="+addon+"&newLocPath="+locationPath, // JQuery loads serverside.php
	          data: '', // Send value of the clicked button
	          dataType: 'html', // Choosing a JSON datatype
	          success: function (data) { 
	              
	              console.log("OK");
	             
	          },
	   			error: function (data) { 
	   				console.log("BAD");
	   				//052114reloadMeeting();
	   			}
	      });
	}
