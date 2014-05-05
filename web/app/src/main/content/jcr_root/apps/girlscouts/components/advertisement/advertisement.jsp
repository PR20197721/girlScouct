<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<hr/>
<%
String rootPath = properties.get("path", "");
if (rootPath.isEmpty()) {
    rootPath = currentSite.get("adsPath", "");
}
if (rootPath.isEmpty()) {
    // TODO: will move "ads" to a constant
    rootPath = currentPage.getAbsoluteParent(2).getPath() + "/ads";
}
%>
<%
if (rootPath.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Contacts List: path not configured.<%
    }
    return;
}

Page adRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
Iterator<Page> Iter = adRoot.listChildren();
while(Iter.hasNext()) {
    Page currentAd = Iter.next();
    String adName = currentAd.getProperties().get("jcr:title", "");
    String path = currentAd.getPath();
    String adLink = currentAd.getProperties().get("link", "");
    if (adLink != null && adLink != ""){
    adLink = adLink + ".html";
    }
    else {
    adLink = path + ".html";	
    }
    %><%=adName%>
<a href="<%=adLink%>"><cq:include path= "<%=path +"/jcr:content/image"%>" resourceType="foundation/components/image" /></a>
<%
}
	%>
