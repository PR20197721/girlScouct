<%@page import="java.util.List,
               java.util.ArrayList,
               javax.jcr.Node,
               javax.jcr.NodeIterator" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts-vtk/components/vtk-data/global.jsp" %>
<head>
	<title>Year Plan Preview</title>
</head>
<body>
<%
	ContextInfo info = new ContextInfo(resourceResolver, out, currentNode.getPath());
	printProperty(info, "ID", "id");
	printProperty(info, "Level", "level");
	printProperty(info, "Name", "name");
	printProperty(info, "Description", "desc");
	
	List<SortItem> meetings = new ArrayList<SortItem>();
	NodeIterator iter = currentNode.getNode("meetings").getNodes();
	while (iter.hasNext()) {
	    Node node = iter.nextNode();
	    String id = node.getProperty("./id").getString();
	    SortItem meeting = new SortItem(Long.parseLong(id), node);
	    meetings.add(meeting);
	}
	sort(meetings);
	
	printTitle(info, "Agenda Items", "h1");
	int meetingCount = 0;
	for (SortItem meeting : meetings) {
	    meetingCount++;
	    ContextInfo info1 = new ContextInfo(resourceResolver, out, meeting.node.getPath());
	    printTitle(info1, "Meeting #" + Integer.toString(meetingCount), "h2");
	    String refId = meeting.node.getProperty("refId").getString();
	    info1 = new ContextInfo(resourceResolver, out, refId);
	    printProperty(info1, "Name", "name", "h3");
		printProperty(info1, "Short Description", "meetingInfo/meeting short description/str", "h3");
	}
%>
</body>