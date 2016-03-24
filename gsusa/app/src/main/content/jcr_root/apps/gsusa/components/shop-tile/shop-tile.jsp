<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
String[] imgs = properties.get("imgs", String[].class);
String shopNowLink = properties.get("shopLink", "#");
String title = properties.get("title", "");
String buttonText = properties.get("buttonText", "");
String text = properties.get("text", "");
if(text.length() > 200){
	text = text.substring(0,200) + "...";
}
if (imgs == null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
<p> Please select at least one image to display</p>
<% }else if(imgs != null){%>
        <div class="shop-slider">
	<% for (int i = 0; i < imgs.length; i++) {
                String[] split = imgs[i].split("\\|\\|\\|");
                String path = split.length >= 1 ? split[0] : "";
                String link = split.length >= 2 ? split[1] : "";
                boolean isNewWindow = Boolean.valueOf(split.length >=3 ? split[2] : "false");
                StringBuilder sb = new StringBuilder();
        if(!path.equals("")){
         	sb.append("<div><a id=\"tag_shop_slider_link_" + i + "\" class=\"slider-link\" href=\"" + link + "\"");
	         if(isNewWindow){
	        	sb.append(" target=\"_blank\"");
	         }
	         sb.append("/>");
         }
         sb.append("<img id=\"tag_shop_slider_img_" + i + "\" src=\"" + path + "\" alt=\"\"/>");
         if(!path.equals("")){
        	 sb.append("</a></div>");
         }
         String image = sb.toString();
         %> <%= image %> <%
	 }

%>
</div>
<div class="rich-text">
<h3><%= title %></h3>
<%= text %>
<a id="tag_shop_tile_shopnow" href="<%=shopNowLink%>" class="button"><%= buttonText %></a>
</div>

<% } %>
