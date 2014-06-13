
        		function loadMeetings(){
        			
        			
        			var url = '/content/girlscouts-vtk/controllers/vtk.meetingInclude.html';
        			 $("#div1").load(url);
        			 
        			 
        			 
        			 
        			 
        		}	
        
        		 function x(planId, planPath){
        			 
        			 
        			 $.ajax({
        				  url: "/content/girlscouts-vtk/controllers/vtk.controller.html?addYearPlanUser="+planPath,
        				  cache: false
        				})
        				  .done(function( html ) {
        					  
        					  
        					 loadMeetings();
        					  
        					 //yesPlan();
        					 
        					location.reload();
        					  
        				  });
        			 
        			
        		  }
        		 
        		 function testIt(){
        			 var s =  document.getElementById("sortable");
        			
        			 s.sortable=true
        			 
        			
        			 
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
        			   console.log("doUpd");
        			   console.log( newVals)
        			   var x =$.ajax({ // ajax call starts
        				   url: '/content/girlscouts-vtk/controllers/vtk.controller.html?isMeetingCngAjax='+ newVals, // JQuery loads serverside.php
        			          data: '', // Send value of the clicked button
        			          
        			          dataType: 'html', // Choosing a JSON datatype
        			          
        			      })
        			      .done(function( html ) { loadMeetings();});
        			     
        			   console.log("doUpd end")
        				
        		   }
        		   
        		   function reloadMeeting(){
        			   
        			  
        			   
        			   var x =$.ajax({ // ajax call starts
     			          url: '/content/girlscouts-vtk/controllers/vtk.meetingInclude.html?isRefresh=true', // JQuery loads serverside.php
     			          data: '', // Send value of the clicked button
     			          dataType: 'html', // Choosing a JSON datatype
     			          success: function (data) { 
     			              
     			              
     			             
     			          },
     			   			error: function (data) { 
     			   				
     			   				
     			   			}
     			      });
        			   
        		   }
        		   
        		   function newActivity(){
        			 
        			  
        			    $( "#newActivity" ).load( "/content/girlscouts-vtk/controllers/vtk.newCustomActivity.html", function( response, status, xhr ) {
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
        		   
      
       
        
        
          
        
        
        function addExistActivity(activityId){
        	
        	$("#addExistActivity_err_"+activityId).load("/content/girlscouts-vtk/controllers/vtk.controller.html?addExistActivity="+activityId);
        	
        }
        
        
        
        function newLocCal(){
        	
        	
        	 $( "#newLocationCal" ).load( "/content/girlscouts-vtk/controllers/vtk.locations.html", function( response, status, xhr ) {
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
        
        function yesPlan(){
        	
        	
        	if( document.getElementById('div1').style.display=='none' ){
        		
        		document.getElementById('div1').style.display='block';
        		document.getElementById('div2').style.display='none';	
        	}else{
        		
        		document.getElementById('div2').style.display='block';
        		document.getElementById('div1').style.display='none';
        	}
        	
        	
        }
        
        function addLocation(){
       	 
       	 var  name = document.getElementById("loc_name").value;
       	 var  address = document.getElementById("loc_address").value;
       	 var  city = document.getElementById("loc_city").value;
       	 var  state = document.getElementById("loc_state").value;
       	 var  zip = document.getElementById("loc_zip").value;
       	 /*
       	 var urlParam = "&name="+ name+
       	 				"&address="+address+
       	 				"&city="+city+
       	 				"&state="+state+
       	 				"&zip="+zip;
       	 
       	 $("#locMsg").load("/content/girlscouts-vtk/controllers/vtk.controller.html?addLocation=true"+ urlParam);
       	*/
       	 
       	 
       	 
       	 $.ajax({
   	      url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
   	      type: 'POST',
   	      data: { 
   	    	  		addLocation:true,
   	    	  		name:name,
	 				address:address,
	 				city:city,
	 				state:state,
	 				zip:zip,
	 				a:Date.now()
   	      },
   	      success: function(result) {
   	    	document.location="/content/girlscouts-vtk/en/vtk.locationManage.html";
   	      }
   	  });
       	
       	 
       	 
       }
        
        
        
        
        
        //locations
        
       
          
          
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
        	
        	$( "#locMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?updSched=true&"+urlParam, function( response, status, xhr ) {
        	   	  if ( status != "error" ) { }else{ }  
        	   	});
        	
        }
          
          
          function buildSched(){
        	  
        	  
        	  
        		 var calStartDt = document.getElementById("calStartDt").value;
        		 var calAP = document.getElementById("calAP").value;
        		 var calFreq = document.getElementById("calFreq");
        		 var z =calFreq.options[calFreq.selectedIndex].text
        		 var calTime = document.getElementById("calTime").value;
        		 if( $.trim(calTime) =='') {alert("Time field empty");return;}
        		
        		 var _level="";
        		 var levels = document.getElementsByName('exclDt');
        		 for (var i=0; i < levels.length; i++){ 
        			 if (levels[i].checked)
        				_level+= levels[i].value +",";
        		 }
        		 
        		 
        		 $.ajax({
        		      url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
        		      type: 'POST',
        		      data: { 
        		    	         buildSched:true,
        		    	         calStartDt:calStartDt ,
        		        		 calAP:calAP,
        		        		 calFreq:z,
        		        		 calTime:calTime,
        		        		 exclDt:_level
        		      },
        		      success: function(result) {
        		    	  location.reload();
        		      }
        		  });
        		 /*
        		 var urlParam = 'calStartDt='+ calStartDt +
        		 "&calAP="+calAP+
        		 "&calFreq="+z+
        		 "&calTime="+calTime+
        		 "&exclDt="+_level;
        		 			
        		 $( "#calView" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?buildSched=true&"+urlParam, function( response, status, xhr ) {
        	   	  if ( status != "error" ) {
        	   		  
        	   		location.reload();
        	   	  }else{
        	   		  
        	   		  location.reload();
        	   	  }
        	   	  
        	   	  
        	   	});
        	   	*/
          }
          
          

          function rmCustActivity(x){
          	
          	$( "#locMsg" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?rmCustActivity="+x, function( response, status, xhr ) {
          	   	  if ( status != "error" ) {
          	   	  }else{
          	   	  }
          	   	  
          	   	});

          }
          
          
        
   


   
   
   function createNewCustActivity(){
  	 
  	 var newCustActivity_name = document.getElementById("newCustActivity_name").value;
 	
  	 //ex: MAND field 
  	 if( $.trim(newCustActivity_name) =='' ){alert("Please fill 'Name' field"); return false;}
 
  	 var newCustActivity_date = document.getElementById("newCustActivity_date").value;

  	 var newCustActivity_startTime = document.getElementById("newCustActivity_startTime").value;

  	 var newCustActivity_endTime = document.getElementById("newCustActivity_endTime").value;
  	 
  	 var newCustActivity_txt = document.getElementById("newCustActivity_txt").value;
  	
  	 var newCustActivityLocName = document.getElementById("newCustActivity_locName").value;
  	var newCustActivityLocAddr = document.getElementById("newCustActivity_locAddr").value;
  	/*
  	 var urlParam = 'newCustActivity_name='+ newCustActivity_name +'&' +
  	 			'newCustActivity_date='+ newCustActivity_date +'&'+
  	 			'newCustActivity_startTime='+ newCustActivity_startTime +'&'+
  	 			'newCustActivity_endTime='+ newCustActivity_endTime +'&'+
  	 			'newCustActivityLocName='+ newCustActivityLocName +'&'+
  	 			'newCustActivityLocAddr='+ newCustActivityLocAddr +'&'+
  	 			'newCustActivity_txt='+ newCustActivity_txt ;
  	 			
  	 */
  	 
  	 
  	 $.ajax({
	      url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
	      type: 'POST',
	      data: { 
	    	  newCustActivity:true,
	    	  newCustActivity_name:newCustActivity_name ,
	 			newCustActivity_date:newCustActivity_date ,
	 			newCustActivity_startTime: newCustActivity_startTime ,
	 			newCustActivity_endTime:newCustActivity_endTime ,
	 			newCustActivityLocName: newCustActivityLocName ,
	 			newCustActivityLocAddr: newCustActivityLocAddr ,
	 			newCustActivity_txt: newCustActivity_txt
	      },
	      success: function(result) {
	    	  location.reload();
	      }
	  });
	  
  	
   }
   
   function searchActivity(){
  	 
  	 var existActivSFind = document.getElementById("existActivSFind").value;
  	 var existActivSMon  = document.getElementById("existActivSMon").value;
  	 var existActivSYr = document.getElementById("existActivSYr").value;
  	 var existActivSDtFrom = document.getElementById("existActivSDtFrom").value;
  	 var existActivSDtTo= document.getElementById("existActivSDtTo").value;
  	 var existActivSReg= document.getElementById("existActivSReg").value;
  	 
  	 var _level="";
  	 var levels = document.getElementsByName('existActivSLevl');
  	 for (var i=0; i < levels.length; i++){ 
  		 if (levels[i].checked)
  			_level+= levels[i].value +",";
  	 }
  	 
  	 var _cat="";
  	 var levels = document.getElementsByName('existActivSCat');
  	 for (var i=0; i < levels.length; i++){ 
  		 if (levels[i].checked)
  			_cat+= levels[i].value +",";
  	 }
  	 
  	 var urlParam="&existActivSFind="+existActivSFind+
  	 		"&existActivSMon="+existActivSMon+
  	 		"&existActivSYr="+existActivSYr+
  	 		"&existActivSDtFrom="+existActivSDtFrom+
  	 		"&existActivSDtTo="+existActivSDtTo+
  	 		"&existActivSReg="+existActivSReg
  	 		"&existActivSLevl="+_level+
  	 		"&existActivSCat="+existActivSCat;
  	 
  	
  	 
  	 
  	 $("#listExistActivity").load("/content/girlscouts-vtk/controllers/vtk.controller.html?searchExistActivity=true"+ urlParam);
   }
   
   

   $('#plan_hlp_hrf').click(function() {
       $('#plan_hlp').dialog();
       return false;
   });
   
   
          