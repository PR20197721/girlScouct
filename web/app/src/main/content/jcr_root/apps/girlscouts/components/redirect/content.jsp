<%@page session="false"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Redirect content script.

  Displays the redirected url as link.

  ==============================================================================

--%><%@ page import="com.day.cq.wcm.foundation.ELEvaluator" %><%
%><%@include file="/libs/foundation/global.jsp" %><%

    // try to resolve the redirect target in order to the the title
    String location = properties.get("cq:redirectTarget", "");
    // resolve variables in location
    location = ELEvaluator.evaluate(location, slingRequest, pageContext);
    Page target = pageManager.getPage(location);
    String title = target == null ? location : target.getTitle();
    // check for absolute path
    final int protocolIndex = location.indexOf(":/");
    final int queryIndex = location.indexOf('?');
    final String redirectPath;
    if (  protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex) ) {
        redirectPath = location;
    } else {
        redirectPath = request.getContextPath() + location + ".html";
    }

%><p align="center">
    This page redirects to <a href="<%= xssAPI.getValidHref(redirectPath) %>"><%= xssAPI.filterHTML(title) %></a>
</p>
