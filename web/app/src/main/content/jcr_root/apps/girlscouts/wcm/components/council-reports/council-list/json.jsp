<%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.Iterator,
	org.apache.sling.commons.json.io.*" %>

<%
response.setContentType("application/json");
response.setCharacterEncoding("utf-8");
JSONWriter writer = new JSONWriter(response.getWriter());

PageManager pm = resourceResolver.adaptTo(PageManager.class);
Resource content = resourceResolver.resolve("/content");
Iterator<Resource> contentChildren = content.listChildren();
writer.array();
while (contentChildren.hasNext()){
	Resource thisRes = contentChildren.next();
	Page thisPage = thisRes.adaptTo(Page.class);
	if(null != thisPage){
		if(thisPage.hasChild("en") && !thisPage.getPath().endsWith("girlscouts-template")){
			Page enPage = pm.getPage(thisPage.getPath() + "/en");
			if(enPage.getTitle() != null){		
				writer.object();
				writer.key("value");
				writer.value(thisPage.getPath());
				writer.key("text");
				writer.value(enPage.getTitle());
				writer.endObject();
			}
		}
	}
}
writer.endArray();
%>