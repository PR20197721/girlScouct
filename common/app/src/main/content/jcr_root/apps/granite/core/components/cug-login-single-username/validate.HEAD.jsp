<%--

  ADOBE CONFIDENTIAL
  __________________

   Copyright 2013 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
  --%><%@page session="false"
              pageEncoding="utf-8"
              import="javax.jcr.Session"%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    String uri = request.getParameter("uri");
    if (uri != null) {
        Session session = resourceResolver.adaptTo(Session.class);
        try {
            if (session != null && session.hasPermission(uri, Session.ACTION_READ)) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        } catch(Exception e) {
            log.error("Failed to check permission for uri: {}", uri);
        }
    } else {
        log.error("Missing uri parameter");
    }
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
%>