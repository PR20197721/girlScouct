<%@page import="java.util.List,
               java.util.ArrayList,
               java.util.Collections,
               java.util.Comparator,
               java.io.IOException,
               javax.jcr.Property,
               javax.jcr.Node,
               javax.jcr.NodeIterator,
               org.apache.sling.api.resource.ResourceResolver" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
	ContextInfo info = new ContextInfo(resourceResolver, out, currentNode.getPath());
	printProperty(info, "ID", "id");
	printProperty(info, "Level", "level");
	printProperty(info, "Name", "name");
	printProperty(info, "Meeting Short Description", "meetingInfo/meeting short description/str");
	printProperty(info, "Blurb", "blurb");
	printProperty(info, "Category", "cat");
	printProperty(info, "Aid Tags", "aidTags");
	printProperty(info, "Resources", "resources");
	printProperty(info, "Overview", "meetingInfo/overview/str");
	printProperty(info, "Detailed Activity Plan", "meetingInfo/overview/str");
	printProperty(info, "Materials", "meetingInfo/materials/str");
	printProperty(info, "Email Invite", "meetingInfo/overview/str");
	printProperty(info, "Email Summary", "meetingInfo/overview/str");
	
	List<Activity> activities = new ArrayList<Activity>();
	NodeIterator iter = currentNode.getNode("activities").getNodes();
	while (iter.hasNext()) {
	    Node node = iter.nextNode();
	    Long id = node.getProperty("./activityNumber").getLong();
	    Activity activity = new Activity(id, node);
	    activities.add(activity);
	}
	Collections.sort(activities, new Comparator<Activity>() {
		public int compare(Activity a0, Activity a1) {
		    return (int)(a0.id - a1.id);
		}
		public boolean equals(Activity a0, Activity a1) {
		    return a0.id == a1.id;
		}
	});
	
	int activityCount = 0;
	for (Activity activity : activities) {
	    activityCount++;
	    ContextInfo info1 = new ContextInfo(resourceResolver, out, activity.node.getPath());
	    printTitle(info1, "Activity " + Integer.toString(activityCount));
	    %><p><%= activity.node.getPath() %></p><%
	    printProperty(info1, "Name", "name");
	    printProperty(info1, "Duration", "duration");
	    printProperty(info1, "Description", "activityDescription");
	}
%>
<%!
	class Activity {
    	Long id;
    	Node node;
    	
    	Activity(Long id, Node node) {
    	    this.id = id;
    	    this.node = node;
    	}
	}
	class ContextInfo {
		ResourceResolver rr;
		String basePath;
		JspWriter out;
		
		public ContextInfo(ResourceResolver rr, JspWriter out, String basePath) {
		    this.rr = rr;
		    this.out = out;
		    this.basePath = basePath;
		}
	}

	void printTitle(ContextInfo info, String key) throws IOException {
	    JspWriter out = info.out;
    	out.println("<p><h1>" + key + "</h1></p>");
	}

	void printProperty(ContextInfo info, String key, String path) throws ServletException {
	    JspWriter out = info.out;
    	try {
    	    printTitle(info, key);
			String value = info.rr.resolve(info.basePath + "/" + path).adaptTo(Property.class).getString();
    		out.println("<p>");
	    	out.println(value);
    		out.println("</p>");
    	} catch (ValueFormatException ve) {
    	   	throw new ServletException(ve); 
    	} catch (RepositoryException re) {
    	   	throw new ServletException(re); 
    	} catch (IOException ie) {
    	   	throw new ServletException(ie); 
    	}
	}
%>