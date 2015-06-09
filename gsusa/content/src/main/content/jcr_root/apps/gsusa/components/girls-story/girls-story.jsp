<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, 
                com.day.cq.wcm.foundation.Image" %>

<%@page session="false"%><%
    String path = properties.get("path", "");
    
    if (!path.isEmpty()) {
        Resource res = resourceResolver.resolve(path + "/jcr:content");
        String imagePath = path + "/jcr:content/image.img.png";
        if (res != null && !res.getResourceType().equals("sling:nonexisting")) {
            ValueMap vm = (ValueMap) res.adaptTo(ValueMap.class);
            String description = vm.get("description", "");%>
            
            <div class="columns large-18">
    	      <ul class="gs-stories-block">
        	    <li>
            	  <div>
			        <img src="<%= imagePath%>" />
			         <a href="#" title="story title"><%= description %></a>
			      </div>
			    </li>
			  </ul>
			</div>
            <%
        }
    } else if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
        %>Please click here to edit. <%
    }
%>
