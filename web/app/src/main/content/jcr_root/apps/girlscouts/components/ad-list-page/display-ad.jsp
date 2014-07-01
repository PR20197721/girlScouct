<%@include file="/libs/foundation/global.jsp"%>

<%
final String AD_ATTR = "apps.girlscouts.components.ad-list-page.currentAd";
Page currentAd = (Page)request.getAttribute(AD_ATTR);
if (currentAd != null) {
	String adName = currentAd.getProperties().get("jcr:title", "");
	String path = currentAd.getPath();
	String adLink = currentAd.getProperties().get("link", "");
	if (adLink != null && !adLink.isEmpty()) {
		adLink = adLink + ".html";
	} else {
		adLink = path + ".html";	
	}
%>
	<a href="<%=adLink%>"><cq:include path= "<%=path +"/jcr:content/image"%>" resourceType="girlscouts/components/image" /></a>
<%
	request.removeAttribute(AD_ATTR);
} 
%>