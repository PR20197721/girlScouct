<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String pathIndexStr = (String)request.getAttribute("gsusa-component-booth-finder-index");
String text = properties.get("path" + pathIndexStr + "Text", "").replaceAll("\\{\\{", "{{council."); // e.g. path1Text
%>
	<p><%= text %></p>
<%
boolean isShowShareDialog = properties.get("path" + pathIndexStr + "ShowShareDialog", "false").equalsIgnoreCase("true"); // e.g. path1ShowShareDialog

if (isShowShareDialog) {
	request.setAttribute("gsusa-share-model-header", properties.get("path" + pathIndexStr + "ShareDialogHeader", ""));
	request.setAttribute("gsusa-share-modal-tweet", properties.get("path" + pathIndexStr + "ShareDialogTweet", "")); 
	request.setAttribute("gsusa-share-modal-description", properties.get("path" + pathIndexStr + "ShareDialogDescription", ""));
	request.setAttribute("gsusa-share-modal-img-path", properties.get("path" + pathIndexStr + "ShareDialogImagePath", ""));
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