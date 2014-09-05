<%@include file="/libs/foundation/global.jsp" %>

<%
String dateStr = properties.get("date", "");
String blurb = properties.get("blurb", "");
String title = properties.get("jcr:title", "");
%>
<h1>Milestone | <%= title %></h1>
<p>Blurb: <%= blurb %></p>
<p>Date: <%= dateStr %></p>