<%@include file="/libs/foundation/global.jsp" %>

<%
String message = properties.get("message","");
String url = properties.get("url","");
//String filePath = properties.get("fileReference", "");
Resource thumbnail = resource.getChild("thumbnail");
String filePath = "";
if(thumbnail != null) {
	filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
}
if(!message.equals("")) {
	%>
	<div class="inner-wrapper">
		<%
		if(!filePath.equals("")) { %>
			<img src="<%= filePath %>" alt="Breaking News Image" style="max-width:50px; max-height:50px" class="thumbnail"/>
		<% }
		if(!url.equals("")) { %>
			<a href="<%= url %>" target="_blank" title="<%= message %>">
		<% } %>
			<strong>BREAKING NEWS:</strong> <span><%= message %></span>
		<%
		if(!url.equals("")) {
			%></a><%
		}
		%>
	</div>
	<%
}
%>
