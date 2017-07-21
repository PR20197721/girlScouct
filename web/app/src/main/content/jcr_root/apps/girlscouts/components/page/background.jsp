<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
String backgroundPath = currentSite.get("bgImagePath",String.class);
if(backgroundPath != null && backgroundPath.trim().length() > 0){
	try{
		Resource content = resourceResolver.getResource(backgroundPath);	
		if(content != null && content.hasChildren()){
			StringBuffer sb = new StringBuffer();
			Iterable<Resource> backgrounds = content.getChildren();
			for (Resource background : backgrounds) {
				if(background.isResourceType("dam:Asset")){
					sb.append("\"").append(background.getPath()).append("\",");
				}			
			}
			%>
		 	<img class="main-bg" />
			<%-- Don't include "src" attribute or broken link icon will appear until the image is loaded --%>
			<script>
			     (function () {
			         "use strict";
			
			         // Set random full-page background image
			         // Select from an array to allow for custom image names
			         var images = [
			        	 	<%=sb.substring(0, sb.length()-1)%>
			            ],
						rand = Math.floor(Math.random() * images.length), // Returns 0 through length-1
						bg = document.querySelector(".main-bg");
			         	bg.addEventListener("load", function () {
							bg.className += " show"; 
			         	});
			         bg.src = images[rand];
			     }());
			</script>
		<%
		}
	}catch(Exception e){
		System.err.println("Error occurred while rendering background images at "+backgroundPath);
		e.printStackTrace();
	}
}
%>

