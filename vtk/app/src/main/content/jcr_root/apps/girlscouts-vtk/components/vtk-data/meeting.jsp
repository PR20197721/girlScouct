<%@page import="java.util.List,
               java.util.ArrayList,
               java.io.IOException,
               javax.jcr.Property,
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
%>
<%!
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