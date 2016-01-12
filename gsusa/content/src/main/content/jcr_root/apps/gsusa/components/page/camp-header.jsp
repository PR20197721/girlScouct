<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="java.util.Map,
                java.util.HashMap,
                com.day.cq.search.QueryBuilder,
                com.day.cq.search.Query,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.result.SearchResult"  %>
                
<%
QueryBuilder builder = sling.getService(QueryBuilder.class);
Map<String, String> map = new HashMap<String, String>();
map.put("path", currentPage.getPath());
map.put("property", "sling:resourceType");
map.put("property.value", "gsusa/components/standalone-camp-finder");

Query query0 = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
map.put("property.value", "gsusa/components/camp-landing-hero");
Query query1 = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));

long matchNum0 = query0.getResult().getTotalMatches();
long matchNum1 = query1.getResult().getTotalMatches();
// TODO: Can we consolidate two queries into one?
boolean hasHeader = (matchNum0 != 0 || matchNum1 != 0);

if (hasHeader) {// contains cookie) {
%>
<div class="show-for-small">
	<cq:include path="mobile-camp-finder" resourceType="gsusa/components/standalone-camp-finder" />
</div>
<%
}
%>