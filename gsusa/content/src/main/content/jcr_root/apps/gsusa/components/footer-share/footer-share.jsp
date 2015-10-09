<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>

<%
  	String[] socialIcons = properties.get("socialIcons", String[].class);
	
	if (WCMMode.fromRequest(request) == WCMMode.EDIT && socialIcons == null) {
%>
		<li>Please click to edit the footer share component</li>
<% 
	}
%>

<ul class="inline-list">
  <% if (socialIcons != null) { 
    for (String settingStr : socialIcons) {
      String[] settings = settingStr.split("\\|\\|\\|");
      if (settings.length < 2) {
          continue;
      }
      String name = settings[0];
      String url = settings[1];
      String iconPath = settings[2];
      String iconClass = settings.length >= 4 ? " "+ settings[3] : "";
    %>
    <li><a id="tag_social-icon_<%=name.toLowerCase()%>" target="_blank" href="<%= url %>"><img alt="<%=name%>" title="<%=name%>" src="<%= iconPath %>"/></a></li>
    <% } 
    } %>
</ul>
