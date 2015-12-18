<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String pathIndexStr = (String)request.getAttribute("gsusa-component-booth-finder-index");
String text = properties.get("path" + pathIndexStr + "Text", "").replaceAll("\\{\\{", "{{council."); // e.g. path1Text
%>
	<p><%= text %></p>