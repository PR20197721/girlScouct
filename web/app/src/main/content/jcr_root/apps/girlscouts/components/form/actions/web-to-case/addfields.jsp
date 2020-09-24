<%@ page import="org.apache.sling.api.resource.ResourceUtil, org.apache.sling.api.resource.ValueMap" %>
<%@ include file="/libs/foundation/global.jsp"%>
<%@ page session="false" %>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects/>
<cq:includeClientLib categories="apps.girlscouts.components.form.actions.web-to-case" />
<div id="validation-errors" class="form_error"></div>
<%
final ValueMap props = ResourceUtil.getValueMap(resource);
String debugEmail = props.get("debug", "");
if(!debugEmail.isEmpty()){
    %>
    <input type="hidden" name="debug" value="1">
    <input type="hidden" name="debugEmail" value="<%=debugEmail%>">
    <%
}
%>