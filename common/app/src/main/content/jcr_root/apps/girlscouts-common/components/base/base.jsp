<!-- GSDO-1030 -->
<%@include file="/libs/foundation/global.jsp" %>
<%@page session="false" %>

<%@page import="org.apache.sling.settings.SlingSettingsService, java.util.regex.Matcher, java.util.regex.Pattern" %>
<%
    String requestProto = request.getHeader("X-Forwarded-Proto");
	Boolean runModeValue = sling.getService(SlingSettingsService.class).getRunModes().contains("author");
    Pattern pattern = Pattern.compile("[^\\/]*");
    try {
        if (requestProto == null) {
            requestProto = "http";
        }
        String hostName = "/";
        if (!(runModeValue)) {
            // hostName = requestProto + "://" + resourceResolver.map(currentPage.getPath()).split("/")[2];
            String mappedPath = resourceResolver.map(currentPage.getPath());
            mappedPath = mappedPath.replace("http://", "").replace("https://", "");
            hostName = requestProto + "://" + mappedPath + ".html";
                %>
                <base href="<%=hostName%>"/>
                <%
        }
    } catch (Exception e) {
    }
%>