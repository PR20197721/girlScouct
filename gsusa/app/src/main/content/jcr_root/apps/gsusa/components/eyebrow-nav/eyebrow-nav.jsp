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
%>
	<ul class="inline-list"><li>### No eyebrow navs found ###</li></ul>
<%	
	}
%>
