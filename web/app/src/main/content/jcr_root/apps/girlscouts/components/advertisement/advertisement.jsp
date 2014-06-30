<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<%
String rootPath = properties.get("path", "");
if (rootPath.isEmpty()) {
    rootPath = currentSite.get("adsPath", "");
}
if (rootPath.isEmpty()) {
    rootPath = currentPage.getAbsoluteParent(2).getPath() + "/ads";
}
%>
<%
if (rootPath.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Advertisement: path not configured.<%
    }
    return;
}
//Setting adCount
String tempAdCount = currentDesign.getStyle("three-column-page/advertisement").get("adCount", "");
int adCount;
if (tempAdCount.isEmpty()) {
	int defaultAdCount = 2;
    adCount = defaultAdCount;
}
else {
    adCount = Integer.parseInt(tempAdCount);
}
Page adRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
if (adRoot != null) {
	Iterator<Page> Iter = adRoot.listChildren();
	
	while(Iter.hasNext() && adCount > 0) {
	    adCount--;
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
	    %>
	<a href="<%=adLink%>"><cq:include path= "<%=path +"/jcr:content/image"%>" resourceType="foundation/components/image" /></a>
<%
    }
}
%>
