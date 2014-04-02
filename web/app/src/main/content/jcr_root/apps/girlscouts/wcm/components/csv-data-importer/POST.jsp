<%@ page import="org.girlscouts.web.dataimport.DataImporterFactory,
org.girlscouts.web.dataimport.DataImporter,
org.girlscouts.web.dataimport.NodeMerger,
java.io.StringReader,
java.util.Queue,
java.util.LinkedList,
java.util.ArrayList,
java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>


<cq:include script="form.jsp"/>

<%
String action = request.getParameter("action");
if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}

if (action.equals("import")) {
	String content = request.getParameter("content");
	if (content == null || content.isEmpty()) {
	    %>File uploaded is empty. Abort.<%
	    return;
	}
	String destPath = request.getParameter("dest-path");
	if (destPath == null || destPath.isEmpty()) {
	    %>Destination path is empty. Abort<%
	    return;
	}
	String type = request.getParameter("type");
	if (type == null || type.isEmpty()) {
	    %>Type is empty. Abort<%
	    return;
	}
	
	DataImporterFactory factory = sling.getService(DataImporterFactory.class);
	DataImporter importer = factory.getDataImporter("csv", new StringReader(content), resourceResolver, type, destPath);
	String[] errorMsgs = importer.doDryRun();
	
	if (importer.isDryRunSuccess()) {
		%>
			<form action="#" method="post">
				<input type="hidden" name="action" value="save" />
				<input type="hidden" name="orig-path" value="<%= importer.getDryRunPath() %>" />
				<input type="hidden" name="dest-path" value="<%= destPath %>" />
				<input type="submit" value="Save" />
			</form>
		<% 
	} else {
	    %><p style="color:red;"><%
	    for (String msg : errorMsgs) {
			%><%= msg %><br /><%
	    }
	    %></p><%
	}
	
	String dryRunPath = importer.getDryRunPath();
	Queue<Node> queue = new LinkedList<Node>();
	Node tmpNode = resourceResolver.resolve(dryRunPath).adaptTo(Node.class);
	NodeIterator tmpIter = tmpNode.getNodes();
	while (tmpIter.hasNext()) {
		queue.offer(tmpIter.nextNode());
	}
	
	Node confNode = resourceResolver.resolve(type + "/fields").adaptTo(Node.class);
    List<String> keys = new ArrayList<String>();
    NodeIterator confIter = confNode.getNodes();
    while (confIter.hasNext()) {
		keys.add(confIter.nextNode().getName());
	}

	%><table border="1"><%
	while (!queue.isEmpty()) {
	    Node node = queue.poll();
	    if (node.hasNodes()) {
			NodeIterator iter = node.getNodes();
			while (iter.hasNext()) {
			    queue.offer(iter.nextNode());
			}
		} else {
			%><tr><%
			for (String key : keys) {
				%><td><%= key %></td><%
			}
			for (String key : keys) {
				%><td><%= node.getProperty(key).getString() %></td><%
			}
			%></tr><%
	    }
	}
	%></table><%
} else if (action.equals("save")) {
	String origPath = request.getParameter("orig-path");
	if (origPath == null || origPath.isEmpty()) {
		%>Origin Path is null.<%
		return;
	}
	String destPath = request.getParameter("dest-path");
	if (destPath == null || destPath.isEmpty()) {
		%>Destination Path is null.<%
		return;
	}
	
	NodeMerger merger = sling.getService(NodeMerger.class);
	String[] msgs = merger.merge(origPath, destPath, resourceResolver);
	
	if (msgs == null || msgs.length == 0) {
	    %> Successfully imported. <%
	} else {
	    %><p><%
	    for (String msg : msgs) {
			%><div style="color:red;"><%=msg%><br/></div><%
	    }
	    %></p><%
	}
}

%>