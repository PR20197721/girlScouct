<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@ page import="com.day.cq.commons.TidyJSONWriter,
				 org.apache.sling.commons.json.JSONObject,
				 java.util.Map,
				 java.util.HashMap,
				 java.util.List,
				 com.day.cq.search.QueryBuilder,
                 com.day.cq.search.Query,
                 com.day.cq.search.PredicateGroup,
                 com.day.cq.search.result.SearchResult,
                 com.day.cq.search.result.Hit,
                 org.apache.sling.api.request.RequestPathInfo" %>

<%
String tag = java.net.URLDecoder.decode(request.getParameter("tag"),"UTF-8");
String path = java.net.URLDecoder.decode(request.getParameter("path"),"UTF-8");
int num = Integer.parseInt(java.net.URLDecoder.decode(request.getParameter("num"),"UTF-8"));
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();
int pageNum = Integer.parseInt(selectors[selectors.length-1]);


//final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
QueryBuilder builder = sling.getService(QueryBuilder.class);
String output = "";
Map<String, String> map = new HashMap<String, String>();
map.put("type","cq:Page");
map.put("path",path);
map.put("tagid",tag);
map.put("tagid.property","jcr:content/cq:tags");
map.put("p.limit",num + "");
map.put("p.offset", num*(pageNum-1) + "");
map.put("orderby","@jcr:content/tilePriority");
map.put("orderby.sort","desc");
map.put("2_orderby","@jcr:content/editedDate");
map.put("2_orderby.sort","desc");

Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
SearchResult sr = query.getResult();
List<Hit> hits = sr.getHits();

long total = sr.getTotalMatches();

for (Hit h : hits){
	request.setAttribute("articlePath", h.getPath()); %>
	<li>
	<div class="article-tile">
	<cq:include script="/apps/gsusa/components/article-tile/article-tile.jsp" /></li></div><%
}
if(total <= num*pageNum){
	%> 
	<script>$(".load-more").css("display","none");</script>
	<%
}
%>