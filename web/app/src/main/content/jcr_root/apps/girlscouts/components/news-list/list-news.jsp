<%@ page import="com.day.cq.wcm.api.WCMMode,com.day.cq.wcm.foundation.List,java.text.DateFormat,
                   com.day.cq.wcm.api.components.DropTarget,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,com.day.cq.search.result.Hit,
                   java.util.Map,java.util.HashMap,com.day.cq.search.QueryBuilder,com.day.cq.search.PredicateGroup,java.util.Arrays,java.util.HashSet,java.util.ArrayList,
                   java.util.Iterator,java.text.SimpleDateFormat,java.util.Date, java.text.Format,com.day.cq.dam.commons.util.DateParser"%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
  SearchResult results = (SearchResult)request.getAttribute("results");
  java.util.List <Hit> resultsHits = results.getHits();
  Format formatter = new SimpleDateFormat("dd MMM yyyy");
  List  list = (List)request.getAttribute("list"); 
  String start = (String) request.getAttribute("start");
  String title = "";
  String description="";
  String text="";
  String imgPath="";
  DateFormat inFormatter = new SimpleDateFormat("MM/dd/yy");
  String date="";
  String external_url = "";
  for(Hit hit:resultsHits)
      {
	  Node content = hit.getNode(); 
	  Node contentNode = content.getNode("jcr:content");
	  title = contentNode.hasProperty("jcr:title")?contentNode.getProperty("jcr:title").getString():"";
	  external_url = contentNode.hasProperty("external-url")?contentNode.getProperty("external-url").getString():"";
	  if(!external_url.isEmpty()){
		%>  
		  <p><a href="<%=external_url%>"><%=title%></a></p>
	   <%}else
	   {
	 	
      	 date = contentNode.hasProperty("date")?contentNode.getProperty("date").getString():"";
         if(!date.isEmpty())
         {
    	  String dateString = contentNode.getProperty("date").getString();
          Date newsDate = inFormatter.parse(dateString);
          date = formatter.format(newsDate);
          }
      	  text = contentNode.hasProperty("middle/par/text/text")? contentNode.getProperty("middle/par/text/text").getString():"";
      	  imgPath = contentNode.hasProperty("middle/par/text/image/fileReference")?contentNode.getProperty("middle/par/text/image/fileReference").getString():"";
   		 %>
      	   <p><a href="<%=hit.getPath()+".html" %>"><%=title%></a>
             <br/>
               <%if(!date.isEmpty()){ %> 
               <%=date%>
               <%} %>  
	        </p> 
         <% 
	         if(!imgPath.isEmpty())
           {%> 
               <%= displayRendition(resourceResolver, imgPath, "cq5dam.web.120.80") %>
           <% }%>
	       <%if(!text.isEmpty()){
	        %>	  
		      <p><%=text %></p>
	        <%}
	  
            }
      }

%>
    
<div id="pagination">
     <% 
     
        long totalResults = results.getTotalMatches();
         if(list.size()>0){
        	 totalResults += list.size();
         }
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
         
         
         
         if(list.size()>0){
        	   Iterator<Page> itemslist = list.getPages();
        	   while(itemslist.hasNext()){
        		   Page pg = itemslist.next();
        		   if(pg.getProperties().containsKey("isFeature")){
        			    Node node = pg.getContentResource().adaptTo(Node.class);
        			    node.setProperty("isFeature", false);
        			    node.save();
        	 }
         }
         } 
     %>



</div>
