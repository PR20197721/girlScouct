<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>

<%
	String header = (String)request.getAttribute("gsusa-share-model-header");
	if (header == null) {
		header = properties.get("header","");
	}
	String button = properties.get("btn","");
	String desc = properties.get("desc","");
	String text1 = properties.get("text1","");
	String link1 = properties.get("link1","#");
	String icon1 = properties.get("icon1","");
	String text2 = properties.get("text2","");
	String link2 = properties.get("link2","#");
	String icon2 = properties.get("icon2", "");

	Resource img = resource.getChild("image");
	String filePath = "";
	if(img != null) {
		filePath = ((ValueMap)img.adaptTo(ValueMap.class)).get("fileReference", "");
	}
	
	if (button.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%> ****Please configure the modal button text **** <%
	} else {
		if(WCMMode.fromRequest(request) == WCMMode.EDIT){
			%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
		}
%>
<div class="share-modal">
	<a href="#" data-reveal-id="shareModal" class="button"><%= button %></a>
	<!-- Reveal Modals begin -->
	<div id="shareModal" class="reveal-modal share-modal" data-reveal aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
	  <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
	  <div class="float-left">
	    <% if(!filePath.equals("")){ %>
	    <img src="<%= filePath %>" alt="" />
	    <% } %>
	  </div>
	  <div class="float-right">
	      <h3><%= header %></h3>
	      <p><%= desc %></p>
	      <% if(!link1.equals("") && !text1.equals("")){ %>
	      <a href="<%= link1 %>" class="button"><%= text1 %> <% if(!icon1.equals("")){ %><i class="<%= icon1 %>"></i><% } %></a>
	      <% } 
	      if(!link2.equals("") && !text2.equals("")){%>
	      <a href="<%= link2 %>" class="button"><%= text2 %> <% if(!icon2.equals("")){ %><i class="<%= icon2 %>"></i><% } %></a>
	      <% } %>
	  </div>
	</div>
</div>
<% } %>