<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*,
    java.io.*, java.util.regex.*,
	java.net.*,
	org.apache.sling.commons.json.*,
	org.apache.sling.api.request.RequestDispatcherOptions,
	com.day.cq.wcm.api.components.IncludeOptions,
	org.apache.sling.jcr.api.SlingRepository,
	java.util.Map,
	java.util.HashMap,
	java.util.List,
	com.day.cq.search.QueryBuilder,
    com.day.cq.search.Query,
    com.day.cq.search.PredicateGroup,
    com.day.cq.search.result.SearchResult,
    com.day.cq.search.result.Hit,
    org.apache.sling.api.request.RequestPathInfo" %>
<%@page session="false" %>
<%
String tag = properties.get("tag","");
String path = "/content/gsusa/en/content-hub/articles";
int num = Integer.parseInt(properties.get("num","10"));
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();

if(tag.isEmpty()){
    %>
	<div class="article-slider">
        <p>###Configure Article Carousel</p>
    </div>
<%
        } else {

QueryBuilder builder = sling.getService(QueryBuilder.class);
String output = "";
Map<String, String> map = new HashMap<String, String>();
map.put("type","cq:Page");
map.put("path",path);
map.put("tagid",tag);
map.put("tagid.property","jcr:content/cq:tags");
map.put("p.limit",num + "");
map.put("orderby","@jcr:content/tilePriority");
map.put("orderby.sort","desc");
map.put("2_orderby","@jcr:content/editedDate");
map.put("2_orderby.sort","desc");

Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
SearchResult sr = query.getResult();
List<Hit> hits = sr.getHits();

    %>

<div class="article-slider">
    <%for (Hit h : hits){
        request.setAttribute("articlePath", h.getPath());%>
        <div>
            <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
        </div>
    <%}
    %>
</div>

<% }%>
