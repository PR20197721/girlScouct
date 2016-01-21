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
                 com.day.cq.search.result.Hit" %>

<%
String tag = java.net.URLDecoder.decode(request.getParameter("tag"),"UTF-8");
int num = Integer.parseInt(java.net.URLDecoder.decode(request.getParameter("num"),"UTF-8"));
int pageNum = Integer.parseInt(java.net.URLDecoder.decode(request.getParameter("page"),"UTF-8"));

final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());

QueryBuilder builder = sling.getService(QueryBuilder.class);
String output = "";
Map<String, String> map = new HashMap<String, String>();
map.put("type","cq:Page");
map.put("tagid",tag);
map.put("tagid.property","jcr:content/cq:tags");
map.put("p.limit",num + "");
map.put("p.offset", num*(pageNum-1) + "");
map.put("p.guessTotal",num + "");
map.put("orderby","@jcr:content/editedDate");
map.put("orderby.sort","desc");

Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
SearchResult sr = query.getResult();
List<Hit> hits = sr.getHits();

writer.object().key("total").value(hits.size()).key("more").value(sr.getNextPage() != null ? "true" : "false");

writer.key("results").array();
for(Hit h : hits){
	JSONObject jo = new JSONObject();
	jo.put("path",h.getPath());
	writer.value(jo);
}
writer.endArray();
writer.endObject();

writer.setTidy("true".equals(request.getParameter("tidy")));
%>