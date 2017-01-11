<%@page import="java.util.List,
               java.util.ArrayList,
               java.util.Collections,
               java.util.Comparator,
               java.io.IOException,
               javax.jcr.Property,
               javax.jcr.Node,
               javax.jcr.NodeIterator,
               org.apache.sling.api.resource.ResourceResolver" %>
<%!
	class SortItem {
    	Long id;
    	Node node;
    	
    	SortItem(Long id, Node node) {
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
	
	void sort(List<SortItem> list) {
		Collections.sort(list, new Comparator<SortItem>() {
			public int compare(SortItem s0, SortItem s1) {
			    return (int)(s0.id - s1.id);
			}
			public boolean equals(SortItem s0, SortItem s1) {
			    return s0.id == s1.id;
			}
		});
	}

	void printTitle(ContextInfo info, String key, String level) throws IOException {
	    JspWriter out = info.out;
    	out.println("<p><" + level + ">" + key + "</" + level + "></p>");
	}

	void printProperty(ContextInfo info, String key, String path, String level) throws ServletException {
	    JspWriter out = info.out;
    	try {
    	    printTitle(info, key, level);
    	    String value = "";
    	    Property prop = info.rr.resolve(info.basePath + "/" + path).adaptTo(Property.class);
    	    if (prop.isMultiple()) {
    	    	 Value[] values = prop.getValues();  
    	         for (int i = 0; i < values.length; i++) {
    	        	if (i != values.length - 1) {
    	        		value += values[i] + ", ";
    	        	} else {
    	        		value += values[i];
    	        	}
    	        	
    	         }
    	    } else {
    	    	value = prop.getString();
    	    }
    		out.println("<p>");
	    	out.println(value);
    		out.println("</p>");
    	} catch (ValueFormatException ve) {
    	   	throw new ServletException(ve); 
    	} catch (RepositoryException re) {
    	   	throw new ServletException(re); 
    	} catch (IOException ie) {
    	   	throw new ServletException(ie); 
    	} catch (NullPointerException npe) {
    	    // Do nothing. Value not found.
    	}
	}

	void printProperty(ContextInfo info, String key, String path) throws ServletException {
		printProperty(info, key, path, "h1");
	}
%>