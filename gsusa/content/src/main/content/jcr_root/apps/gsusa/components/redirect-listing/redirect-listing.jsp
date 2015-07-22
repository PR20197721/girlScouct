<%@page import="com.day.cq.wcm.api.WCMMode,
                java.util.Iterator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
if (WCMMode.fromRequest(request) != WCMMode.EDIT) {
    response.setStatus(404);
    response.setHeader("Connection", "close");
} else {
%>
	<p>Welcome to the Permanent Redirect listing page.</p>
	<p>Click <a href="/etc/scaffolding/gsusa/permanent-redirect.html">here</a> to create a permanent redirect.</p>
	<p>Current redirects:</p>
	<table>
<%
	
	Iterator<Page> iter = currentPage.listChildren();
	while (iter.hasNext()) {
		Page redirectPage = iter.next();
		ValueMap props = resourceResolver.resolve(redirectPage.getPath() + "/jcr:content").adaptTo(ValueMap.class);
		
		String scaffoldingLink = redirectPage.getPath() + ".scaffolding.html";
		String src = props.get("sling:vanityPath", "").replaceAll("^\\/content\\/gsusa", "http://www.girlscouts.org");
		String dst = props.get("redirectTarget", "");
%>
		<tr>
			<td><a href="<%= scaffoldingLink %>">EDIT</a></td>
			<td><%= src %></td>
			<td> => </td>
			<td><%= dst %></td>
		</tr>
<% 
	}
%>
	</table>
<%
}
%>