<%@page session="false"%><%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Design component

  Displays overview of a design

--%><%@page contentType="text/html"
            pageEncoding="utf-8"
            import="org.apache.commons.lang3.StringEscapeUtils"
    %><%!

%><%@include file="/libs/foundation/global.jsp"%><%

    String title = currentPage.getTitle();
    if (title == null) {
        title = currentPage.getName();
    }

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title>CQ5 Design | <%= StringEscapeUtils.escapeHtml4(title) %></title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/>
</head>
<body>
    <br>
    <h1><%= StringEscapeUtils.escapeHtml4(title) %></h1>
    <em>(currently not editable)</em>
</body>
</html>
