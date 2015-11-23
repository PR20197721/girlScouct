<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/booth-finder/replace-council-info.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
if (council == null) {
	council = new Council();
}
String text = properties.get("path4Text", "");
%>
<p><%= replaceCouncilInfo(text, council.adaptToMap()) %></p>

<%
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