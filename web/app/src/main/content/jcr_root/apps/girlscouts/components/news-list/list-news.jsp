<%@ page import="com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List,
                   com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit,
                   java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList,
                   java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser"%>

<%@include file="/libs/foundation/global.jsp"%>
<%
  List list = (List)request.getAttribute("list"); 

  HashSet<String>set = new HashSet<String>(); 
  
   
  String offset = request.getParameter("offset");
  String path = currentPage.getAbsoluteParent(2).getPath();
  
  
  Map <String, String> queryMap = new HashMap<String, String>();
  queryMap.put("type", "cq:Page");
  queryMap.put("path", path+"/news");
  queryMap.put("boolproperty","jcr:content/isFeature");
  queryMap.put("boolproperty.value","false");
  queryMap.put("orderby","@jcr:content/date");
  queryMap.put("orderby.sort","desc");
  

  String start = request.getParameter("start");
  
  if(start!=null && !start.isEmpty()){
	 
	  queryMap.put("p.limit", "10");
	  
  }
  else{
	 start = Integer.toString(10 - list.size()); 
	 //queryMap.put("p.limit", 10));
	  
  }
  if(offset!=null && !offset.isEmpty()){
	  queryMap.put("p.offset",offset);
	  
	    
  }else{
	  queryMap.put("p.offset", "0");
  }
  
  QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
  Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), slingRequest.getResourceResolver().adaptTo(Session.class));
  
  if(start!=null && !start.isEmpty()){
	  query.setStart(Long.parseLong(start));
  }
 
  
  SearchResult results = query.getResult();
 
  
  System.out.println("How many matches-------------->" +results.getTotalMatches());
  java.util.List <Hit> resultsHits = results.getHits();
  
  System.out.println("How many matches-------------->" +results.getTotalMatches() +"HIts " +results.getHits().size());
  
  
  
  Format formatter = new SimpleDateFormat("dd MMM yyyy");
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
