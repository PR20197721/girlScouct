<%@ page import="com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String mainMetaName = "";
String tagName = properties.get("tagName", "div");
String text = properties.get("text", "");
if (text.isEmpty()) {
	String metaPropName = properties.get("metaProperty", "");
	if (!metaPropName.isEmpty()) {
	    ValueMap pageProps = resourceResolver.resolve(currentPage.getPath() + "/jcr:content").adaptTo(ValueMap.class);
	    String[] names = metaPropName.split(",");
	    mainMetaName = camelToDash(names[0]);
	    for (int i = 0; i < names.length; i++) {
	        text = pageProps.get(names[i], "");
	        if (!text.isEmpty()) {
	            break;
	        }
	    }
	}
}

String placeholder = "&lt; Placeholder for matadata <i>" + mainMetaName + "</i> &gt;";
%>
<cq:text value="<%= text %>" tagClass="<%= mainMetaName %>" tagName="<%= tagName %>" escapeXml="true" placeholder="<%= placeholder %>"/>

<%!
String camelToDash(String input) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (Character.isUpperCase(c)) {
            builder.append("-" + Character.toLowerCase(c));
        } else {
            builder.append(c);
        }
    }
    return builder.toString();
}
%>