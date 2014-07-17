
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
	    	  //alert("Done");
	    	  document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	      }
	  });
	 //document.location="/content/girlscouts-vtk/en/vtk.plan.html";
	 //alert("over");
	
}