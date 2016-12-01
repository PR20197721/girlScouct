<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
  String[] links = properties.get("links", String[].class);
  String[] socialIcons = properties.get("socialIcons", String[].class);
%>
<div class="columns large-16 medium-16 small-19 small-centered large-uncentered medium-uncentered">
  <ul>
  <% 
    if ((links == null || links.length == 0) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
      %> ##### Footer Navigation Placeholder ##### <% }
      else {
        for (int i = 0; i < links.length; i++) {
          String[] values = links[i].split("\\|\\|\\|");
          String label = values[0];
          String path = values.length >= 2 ? values[1] : "";
          path = genLink(resourceResolver, path);
          String linkClass = values.length >= 3 ? "class=\"" + values[2] + "\"" : "";
          String newTab = values.length >= 4 ? values[3] : "false";
  %> 
    <li><a <%= newTab.equals("true") ? "target=\"_blank\"" : "" %> <%= linkClass%> href="<%= path %>"><%= label %></a></li>
  <% } 
  } %>
  </ul>
</div>
<div class="columns large-8 medium-8">
  <ul>
  <% if (socialIcons != null) { 
    for (String settingStr : socialIcons) {
      String[] settings = settingStr.split("\\|\\|\\|");
      if (settings.length < 2) {
          continue;
      }
      String url = settings[0];
      String iconPath = settings[1];
      String newTab = settings.length >= 3 ? settings[2] : "false";
    %>
    <li><a <%= newTab.equals("true") ? "target=\"_blank\"" : "" %> href="<%= url %>"><img src="<%= iconPath %>"/></a></li>
    <% } 
    } %>
  </ul>
</div>
