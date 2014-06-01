function rmLocation(locationName){
		
		 $("#locMsg1").load("/VTK/include/controller.jsp?rmLocation="+locationName);
		 
	}
	
	function applyLocToAllMeetings(locationPath){
		
		 $("#locMsg1").load("/VTK/include/controller.jsp?setLocationToAllMeetings="+locationPath);
		 
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
		
		//$("#locMsg1").load("/VTK/include/controller.jsp?chnLocation="+addon+"&newLocPath="+locationPath);
		 
		
		 var x =$.ajax({ // ajax call starts
	          url: "/VTK/include/controller.jsp?chnLocation="+addon+"&newLocPath="+locationPath, // JQuery loads serverside.php
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
