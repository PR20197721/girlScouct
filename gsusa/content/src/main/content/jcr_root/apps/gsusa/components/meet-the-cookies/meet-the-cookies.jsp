<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
    %><cq:includeClientLib categories="apps.gsusa.authoring" /><%
}

String[] cookies = properties.get("cookies", String[].class);
if (cookies != null && cookies.length != 0) {
	%><div id="meet-cookie-layout"><%
	for (String cookie : cookies) {
%>
        <div>
            <img src="/etc/designs/gsusa/images/thin_mint.png" alt="" />
            <div class="wrapper">
                <h4><%= cookie %></h4>
                <section>
                    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna</p>
                </section>
            </div>
        </div>
<%
	}
    %></div><%
}
%>