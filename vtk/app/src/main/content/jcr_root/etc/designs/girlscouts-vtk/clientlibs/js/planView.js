
function rmCustActivity12(x){
	
	//6/4/$( "#planMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?rmCustActivity="+x, function( response, status, xhr ) {
	
	$( "#editAgenda" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?rmCustActivity="+x, function( response, status, xhr ) {
	   	  if ( status != "error" ) {
	   	    
	   		 
	   	  }else{
	   		  
	   		
	   	  }
	   	  
	   	  
	   	});
	
	
	//6/4/document.location="planView.jsp";
	document.location="/content/girlscouts-vtk/en/vtk.planView.html";
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
	

	<!-- TODO: change js name -->
   	function createCustAgendaItem1(mid, time, mPath){
   		console.log(1)
   		var newCustAgendaName = document.getElementById("newCustAgendaName").value;
   		var newCustAgendaDuration= document.getElementById("newCustAgendaDuration").value;
   		var createCustAgendaTxt = document.getElementById("newCustAgendaTxt").value;
   		var urlPath =mPath +"&duration="+newCustAgendaDuration+"&name="+ newCustAgendaName+"&startTime="+time+"&txt="+createCustAgendaTxt ;
   		console.log( urlPath )
   		 $.ajax({
    				  url: "/content/girlscouts-vtk/controllers/vtk.controller.html?newCustAgendaName="+urlPath,
    				  cache: false
    				})
    				  .done(function( html ) {
    					  //document.location="meeting.jsp?mid="+ mPath;
    					  document.location="/content/girlscouts-vtk/en/vtk.planView.html?elem="+mid;
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
	  

	  
	  function revertAgenda(mid) {
		  
		  var x =$.ajax({ // ajax call starts
	          url: '/content/girlscouts-vtk/controllers/vtk.controller.html?revertAgenda=true&mid='+ mid, // JQuery loads serverside.php
	          data: '', // Send value of the clicked button
	          dataType: 'html', // Choosing a JSON datatype
	          success: function (data) { 
	              
	              location.reload();
	          },
	   			error: function (data) { 
	   				
	   			}
	      });
		  
	  }
	  
	  
	  function sendMeetingReminderEmail(mid){
		  
		  var email_to_gp = document.getElementById("email_to_gp").value;
		  var email_to_sf = document.getElementById("email_to_sf").value;
		  var email_to_tv = document.getElementById("email_to_tv").value;
		  var email_cc = document.getElementById("email_to_cc").value;
		  var email_subj = document.getElementById("email_subj").value;
		  var email_htm = document.getElementById("email_htm").value; 
		  
		  var urlParam = "&email_to_gp="+email_to_gp+
		  				"&email_to_sf="+email_to_sf+
		  				"&email_to_tv="+email_to_tv+
		  				"&email_cc="+ email_cc+
		  				"&email_subj="+email_subj+
		  				"&email_htm="+ email_htm;
		  //console.log(urlParam);
		  
		  
		  
		  
		  
		  var str = '?sendMeetingReminderEmail=true&mid='+ mid+''+ urlParam;
		  $.ajax({
		      url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
		      type: 'POST',
		      data: { mystring: str },
		      success: function(result) {
		          console.log(result);
		      }
		  });
		  
		  
		  if(true)return;
		  
		  
		  var x =$.ajax({ // ajax call starts
	          url: '/content/girlscouts-vtk/controllers/vtk.controller.html', // JQuery loads serverside.php
	          data: 'sendMeetingReminderEmail=true&mid='+ mid+''+ urlParam, // Send value of the clicked button
	          //type: "POST",
	          dataType: 'html', // Choosing a JSON datatype
	          success: function (data) { 
	              
	              //location.reload();
	          },
	   			error: function (data) { 
	   				
	   			}
	      });
		  
	  }
	   