<%@include file="/libs/foundation/global.jsp" %>

<%
String message = properties.get("message","");
String url = properties.get("url","");
String filePath = properties.get("fileReference", "");
if(!message.equals("")){
%>
<div id="breaking-news">
<%
if(!filePath.equals("")){
%>
<img src="<%= filePath %>" alt="Breaking News Image" />
<%
}
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
