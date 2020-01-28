<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode,
                java.net.URLEncoder" %>
<%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %>
<ui:includeClientLib categories="apps.girlscouts.components.searchbox"/>
<%
    String placeholderText = properties.get("placeholder-text", "");
    String lastSearch = slingRequest.getParameter("q") != null ? slingRequest.getParameter("q") : "";
    lastSearch = URLEncoder.encode(lastSearch, "UTF-8");
    if (!lastSearch.equals("") && currentNode.getPath().contains("/event_search_facets")) {
        placeholderText = lastSearch.replaceAll("\\+", " ");
    }
    String searchAction = properties.get("searchAction", null);
    String action = (String) request.getAttribute("altSearchPath");
    if ((null == searchAction) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
Please edit Search Box Component
<%
} else if (searchAction == null) {
%> Search Not Configured <%
} else {
    if (!(action != null && !action.trim().equals(""))) {
        action = currentSite.get(searchAction, String.class);
    }
%>
<form action="<%=action%>.html" method="get" path="<%=currentNode.getPath()%>">
    <input type="text" name="q" id="eventSearch" placeholder="<%=placeholderText %>" class="searchField" searchHolder="<%=placeholderText %>"/>
</form>
<%}%>