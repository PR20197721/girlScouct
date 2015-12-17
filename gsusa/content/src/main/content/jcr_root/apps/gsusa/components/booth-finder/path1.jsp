<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.util.Map,
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
String text = properties.get("path1Text", "");
if (text.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>Booth Result Path 1: double click here to configure.<% 
} else {
    %><p><%= replaceCouncilInfo(text, councilMap) %></p><%
}

boolean isShowShareDialog = properties.get("path1ShowShareDialog", "false").equalsIgnoreCase("true");
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
%>