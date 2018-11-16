<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	try{
		String currentPath = slingRequest.getRequestPathInfo().getSuffix();
		String regex="/content/vtk-resources2/";
		int parentLevel=3;
		int slashIndex = 0;
		if (currentPath.indexOf("/content/vtkcontent") == 0) {
			parentLevel = 2;
    		regex = "/content/";
	    } 
		for (int i = 0; i < parentLevel; i++) {
			slashIndex = currentPath.indexOf("/", slashIndex + 1);
        }
		String rootPath = slashIndex == -1 ? "/vtk-assets.html" : currentPath.substring(0, slashIndex).replace(regex, "/vtk-assets.html/content/dam-resources2/girlscouts-");
		%>
		<a is="coral-anchorbutton" target="_blank" href="<%= rootPath%>" variant="primary" icon="upload" iconsize="S">Click here to upload resources</a>
		<%
	}catch (Exception e){
	
	}
%>  


