<%@include file="/libs/foundation/global.jsp" %>

<%
String message = properties.get("message","");
String url = properties.get("url","");
//String filePath = properties.get("fileReference", "");
Resource thumbnail = resource.getChild("thumbnail");
if(thumbnail != null){
	String filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
	if(!"".equals(message)){
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
}
%>
