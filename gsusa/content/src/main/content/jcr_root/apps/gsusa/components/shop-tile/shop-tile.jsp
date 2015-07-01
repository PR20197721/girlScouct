<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
String[] imgs = properties.get("imgs", String[].class);
String shopNowLink = properties.get("shopLink", "#");
if (imgs == null && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
<p>Please select at least one image to display</p>
<% } else {
	 for (int i = 0; i < imgs.length; i++) {
                String[] split = imgs[i].split("\\|\\|\\|");
                String path = split.length >= 1 ? split[0] : "";
                String link = split.length >= 2 ? split[1] : "";
                boolean isNewWindow = Boolean.valueOf(split.length >=3 ? split[2] : "false");
                StringBuilder sb = new StringBuilder();
                if(!path.equals("")){
                 	sb.append("<a href=\"" + link + "\"");
        	         if(isNewWindow){
        	        	 sb.append(" target=\"_blank\"");
        	         }
        	         sb.append("/>");
                }
                 sb.append("<img src=\"" + path + "\"/>");
                 if(!path.equals("")){
                	 sb.append("</a>");
                }
                String image = sb.toString();
                %> <%= image %> <%
	 }
%>
<div class="rich-text">
<cq:text property="text" escapeXml="true"
        placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>
</div>
<a href="<%=shopNowLink%>" class="button">Shop Now</a>

<% } %>