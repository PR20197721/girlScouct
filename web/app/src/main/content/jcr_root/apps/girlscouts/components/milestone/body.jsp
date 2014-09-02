<%@include file="/libs/foundation/global.jsp" %>

<%
String dateStr = properties.get("date", "");
String title = properties.get("jcr:title", "");
%>
<h1>Milestone</h1>
<p>Title: <%= title %></p>
<p>Date: <%= dateStr %></p>