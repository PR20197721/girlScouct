<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
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
try{
	QueryBuilder builder = sling.getService(QueryBuilder.class);
	Map<String, String> map = new HashMap<String, String>();
	map.put("path", currentPage.getPath() + "/jcr:content");
	map.put("property", "sling:resourceType");
	map.put("property.value", "girlscouts/components/standalone-cookie-header");
	
	Query query0 = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
	map.put("property.value", "girlscouts/components/cookie-landing-hero");
	Query query1 = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
	
	long matchNum0 = query0.getResult().getTotalMatches() - (resourceResolver.adaptTo(Session.class).nodeExists(currentPage.getPath() + "/jcr:content/mobile-cookie-header") ? 1 : 0);
	long matchNum1 = query1.getResult().getTotalMatches();
	// TODO: Can we consolidate two queries into one?
	boolean hasHeader = (matchNum0 != 0 || matchNum1 != 0);
	
	if (hasHeader) {// contains cookie) {
	%>
		<cq:include path="mobile-cookie-header" resourceType="girlscouts/components/standalone-cookie-header" />
	<%
	}else{
	%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
	} 
}catch(Exception e){
	e.printStackTrace();

}%>
