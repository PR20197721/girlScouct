<%--

  Base component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="org.apache.sling.api.SlingHttpServletRequest,
				org.apache.sling.settings.SlingSettingsService" %><%
	// TODO add you code here
    /* String requestProto = request.getHeader("X-Forwarded-Proto");
	if(requestProto == null) 
        requestProto = "http"; */
	String hostName="/";
	if (!(sling.getService(SlingSettingsService.class).getRunModes().contains("author"))) {
	    hostName = resourceResolver.map(currentPage.getPath()).split("/")[2];
	}
%>

<base href="<%=hostName%>" />