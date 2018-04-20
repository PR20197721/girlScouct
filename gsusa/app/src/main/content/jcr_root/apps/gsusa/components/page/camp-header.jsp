<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="java.util.Map,
                java.util.HashMap,
                javax.jcr.query.*"  %>
<%!
long getCount(Session session, String path, String resourceType) {
	String expression = "SELECT s.[jcr:path] "+
	                    "FROM [nt:unstructured] AS s "+
	                    "WHERE ISDESCENDANTNODE('"+path+"') AND  s.[sling:resourceType]='"+resourceType+"'";
	try {
		javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
		javax.jcr.query.Query sql2Query = queryManager.createQuery(expression, "JCR-SQL2");
		javax.jcr.query.QueryResult result = sql2Query.execute();		
		return result.getNodes().getSize();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return 0;
}
%>                
<%
String RESOURCE_TYPE = "gsusa/components/standalone-camp-finder";
String RESOURCE_TYPE2 = "gsusa/components/camp-landing-hero";
String PATH = currentPage.getPath() + "/jcr:content";


long matchNum0 = getCount(resourceResolver.adaptTo(Session.class), PATH, RESOURCE_TYPE) - (resourceResolver.adaptTo(Session.class).nodeExists(currentPage.getPath() + "/jcr:content/mobile-camp-finder") ? 1 : 0);
long matchNum1 = getCount(resourceResolver.adaptTo(Session.class), PATH, RESOURCE_TYPE2);
// TODO: Can we consolidate two queries into one?
boolean hasHeader = (matchNum0 > 0 || matchNum1 > 0);

if (hasHeader) {// contains cookie) {
%>
<div class="show-for-small">
	<cq:include path="mobile-camp-finder" resourceType="gsusa/components/standalone-camp-finder" />
</div>
<%
}
%>