<%@include file="/libs/foundation/global.jsp"%>
<%@ page session="false" %>
<%@ page import="com.day.cq.wcm.foundation.forms.FormResourceEdit,
                 com.day.cq.wcm.foundation.forms.FormsConstants,
                 org.apache.commons.lang3.StringUtils,
                 org.apache.sling.api.resource.Resource,
                 org.apache.sling.api.resource.ResourceUtil,
                 org.apache.sling.api.resource.ValueMap, org.girlscouts.common.osgi.component.CouncilCodeToPathMapper" %>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects/>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts.components.form.actions.web-to-lead" />
<div id="validation-errors" class="form_error"></div>
<%
final ValueMap props = ResourceUtil.getValueMap(resource);
String campaignID = props.get("campaignID", "");
String leadType = props.get("leadType", "");
CouncilCodeToPathMapper councilCodeToPathMapper = sling.getService(CouncilCodeToPathMapper.class);
Page site = currentPage.getAbsoluteParent(1);
String councilCode = councilCodeToPathMapper.getCouncilCode(site.getPath());
String secret = currentSite.get("recaptcha_secret", "");
%>
<input type="hidden" name="secret" value="<%=secret%>">
<%
if (!StringUtils.isBlank(councilCode)) {
    %>
    <input class="form_field" name="CouncilCode" value="<%=xssAPI.encodeForHTMLAttr(councilCode)%>" type="hidden"/>
    <%
}
if (!StringUtils.isBlank(leadType)) {
    %>
    <input class="form_field" name="LeadType" value="<%=xssAPI.encodeForHTMLAttr(leadType)%>" type="hidden"/>
    <%
}
if (!StringUtils.isBlank(campaignID)) {
    %>
    <input class="form_field" name="CampaignID" value="<%=xssAPI.encodeForHTMLAttr(campaignID)%>" type=hidden />
    <%
}
%>
<input class="form_field" name="UTM_Campaign" value="" type="hidden"/>
<input class="form_field" name="UTM_Medium" value="" type="hidden"/>
<input class="form_field" name="UTM_Source" value="" type="hidden"/>

