<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
String[] imgs = properties.get("imgs", String[].class);
String shopNowLink = properties.get("shopLink", "#");
if (imgs == null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
<p> Please select at least one image to display</p>
<% }else{%>
        <div class="shop-slider">
	<% for (int i = 0; i < imgs.length; i++) {
                String[] split = imgs[i].split("\\|\\|\\|");
                String path = split.length >= 1 ? split[0] : "";
                String link = split.length >= 2 ? split[1] : "";
                boolean isNewWindow = Boolean.valueOf(split.length >=3 ? split[2] : "false");
                StringBuilder sb = new StringBuilder();
        if(!path.equals("")){
         	sb.append("<div><a class=\"slider-link\" href=\"" + link + "\"");
	         if(isNewWindow){
	        	sb.append(" target=\"_blank\"");
	         }
	         sb.append("/>");
         }
         sb.append("<img src=\"" + path + "\" alt=\"\"/>");
         if(!path.equals("")){
        	 sb.append("</a></div>");
         }
         String image = sb.toString();
         %> <%= image %> <%
	 }

%>
</div>
<div class="rich-text">
<h3>Gifts for girl scout dads</h3>
<cq:text property="text" escapeXml="true"
        placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>
<a href="<%=shopNowLink%>" class="button">Shop Now</a>
</div>

<% } %>