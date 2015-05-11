<%@include file="/libs/foundation/global.jsp" %>
<%
    int numberOfStories;
    try {
        numberOfStories = properties.get("number", Integer.class);
    } catch (NullPointerException e) {
        numberOfStories = 6;
    }
    for (int i = 0; i < numberOfStories; i++) {
        String id = "story_" + Integer.toString(i);
%>
        <cq:include path="<%= id %>" resourceType="gsusa/components/featured-story" />
<%
    }
%>