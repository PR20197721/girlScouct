<%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="org.apache.commons.lang3.StringEscapeUtils,
        com.day.cq.commons.Doctype,
        com.day.cq.commons.DiffInfo,
        javax.jcr.NodeIterator,
        javax.jcr.Node,
        com.day.cq.commons.DiffService,
        org.slf4j.Logger,
        org.slf4j.LoggerFactory,
        org.apache.sling.api.resource.ResourceUtil" %>
        <%@page import="com.day.cq.wcm.api.WCMMode" %>
        
     
<%
  Logger logger= LoggerFactory.getLogger(this.getClass().getName());
  String title = properties.get("title", String.class);

  if(title == null || title.equals(""))
    {
       title = properties.get("jcr:title", String.class);
    }
  if (title == null || title.equals("")) 
    {
       title = currentPage.getTitle();
    }
    Node pageNode = currentPage.getContentResource().adaptTo(Node.class);
      boolean showButton = false;
      try{
         if(pageNode.hasProperty("cssPrint"))
            showButton = pageNode.getProperty("cssPrint").getBoolean();
      }catch(Exception e){
         logger.error("Error finding cssPrint property: ",e);
      }
      String buttonPath = currentPage.getPath() + "/print-css";
    NodeIterator nodeItrFirstEl = currentPage.adaptTo(Node.class).getNode("jcr:content/content/middle/par").getNodes();
    NodeIterator nodeItr = currentPage.adaptTo(Node.class).getNode("jcr:content/content/middle/par").getNodes();
    Node currNode = currentPage.adaptTo(Node.class).getNode("jcr:content");
    try{
        currNode = nodeItrFirstEl.nextNode();
    }catch(Exception e){
        if(currentPage.adaptTo(Node.class).getNode("jcr:content/content/hero/par").getNodes().nextNode() != null){
            currNode = currentPage.adaptTo(Node.class).getNode("jcr:content/content/hero/par").getNodes().nextNode();
        }
        logger.error("Error occurred: ",e);
    }
    if(showButton){
        if(currNode.hasProperty("sling:resourceType") && !"girlscouts/components/image".equals(currNode.getProperty("sling:resourceType").getString())){
            try{
                while(nodeItr.hasNext()){
                    Node node = nodeItr.nextNode();
                    if(node.hasProperty("sling:resourceType") && "girlscouts/components/title".equals(node.getProperty("sling:resourceType").getString())){
                    %>
                        <cq:include path="<%= buttonPath %>" resourceType="girlscouts/components/print-css" />
                  <% }
    

                }
             }catch(Exception e){
                logger.error("Error occurred: ",e);
             }
     }

  if (title==null)
  {
  %><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><% }else
  {
	  String type = properties.get("type", String.class);
    // Get Type -
      if(type==null || type.equals("")){%>
         <h1><%=title%></h1>
     <% } else{
        //adding h1 h2 h3 h4 etc to title
        String styledTitle = "<"+type+">"+title+"</"+type+">";
       %>
	      <%=styledTitle %>
	     
   <%}%>
  <%}
%>
