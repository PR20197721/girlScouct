<%--

  Base component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="org.apache.sling.api.SlingHttpServletRequest" %><%
	// TODO add you code here
	String hostName = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
%>

<base href="<%=hostName%>" />