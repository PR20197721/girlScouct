<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                com.day.cq.wcm.api.WCMMode,
                java.text.DateFormat,
                java.text.SimpleDateFormat,
                java.util.Map,
                java.util.Date" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/booth-finder/replace-council-info.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
if (council == null) {
	council = new Council();
}
DateFormat format = new SimpleDateFormat("M/d/yyyy");
long daysBetween = 0;
try {
	Date startDate = format.parse(council.cookieSaleStartDate);
	// Should we use joda time?
	daysBetween = (startDate.getTime() - new Date().getTime() + 1000*3600*24) / 1000 / 3600 / 24;
	if (daysBetween <= 0) {
		daysBetween = 0;
	}
} catch (java.text.ParseException e) {}

Map<String, String> councilMap = council.adaptToMap();
councilMap.put("daysLeft", Long.toString(daysBetween));

String text = properties.get("path5Text", "");
if (text.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%>Booth Result Path 5: double click here to configure.<% 
} else {
    %><p><%= replaceCouncilInfo(text, councilMap) %></p><%
}

boolean isShowShareDialog = properties.get("path5ShowShareDialog", "false").equalsIgnoreCase("true");
if (isShowShareDialog) {
	String header = replaceCouncilInfo(properties.get("path5ShareDialogHeader", ""), councilMap);
	String tweet = replaceCouncilInfo(properties.get("path5ShareDialogTweet", ""), councilMap);
	String description = replaceCouncilInfo(properties.get("path5ShareDialogDescription", ""), councilMap);
	String modImgPath = properties.get("path5ShareDialogModImagePath", "");
	String shareImgPath = properties.get("path5ShareDialogShareImagePath","");
	request.setAttribute("gsusa-share-model-header", header);
	request.setAttribute("gsusa-share-modal-tweet", tweet); 
	request.setAttribute("gsusa-share-modal-description", description);
	request.setAttribute("gsusa-share-modal-mod-img-path", modImgPath);
	request.setAttribute("gsusa-share-modal-share-img-path", shareImgPath);
	slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true);
	%>
	<cq:include path="share-modal" resourceType="gsusa/components/share-modal" />
	<%
	slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE);
	request.setAttribute("gsusa-share-modal-mod-img-path", null);
	request.setAttribute("gsusa-share-modal-share-img-path", null);
	request.setAttribute("gsusa-share-model-header", null);
	request.setAttribute("gsusa-share-modal-tweet", null); 
	request.setAttribute("gsusa-share-modal-description", null);
}
%>