<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
    final String[] navs = properties.get("navs", String[].class);
	if (navs != null) {
%>
		<ul class="inline-list">
			<cq:include script="items.jsp" />
		</ul>
<%
	} else {

		if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
			<ul class="inline-list"><li>### No navs found ###</li></ul>
<%		} else {
%>
			<ul class="inline-list"><li>&nbsp;</li></ul>
<%
		}
	}
%>
