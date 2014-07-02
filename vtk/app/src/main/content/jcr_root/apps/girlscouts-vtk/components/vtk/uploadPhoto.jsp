 <%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%

String id= new java.util.Date().getTime() +"_" + Math.random();

%>
<a href="javascript:void(0)" onclick="location.reload()">CLOSE</a>

  <div style="background-color:orange;">
        	<h4>Upload File</h4>
        	
              <form action="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/" method="post"
                      onsubmit="bindAssetToYPC('<%=id %>', '<%=request.getParameter("refId") %>')"  enctype="multipart/form-data">
                
               <input type="hidden" name="id" value="<%=id%>"/>                 
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="file" name="custasset" size="50" />
               <br />
                <input type="submit" value="Upload File" />
         </form>
</div>
 
 <div style="background-color:red">CAMERA
         
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
         	
         		
         	var x =$.ajax({ // ajax call starts
         		url: '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/', // JQuery loads serverside.php
         		
         		data: {
         			"./jcr:data":myImage.src,
         			"./jcr:mimeType":"image/png"
         			
         		},
         		
         		type: 'POST',
         		success: function (data) { 
         		},
         		error: function (data) { 
         		}
         	});
         	
         };
         
         
         
         
        </script>
        
           
        
        
        
        
         </div>
         
         <script>
         
         function bindAssetToYPC(assetId, ypcId){
        	 
        	 $.ajax({
        			url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand='+Date.now(),
        			type: 'POST',
        			data: { 
        				bindAssetToYPC:assetId,
        				ypcId:ypcId,
        				a:Date.now()
        			},
        			success: function(result) {
        				
        			}
        		});
        	 
         }
         </script>