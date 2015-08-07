<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>

<%
    int numberOfStories;
    try {
        numberOfStories = properties.get("number", Integer.class);
    } catch (NullPointerException e) {
        numberOfStories = 6;
    }
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
            bg = getImageRenditionSrc(resourceResolver, bg, "cq5dam.npd.middle.");
            style = "style=\"background: url(" + bg+ ") no-repeat transparent center center / cover\"";
        } catch (Exception e) {}
%>
        <li <%=style%> id="tag_tile_featured_story_<%= i %>">
            <cq:include path="<%= id %>" resourceType="gsusa/components/featured-story" />
        </li>
<%
    }
	request.removeAttribute("index");
%>
</ul>
