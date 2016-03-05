<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.util.Map,
                java.util.HashMap,
                com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/booth-finder/replace-council-info.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
if (council == null) {
	council = new Council();
}
Map<String, String> councilMap = council.adaptToMap();
String text = properties.get("path2Text", "");
if (text.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>Booth Result Path 2: double click here to configure.<% 
} else {
    %><p><%= replaceCouncilInfo(text, councilMap) %></p><%
}

boolean isShowShareDialog = properties.get("path2ShowShareDialog", "false").equalsIgnoreCase("true");
if (isShowShareDialog) {
	String header = replaceCouncilInfo(properties.get("path5ShareDialogHeader", ""), councilMap);
	String tweet = replaceCouncilInfo(properties.get("path5ShareDialogTweet", ""), councilMap);
	String description = replaceCouncilInfo(properties.get("path5ShareDialogDescription", ""), councilMap);
	String imgPath = properties.get("path5ShareDialogImgPath", "");
	request.setAttribute("gsusa-share-model-header", header);
	request.setAttribute("gsusa-share-modal-tweet", tweet); 
	request.setAttribute("gsusa-share-modal-description", description);
	request.setAttribute("gsusa-share-modal-img-path", imgPath);
	slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);
	%>
	<cq:include path="share-modal" resourceType="gsusa/components/share-modal" />
	<%
	slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE);
	request.setAttribute("gsusa-share-modal-img-path", null);
	request.setAttribute("gsusa-share-model-header", null);
	request.setAttribute("gsusa-share-modal-tweet", null); 
	request.setAttribute("gsusa-share-modal-description", null);
}

Map<String, String> conf = new HashMap<String, String>();
conf.put("title", "Cookies are Here!");
conf.put("desc", "Enter your info below and girls from the " + council.name + " will contact you to help you place your cookie order.");
request.setAttribute("gsusa-contact-banner-conf", conf);
%>
<cq:include path="contact-banner" resourceType="gsusa/components/contact-banner"/>