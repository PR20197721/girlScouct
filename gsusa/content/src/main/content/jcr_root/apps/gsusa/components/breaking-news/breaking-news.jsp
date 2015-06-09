<%@include file="/libs/foundation/global.jsp" %>

<%
String message = properties.get("message","");
String url = properties.get("url","");
if(!message.equals("")){
%>
<div id="breaking-news">
<%
if(!url.equals("")){
	%><a href="<%= url %>"><%
}
%>
<strong>Breaking News:</strong><%= message %>
<%
if(!url.equals("")){
	%></a><%
}
%>
</div>
<%
}
%>