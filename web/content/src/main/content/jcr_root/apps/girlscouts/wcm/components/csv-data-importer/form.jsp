<%@ page import="javax.jcr.Node,
javax.jcr.NodeIterator,
java.util.List,
java.util.ArrayList" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
Node confNode = resourceResolver.resolve("/etc/data-import/girlscouts/csv").adaptTo(Node.class);
NodeIterator iter = confNode.getNodes();
%>

<form action="#" method="post" enctype="multipart/form-data">
	<input type="hidden" name="action" value="import"/>
	<div>Upload File<input type="file" name="file" /></div>
	<div>Import to path:<input type="text" name="dest-dir" /></div>
	<div>Type: 
	<% 
		while (iter.hasNext()) {
    		Node typeNode = iter.nextNode();
    %>
		<input type="radio" name="type" value="<%= typeNode.getPath() %>" checked><%=typeNode.getName()%></input>	 
    <% } %>
	</div>
</form>