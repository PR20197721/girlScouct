<%--

  Let Us Know component.

  Show a "let us know" link to allow users to send the council an email.

--%><%
%><%@page import="com.day.cq.wcm.api.WCMMode"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<p class='__important'>
<%
	String email = properties.get("email", "");
	if ("".equals(email)) {
		if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
			%>Double click here to add an email address<%
		}
	} else {
		%>Are we missing an important resource? <a href="mailto:<%= email %>" subject="Are%20we%20missing%20an%20important%20resource?">Let us know</a><div style="margin-bottom:40px"></div><%
	}
%>
</p>
