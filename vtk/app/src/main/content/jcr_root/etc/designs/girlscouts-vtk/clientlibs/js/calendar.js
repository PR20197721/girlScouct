
$(function() {
    $( "#calStartDt" ).datepicker();
  });
  
  
function updSched1(i, meetingPath, currDt){
	
	var date = document.getElementById("cngDate"+i).value;
	var time = document.getElementById("cngTime"+i).value;
	var ap = document.getElementById("cngAP"+i).value;
	var isCancelled = document.getElementById("isCancellMeeting"+i).checked;

	
	
	var urlParam =  "meetingPath="+meetingPath+
					"&date="+date+
					"&time="+time+
					"&ap="+ap+
					"&currDt="+currDt+
					"&isCancelledMeeting="+ isCancelled;
	
	$( "#locMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?updSched=true&"+urlParam, function( response, status, xhr ) {
	   	  if ( status != "error" ) {}else{}
	   	 });
	
	document.location="/content/girlscouts-vtk/en/vtk.plan.html";
}