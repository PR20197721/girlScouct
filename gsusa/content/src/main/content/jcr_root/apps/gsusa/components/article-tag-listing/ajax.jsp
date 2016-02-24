<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@ page import="com.day.cq.commons.TidyJSONWriter,
				 org.apache.sling.commons.json.JSONObject,
				 java.util.Map,
				 java.util.HashMap,
				 java.util.List,
				 java.util.Arrays,
				 java.util.ArrayList,
				 com.day.cq.search.QueryBuilder,
                 com.day.cq.search.Query,
                 com.day.cq.search.PredicateGroup,
                 com.day.cq.search.result.SearchResult,
                 com.day.cq.search.result.Hit,
                 org.apache.sling.api.request.RequestPathInfo" %>

<%
String tagsStr = request.getParameter("tag");
String listing = java.net.URLDecoder.decode(request.getParameter("listing"),"UTF-8");
String[] tags = tagsStr.split(",");
for (String t : tags){
	t = java.net.URLDecoder.decode(t,"UTF-8");
}
int num = Integer.parseInt(java.net.URLDecoder.decode(request.getParameter("num"),"UTF-8"));
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();
int pageNum = Integer.parseInt(selectors[selectors.length-1]);
String priority = java.net.URLDecoder.decode(request.getParameter("priority"),"UTF-8");

//final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
QueryBuilder builder = sling.getService(QueryBuilder.class);
String output = "";

List<String> tagIds = new ArrayList<String>();
StringBuilder anchorsBuilder = new StringBuilder("#");
for(String t : tags){
	t = java.net.URLDecoder.decode(t,"UTF-8");
	tagIds.add(t);
	anchorsBuilder.append("|").append(t.replaceAll("gsusa:content-hub/", ""));
}

anchorsBuilder.deleteCharAt(1);
if(!"".equals(listing)){
	anchorsBuilder.append("$$$");
	anchorsBuilder.append(resourceResolver.map(listing));
}
request.setAttribute("linkTagAnchors", anchorsBuilder.toString());

SearchResult sr = getArticlesWithPaging(tagIds,num,resourceResolver, builder, priority, num*(pageNum-1));

List<Hit> hits = sr.getHits();

long total = sr.getTotalMatches();

for (Hit h : hits){
	request.setAttribute("articlePath", h.getPath());
	
	%>
	<li>
	<div class="article-tile">
	<cq:include script="/apps/gsusa/components/article-tile/article-tile.jsp" /></li></div><%
}
if(total <= num*pageNum){
	%> 
	<script>$("#more").css("display","none");</script>
	<%
}
%>