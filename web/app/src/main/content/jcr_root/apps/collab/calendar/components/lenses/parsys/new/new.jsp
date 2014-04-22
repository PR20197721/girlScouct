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

  Lenses parsys/new component
  
  Overwritten only for calling setEmpty(true) on the edit config, which will
  make the cq:emptyText edit config apply, to change the text on the drop
  target for new lenses in this parsys. See also bug 29814.
  ==============================================================================
  DEPRECATED since CQ 5.6.
  ==============================================================================
--%><%@ page session="false" import="
    com.day.cq.wcm.api.components.EditContext,
    com.day.cq.wcm.commons.WCMUtils" %><%
%><%@include file="/libs/foundation/global.jsp"%><%

    editContext.getEditConfig().setEmpty(true);
    
%><cq:include path="." resourceType="foundation/components/parsys/new" />