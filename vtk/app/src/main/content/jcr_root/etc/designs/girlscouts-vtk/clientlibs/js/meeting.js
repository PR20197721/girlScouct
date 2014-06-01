
       	function addCustAgenda(){
       		
       	  	$(function() {
				    $( "#newMeetingAgenda" ).dialog({width:800});
				  });
       	        	 	
       	}
       	
       	
       	
       	function createCustAgendaItem(mPath, time){
       		
       		var newCustAgendaName = document.getElementById("newCustAgendaName").value;
       		var newCustAgendaDuration= document.getElementById("newCustAgendaDuration").value;
       		var urlPath =mPath +"&duration="+newCustAgendaDuration+"&name="+ newCustAgendaName+"&startTime="+time ;
       		
       		 $.ajax({
        				  url: "/VTK/include/controller.jsp?newCustAgendaName="+urlPath,
        				  cache: false
        				})
        				  .done(function( html ) {
        					  document.location="meeting.jsp?mid="+ mPath;
        				  });
       		
       		
       	}