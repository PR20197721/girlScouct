<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%@page import="com.day.cq.wcm.api.WCMMode,
    java.util.ArrayList,
    java.util.Iterator,
    java.util.Collections,
    java.util.regex.Pattern,
    java.util.regex.Matcher,
    java.io.BufferedReader,
    java.net.*,
    java.io.InputStreamReader,
    org.apache.jackrabbit.commons.json.JsonParser,
    org.apache.sling.commons.json.*,
    com.day.cq.wcm.foundation.Image" %>

<%@page session="false"%><%
final String[] carouselList = properties.get("carouselList", String[].class);
final boolean isCarousel = properties.get("dynamiccarousel", false);
final String carouselTitle = properties.get("carouselTitle", "");
final String carouselSubTitle = properties.get("carouselSubTitle", "");
final String callToActionName = properties.get("calltoactionname", "Default Call To Action");
String callToActionLink = properties.get("calltoactionlink", "");
final int timedelay = properties.get("timedelay", 2000);
final boolean autoscroll = properties.get("autoscroll", false);
final boolean showVerticalRule = properties.get("showverticalrule", false);

int numberOfImages= 0;

if (carouselList != null) {
	if (isCarousel) {
		numberOfImages = carouselList.length;
	} else {
		numberOfImages = 1; //only show the first image if the user uncheck this option
	}
%>
<script>
	shoptimedelay = <%= timedelay %>;
	shopautoscroll = <%= autoscroll %>;
</script>

<% if (showVerticalRule) {%>
	<div class="rotator border">
<%} else { %>
	<div class="rotator">
<%} %>

  <h5> <%= carouselTitle %></h5>
  <h6> <%= carouselSubTitle %></h6>
  <div class="shop-carousel">
	<%
		for (int i = 0; i < numberOfImages; i++) {
			String[] split = carouselList[i].split("\\|\\|\\|");
			String label = split.length >= 1 ? split[0] : "";
			String link = split.length >= 2 ? split[1] : "";
			String target = "";
			boolean openInNewWindow = split.length >= 3 ? Boolean.parseBoolean(split[2]) : false;
			String imagePath = split.length >= 4 ? split[3] : "";

			Page linkPage = resourceResolver.resolve(link).adaptTo(Page.class);
			if (linkPage != null && !link.contains(".html")) {
				link += ".html";
			}
			Page callToActionPage = resourceResolver.resolve(callToActionLink).adaptTo(Page.class);
			if (callToActionPage != null && !callToActionLink.contains(".html")) {
				callToActionLink += ".html";
			}
			if (openInNewWindow) {
				target = "target=\"_blank\"";
			}
			%><div><a href="<%= link %>" <%= target %>><img src="<%= imagePath %>" alt="<%=label %>"/><p><%=label %></p></a></div><%
		}
	%>
  </div>
   <a class="button arrow" href="<%= callToActionLink %>" title="<%= callToActionName%>"><%= callToActionName%></a>
</div>
<%
} else {%>
<p>Please right click here to edit this right rail carousel.</p>
<% }
%>

