<%@include file="/libs/foundation/global.jsp" %>
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
        try {
            bg = currentNode.getNode(id + "/bg").getProperty("fileReference").getString();
        } catch (Exception e) {}
%>
        <li style="background: url('<%=bg%>') no-repeat transparent 0 50% / cover">
            <cq:include path="<%= id %>" resourceType="gsusa/components/featured-story" />
        </li>
<%
    }
%>
</ul>