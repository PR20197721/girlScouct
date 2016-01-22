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
int num = Integer.parseInt(java.net.URLDecoder.decode(request.getParameter("num"),"UTF-8"));
int pageNum = Integer.parseInt(slingRequest.getRequestPathInfo().getSelectorString());


//final TidyJSONWriter writer = new TidyJSONWriter(response.getWriter());
QueryBuilder builder = sling.getService(QueryBuilder.class);
String output = "";
Map<String, String> map = new HashMap<String, String>();
map.put("type","cq:Page");
map.put("tagid",tag);
map.put("tagid.property","jcr:content/cq:tags");
map.put("p.limit",num + "");
map.put("p.offset", num*(pageNum-1) + "");
map.put("orderby","@jcr:content/editedDate");
map.put("orderby.sort","desc");

Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
SearchResult sr = query.getResult();
List<Hit> hits = sr.getHits();

long total = sr.getTotalMatches();
/*writer.object().key("total").value(total).key("more").value(num*pageNum < total ? "true" : "false");

writer.key("results").array();
for(Hit h : hits){
	JSONObject jo = new JSONObject();
	jo.put("path",h.getPath());
	writer.value(jo);
}
writer.endArray();
writer.key("pageNum").value(pageNum);
writer.endObject();

writer.setTidy("true".equals(request.getParameter("tidy")));*/

for (Hit h : hits){
	request.setAttribute("articlePath", h.getPath()); %>
	<cq:include script="/apps/gsusa/components/article-tile/article-tile.jsp" /><%
	}
%>