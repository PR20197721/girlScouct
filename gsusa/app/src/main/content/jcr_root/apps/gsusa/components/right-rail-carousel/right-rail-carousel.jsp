<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%@page import="com.day.cq.wcm.api.WCMMode,
	java.util.List,
    java.util.ArrayList,
    java.util.Iterator,
    java.util.Optional,
    java.util.Collections,
    java.util.regex.Pattern,
    java.util.regex.Matcher,
    java.io.BufferedReader,
    java.net.*,
    java.io.InputStreamReader,
    org.apache.jackrabbit.commons.json.JsonParser,
    org.apache.sling.commons.json.*,
    org.girlscouts.web.gsusa.component.rightrailcarousel.RightRailCarouselItem" %>

<%@page session="false"%><%
final boolean isCarousel = properties.get("dynamiccarousel", false);
final String carouselTitle = properties.get("carouselTitle", "");
final String carouselSubTitle = properties.get("carouselSubTitle", "");
final String callToActionName = properties.get("calltoactionname", "Default Call To Action");
String callToActionLink = properties.get("calltoactionlink", "");
final int timedelay = properties.get("timedelay", 2000);
final boolean autoscroll = properties.get("autoscroll", false);
final boolean showVerticalRule = properties.get("showverticalrule", false);

int numberOfImages= 0;


if (resourceResolver.resolve(callToActionLink).adaptTo(Page.class) != null && !callToActionLink.contains(".html")) {
	callToActionLink += ".html";
}

List<RightRailCarouselItem> carouselItems = new ArrayList<>();
Resource carouselItemsResource = resource.getChild("carouselItems");
if (carouselItemsResource != null && !carouselItemsResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
	Iterator<Resource> carouselChildren = carouselItemsResource.listChildren(); 
	if(carouselChildren != null && carouselChildren.hasNext()){
		while(carouselChildren.hasNext()){
			Node carouselChildNode = carouselChildren.next().adaptTo(Node.class);
			carouselItems.add(RightRailCarouselItem.fromNode(carouselChildNode));
		}
	}
}

if (carouselItems.size() > 0) {
	if (isCarousel) {
		numberOfImages = carouselItems.size();
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
			
			RightRailCarouselItem carouselItem = carouselItems.get(i);
			if(carouselItem == null){
				continue;
			}
			
			String label = Optional.ofNullable(carouselItem.getLabel()).orElse("");
			String link = Optional.ofNullable(carouselItem.getLink()).orElse("");
			String target = Optional.ofNullable(carouselItem.isNewWindow()).orElse(false) ? "target=\"_blank\"" : "";
			String imagePath = Optional.ofNullable(carouselItem.getImagePath()).orElse("");
			
			if (resourceResolver.resolve(link).adaptTo(Page.class) != null && !link.contains(".html")) {
				link += ".html";
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

