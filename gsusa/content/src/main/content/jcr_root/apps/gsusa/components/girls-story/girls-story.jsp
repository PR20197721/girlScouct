<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode,
    			java.util.ArrayList,
				java.util.Iterator,
				java.util.Collections,
                com.day.cq.wcm.foundation.Image" %>

<%@page session="false"%><%

    String path = properties.get("path", "");
    String[] storyPathArray = properties.get("storypath", String[].class);
    String selected = properties.get("source", "");
    ArrayList<String> validStoryPath = new ArrayList<String>();
    Boolean firstTimeInit = true;
 
    if (selected.equals("dir")) { //user picks a directory
        if (!path.isEmpty()) {
			firstTimeInit = false;
			PageManager pm = resourceResolver.adaptTo(PageManager.class);
			Page parent = pm.getPage(path);
			Iterator<Page> children = parent.listChildren();
			while (children.hasNext()){
				Page child = children.next();
				validStoryPath.add(child.getPath());
			}
        }
    } else { //manually picked
        if (storyPathArray != null) {
	    	for(String storyPath: storyPathArray){
	            if (!storyPath.isEmpty()) {
	                firstTimeInit = false;
					validStoryPath.add(storyPath);
	            }
	        }
        } 
    }
    
    if (validStoryPath.isEmpty()) {
		firstTimeInit = true;
	}
    %>
    <%if (!firstTimeInit) { %>
    	<ul class="gs-stories-block"><%
        for (String storyPath: validStoryPath) {
            Resource res = resourceResolver.resolve(storyPath + "/jcr:content");
            String imagePath = storyPath + "/jcr:content/image.img.png";
            if (res != null && !res.getResourceType().equals("sling:nonexisting")) {
                ValueMap vm = (ValueMap) res.adaptTo(ValueMap.class);
                String description = vm.get("description", "");%>
                <li>
                  <div>
                    <img src="<%= imagePath %>" alt="<%= description %>"/>
                    <p><a href="#" title="story title"><%= description %></a></p>
                  </div>
                </li><%
             }
        }%>
	</ul>
    	
    	
		<%
        } else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
        	%>Please click here to edit. <%
    	}
    %>
