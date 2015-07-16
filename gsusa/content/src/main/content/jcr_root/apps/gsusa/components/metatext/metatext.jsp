<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String text = properties.get("text", "");
if (text.isEmpty()) {
    String metaPropName = properties.get("metaproperty", "");
    if (!metaPropName.isEmpty()) {
        ValueMap pageProps = resourceResolver.resolve(currentPage.getPath() + "/jcr:content").adaptTo(ValueMap.class);
        text = pageProps.get(metaPropName, "");
    }
}
%>
<cq:text value="<%= text %>" escapeXml="true" placeholder="<%= Placeholder.getDefaultPlaceholder(slingRequest, component, null)%>"/>