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
final String carouselTitle = properties.get("carouselTitle", "Default Title");

if (isCarousel) {
	%>
<div class="rotator">
  <h5> <%= carouselTitle %></h5>
  <div class="shop-carousel">
	<%
	if (carouselList != null) {
		for (int i = 0; i < carouselList.length; i++) {
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
			if (openInNewWindow) {
				target = "target=\"_blank\"";
			}
			%><div><a href="<%= linkPage %>"><img src="<%= imagePath %>" alt=""/></a><p><%=label %></p></div><%
		}
	}
	//closing div
	%>
  </div>
</div>
<%
} else {
	%>
<div class="rotator">
   	<h5>Get Supper-Fun Girl Scout Stuff</h5>
           <div class="shop-carousel">
             <div><img src="/etc/designs/gsusa/images/test-tmp.png" alt=""/><p>Cookie Charm Bracelet</p></div>
             <div><img src="/etc/designs/gsusa/images/test-tmp.png" alt=""/><p>Some Other product</p></div>
             <div><img src="/etc/designs/gsusa/images/test-tmp.png" alt=""/><p>Badges</p></div>
           </div>
       <a class="button arrow" href="http://www.girlscoutshop.com" title="shop now">Shop Now</a>
</div>
	<% 
}


%>
    
    
    
    
    
    
    
    
    
    
    
    
<%
%>

