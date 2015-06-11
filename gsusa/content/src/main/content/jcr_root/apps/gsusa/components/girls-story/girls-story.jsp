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
            String deabout scription = vm.get("description", "");%>
            <!--  the following lines (div and ul) should goes to the container of the component instead of individual girls story component -->
            <div class="columns large-18 medium-18"> 
              <ul class="gs-stories-block">
                <li>
                  <div>
                    <img src="<%= imagePath%>" />
                    <p><a href="#" title="story title"><%= description %></a></p>
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
