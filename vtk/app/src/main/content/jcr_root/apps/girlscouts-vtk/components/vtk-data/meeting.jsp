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
        printProperty(info1, "Subtitle", "subtitle", "h3");
	    /*
	    printProperty(info1, "Description", "activityDescription", "h3");
	    printProperty(info1, "Outdoor?", "isOutdoorAvailable", "h3");
	    printProperty(info1, "Outdoor Description", "activityDescription_outdoor", "h3");
	    printProperty(info1, "Global?", "isGlobalAvailable", "h3");
	    printProperty(info1, "Global Description", "activityDescription_global", "h3");
	    */
        try {
            List<SortItem> additionalActivities = new ArrayList<SortItem>();
            NodeIterator iter2 = activity.node.getNode("multiactivities").getNodes();
            while (iter2.hasNext()) {
                Node additionalActivity = iter2.nextNode();
                Long id = additionalActivity.getProperty("./activityNumber").getLong();
                SortItem additionalActivityItem = new SortItem(id, additionalActivity);
                additionalActivities.add(additionalActivityItem);
            }
            sort(additionalActivities);
            printTitle(info, "Additional Agenda Items", "h3");
            int additionalActivityCount = 0;
            for (SortItem additionalActivity : additionalActivities) {
                additionalActivityCount++;
                ContextInfo additionalActivityInfo = new ContextInfo(resourceResolver, out, additionalActivity.node.getPath());
                printTitle(additionalActivityInfo, "Additional Agenda Item #" + Integer.toString(additionalActivityCount), "h4");
                printProperty(additionalActivityInfo, "Name", "name", "h5");
                printProperty(additionalActivityInfo, "Duration", "duration", "h5");
                printProperty(additionalActivityInfo, "Description", "activityDescription", "h5");
                printProperty(additionalActivityInfo, "Outdoor?", "isOutdoorAvailable", "h5");
                printProperty(additionalActivityInfo, "Global?", "isGlobalAvailable", "h5");
                printProperty(additionalActivityInfo, "Materials", "materials", "h5");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
%>
</body>