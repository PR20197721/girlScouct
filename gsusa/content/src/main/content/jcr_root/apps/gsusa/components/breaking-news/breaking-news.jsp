<%@include file="/libs/foundation/global.jsp" %>

<%
String message = properties.get("message","");
String url = properties.get("url","");
//String filePath = properties.get("fileReference", "");
Resource thumbnail = resource.getChild("thumbnail");
if(thumbnail != null) {
	String filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
	if(!message.equals("")) {
		%>
		<div class="inner-wrapper">
			<%
			if(!filePath.equals("")) { %>
				<img src="<%= filePath %>" alt="Breaking News Image" />
			<% } %>
			<p>
			<%
			if(!url.equals("")) { %>
				<a href="<%= url %>" title="<%= message %>">
			<% } %>
				<strong>BREAKING NEWS:</strong> <%= message %>
			<%
			if(!url.equals("")) {
				%></a><%
			}
			%>
			</p>
		</div>
		<%
	}
}
%>
