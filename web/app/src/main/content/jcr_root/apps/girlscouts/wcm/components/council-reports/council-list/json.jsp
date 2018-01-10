<%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.Iterator,
	java.util.Map,java.util.TreeMap,
	org.apache.sling.commons.json.io.*" %>

<%
response.setContentType("application/json");
response.setCharacterEncoding("utf-8");
JSONWriter writer = new JSONWriter(response.getWriter());

PageManager pm = resourceResolver.adaptTo(PageManager.class);
Resource content = resourceResolver.resolve("/content");
Iterator<Resource> contentChildren = content.listChildren();
Map<String,String> sortedSites = new TreeMap<String,String>();
while (contentChildren.hasNext()){
	Resource thisRes = contentChildren.next();
	Page thisPage = thisRes.adaptTo(Page.class);
	if(null != thisPage){
		if(thisPage.hasChild("en") && !thisPage.getPath().endsWith("girlscouts-template")){
			if(thisPage.getTitle() != null){
				sortedSites.put(thisPage.getTitle(), thisPage.getPath());
			}else{
				sortedSites.put(thisPage.getName(), thisPage.getPath());
			}
		}
	}
}
writer.array();
for(Map.Entry<String,String> entry : sortedSites.entrySet()){
	writer.object();
	writer.key("text");
	writer.value(entry.getKey());
	writer.key("value");
	writer.value(entry.getValue());
	writer.endObject();
}
writer.endArray();
%>