<%@ page import="com.day.cq.wcm.api.Page, org.apache.commons.lang3.StringUtils, org.apache.sling.api.resource.ResourceUtil, org.apache.sling.api.resource.ValueMap, org.girlscouts.common.osgi.component.CouncilCodeToPathMapper" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page session="false" %>
<cq:includeClientLib categories="apps.girlscouts.components.form.actions.web-to-case" />
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects/>
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
%>