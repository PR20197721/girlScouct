<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
    int numberOfStories;
    try {
        numberOfStories = properties.get("number", Integer.class);
    } catch (NullPointerException e) {
        numberOfStories = 6;
    }
    
    String resourceType = "gsusa/components/homepage";
    String storiesClass = "";
    try{
    	resourceType = currentPage.getProperties().get("sling:resourceType", String.class);
    	String resourceName = resourceType.substring(resourceType.lastIndexOf("/") + 1);
    	storiesClass = resourceName;
    } catch(Exception e){
    	e.printStackTrace();
    }
    
    if(storiesClass.equals("three-column-page")){
    	if(WCMMode.fromRequest(request) == WCMMode.EDIT){
    		%> The Featured Stories component should only be used with One Column and Two Column Pages, and the Homepage. It has been disabled for the Three Column Page template. <%
    	}
    } else {
%>
	<ul class="featured-stories inline-list clearfix">
<%
    for (int i = 0; i < numberOfStories; i++) {
        String id = "story_" + Integer.toString(i);
        String bg = "";
        String style = "";
        
        request.setAttribute("index", i);
        try {
            bg = currentNode.getNode(id + "/bg").getProperty("fileReference").getString();
            bg = gsImagePathProvider.getImagePath(bg,"cq5dam.npd.middle");
            style = "style=\"background: url(" + bg+ ") no-repeat transparent center center / cover\"";
        } catch (Exception e) {}
%>
        <li <%=style%> id="tag_tile_featured_story_<%= i %>" class="<%= storiesClass%>">
            <cq:include path="<%= id %>" resourceType="gsusa/components/featured-story" />
        </li>
<%
    }
	request.removeAttribute("index");
    }
%>
</ul>
