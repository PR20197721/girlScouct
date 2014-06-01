<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<!doctype html>
<html class="no-js" lang="en">
    <head>

<!-- Artifact Browser -->
<!--[if lt IE 9]>
        <link rel="stylesheet" href="css/foundation-ie8.css" />
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
        <link rel="stylesheet" href="css/foundation.css" />
<!--<![endif]-->
        <link rel="stylesheet" href="css/app.css" />
        <script src="js/vendor/modernizr.js"></script>
        
       
       
 
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
  
        
        
       
       
        <script>
         
        	
        	
        	
        		 function x(planId){
        			
        		    $("#div1").load("/VTK/include/meeting.jsp?planId="+planId);
        			alert(1);
        		  }
        		 
        		 function testIt(){
        			 var s =  document.getElementById("sortable");
        			 alert(s);
        			 s.sortable=true
        			 alert(s.sortable ==true)
        			 /*  $("#sortable1234").sortable();  */
        			 
					}
        		

        		  
        		   

        		   
        		   function getNewMeetingSetup() {
        			   var toRet="";
        			   
        		       var liTags = document.getElementById ("sortable123").getElementsByTagName ("li");
        		       for (var i = 0; i < liTags.length; i++) {
        		    
        		    	   toRet+=  liTags[i].value +"," ; 
        		       }
        		       return toRet.substring(0, toRet.length-1);
        		   }
        		   
        		   function doUpdMeeting(){
        			
        			   var newVals = getNewMeetingSetup();
        			   alert("SWAPING: "+ newVals );
        			  
        			   
        			   var x =$.ajax({ // ajax call starts
        			          url: '/VTK/include/controller.jsp?isMeetingCngAjax='+ newVals, // JQuery loads serverside.php
        			          data: '', // Send value of the clicked button
        			          dataType: 'json', // Choosing a JSON datatype
        			          success: function (data) { 
        			              
        			              console.log("OK");
        			             
        			          },
        			   			error: function (data) { 
        			   				console.log("BAD");
        			   				reloadMeeting();
        			   			}
        			      });
        			     
        			 
        			   
        				
        				
        				
        		   }
        		   
        		   function reloadMeeting(){
        			   
        			   $("#div1").load("/VTK/include/meeting.jsp?isRefresh=true");
        		   }
        		   
        		   function newActivity(){
        			 
        			   // $("newActivity").load("/VTK/include/newCustomActivity.jsp");
        			  
        			    $( "#newActivity" ).load( "/VTK/include/newCustomActivity.jsp", function( response, status, xhr ) {
        			    	  if ( status == "error" ) {
        			    	    var msg = "Sorry but there was an error: ";
        			    	    $( "#error" ).html( msg + xhr.status + " " + xhr.statusText );
        			    	  }else{
        			    	
        			    		  
        			    		  $(function() {
        	        				    $( "#newActivity" ).dialog({width:800});
        	        				  });
        	        			
        			    		  
        			    	  }
        			    	  
        			    	  
        			    	});
        			    
        			    
        		   }
        		   
      
       
        
        
        $(function() {
            $( "#datepicker" ).datepicker();
          });
          
        
        
        function addExistActivity(activityId){
        	
        	$("#addExistActivity_err_"+activityId).load("/VTK/include/controller.jsp?addExistActivity="+activityId);
        	
        }
        
        
        
        function newLocCal(){
        	
        	
        	 $( "#newLocationCal" ).load( "/VTK/include/locations.jsp", function( response, status, xhr ) {
		    	  if ( status == "error" ) {
		    	    var msg = "Sorry but there was an error: ";
		    	    $( "#error" ).html( msg + xhr.status + " " + xhr.statusText );
		    	  }else{
		    	
		    		  
		    		  $(function() {
       				    $( "#newLocationCal" ).dialog({width:800});
       				  });
       			
		    		  
		    	  }
		    	  
		    	  
		    	});
        	
        }
        </script>
        
        
</head>
<body>



<div id="newActivity" title="New Activity"> </div>
<div id="newLocationCal" title="New Location & Calendar"> </div>

 <div style="background-color:#FFF;">
                
                <%=new java.util.Date()%>
       
       
       
                
<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd class="active"><a href="#panel2-2">Year Plan</a></dd>
  <dd><a href="#panel2-3">Meeting Plan</a></dd>
  <dd><a href="#panel2-4">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
  <div class="content" id="panel2-1">
    <p>First panel content goes here...
   
    <a href="javascript:void(0)" onclick="x()">test</a>
    
    
    
    </p>
  </div>
  <div class="content active" id="panel2-2">
  
  <div id="div1">
    <p><%@include file="include/yearPlan.jsp" %>
    
 
    </div>
     
     
    </p>
  </div>
  
    
 
  <div class="content" id="panel2-3">
    <p>Third panel content goes here...</p>
  </div>
  <div class="content" id="panel2-4">
    <p>Fourth panel content goes here...</p>
  </div>
  <div class="content" id="panel2-5">
    <p>Fourth panel content goes here...</p>
  </div>
</div>

<br/></br>

    

                </div>


</body>
</html>