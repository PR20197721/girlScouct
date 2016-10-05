<%@page import="java.util.List,
               java.util.ArrayList,
               javax.jcr.Node,
               javax.jcr.NodeIterator" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts-vtk/components/vtk-data/global.jsp" %>
<head>
	<title>Meeting Preview</title>
</head>
<body>
<%
	ContextInfo info = new ContextInfo(resourceResolver, out, currentNode.getPath());
	printProperty(info, "ID", "id");
	printProperty(info, "Level", "level");
	printProperty(info, "Name", "name");
	printProperty(info, "Meeting Short Description", "meetingInfo/meeting short description/str");
	printProperty(info, "Blurb", "blurb");
	printProperty(info, "Meeting Plan Type", "meetingPlanType");
	printProperty(info, "Category", "cat");
	printProperty(info, "Category (for filter)", "catTags");
	printProperty(info, "Aid Tags", "aidTags");
	printProperty(info, "Resources", "resources");
	printProperty(info, "Overview", "meetingInfo/overview/str");
	printProperty(info, "Detailed Activity Plan", "meetingInfo/overview/str");
	printProperty(info, "Materials", "meetingInfo/materials/str");
	printProperty(info, "Email Invite", "meetingInfo/email invite/str");
	printProperty(info, "Email Summary", "meetingInfo/email summary/str");
	
	List<SortItem> activities = new ArrayList<SortItem>();
	NodeIterator iter = currentNode.getNode("activities").getNodes();
	while (iter.hasNext()) {
	    Node node = iter.nextNode();
	    Long id = node.getProperty("./activityNumber").getLong();
	    SortItem activity = new SortItem(id, node);
	    activities.add(activity);
	}
	sort(activities);
	
	printTitle(info, "Agenda Items", "h1");
	int activityCount = 0;
	for (SortItem activity : activities) {
	    activityCount++;
	    ContextInfo info1 = new ContextInfo(resourceResolver, out, activity.node.getPath());
	    printTitle(info1, "Agenda Item #" + Integer.toString(activityCount), "h2");
	    printProperty(info1, "Name", "name", "h3");
	    printProperty(info1, "Duration", "duration", "h3");
	    printProperty(info1, "Description", "activityDescription", "h3");
	    printProperty(info1, "Outdoor?", "isOutdoorAvailable", "h3");
	    printProperty(info1, "Outdoor Description", "activityDescription_outdoor", "h3");
	}
%>
</body>
