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
	PageManager pm = resourceResolver.adaptTo(PageManager.class);

    if (selected.equals("dir")) { //user picks a directory
        if (!path.isEmpty()) {
        	//grab the directory and get all its children
			firstTimeInit = false;
			Page parent = pm.getPage(path);
			Iterator<Page> children = parent.listChildren();
			while (children.hasNext()){
				Page child = children.next();
				Node childNode = child.adaptTo(Node.class);
				Node contentNode = childNode.getNode("jcr:content");
				//TODO: we need a more robust validation.
				//Currently only check to see if there is a description
				if(contentNode.hasProperty("description")){
					validStoryPath.add(child.getPath());
				}
			}
        }
    } else { //manually picked
        if (storyPathArray != null) {
	    	for(String storyPath: storyPathArray){
	            if (!storyPath.isEmpty()) {
	                firstTimeInit = false;
	    			Page p = pm.getPage(storyPath);
	    			Node pageNode = p.adaptTo(Node.class);
					Node contentNode = pageNode.getNode("jcr:content");
					//TODO: we need a more robust validation.
					//Currently only check to see if there is a description
					if(contentNode.hasProperty("description")){
						validStoryPath.add(storyPath);
					}
	            }
	        }
        }
    }

    //if the validStoryPath is still empty after all the data processing
    //the user input is likely to be invalid. Set it to firstTimeInit
    if (validStoryPath.isEmpty()) {
        firstTimeInit = true;
    }
    %>

    <%if (!firstTimeInit) { %>
        <ul class="gs-stories-block"><%
        //use validStoryPath to generate the girls story component
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
        </ul></div><%
        return;
	} else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
        %>Please click here to edit. <%
        return;
    }
%>
