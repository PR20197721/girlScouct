
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
        				  url: "/content/girlscouts-vtk/controllers/vtk.include.controller.html?newCustAgendaName="+urlPath,
        				  cache: false
        				})
        				  .done(function( html ) {
        					  document.location="meeting.jsp?mid="+ mPath;
        				  });
       		
       		
       	}