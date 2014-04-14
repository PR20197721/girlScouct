<%@ page import="com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List,
                   com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit,
                   java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList,
                   java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser"%>

<%@include file="/libs/foundation/global.jsp"%>
<%
  SearchResult results = (SearchResult)request.getAttribute("results");
  java.util.List <Hit> resultsHits = results.getHits();
  Format formatter = new SimpleDateFormat("dd MMM yyyy");
  List list = (List)request.getAttribute("list"); 
  String start = (String) request.getAttribute("start");
  
  for(Hit hit:resultsHits)
      {
	  Node content = hit.getNode(); 
	  if(content.hasNode("jcr:content/title")){%>
	           <p><a href="<%=hit.getPath()+".html" %>"><%=content.getNode("jcr:content/title").getProperty("jcr:title").getString()%></a>
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
    
<div id="pagination">
     <% 
         long totalResults = results.getTotalMatches() + list.size();
         long numberofPage = totalResults/10;
         if(numberofPage>1)
         {
        	   String pc="";
        	   pc =  request.getParameter("pc");
         	   int cPage = 1;
         	   if(null==pc){
        	 
         		   }
         	   else
         	   {
         		   cPage = Integer.parseInt(pc); 
         	   }
         	    
         	   if(totalResults%10>0)
         	   {
         		  numberofPage++;
         		}
         	   int st = Integer.parseInt(start);
         	   int counter = 0;
         	   for(int i=1;i<numberofPage+1;i++){
         		  long startCount = i*10;
         		  long offst = counter*10;
         		  long sta = startCount-list.size() -10 +1;
         		  if(cPage==i){
        	    
               	 %>
            		 <%=i %>
        	    <%}else
        	    	  {  
        	    	   	 if(i==1)
        	    	   	 {
        	      %>
                            <a href="<%=currentPage.getPath()%>.html?&pc=<%=i%>"><%=i %></a>
        	           <% }else{
        		            %> <a href="<%=currentPage.getPath()%>.html?offset=<%=offst%>&start=<%=sta%>&pc=<%=i%>"><%=i %></a>
                    	<% }
        	    	   }
         		  counter++;
         		   }
         	    }  
         
         Iterator<Page> itemslist = list.getPages();
         while(itemslist.hasNext()){
        	 Page pg = itemslist.next();
        	 if(pg.getProperties().containsKey("isFeature")){
        		 Node node = pg.getContentResource().adaptTo(Node.class);
        		 node.setProperty("isFeature", false);
        		 node.save();
        	 }
         }
     %>



</div>
