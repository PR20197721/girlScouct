<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String html = properties.get("html", "");

    if (!html.isEmpty()) {
		// Mike Z. We need this extra div because of a CQ bug on iframe.
        // It is trying to correct iframe height but in a wrong way.
        // Widget.js 1449975 
        // //in case of an iframe element height is not correct
        // var iframe = this.element.first("iframe");
        // if (iframe) {
        //     this.element.setHeight(this.element.getHeight() + iframe.getHeight());
        // }
        %><div><%= html %></div><%
    } 
%>
<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder">
		</div>
