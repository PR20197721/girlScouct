

<%@ page session="false"%>

<%@page import="java.util.Date,java.text.DateFormat,com.day.cq.wcm.api.Page,org.apache.commons.lang3.StringEscapeUtils,com.day.cq.dam.commons.util.DateParser, java.text.SimpleDateFormat,java.text.Format"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
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
    String title = "";
    String date = "";
    String imgTitle="";
    String description= "";
    
    if(node.hasProperty("jcr:title")){
         title = node.getProperty("jcr:title").getString();
    }
    
    if(node.hasProperty("description")){
    	description = node.getProperty("description").getString();
    	
    }
    
    
    src = node.hasProperty("middle/par/text/image/fileReference") ? node.getProperty("middle/par/text/image/fileReference").getString() : "";
    alt = node.hasProperty("middle/par/text/image/alt") ? node.getProperty("middle/par/text/image/alt").getString():"";
    imgTitle = node.hasProperty("middle/par/text/image/title") ? node.getProperty("middle/par/text/image/title").getString():"";
    
    
    if(node.hasProperty("middle/par/text/image/width")){
    	width = node.getProperty("middle/par/text/image/width").getString();
    	
    }
    if(node.hasProperty("middle/par/text/image/height")){
        height = node.getProperty("middle/par/text/image/height").getString();
        
    }
    
    DateFormat inFormatter = new SimpleDateFormat("MM/dd/yy");
    Format formatter = new SimpleDateFormat("dd MMM yyyy");
   
    if(node.hasProperty("date")){
        
    	String dateString = node.getProperty("date").getString();
        Date newsDate = inFormatter.parse(dateString);
        date = formatter.format(newsDate);
    }
    
    
  
%><li>
    <p> Features</p>
    <p>
        <a href="<%= listItem.getPath() %>.html"><%= title %></a>
         <br/> <%=date %>
    </p>
    <%if(!src.isEmpty()){ %>
          <%= displayRendition(resourceResolver, src, "cq5dam.web.120.80") %>
    <%} %>
    <p>
    <%if(!description.isEmpty()){ %>
       <%=description %>
    <%}%>
   </p> 
</li>