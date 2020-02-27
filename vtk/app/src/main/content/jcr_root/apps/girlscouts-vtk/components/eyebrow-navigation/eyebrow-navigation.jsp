<%@ page import="com.day.cq.wcm.api.WCMMode, javax.jcr.Node,javax.jcr.NodeIterator,java.util.List,java.util.ArrayList,
    com.day.cq.commons.Externalizer,
                 org.slf4j.Logger,
                 org.slf4j.LoggerFactory,
                 java.text.SimpleDateFormat,
                 java.util.*, org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.resource.Resource" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%!
public String generateLink(Page currentPage,  ResourceResolver rr, String path){
        Logger log = LoggerFactory.getLogger(this.getClass().getName());
        String url = path;
        if(url.startsWith("/content/")){
            try {
                final Externalizer externalizer = rr.adaptTo(Externalizer.class);
                String siteRootPath = currentPage.getAbsoluteParent(1).getPath();

                url = externalizer.externalLink(rr,siteRootPath,reqProtocol,  path);
                if (!url.endsWith(".html")){
                    url.concat(".html");
                }
                if("https".equals(reqProtocol)){
                    url.replace("http://","https://");
                }
            }catch(Exception e){

            }
        }
        return url;
    }
%>
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
