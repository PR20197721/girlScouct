<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'action' component

  Called after the form start to add action-specific fields

--%><%@include file="/libs/foundation/global.jsp"
%><%@ page session="false" %><%
%><%@ page import="java.util.List,
                org.apache.sling.api.resource.ResourceUtil,
                   org.apache.commons.lang3.StringEscapeUtils,
                   org.apache.sling.api.resource.Resource,
                   org.apache.sling.api.resource.ValueMap,
                   java.util.HashMap,
                   com.day.cq.wcm.foundation.forms.FormsConstants,
                   com.day.cq.wcm.foundation.forms.FormResourceEdit,
                   org.girlscouts.web.webtolead.config.WebToLeadConfig"%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    
	WebToLeadConfig webToLeadConfig = sling.getService(WebToLeadConfig.class);	
    final ValueMap props = ResourceUtil.getValueMap(resource);
	String campaignID = props.get("campaignID", "");
	String organizationID = webToLeadConfig.getOID();
	String campaignIDFieldName1 = webToLeadConfig.getCampaignIDPrimaryName();
	String campaignIDFieldName2 = webToLeadConfig.getCampaignIDSecondaryName();
	String apiURL = webToLeadConfig.getAPIURL();
    if(!campaignID.isEmpty()){
%>
<!-- ORGANIZATION ID -->
<input type=hidden name="oid" value="<%=organizationID%>"> 
<!-- CAMPAIGN ID -->
<input type=hidden name="<%=campaignIDFieldName1%>" value="<%=campaignID%>" />
<input type=hidden name="<%=campaignIDFieldName2%>" value="<%=campaignID%>" />
<!-- API URL -->
<input type=hidden name="apiUrl" value="<%=apiURL%>" />

<%  } %>
