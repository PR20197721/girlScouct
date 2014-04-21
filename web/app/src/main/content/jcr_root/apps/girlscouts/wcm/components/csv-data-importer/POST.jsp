<%@ page import="org.girlscouts.web.dataimport.DataImporterFactory,
org.girlscouts.web.dataimport.DataImporter,
org.girlscouts.web.dataimport.NodeMerger,
java.io.StringReader,
java.util.Queue,
java.util.LinkedList,
java.util.ArrayList,
java.util.List,
java.util.Arrays,
java.util.Iterator" %>
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
                <input type="submit" value="Save"> to <%= destPath %> </input>
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
	Queue<Page> queue = new LinkedList<Page>();
	Node tmpNode = resourceResolver.resolve(dryRunPath).adaptTo(Node.class);
	NodeIterator tmpIter = tmpNode.getNodes();
	while (tmpIter.hasNext()) {
		queue.offer(resourceResolver.resolve(tmpIter.nextNode().getPath()).adaptTo(Page.class));
	}
	
	Node confNode = resourceResolver.resolve(type + "/fields").adaptTo(Node.class);
    List<String[]> keys = new ArrayList<String[]>();
    NodeIterator confIter = confNode.getNodes();
    while (confIter.hasNext()) {
		Node currentConfNode = confIter.nextNode();
		String key = null;
		if (currentConfNode.hasProperty("name")) {
		   	key = currentConfNode.getProperty("name").getString(); 
		} else {
		    key = currentConfNode.getName();
		}
		String thisType = null;
		if (currentConfNode.hasProperty("type")) {
		    thisType = currentConfNode.getProperty("type").getString();
		}
		keys.add(new String[]{key, thisType});
	}

	%><table border="1"><%
	%><tr><%
		for (String[] key : keys) {
			%><td><%= key[0] %></td><%
		}
	%></tr><%
	while (!queue.isEmpty()) {
	    Page thisPage = queue.poll();
	    if (thisPage.listChildren().hasNext()) {
			Iterator<Page> iter = thisPage .listChildren();
			while (iter.hasNext()) {
			    queue.offer(iter.next());
			}
		} else {
			%><tr><%
			for (String[] key : keys) {
			    if (!key[1].isEmpty() && key[1].equals("string[]")) {
					%><td><%= Arrays.toString(thisPage.getProperties().get(key[0], String[].class)) %></td><%
			    } else {
					%><td><%= thisPage.getProperties().get(key[0], "") %></td><%
			    }
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