<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<hr/>
<%
String rootPath = properties.get("path", "");%>
<%=rootPath%><%
if (rootPath.isEmpty()) {
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%>Contacts List: path not configured.<%
    }
    return;
}

Page adRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
Iterator<Page> Iter = adRoot.listChildren();
while(Iter.hasNext()) {
    Page currentAd = Iter.next();
    String adName = currentAd.getProperties().get("jcr:title", "");
%><%=adName%><%
}
/*Iterator<Page> teamIter = contactRoot.listChildren();
while (teamIter.hasNext()) {
    Page currentTeam = teamIter.next();
    String teamName = currentTeam.getProperties().get("jcr:title", "");*/
    %><%
	%><%
        /*    Iterator<Page> contactIter = currentTeam.listChildren();
    while (contactIter.hasNext()) {
		Page currentContact = contactIter.next();
		ValueMap props = currentContact.getProperties();
		String name = props.get("jcr:title", "");
        */	
		%><tr><td></td><%
        //}
    %></tr><%
        //}

%>