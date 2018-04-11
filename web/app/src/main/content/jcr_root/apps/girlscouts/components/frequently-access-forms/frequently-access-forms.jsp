<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.day.cq.wcm.api.WCMMode, javax.jcr.Node,javax.jcr.NodeIterator,java.util.List,java.util.ArrayList" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%
Node links = currentNode.getNode("links");
String freqTitle = properties.get("freq-title","");
if (freqTitle == null && links == null) {
	%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
}else {%>
     <div class="row">
       <div class="small-24 large-24 medium-24 columns">
         <strong><%=freqTitle%></strong>
       </div>
     </div>
	 <div class="row">
	   <div class="small-24 large-24 medium-24 columns">
	    <div class="checkbox-grid">
			<%
			if(currentNode.hasNode("links")){				
				NodeIterator iter = links.getNodes();
			    while(iter.hasNext()){
			    	String href = "";
					Node linkNode = iter.nextNode();
		    		String pdfTitle = linkNode.hasProperty("pdfTitle") ? linkNode.getProperty("pdfTitle").getString() : "";
					String externalLink = linkNode.hasProperty("externalLink") ? linkNode.getProperty("externalLink").getString() : "";
					String path = linkNode.hasProperty("path") ? linkNode.getProperty("path").getString() : "";
					Boolean newWindow = linkNode.hasProperty("newWindow") ? linkNode.getProperty("newWindow").getBoolean() : Boolean.FALSE;
					String target = newWindow ? "target=\"_self\"" :"target=\"_blank\"";
					if(!externalLink.isEmpty()){
						href = externalLink;
					}
					if(!StringUtils.isBlank(path)){
						href = genLink(resourceResolver, path);						
					}
					%> <span><a href="<%= href %>" <%=target%>> <%= pdfTitle %></a></span><%
			    }
			}
			%>
	    </div>
	   </div>	 
	 </div> 
	 <div class="row">
	 	<div class="small-24 large-24 medium-24 columns">&nbsp;</div>
	 </div>
<%	
}
%>

