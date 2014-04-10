<%@ page import="javax.jcr.Node,
javax.jcr.NodeIterator,
java.util.List,
java.util.ArrayList" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
Node confNode = resourceResolver.resolve("/etc/data-import/girlscouts/csv").adaptTo(Node.class);
NodeIterator iter = confNode.getNodes();

String destPathString = request.getParameter("dest-path");
String type = request.getParameter("type");
%>

<form action="#" method="post" enctype="multipart/form-data">
	<input type="hidden" name="action" value="import"/>
	<div>Upload File<input type="file" name="content" /></div>
	<div>Import to path:<input type="text" name="dest-path" 
		<% if (destPathString != null) { %> value="<%= destPathString %>"<% } %>
	/></div>
	<div>Type: 
	<% 
		while (iter.hasNext()) {
    		Node typeNode = iter.nextNode();
    %>
		<input type="radio" name="type" value="<%= typeNode.getPath() %>"
		<% if (typeNode.getPath().equals(type)) { %> checked<% } %>
	><%=typeNode.getName()%></input>	 
    <% } %>
		<div><input type="submit" /></div>
	</div>
</form>