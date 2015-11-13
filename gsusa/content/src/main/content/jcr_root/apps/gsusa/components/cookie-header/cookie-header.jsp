<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, 
                com.day.cq.wcm.foundation.Placeholder, 
                java.util.Random,
                java.util.Map,
                java.util.HashMap,
                com.day.cq.search.QueryBuilder,
                com.day.cq.search.Query,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.result.SearchResult"  %>
<%@page session="false" %>

<%
QueryBuilder builder = sling.getService(QueryBuilder.class);
Map<String, String> map = new HashMap<String, String>();
map.put("path", currentPage.getPath());
map.put("property", "sling:resourceType");
map.put("property.value", "gsusa/components/standalone-cookie-header");

Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
SearchResult result = query.getResult();
long matchNum = result.getTotalMatches();
boolean hasHeader = matchNum != 0;

    if (hasHeader) {// contains cookie) {
%>
<div id="stay-cheader" class="show-for-small">
	<cq:include path="mobile-cookie-header" resourceType="gsusa/components/standalone-cookie-header" />
</div>
<%
}
%>
