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

  Includes the scripts and css to be included in the head tag

  ==============================================================================

--%><%@ page session="false" %><%
%><%@page import="com.day.cq.wcm.api.WCMMode" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
%><cq:includeClientLib categories="cq.foundation-main"/><%
%><cq:includeClientLib categories="cq.shared"/><%
%><cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
<cq:includeClientLib css="apps.gsusa" />
<script src="/etc/designs/gsusa/clientlibs/js/modernizr.js" type="text/javascript"></script>
<%
	ValueMap siteProps = resourceResolver.resolve(currentPage.getAbsoluteParent(2).getPath()).adaptTo(ValueMap.class);
	String addThisId = siteProps.get("addThisId", "");
	if (!addThisId.isEmpty()) {
%>
		<script type="text/javascript" src="//s7.addthis.com/js/300/addthis_widget.js#pubid=<%= addThisId %>"></script>
<%
	}
%>

<% if (WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
	<cq:includeClientLib categories="apps.girlscouts.authoring" />
	<cq:includeClientLib categories="apps.gsusa.authoring" />
<% } %>
