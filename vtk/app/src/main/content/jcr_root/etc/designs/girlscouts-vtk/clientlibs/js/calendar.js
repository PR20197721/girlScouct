/*
$(function() {
    $( "#calStartDt" ).datepicker({minDate: 0});
  });
*/
  


  
function updSched1(i, meetingPath, currDt){
	
	var date = document.getElementById("cngDate"+i).value;
	var time = document.getElementById("cngTime"+i).value;
	var ap = document.getElementById("cngAP"+i).value;
	var isCancelled = false;//document.getElementById("isCancellMeeting"+i).checked;

	
	
	if( new Date(date)<= new Date() ){
		
		var x = document.getElementById("cngDate0ErrMsg");
		if( x!=null){
			
			x.innerHTML ="<span style='color:red;'>You cannot select a date in the past to reschedule the meetings. Please type or select a date in the future.</span>";
			
		}else{
			alert("You cannot select a date in the past to reschedule the meetings. Please type or select a date in the future."); 
		}
		return;
	}
	
	
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
	    	  
	    	  vtkTrackerPushAction('UpdateSched');
	    	  document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	    	  location.reload();
	      },
	      error: function (xhr, ajaxOptions, thrownError) {
	    	  if( xhr.status==499)
	    		  alert("This date and time have already been selected for another meeting. Please select a different date and time.");
	    	  else
	    		  alert("This date and time have already been selected for another meeting. Please select a different date and time.");
	          
	        }
	  });
	 //document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	 //alert("over");
	
}