<%@page session="false"%><%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
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

  Text component

  Draws text.

--%><%@include file="/libs/foundation/global.jsp"%>
<%

String pTop = properties.get("./ptop", "0");
String pBottom = properties.get("./pbottom", "0");
String pLeft = properties.get("./pleft", "0");
String pRight = properties.get("./pright", "0");

String style = "padding: " + pTop + "px " + pRight + "px " + pBottom + "px " + pLeft + "px;";
String text = properties.get("text","");

if(text != null && !text.equals("")){%>
	<div style="<%= style %>">
		<cq:text property="text" escapeXml="true"
       		 placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>
	</div>
<%}else{%>
	<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div>
<%}%> 

