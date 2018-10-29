<%--

  From Your Council component.

  The council can leave VTK users a message.

--%><%
%><%@page import="com.day.cq.wcm.api.WCMMode"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
ValueMap textProp = resourceResolver.resolve(resource.getPath() + "/text").adaptTo(ValueMap.class);
if (textProp == null || "" == textProp.get("text", "").trim()) {
	if (WCMMode.EDIT == WCMMode.fromRequest(request)) {
		%>Double click here to edit "From Your Council"<%
	}
} else {
%>
<div class="__message_from">
	<div>
		<div class="__title" style="float: left;">
			<i class="icon-horn-promote"></i> <span>From your council</span> <span class="arrow arrow-close"></span>
		</div>
		<div style="float: right; padding: 3px 0 0 0;">
			<span><%= properties.get("lastUpdated", "") %></span>
		</div>
	</div>
	<div style="clear: both"></div>
</div>

<div class="__body">
	<cq:include path="text" resourceType="girlscouts/components/text" />
</div>
<% } %>