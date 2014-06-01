
$(function() {
    $( "#calStartDt" ).datepicker();
  });
  
  
function updSched(i, meetingPath, currDt){
	
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
	
	$( "#locMsg" ).load( "/VTK/include/controller.jsp?updSched=true&"+urlParam, function( response, status, xhr ) {
	   	  if ( status != "error" ) {}else{}
	   	 });
	
}