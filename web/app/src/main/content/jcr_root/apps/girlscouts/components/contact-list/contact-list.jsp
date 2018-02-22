<%@page import="java.util.Iterator,
com.day.cq.wcm.api.WCMMode,
com.day.cq.wcm.api.PageManager,
org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<cq:defineObjects/>
<%
String rootPath = properties.get("path", "");
if (!rootPath.isEmpty()) {
	try {
		Page contactRoot = resourceResolver.resolve(rootPath).adaptTo(Page.class);
		if (contactRoot != null) {
			Iterator<Page> teamIter = contactRoot.listChildren();
			while (teamIter.hasNext()) {
			    Page currentTeam = teamIter.next();
			    String teamName = currentTeam.getProperties().get("jcr:title", "");
			    %><div id="contactsList"><h2><%= teamName %><h2/><%
			    %><table><tbody><%
			    Iterator<Page> contactIter = currentTeam.listChildren();
			    while (contactIter.hasNext()) {
					Page currentContact = contactIter.next();
					ValueMap props = currentContact.getProperties();
					String name = props.get("jcr:title", "");
					String title = props.get("title", "");
					String phone = props.get("phone", "");
					String email = props.get("email", "");
			        %><tr><td class="name"><%=name%></td><td class="title"><%=title%></td><td class="phone"><%=phone%></td><td class="email"><a href="mailto:<%=email%>"><div class="emailicon">&nbsp;</div></a></td></tr><%
			    }
			    %></tbody></table></div><%
			}
		}else{
			%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder">Contacts List: path not configured correctly.</div><%
		}
	} catch (Exception e) {
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder">Contacts List: path not configured correctly.</div><%
	}
}else{
	%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder">Contacts List: path not configured.</div><%
}%>
