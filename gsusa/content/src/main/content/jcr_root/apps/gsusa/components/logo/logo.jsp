<%@include file="/libs/foundation/global.jsp" %>
<%
    String imgPath = "";
    try {
        imgPath = ((ValueMap)resource.getChild("image").adaptTo(ValueMap.class)).get("fileReference", "");
    } catch (Exception e) {}

%>
<img src="<%= imgPath %>" alt="Girl Scouts U.S.A. Home" aria-label="Girl Scouts U.S.A. Home"/>
