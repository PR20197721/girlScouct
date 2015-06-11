<%@include file="/libs/foundation/global.jsp" %>
<%@page import="java.net.Proxy, java.net.InetSocketAddress, java.net.URL, java.net.URLConnection" %>

<%
Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));
URLConnection conn = new URL("http://www.girlscouts.org/councilfinder/ajax_results.asp?zip=" + request.getParameter("zip")).openConnection(proxy);
%>