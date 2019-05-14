<%@ page import="com.day.cq.commons.Doctype,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.foundation.Image, com.day.cq.wcm.api.WCMMode, javax.jcr.NodeIterator,javax.jcr.Node,com.day.cq.wcm.foundation.Placeholder" %><%
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
	String html = properties.get("html", "");
	Node pageNode = currentPage.getContentResource().adaptTo(Node.class);
    boolean showButton;
     try{
        showButton = pageNode.getProperty("cssPrint").getBoolean();
     }catch(Exception e){
        showButton = false;
     }
    String buttonPath = currentPage.getPath() + "/print-css";
    if (html.isEmpty()) {
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
    } else {
        // Mike Z. We need this extra div because of a CQ bug on iframe.
        // It is trying to correct iframe height but in a wrong way.
        // Widget.js 1449975 
        // //in case of an iframe element height is not correct
        // var iframe = this.element.first("iframe");
        // if (iframe) {
        //     this.element.setHeight(this.element.getHeight() + iframe.getHeight());
        // }
        %><div class="video-container"><%= html %></div><%
    }

    NodeIterator nodeItr = currentPage.adaptTo(Node.class).getNode("jcr:content/content/middle/par").getNodes();
    Node currNode = nodeItr.nextNode();
    if(showButton){
    if("girlscouts/components/video".equals(currNode.getProperty("sling:resourceType").getString()) && currentNode.getPath().equals(currNode.getPath())){
    %>
        <cq:include path="<%= buttonPath %>" resourceType="girlscouts/components/print-css" />
   <% }
   }

%>