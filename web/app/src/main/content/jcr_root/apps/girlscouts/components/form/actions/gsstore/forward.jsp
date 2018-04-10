<%--
  Copyright 1997-2010 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'action' component

  Return the action path for the store form handling

--%><%@page session="false" %><%
%><%@page import="org.apache.sling.api.resource.ResourceUtil,
                com.adobe.cq.social.commons.CollabUtil,
                org.apache.sling.jcr.api.SlingRepository,
                org.apache.sling.api.resource.ValueMap,
                com.day.cq.wcm.foundation.forms.FormsConstants,
                com.day.cq.wcm.foundation.forms.FormsHelper,
                java.util.concurrent.atomic.AtomicInteger,
                java.util.Map,
                java.util.HashMap,
                javax.jcr.Session,
                javax.jcr.Node,
                javax.jcr.security.Privilege,
                javax.jcr.RepositoryException,
                org.slf4j.Logger,
                org.slf4j.LoggerFactory,
                com.day.cq.commons.jcr.JcrUtil,
                com.day.cq.security.UserManager,
                com.day.cq.security.UserManagerFactory,
                javax.jcr.security.AccessControlManager,
                javax.jcr.security.AccessControlPolicyIterator,
                javax.jcr.security.AccessControlPolicy,
                javax.jcr.security.AccessControlList"%><%!

    private static final AtomicInteger uniqueIdCounter = new AtomicInteger();
    private final Logger log = LoggerFactory.getLogger(getClass());

    %><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
    %><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
    <cq:defineObjects/><sling:defineObjects/><%
    final ValueMap props = ResourceUtil.getValueMap(resource);
    String path = props.get(FormsConstants.START_PROPERTY_ACTION_PATH, "");
    slingRequest.setAttribute("contentPath",path);
    FormsHelper.setForwardPath(slingRequest, resource.getPath() + ".gsstore.html");
    FormsHelper.setRedirectToReferrer(request, true);
%>