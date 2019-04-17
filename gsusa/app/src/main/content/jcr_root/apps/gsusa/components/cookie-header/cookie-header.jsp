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
map.put("path", currentPage.getPath() + "/jcr:content");
map.put("property", "sling:resourceType");
map.put("property.value", "gsusa/components/standalone-cookie-header");

Query query0 = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
map.put("property.value", "gsusa/components/cookie-landing-hero");
Query query1 = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));

boolean hasStandalone  = (query0.getResult().getTotalMatches() > 0 ? true : false);
boolean hasLandingHero = query1.getResult().getTotalMatches() > 0 ? true : false;
// TODO: Can we consolidate two queries into one?
boolean isHomepage = currentPage.getPath().equals(currentPage.getAbsoluteParent(2).getPath());
if (hasStandalone || hasLandingHero) {// contains cookie) {
	String mobileCookieHeaderPath = isHomepage ? currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/mobile-cookie-header" : "mobile-cookie-header";
	%>
	<div class="show-for-small">
		<cq:include path="<%= mobileCookieHeaderPath%>" resourceType="gsusa/components/standalone-cookie-header" />
	</div>
	<%
}
%>
<div data-emptytext="Mobile Cookie Header" class="cq-placeholder">
</div>

