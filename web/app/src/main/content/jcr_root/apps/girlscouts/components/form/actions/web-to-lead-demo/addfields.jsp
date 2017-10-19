<%@include file="/libs/foundation/global.jsp"
%><%@ page session="false" %><%
%><%@ page import="java.util.List,
                org.apache.sling.api.resource.ResourceUtil,
                   org.apache.commons.lang3.StringEscapeUtils,
                   org.apache.sling.api.resource.Resource,
                   org.apache.sling.api.resource.ValueMap,
                   java.util.HashMap,
                   com.day.cq.wcm.foundation.forms.FormsConstants,
                   com.day.cq.wcm.foundation.forms.FormResourceEdit"%><%
%><%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
    
	
    final ValueMap props = ResourceUtil.getValueMap(resource);
	String campaignID = props.get("campaignID", "");
    if(!campaignID.isEmpty()){
%>
<!-- ORGANIZATION ID -->
<input type=hidden name="oid" value="00D630000009G0r"> 
<!-- CAMPAIGN ID -->
<input type=hidden name="Campaign_ID" value="<%=campaignID%>" />
<input type=hidden name="00N63000001SuaL" value="<%=campaignID%>" />
<!-- API URL -->
<input type=hidden name="apiUrl" value="https://test.salesforce.com/servlet/servlet.WebToLead?encoding=UTF-8" />

<%  } %>
