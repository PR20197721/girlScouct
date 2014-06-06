<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<%
String rootPath = properties.get("path", "");
if (rootPath.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Contacts List: path not configured.<%
    }
    return;
}

boolean pathGood = false;
Page contactRoot;
try {
	contactRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
	if (contactRoot == null) {
	    throw new NullPointerException();
	}
} catch (Exception e) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Contacts List: path not configured correctly.<%
    }
    return;
}

%><hr/><%
Iterator<Page> teamIter = contactRoot.listChildren();
while (teamIter.hasNext()) {
    Page currentTeam = teamIter.next();
    String teamName = currentTeam.getProperties().get("jcr:title", "");
    %><div id="contactsList"><h2><%= teamName %><h2/><%
	%><table><tbody><tr><th>Name</th><th>Title</th><th>Phone</th><th>Email</th></tr><%
    Iterator<Page> contactIter = currentTeam.listChildren();
    while (contactIter.hasNext()) {
		Page currentContact = contactIter.next();
		ValueMap props = currentContact.getProperties();
		String name = props.get("jcr:title", "");
		String title = props.get("title", "");
		String phone = props.get("phone", "");
		String email = props.get("email", "");
		
		%><tr><td><%=name%></td><td><%=title%></td><td><%=phone%></td><td><%=email%></td><%
    }
    %></tr></tbody></table></div><%
}

%>
