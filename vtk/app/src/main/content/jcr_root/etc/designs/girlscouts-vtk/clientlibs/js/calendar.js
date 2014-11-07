
$(function() {
    $( "#calStartDt" ).datepicker({minDate: 0});
  });
  


  
function updSched1(i, meetingPath, currDt){
	
	var date = document.getElementById("cngDate"+i).value;
	var time = document.getElementById("cngTime"+i).value;
	var ap = document.getElementById("cngAP"+i).value;
	var isCancelled = document.getElementById("isCancellMeeting"+i).checked;
//alert(date+" : "+ time +" : "+ ap +" : "+ isCancelled);
	
	
	 $.ajax({
	      url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
	      type: 'POST',
	      data: { 
	    	  act:'UpdateSched',
	    	  updSched:true,
	    	  meetingPath:meetingPath,
			  date:date,
				time:time,
				ap:ap,
				currDt:currDt,
				isCancelledMeeting:isCancelled,
				a:Date.now()
	      },
	      success: function(result) {
	    	  
	    	  document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	      },
	      error: function (xhr, ajaxOptions, thrownError) {
	    	  if( xhr.status==499)
	    		  alert("Error: Found duplicate DATE in your schedule. Please change date/time and try again.");
	    	  else
	    		  alert("Error occured updating schedule date.please try again");
	          
	        }
	  });
	 //document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	 //alert("over");
	
}