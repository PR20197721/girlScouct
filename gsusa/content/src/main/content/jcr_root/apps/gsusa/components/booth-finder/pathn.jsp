<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String pathIndexStr = (String)request.getAttribute("gsusa-component-booth-finder-index");
String text = properties.get("path" + pathIndexStr + "Text", "").replaceAll("\\{\\{", "{{council."); // e.g. path1Text
%>
	<p><%= text %></p>
<%
boolean isShowShareDialog = properties.get("path" + pathIndexStr + "ShowShareDialog", "false").equalsIgnoreCase("true"); // e.g. path1ShowShareDialog
// TODO: force false now;
isShowShareDialog = false;

if (isShowShareDialog) {
	String header = properties.get("path" + pathIndexStr + "ShareDialogHeader", "");
	String tweet = properties.get("path" + pathIndexStr + "ShareDialogTweet", "");
	String description = properties.get("path" + pathIndexStr + "ShareDialogDescription", "");
	String imgPath = properties.get("path" + pathIndexStr + "ShareDialogImgPath", "");
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