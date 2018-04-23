<%@page import="
				com.day.cq.wcm.api.WCMMode,
				org.apache.sling.api.resource.Resource, 
				java.util.Iterator,
				javax.jcr.Node,
				java.util.List,
				java.util.ArrayList" 
%>

<%@include file="/libs/foundation/global.jsp" %>
<%

	Resource cookiesResource = resource.getChild("cookies");
	List<Resource> cookieResourceList = new ArrayList<>();
	if (cookiesResource != null && !cookiesResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		Iterator<Resource> cookiesItr = cookiesResource.listChildren(); 
		if(cookiesItr != null){
			while(cookiesItr.hasNext()){
				cookieResourceList.add(cookiesItr.next());
			}
		}
	}
	
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
 %>
 	<cq:includeClientLib categories="apps.gsusa.authoring" />
<%
		if (cookieResourceList.size() == 0) {
			%>Meet The Cookies: Double click here to edit.<%
		}
	}

	if (cookieResourceList.size() > 0) {
%>
	    <div id="meet-cookie-layout">
<%
	    for (Resource cookieResource : cookieResourceList) {
			Node cookieNode = cookieResource.adaptTo(Node.class);
	    	
	        String title = "", image = "", description = "";
	        try {
		        	title = cookieNode.getProperty("title").getString();
	        } catch (Exception e) {} 
	        
	        try {
	        	image = cookieNode.getProperty("image").getString();
	        } catch (Exception e) {} 
	        
	        try {
	        	description = cookieNode.getProperty("description").getString();
	        } catch (Exception e) {} 
%>
		    
	        <div>
	            <img src="<%= image %>" alt="" />
	            <div class="wrapper">
	                <h4><%= title %></h4>
	                <section><%= description %></section>
	            </div>
	        </div>
<%
		}
%>
	    </div>
	    <script>
		    	$(document).ready(function(){
		        $('#meet-cookie-layout').layout({
	                itemPadding : 0
		        });
		    	})
	    </script>
<% 
	} 
%>
