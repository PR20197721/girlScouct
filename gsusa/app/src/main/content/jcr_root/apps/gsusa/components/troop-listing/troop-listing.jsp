<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@page session="false" %>

<cq:includeClientLib categories="apps.gsusa.components.troopListing" />

<%-- Placeholder for the actual render --%>
<div id="booth-finder-result" class="booth-finder cq-placeholder" data-empty-text="Troop Finder" style="min-height: 100px">
	<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<span>Troop Listing</span>
	<% } %>
</div>

<%-- Code for Troop Listing Starts here--%>

<%-- Template for troop listing filter start here --%>
<script id="template-troop-listing" type="text/x-handlebars-template">
	<cq:include script="troop-listing-filter.jsp" />
</script>
<%-- Template for troop listing filter end here--%>



