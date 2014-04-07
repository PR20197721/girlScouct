

<%@ page session="false" import="com.day.cq.wcm.api.Page" import="org.apache.commons.lang3.StringEscapeUtils" import="com.day.cq.dam.commons.util.DateParser" import="java.text.SimpleDateFormat" import="java.text.Format"%>
<%@include file="/libs/foundation/global.jsp"%>
<%
    
    
    Page listItem = (Page)request.getAttribute("listitem");
    
    Node node = listItem.getContentResource().adaptTo(Node.class);
    node.setProperty("isFeature", true);
    node.save();
    
    String alt = "";
    String width="";
    String height="";
    String src="";
    String paragraph="";
    String date = node.getProperty("date").getString();
    if(node.hasNode("text")){
         paragraph = node.getNode("text").getProperty("text").getString();
    }
    if(node.hasNode("image")){
    	if(node.getNode("image").hasProperty("alt"))
    	  alt = node.getNode("image").getProperty("alt").getString();
    	if(node.getNode("image").hasProperty("width"))
    	   width = node.getNode("image").getProperty("width").getString();
    	if(node.getNode("image").hasProperty("height"))
    		height = node.getNode("image").getProperty("height").getString();
    	if(node.getNode("image").hasProperty("fileReference"))
            src = node.getNode("image").getProperty("fileReference").getString();
    	
    }
   
    Format formatter = new SimpleDateFormat("dd MMM yyyy");
    
    
  
%><li>
    <p>
       Features <a href="<%= listItem.getPath() %>.html" title="<%= StringEscapeUtils.escapeHtml4(node.getNode("title").getProperty("jcr:title").getString()) %>"
           onclick="CQ_Analytics.record({event: 'listItemClicked', values: { listItemPath: '<%= listItem.getPath() %>' }, collect:  false, options: { obj: this }, componentPath: '<%=resource.getResourceType()%>'})"><%= StringEscapeUtils.escapeHtml4(node.getNode("title").getProperty("jcr:title").getString()) %></a>
         <br/> <%=formatter.format(DateParser.parseDate(date)) %>
    </p>
    <img src="<%=src%>" alt="<%=alt%>" height="100" width="100"/>
    <%=paragraph %>
</li>