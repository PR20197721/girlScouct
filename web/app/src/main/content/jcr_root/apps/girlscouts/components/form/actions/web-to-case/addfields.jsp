<%@ page import="com.day.cq.wcm.api.Page, org.apache.commons.lang3.StringUtils, org.apache.sling.api.resource.ResourceUtil, org.apache.sling.api.resource.ValueMap, org.girlscouts.common.osgi.component.CouncilCodeToPathMapper" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page session="false" %>
<cq:includeClientLib categories="apps.girlscouts.components.form.actions.web-to-case" />
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects/>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<div id="validation-errors" class="form_error"></div>
<%
final ValueMap props = ResourceUtil.getValueMap(resource);
String debugEmail = props.get("debug", "");
if(!debugEmail.isEmpty()){
    %>
    <input type="hidden" name="debug" value="true">
    <input type="hidden" name="debugEmail" value="<%=debugEmail%>">
    <%
}
CouncilCodeToPathMapper councilCodeToPathMapper = sling.getService(CouncilCodeToPathMapper.class);
Page site = currentPage.getAbsoluteParent(1);
String councilCode = councilCodeToPathMapper.getCouncilCode(site.getPath());
if (!StringUtils.isBlank(councilCode)) {
    %>
    <input class="form_field" name="CouncilCode" value="<%=xssAPI.encodeForHTMLAttr(councilCode)%>" type="hidden"/>
    <%
}
        String site_key = currentSite.get("recaptcha_site_key", "");
%>
<input class="form_field" type="hidden" name="captcha_settings" value='{"keyname":"<%=homepage.getParent().getName().toUpperCase()%>webtocase","fallback":"true","orgId":"00D220000004chr","ts":""}'>
<input class="form_field" type="hidden" name="orgid" value="00D220000004chr">
<input class="form_field" type="hidden" name="retURL" value="">
<div class="g-recaptcha" data-sitekey="<%=site_key%>"></div>