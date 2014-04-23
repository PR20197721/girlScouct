<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'element' component

  Draws an ICS link for the event

  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/form/icslink/icslink.jsp directly.
  ==============================================================================
--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="org.apache.sling.api.resource.ResourceUtil,
                   org.apache.commons.lang3.StringEscapeUtils,
                   com.day.cq.i18n.I18n,
                   com.day.cq.wcm.foundation.forms.FormsHelper" %><%

    final String title = properties.get("jcr:title", "Download event as ICS");
    final Resource editResource = FormsHelper.getFormLoadResource(slingRequest);
    if (editResource != null) {
        // do nothing for non-existing resources (eg. for "create" form)
        if (!ResourceUtil.isNonExistingResource(editResource)) {
            final String link = editResource.getPath() + ".request.ics";
%>
    <div class="form_row">
        <div class="form_rightcol">
            <a href="<%= link %>"><%= StringEscapeUtils.escapeHtml4(title) %></a>
        </div>
    </div>
<%
        }
    } else { %>
    <div class="form_row">
        <div class="form_rightcol">
            <span><%= StringEscapeUtils.escapeHtml4(title) %> <%= I18n.get(slingRequest.getResourceBundle(currentPage.getLanguage(true)), "(no resource available)", null) %></span>
        </div>
    </div>
<%  } %>
    