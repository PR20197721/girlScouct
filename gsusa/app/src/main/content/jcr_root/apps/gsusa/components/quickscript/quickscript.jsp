<%@page import="java.util.*,
		java.util.regex.*,
		com.day.cq.wcm.api.WCMMode,
				javax.jcr.NodeIterator,
				java.text.SimpleDateFormat,
				java.text.DateFormat,
				java.util.Locale,
				java.util.Date" %>
<%@include file="/libs/foundation/global.jsp" %>
<%!
	// This method recursively returns all nodes below a particular resource
	java.util.List<Resource> getChildren(Resource r) {
		java.util.List<Resource> returnChildren = new java.util.ArrayList<Resource>();
		for (org.apache.sling.api.resource.Resource child: r.getChildren()) {
			if (child != null) {
				returnChildren.addAll(getChildren(child));
			}
			returnChildren.add(child);
		}
		return returnChildren;
	}

	// This method deletes all renditions with a particular pattern (e.g. "cq5dam.npd" or "resized.web")
	String deleteAllNPDRenditions(org.apache.sling.api.resource.ResourceResolver rr, String pathPattern) throws Exception {
		StringBuffer ret = new StringBuffer();
		Resource dam = rr.resolve("/content/dam/girlscouts-gsusa/");
		// delete existing npd renditions
		Session sess = rr.adaptTo(Session.class);
		for (Resource child: getChildren(dam)) {
			if (child.getResourceType().equals("nt:file")) {
				if (child.getPath().indexOf(pathPattern) != -1) {
					ret.append("Deleting " + child.getPath() + " <br>");
					sess.removeItem(child.getPath());
				}
			}
		}
		sess.save();
		return ret.toString();
	}

	// This method simply touches the modification time of the jcr:content node of all original resources.  This should trigger the renditions to be re-generated.
	String generateNPDRenditions(org.apache.sling.api.resource.ResourceResolver rr) throws Exception {
		StringBuffer ret = new StringBuffer();
		Resource dam = rr.resolve("/content/dam/girlscouts-gsusa/");
		PageManager pageManager = rr.adaptTo(PageManager.class);
		// List of CQ adapters: http://docs.adobe.com/docs/en/cq/5-6-1/developing/sling-adapters.html
		Session sess = rr.adaptTo(Session.class);
		for (Resource child: getChildren(dam)) {
			if (child.getResourceType().equals("dam:Asset")) {
				Pattern pattern = Pattern.compile(".*\\.(jpg|jpeg|png)");
				Matcher matcher = pattern.matcher(child.getPath());
				if (matcher.matches()) {
					Resource origin = rr.resolve(child.getPath() + "/" + Node.JCR_CONTENT + "/renditions/original/" + Node.JCR_CONTENT);
					Node original = sess.getNode(origin.getPath());
					ret.append("Touching " + origin.getPath());
					original.setProperty("jcr:lastModified", Calendar.getInstance());
				}
			}
		}
		sess.save();
		return ret.toString();
	}
%>
<%
	StringBuffer resultString = new StringBuffer("<p>------DO NO USE THIS COMPONENT UNLESS YOU KNOW WHAT IT IS------</p>");

// Never check into git uncommented or these script will execute when the Quickscript component is dragged from the sidekick
//	resultString.append(deleteAllNPDRenditions(resourceResolver,"cq5dam.web.120.80"));
//	resultString.append(deleteAllNPDRenditions(resourceResolver, "cq5dam.web.240.240"));
//	resultString.append(deleteAllNPDRenditions(resourceResolver, "cq5dam.web.400.400"));
//	resultString.append(deleteAllNPDRenditions(resourceResolver, "cq5dam.web.520.520"));
//	resultString.append(deleteAllNPDRenditions(resourceResolver, "cq5dam.npd"));
//	resultString.append(deleteAllNPDRenditions(resourceResolver, "resized.web"));

//	resultString.append(deleteAllNPDRenditions(resourceResolver, "cq5dam.npd.top@2x.jpeg"));
//	resultString.append(deleteAllNPDRenditions(resourceResolver, "cq5dam.npd.top.jpeg"));

//	resultString.append(generateNPDRenditions (resourceResolver));

%>
	<hr>
	<H1>QUICKSCRIPT</H1>
<%
	try {
%>
	<%= resultString.toString() %>
<%
	} catch (Exception e) {
%>
	Exception: <%=String.valueOf(e) %>
<%
	}
%>
	<hr>
