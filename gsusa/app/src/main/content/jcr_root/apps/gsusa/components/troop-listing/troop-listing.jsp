<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@ page import="com.day.cq.wcm.api.WCMMode"
         import="java.sql.Timestamp"
 %>
<%@page session="false" %>

<%-- Placeholder for the actual render --%>
<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<span>Troop Listing</span>
<% } %>

<%-- Code for Troop Listing Starts here--%>
<%
Date onTime = properties.get("onTime",Date.class);
Date offTime = properties.get("offTime",Date.class);

if(null!= onTime && null!=offTime){
	long onTimeTimestamp = new Timestamp(onTime.getTime()).getTime();
	long offTimeTimestamp = new Timestamp(offTime.getTime()).getTime();

	Timestamp currentDate = new Timestamp(System.currentTimeMillis());
	long currentDateTimestamp = currentDate.getTime();

	if(onTimeTimestamp<=currentDateTimestamp  && currentDateTimestamp<=offTimeTimestamp){
	%>

	<div id="troop-listing-result" class="troop-listing cq-placeholder" data-empty-text="Troop Finder">
	</div>

	<span id="troop-listing-config" data-troop-listing="true" data-show-one-link="<%= properties.get("showOneLink")%>"
	data-support-another-troop="<%= properties.get("supportAnotherTroop")%>" data-get-cookie-button-color="<%= properties.get("getCookieButtonColor")%>"
	data-hover-button-color="<%= properties.get("hoverButtonColor")%>" data-text-color="<%= properties.get("textColor")%>"
	data-text="<%= properties.get("text")%>" data-another-troop-button-color="<%= properties.get("anotherTroopButtonColor")%>"
	data-another-troop-hover-button-color="<%= properties.get("anotherTroopHoverButtonColor")%>" data-another-troop-text-color="<%= properties.get("anotherTroopTextColor")%>"
	data-another-troop-text="<%= properties.get("anotherTroopText")%>" data-one-link-count="<%= properties.get("oneLinkCount")%>"
	data-troop-listing-api-url="<%= properties.get("troopListingApiURL")%>" data-troop-listing-lookup-api-url="<%= properties.get("troopListingLookupApiURL")%>"
	> </span>

	<div id="troop-listing-details" data-num-per-page="<%= properties.get("numPerPage", 25)%>" data-res-path="<%= resource.getPath() %>"></div>

	    <cq:includeClientLib categories="apps.gsusa.components.troopListing" />
	    <%-- Template for troop listing filter start here --%>
	    <script id="template-troop-listing" type="text/x-handlebars-template">
	        <cq:include script="troop-listing-content.jsp" />
	    </script>
	    <%-- Template for troop listing filter end here--%>

	    <%-- Template for more Troop Listing --%>
	    <script id="template-more-troop-listing" type="text/x-handlebars-template">
	        <cq:include script="troop-list-more.jsp" />
	    </script>

	    <%-- Template for not found --%>
	    <script id="template-notfound-troop-listing" type="text/x-handlebars-template">
	        <cq:include script="not-found.jsp" />
	    </script>

	<%
	}
}
%>
<style>
.troopListingCustomButton{
	<%
		String textColor = properties.get("textColor","#FFFFFF");
		String getCookieButtonColor = properties.get("getCookieButtonColor","#00AE58");
		String hoverButtonColor = properties.get("hoverButtonColor","#008b46");
	%>
		color: <%= textColor%> !important;
		background-color: <%= getCookieButtonColor%> !important;
}
.troopListingCustomButton:hover{
    	background-color: <%= hoverButtonColor%> !important;
}

.anotherTroopListingCustomButton{
	<%
		String anotherTroopTextColor = properties.get("anotherTroopTextColor","#FFFFFF");
		String anotherTroopButtonColor = properties.get("anotherTroopButtonColor","#00AE58");
		String anotherTroopHoverButtonColor = properties.get("anotherTroopHoverButtonColor","#008b46");
	%>
		color: <%= anotherTroopTextColor%> !important;
		background-color: <%= anotherTroopButtonColor%> !important;
}
.anotherTroopListingCustomButton:hover{
    	background-color: <%= anotherTroopHoverButtonColor%> !important;
}

</style>





