<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@page session="false" %>

<% String filterDistancePerMiles = properties.get("./filterDistancePerMiles", "500"); %>
<span id="filter-distance-per-miles" data="<%= filterDistancePerMiles %>" ></span>
<span id="booth-listing-api-url" data="<%= properties.get("boothListingApiURL")%>" ></span>
<span id="booth-listing-lookup-api-url" data="<%= properties.get("boothListingLookupApiURL")%>" ></span>

<cq:includeClientLib categories="apps.gsusa.components.boothFinder" />

<%-- Placeholder for the actual render --%>
<div id="booth-finder-result" class="booth-finder cq-placeholder" data-empty-text="Booth Finder">
	<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<span>Booth Finder</span>
	<% } %>
</div>

<%
	// Templates
// Templates for path 1 to path 5.
	for (int pathIndex = 1; pathIndex <= 5; pathIndex++) {
		if (pathIndex == 3) {continue;} // Skip obsolete path3.
		String pathIndexStr = Integer.toString(pathIndex);
		request.setAttribute("gsusa-component-booth-finder-index", pathIndexStr);
%>
<script id="template-path<%= pathIndexStr %>" type="text/x-handlebars-template">
	<cq:include script="pathn.jsp" />

	<div id="share-showShareDialog" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShowShareDialog", "")) %>" />
	<div id="share-shareDialogHeader" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogHeader", "")) %>" />
	<div id="share-shareDialogDescription" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogDescription", "")) %>" />
	<div id="share-shareDialogTweet" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogTweet", "")) %>" />
	<div id="share-shareDialogImagePath" data="<%= escapeDoubleQuotesAddCouncil(properties.get("path" + pathIndex + "ShareDialogImagePath", "")) %>" />

	<%
		if (pathIndex == 2) {
	%><cq:include path="contact-banner" resourceType="gsusa/components/contact-banner"/><%
		}
	%>
	<cq:include script="booth-list-empty.jsp" />
</script>

<%
		boolean isShowShareDialog = properties.get("path" + pathIndexStr + "ShowShareDialog", "false").equalsIgnoreCase("true"); // e.g. path1ShowShareDialog
		request.setAttribute("gsusa-component-booth-finder-index", null);
	}
%>

<%-- Template for not found --%>
<script id="template-notfound" type="text/x-handlebars-template">
	<cq:include script="not-found.jsp" />
</script>

<%-- Template for booths if the list is not empty --%>
<script id="template-booths" type="text/x-handlebars-template">
	<cq:include script="booth-list.jsp" />
	<div id="share-map-FBTitle" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapFBTitle", "")) %>" />
	<div id="share-map-FBDesc" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapFBDesc", "")) %>" />
	<div id="share-map-Tweet" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapTweet", "")) %>" />
	<div id="share-map-FBImgPath" data="<%= escapeDoubleQuotesAddCouncil(properties.get("mapFBImgPath", "")) %>" />
</script>

<%-- Template for more booths --%>
<script id="template-more-booths" type="text/x-handlebars-template">
	<cq:include script="booth-list-more.jsp" />
</script>

<%-- Template for share modal --%>
<script id="template-sharemodal" type="text/x-handlebars-template">
	<cq:include script="share-modal.jsp" />
</script>

<div id="booth-finder-details" data-fb-id="<%= currentSite.get("facebookId", "") %>"  data-num-per-page="<%= properties.get("numPerPage", 50)%>" data-res-path="<%= resource.getPath() %>"></div>

<%!
	public String escapeDoubleQuotesAddCouncil(String str) {
		return str.replaceAll("\"", "\\\"").replaceAll("\\{\\{", "{{escapeDoubleQuotes council.");
	}
%>
