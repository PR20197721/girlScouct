<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
	String[] backgroundPaths = currentSite.get("bgImagePaths",String[].class);
	if(backgroundPaths != null && backgroundPaths.length>0){
		StringBuffer sb = new StringBuffer();
		for(String path: backgroundPaths){
	 		sb.append("\"").append(path).append("\",");
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
		         if (images.length > 0) {
		        	 document.body.style.background = "#000000";
		         }
		     }());
		</script>
<%	}%>