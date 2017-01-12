<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%

String id= new java.util.Date().getTime() +"_" + Math.random();
int isFile=0;
try{ isFile = Integer.parseInt( request.getParameter("isFile") ); }catch(Exception e){e.printStackTrace();}
%>
<a href="javascript:void(0)" onclick="location.reload()">CLOSE</a>

  <div style="background-color:orange; <%if(isFile==0){%> display:none; <% } %>">
  
  
  
        	<h4>Upload File</h4>
        	
        	<%String assetId = new java.util.Date().getTime() +"_"+ Math.random(); %>
        	
        	
        
        
       <a href="/content/girlscouts-vtk/en/vtk.test2.html?refId=<%=request.getParameter("refId")%>&myId=<%=request.getParameter("myId")%>">dropbox</a>
        
        
        
         
   </div>
   
 
 
 
 
 
 <% if(isFile==0){%> 
    <div style="background-color:red; ">CAMERA
          
          <input type="button" value="DESTROY" onclick="stopId()"/>
          
         <div id="example" style="height:300px; width:300px"></div>
         
          <canvas id="myCanvas" width="578" height="200" style="border: 2px solid blue;"></canvas>
         
         <div id="upld" style="background-color:yellow; border:2px solid green;display:none;">
         
         Would you like to upload photo?
         
         <input type="button" value="RESET" onclick="container.style.display='inline';gallery.innerHTML=''; document.getElementById( 'upld').style.display='none'; "/>
         </hr>
         <br/>Asset NAme<input type="text" id="aName" value="" maxlength="10"/>
           <br/>Asset Description<input type="text" id="aDesc" value=""  maxlength="10"/>
           <input type="button" value="UPLOAD" onclick="xx12()"/>
         </div>
			<div id="gallery" style=""></div>
			
			
			
			
			 <form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"  
              		id="frmImg"	name="frmImg" onsubmit="return bindAssetToYPC( '<%=VtkUtil.getYearPlanBase(user, troop)%><%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets/<%=assetId %>', '<%=request.getParameter("refId")%>' )"  enctype="multipart/form-data">
              
                       <input type="hidden" name="loc" value="<%=VtkUtil.getYearPlanBase(user, troop)%><%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets"/>
             
               Asset Title: <input type="text" id="assetTitle" name="assetTitle" value="" />
                Asset Description: <input type="text" id="assetDesc" name="assetDesc" value="" />
               <input type="hidden" name="id" value="<%=assetId %>"/>     
                <input type="hidden" name="me" value="<%=request.getParameter("myId")%>"/>      
               <input type="hidden" name="owner" value="<%=troop.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="hidden" id="custasset" name="custasset" value="" />
			   <input type="file" id="custasset1" name="custasset" value="" />
               <br />
                <input type="submit" value="Upload Img" />
         </form>
			
			
			
			
        <script> 
         container = document.getElementById( "example" );
         gallery = document.getElementById( "gallery" );
         var myImage;
         
         myPhotobooth = new Photobooth( container );

         myPhotobooth.onImage = function( dataUrl ){

        	

        	 myImage = document.createElement( "img" );
         	myImage.src = dataUrl;
         	gallery.appendChild( myImage );
         	
         	container.style.display="none";
         	document.getElementById( "upld" ).style.display="inline";
         	
         	
         	loadCanvas( dataUrl );
         
         	var canvas = document.getElementById("myCanvas");
         	var img    = canvas.toDataURL("image/png");

         	 document.getElementById('custasset').value = dataUrl.replace('data:image/png;base64,', '');//img.replace('data:image/png;base64,', '');
         
         	
         	 
         	 
         	 
         	var data = img.replace('data:image/png;base64,', '');
         
         	/* good -dont rm
         	$.ajax({
         		type: "POST",
         		url: "<%=VtkUtil.getYearPlanBase(user, troop)%><%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets",
         		contentType: false,
         		processData: false,
         		data: data,
         		contentType: 'application/x-www-form-urlencoded',
         		success: function (msg) {
         			alert("Done!");
         		},
         		error: alert('No')
         	});
         	*/
         	
         	
         	
         	var jForm = new FormData();
            jForm.append("aType", "AID");
            //jForm.append("loc", "/vtk/<%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets");
            jForm.append("file", data);
            $.ajax({
         		
             	type: "POST",         	
             	url: '/content/girlscouts-vtk/controllers/auth.asset.html', // JQuery loads serverside
             	contentType: false,
         		processData: false,
         		cache: false,
         		data: jForm,
         		
         		contentType: 'multipart/form-data',
         		success: function (msg) {
         			alert("second441 OK");
         		},
         		error: alert('second441 No')
             	});
            
            
            
            
            
            
            /*
         $.ajax({
         		
         	type: "POST",         	
         	url: '/content/girlscouts-vtk/controllers/auth.asset.html', // JQuery loads serverside
         	contentType: false,
     		processData: false,
     		
     		data: {
     			
     			loc: "<%=VtkUtil.getYearPlanBase(user, troop)%><%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets",
     			id:"<%=assetId %>",
     			me:"<%=request.getParameter("myId")%>",
     			assetDesc: aDesc,
     			aType: "AID",
     			assetName : aName,
     			data:data,
     			owner:"<%=troop.getId()%>"
     			
     		},
     		contentType: 'application/x-www-form-urlencoded',
     		contentType: 'multipart/x-www-form-urlencoded',
     		success: function (msg) {
     			alert("second OK");
     		},
     		error: alert('second No')
         	});
         	*/
         	
         	
         	
         	
         	
         	
         	
         	
         	
         	/*
         	 
         	$.ajax({
       		  type: "POST",
       		  url: "<%=VtkUtil.getYearPlanBase(user, troop)%><%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets",
       		
       		contentType: false,
       		
       		contentType: 'application/x-www-form-urlencoded',
       		  
       		  data: { 
       		     imgBase64: img
       		  }
       		}).done(function(o) {
       		  console.log('saved'); 
       		  // If you want the file to be visible in the browser 
       		  // - please modify the callback in javascript. All you
       		  // need is to return the url to the file, you just saved 
       		  // and than put the image in your browser.
       		});
       */
        }
        
        
        function xx12(){
         	alert("UPLOADINGGGG")
         	
         	
         	 var aName= document.getElementById( "aName" );
         	 var aDesc= document.getElementById( "aDesc" );
         	 
         	 if( $.trim(aName.value) =='' || $.trim(aDesc.value) =='' ){alert('Asset Name,Desc can not be blank'); return false;}
         	//if( $.trim(aDesc.value) =='' ){alert('Asset Description can not be blank'); return false;}
         	 
         	
         	console.log("::"+toDataUrl(myImage));
         	
         	var gg= toDataUrl(myImage);
         	//var blob = dataURItoBlob(gg);
         	//var fd = new FormData(document.forms[0]);
         	
         	
         	
         	  var fd = new FormData();
         	  fd.append("file", gg);
         	    
         alert(1);
         
         var canvas = document.getElementById("myCanvas");
         var img    = canvas.toDataURL("image/png");
     
         	    
      	 
        
      	
      	
      	 
     
         	var x =$.ajax({ // ajax call starts
         	
         		url: '/content/girlscouts-vtk/controllers/auth.asset.html', // JQuery loads serverside
         		
         		data: {
         			custasset:fd,
         			loc: "<%=VtkUtil.getYearPlanBase(user, troop)%><%=troop.getTroop().getCouncilCode()%>/<%=troop.getTroop().getTroopId() %>/assets",
         			id:"<%=assetId %>",
         			me:"<%=request.getParameter("myId")%>",
         			assetDesc: aDesc,
         			aType: "AID",
         			assetName : aName,
         			data:img,
         			owner:"<%=troop.getId()%>"
         			
         		},
         		
         		type: 'POST',
         		
         		async: false,
         	    cache: false,
         	    contentType: false,
         	    processData: false,
         	    
         		success: function (data) { 
         			alert("GOOD")
         		},
         		error: function (data) {
         			alert("NO")
         		}
         	});
         	
         	
         	
         	
         	
         };
         
         function toDataUrl( source )
         {
             var canvas = document.createElement( "canvas" );
             canvas.width = source.videoWidth || source.width;
             canvas.height = source.videoHeight || source.height;
             canvas.getContext( "2d" ).drawImage( source, 0, 0 );
             return canvas.toDataURL();
         };
         
         function saveDataUrl( fileName, dataUrl )
         {
             var dataString = dataUrl.split( "," )[ 1 ];
             var buffer = new Buffer( dataString, 'base64');
             var extension = dataUrl.match(/\/(.*)\;/)[ 1 ];
             var fs = require( "fs" );
             var fullFileName = fileName + "." + extension;
             fs.writeFileSync( fullFileName, buffer, "binary" );
         }
         
         function stopId(){
        	 
        	 myPhotobooth.destroy();
        	 location.reload(); 
 		}
         
         function loadCanvas(dataURL) {
             var canvas = document.getElementById('myCanvas');
             var context = canvas.getContext('2d');

             // load image from data url
             var imageObj = new Image();
             imageObj.onload = function() {
               context.drawImage(this, 0, 0);
             };

             imageObj.src = dataURL;
           }
         
        </script>
        
           
        
        
        
        
         </div>
  <%} %>
         
         <script>
         
         function bindAssetToYPC(assetId, ypcId){
        	
        	 var assetTitle = "tt"; //document.getElementById("assetTitle").value;
        	 var assetDesc =  "rr"; //document.getElementById("assetDesc").value;
        	/*
        	 var custasset = document.getElementById("custasset1").value;
        	 
        	
        	 if( $.trim(custasset)=='' ){alert('Please select file to upload');return false;}
        	 if( $.trim(assetDesc)=='' ){alert('Please enter description of asset');return false;}
        	 if( $.trim(assetTitle)=='' ){alert('Please enter title of asset');return false;}
        	 */
        	 
        	 
        	 
         	
        	
        	 $.ajax({
        			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
        			type: 'POST',
        			data: { 
        				act:'BindAssetToYPC',
        				bindAssetToYPC:assetId,
        				ypcId:ypcId,
        				assetDesc:assetDesc,
        				assetTitle:assetTitle,
        				a:Date.now()
        			},
        			success: function(result) {
        				
        			}
        		});
        	
        	 
         }
         
         function dataURItoBlob(dataURI) {
        	    // convert base64/URLEncoded data component to raw binary data held in a string
        	    var byteString;
        	    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        	        byteString = atob(dataURI.split(',')[1]);
        	    else
        	        byteString = unescape(dataURI.split(',')[1]);

        	    // separate out the mime component
        	    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]

        	    // write the bytes of the string to a typed array
        	    var ia = new Uint8Array(byteString.length);
        	    for (var i = 0; i < byteString.length; i++) {
        	        ia[i] = byteString.charCodeAt(i);
        	    }

        	    return new Blob([ia], {type:mimeString});
        	}
         </script>
