 <%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
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
        	
        	
        
          <form action="/content/girlscouts-vtk/controllers/auth.asset.html" method="post"  
              			onsubmit="return bindAssetToYPC( '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/<%=assetId %>', '<%=request.getParameter("refId")%>' )"  enctype="multipart/form-data">
              
                       <input type="hidden" name="loc" value="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets"/>
              Asset Name: <input type="text" id="assetDesc" name="assetDesc" value="" />
               <input type="hidden" name="id" value="<%=assetId %>"/>     
                <input type="hidden" name="me" value="<%=request.getParameter("myId")%>"/>      
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="file" id="custasset" name="custasset" size="50" />
               <br />
                <input type="submit" value="Upload File" />
         </form>
       
        
       <a href="/content/girlscouts-vtk/en/vtk.test2.html?refId=<%=request.getParameter("refId")%>&myId=<%=request.getParameter("myId")%>">dropbox</a>
        
        
        
         
   </div>
   
 
 
 
 
 
 <% if(isFile==0){%> 
    <div style="background-color:red; ">CAMERA
          
          
         <div id="example" style="height:300px;"></div>
			<div id="gallery" style=""></div>
			
			
        <script> 
         container = document.getElementById( "example" );
         gallery = document.getElementById( "gallery" );

         myPhotobooth = new Photobooth( container );

         myPhotobooth.onImage = function( dataUrl ){
         	var myImage = document.createElement( "img" );
         	myImage.src = dataUrl;
         	gallery.appendChild( myImage );
         	
         		
         	console.log(toDataUrl(myImage));
         	
         	var gg= toDataUrl(myImage);
         	//var blob = dataURItoBlob(gg);
         	//var fd = new FormData(document.forms[0]);
         	
         	
         	  var fd = new FormData();
         	  fd.append("file", gg);
         	    
         
         	    
         	var x =$.ajax({ // ajax call starts
         	
         		url: '/content/girlscouts-vtk/controllers/auth.asset.html', // JQuery loads serverside
         		
         		data: {
         			"custasset":fd,
         			"loc": "/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets",
         			"id":"<%=assetId %>",
         			"me":"<%=request.getParameter("myId")%>",
         			"assetDesc": "TODO",
         			"owner":"<%=user.getId()%>"
         			
         		},
         		
         		type: 'POST',
         		
         		async: false,
         	    cache: false,
         	    contentType: false,
         	    processData: false,
         	    
         		success: function (data) { 
         			
         		},
         		error: function (data) {
         			
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
         
        
        </script>
        
           
        
        
        
        
         </div>
  <%} %>
         
         <script>
         
         function bindAssetToYPC(assetId, ypcId){
        	 
        	 var assetDesc = document.getElementById("assetDesc").value;
        	 var custasset = document.getElementById("custasset").value;
        	 
        	 if( $.trim(custasset)=='' ){alert('Please select file to upload');return false;}
        	 if( $.trim(assetDesc)=='' ){alert('Please enter name of asset');return false;}
        	 
        	 
        	 $.ajax({
        			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
        			type: 'POST',
        			data: { 
        				bindAssetToYPC:assetId,
        				ypcId:ypcId,
        				assetDesc:assetDesc,
        				a:Date.now()
        			},
        			success: function(result) {
        				
        			}
        		});
        	 
         }
         </script>