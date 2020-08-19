<%@include file="/libs/foundation/global.jsp"
%>
<%@ page session="false" %>
<%
%>
<%@ page import="com.day.cq.wcm.foundation.forms.FormResourceEdit,
                 com.day.cq.wcm.foundation.forms.FormsConstants,
                 org.apache.commons.lang3.StringEscapeUtils,
                 org.apache.sling.api.resource.Resource,
                 org.apache.sling.api.resource.ResourceUtil,
                 org.apache.sling.api.resource.ValueMap,
                 org.girlscouts.common.webtolead.config.WebToLeadConfig" %>
<%
%>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%
%><sling:defineObjects/><%
    final ValueMap props = ResourceUtil.getValueMap(resource);
    String campaignID = props.get("campaignID", "");
    String apiURL = webToLeadConfig.getAPIURL();
    String utm_campaign = request.getParameter("utm_campaign") != null ? request.getParameter("utm_campaign") : null;
    String utm_medium = request.getParameter("utm_medium") != null ? request.getParameter("utm_medium") : null;
    String utm_source = request.getParameter("utm_source") != null ? request.getParameter("utm_source") : null;
    String leadType = properties.get("leadType", "");
    CouncilCodeToPathMapper councilCodeToPathMapper = sling.getService(CouncilCodeToPathMapper.class);
    Page site = currentPage.getAbsoluteParent(1);
    String councilCode = councilCodeToPathMapper.getCouncilCode(site.getPath());
    if (councilCode != null) {
        %>
        <input name="CouncilCode" value="<%=xssAPI.encodeForHTMLAttr(councilCode)%>" type="hidden">
        <%
    }
    if (utm_campaign != null) {
        %>
        <input name="UTM_Campaign" value="<%=xssAPI.encodeForHTMLAttr(utm_campaign)%>" type="hidden">
        <%
    }
    if (utm_medium != null) {
        %>
        <input name="UTM_Medium" value="<%=xssAPI.encodeForHTMLAttr(utm_medium)%>" type="hidden">
        <%
    }
    if (utm_source != null) {
        %>
        <input name="UTM_Source" value="<%=xssAPI.encodeForHTMLAttr(utm_source)%>" type="hidden">
        <%
    }
    if (leadType != null) {
        %>
        <input name="leadType" value="<%=xssAPI.encodeForHTMLAttr(leadType)%>" type="hidden">
        <%
    }
    if (!campaignID.isEmpty()) {
        %>
        <input type=hidden name="Campaign_ID" value="<%=campaignID%>"/>
        <%
    }
    if (leadType.equals("General")) {

    } else {
        if (leadType.equals("DirectContact")) {

        } else {
            if (leadType.equals("Newsletter")) {
                
            }
        }
    }
%>
