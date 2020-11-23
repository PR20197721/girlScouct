<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@ page import="com.day.cq.wcm.api.WCMMode"
         import="java.sql.Timestamp"
 %>
<%@page session="false" %>

<%-- Placeholder for the actual render --%>
<div id="troop-listing-result" class="troop-listing booth-finder cq-placeholder" data-empty-text="Troop Finder" style="min-height: 100px">
	<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<span>Troop Listing</span>
	<% } %>
</div>

<%-- Code for Troop Listing Starts here--%>
<%
Date onTime = properties.get("onTime",Date.class);
Date offTime = properties.get("offTime",Date.class);
long onTimeTimestamp = new Timestamp(onTime.getTime()).getTime();
long offTimeTimestamp = new Timestamp(offTime.getTime()).getTime();

Timestamp currentDate = new Timestamp(System.currentTimeMillis());
long currentDateTimestamp = currentDate.getTime();

if(onTimeTimestamp<=currentDateTimestamp  && currentDateTimestamp<=offTimeTimestamp){
%>
<div data-attribute-troopListing="yes"></div>
<div id="troop-listing-details" data-fb-id="<%= currentSite.get("facebookId", "") %>"  data-num-per-page="<%= properties.get("numPerPage", 25)%>" data-res-path="<%= resource.getPath() %>"></div>

    <cq:includeClientLib categories="apps.gsusa.components.troopListing" />
    <%-- Template for troop listing filter start here --%>
    <script id="template-troop-listing" type="text/x-handlebars-template">
        <cq:include script="troop-listing-filter.jsp" />
    </script>
    <%-- Template for troop listing filter end here--%>

<%
}
%>






