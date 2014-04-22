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

  Calendar ICS/webcal export link

  Draws an ICS link for a calendar component
  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/exportlink/exportlink.jsp directly.
  ==============================================================================
--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="java.util.Iterator,
                   java.net.URI,
                   com.day.cq.commons.Externalizer,
                   com.day.cq.wcm.api.WCMMode,
                   com.day.cq.search.Predicate,
                   com.day.cq.search.PredicateGroup,
                   com.day.cq.collab.calendar.CalendarConstants,
                   com.day.cq.collab.calendar.CalendarComponent,
                   org.apache.sling.api.resource.ResourceUtil" %><%

    final String title = properties.get("jcr:title", "Export calendar");
    final String type  = properties.get("type",      "download");
    final String path  = properties.get("path",      String.class);
    
    String scheme;
    if ("webcal".equals(type)) {
        scheme = "webcal";
    } else {
        scheme = "http";
    }

    Resource calResource = null;
    if (path != null) {
        Resource res = resourceResolver.getResource(path);
        if (CalendarConstants.RT_CALENDAR_COMPONENT.equals(res.getResourceType())) {
            calResource = res;
        }
    } else {
        // find calendar component within our siblings
        Resource parent = ResourceUtil.getParent(resource);
        // first named "calendar"
        calResource = resourceResolver.getResource(parent, "calendar");
        if (calResource == null) {
            // secondly with the calendar component resource type
            Iterator<Resource> siblings = resourceResolver.listChildren(parent);
            while (siblings.hasNext()) {
                Resource sibling = siblings.next();
                if (CalendarConstants.RT_CALENDAR_COMPONENT.equals(sibling.getResourceType())) {
                    calResource = sibling;
                }
            }
        }
    }
            
    if (calResource != null) {
        String link = "/bin/querybuilder.ics";
        if ("webcal".equals(scheme)) {
            Externalizer externalizer = sling.getService(Externalizer.class);
            link = externalizer.absoluteLink(slingRequest, "webcal", link);
        }
        
        CalendarComponent cal = new CalendarComponent(calResource);
        
        PredicateGroup group = new PredicateGroup();
        group.set("calname", currentPage.getTitle());
        group.set("limit", "0");
        group.add(new Predicate("type").set("type", CalendarConstants.NT_CALENDAR_EVENT));
        group.add(cal.getCalendarPathQuery());
        group.add(new Predicate("orderby").set("orderby", "@" + CalendarConstants.PN_START));
        
        link += "?" + group.toURL();
        
%><a href="<%= link %>"><%= title %></a><%
        
    } else {
        // no calendar resource found
        if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%><div class="cq-texthint-placeholder"><a href="Calendar component was not found">Calendar component was not found: <%= path %></a></div><%
        }
    }
%>
