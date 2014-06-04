
function rmCustActivity(x){
	
	$( "#planMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?rmCustActivity="+x, function( response, status, xhr ) {
	   	  if ( status != "error" ) {
	   	    
	   		 
	   	  }else{
	   		  
	   		
	   	  }
	   	  
	   	  
	   	});
	
	
	document.location="planView.jsp";
}

function viewMeetingLibrary(meetingPath){
	
	 
	 

		$( "#meetingLibraryView" ).load( "/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath="+meetingPath,function( html ) {
			  
			  
			  $(function() {
				    $( "#meetingLibraryView" ).dialog({width:800});
				  });
			  
		  });
}






	function addCustAgenda(){
		
	  	$(function() {
		    $( "#newMeetingAgenda" ).dialog({width:800});
		  });
	        	 	
	}
	

   	function createCustAgendaItem(mid, time, mPath){
   		console.log(1)
   		var newCustAgendaName = document.getElementById("newCustAgendaName").value;
   		var newCustAgendaDuration= document.getElementById("newCustAgendaDuration").value;
   		var urlPath =mPath +"&duration="+newCustAgendaDuration+"&name="+ newCustAgendaName+"&startTime="+time ;
   		console.log( urlPath )
   		 $.ajax({
    				  url: "/content/girlscouts-vtk/controllers/vtk.controller.html?newCustAgendaName="+urlPath,
    				  cache: false
    				})
    				  .done(function( html ) {
    					  //document.location="meeting.jsp?mid="+ mPath;
    					  document.location="/content/girlscouts-vtk/controllers/vtk.planView.html?elem="+mid;
    				  });
   		
   		
   	}
	
   	function showIt(x){
   		$( "#"+x ).show();
   	}
   	
   	
   	
   	

	   function getNewActivitySetup() {
		   var toRet="";
		   
	       var liTags = document.getElementById ("sortable").getElementsByTagName ("li");
	       for (var i = 0; i < liTags.length; i++) {
	    	   
	    	   			toRet+=  liTags[i].value +"," ;
	       		
	    	   
	       }
	       return toRet.substring(0, toRet.length-1);
	   }
	   
	   function repositionActivity(meetingPath){
		
		   var newVals = getNewActivitySetup();
		   
		   
		   var x =$.ajax({ // ajax call starts
		          url: '/content/girlscouts-vtk/controllers/vtk.controller.html?mid='+meetingPath+'&isActivityCngAjax='+ newVals, // JQuery loads serverside.php
		          data: '', // Send value of the clicked button
		          dataType: 'html', // Choosing a JSON datatype
		          success: function (data) { 
		              
		             
		            
		        	  location.reload();
		              
		          },
		   			error: function (data) { 
		   				
		   			}
		      });
		 	
	   }
	   
	   
	   function editAgenda(x){
		   
		    
		   $(function() {
			    $( "#"+x ).dialog({width:800});
			  });
	   }
	   
	  function rmAgenda(id, mid){
		  
		  
		   var x =$.ajax({ // ajax call starts
		          url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rmAgenda='+id+'&mid='+mid, // JQuery loads serverside.php
		          data: '', // Send value of the clicked button
		          dataType: 'html', // Choosing a JSON datatype
		          success: function (data) { 
		              
		              location.reload();
		          },
		   			error: function (data) { 
		   				
		   			}
		      });
	  }
	  
	  
	  function durEditActiv(duration, activPath, meetingPath){
		  
		  var x =$.ajax({ // ajax call starts
	          url: '/content/girlscouts-vtk/controllers/vtk.controller.html?editAgendaDuration='+duration+'&aid='+activPath+'&mid='+meetingPath, // JQuery loads serverside.php
	          data: '', // Send value of the clicked button
	          dataType: 'html', // Choosing a JSON datatype
	          success: function (data) { 
	              
	              location.reload();
	          },
	   			error: function (data) { 
	   				
	   			}
	      });
		  
	  }
	   