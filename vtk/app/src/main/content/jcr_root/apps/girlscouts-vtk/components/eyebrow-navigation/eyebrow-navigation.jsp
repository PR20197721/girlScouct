<%@ page import="com.day.cq.wcm.api.WCMMode, javax.jcr.Node,javax.jcr.NodeIterator,java.util.List,java.util.ArrayList,
                 java.text.SimpleDateFormat,
                 java.util.*, org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.resource.Resource" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>


<%
List<String> linksList = new ArrayList<String>();
if(currentNode.hasNode("links")){
	Node links = currentNode.getNode("links");
    NodeIterator iter = links.getNodes();
    while(iter.hasNext()){
		Node linkNode = iter.nextNode();
    	if(linkNode.hasProperty("linkTitle") && linkNode.hasProperty("url")){
			String title = linkNode.getProperty("linkTitle").getString();
        	String url = linkNode.getProperty("url").getString();
            String newWindow = "";
            if(linkNode.hasProperty("newWindow")){
                newWindow= linkNode.getProperty("newWindow").getString();
            }
            String suppress = "";
            if(linkNode.hasProperty("suppress")){
                suppress = linkNode.getProperty("suppress").getString();
            }
			String clazz = "";
            if(linkNode.hasProperty("class")){
                clazz = linkNode.getProperty("class").getString();
            }

        	linksList.add(title + "|||" + url + "|||" + clazz + "|||" + newWindow + "|||" + suppress);
    	}

	}
}
String[] links = linksList.toArray(new String[0]);
request.setAttribute("links", links);
if (links == null || links.length == 0) {
	%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
} else if (links != null){
    %><div class="eyebrow-container">
		    <ul class="inline-list eyebrow-fontsize">
		      <cq:include script="main.jsp"/>
		   </ul>
	   </div>
   <%
}
%>
