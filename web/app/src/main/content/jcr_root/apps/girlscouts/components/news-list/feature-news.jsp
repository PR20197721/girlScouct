<%@ page import="com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List,
                   com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit,
                   java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList,
                   java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser"%>

<%@include file="/libs/foundation/global.jsp"%>
<%

  SearchResult results = (SearchResult)request.getAttribute("results");
  java.util.List <Hit> resultsHits = results.getHits();
  Format formatter = new SimpleDateFormat("dd MMM yyyy");
 
  Integer count =  Integer.parseInt(properties.get("count",String.class));
  if(count > resultsHits.size()){
	  count = resultsHits.size();
  }
  
%>
<img src="<%=properties.get("fileReference", String.class)%>" width="400" height="400"/>
<%
for(int i=0;i<count;i++)
      {
      Node content = resultsHits.get(i).getNode(); 
      if(content.hasNode("jcr:content/title")){%>
               <p><a href="<%=resultsHits.get(i).getPath()+".html" %>"><%=content.getNode("jcr:content/title").getProperty("jcr:title").getString()%></a>
                   <%  if(content.getNode("jcr:content").hasProperty("date"))
                        {
                           String contentDt = content.getNode("jcr:content").getProperty("date").getString();
                       %>
                   <br/> <%=formatter.format(DateParser.parseDate(contentDt)) %>
                   <%} %>
               </p> 
        <% }%>
      
      <% 
         if(content.hasNode("jcr:content/image"))
      {%> 
         <img src="<%=content.getNode("jcr:content/image").getProperty("fileReference").getString()%>" height="100" width="100" alt="<%if(content.getNode("jcr:content/image").hasProperty("alt")){%><%=content.getNode("jcr:content/image").getProperty("fileReference").getValue()%><%}%>"/>
      <% }%>
      <%if(content.hasNode("jcr:content/text")){
    %>    
          <p><%=content.getNode("jcr:content/text").getProperty("text").getString() %></p>
      <%}
      
      }

%>

<div>
    <%
      if(properties.containsKey("urltolink")){
    %>    
        <a href="<%=properties.get("urltolink")%>.html"><%=properties.get("linktext")%></a>
          
     <% }
    
    %>
  
  </div>

   
   
   



