<%@ page import="
                 com.day.cq.tagging.TagManager,
                 java.util.Locale,java.util.Map,java.util.Iterator,
                 java.util.ResourceBundle,
                 com.day.cq.i18n.I18n,com.day.cq.search.QueryBuilder,org.girlscouts.web.search.*,org.girlscouts.web.search.helper.SearchResults" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<style type="text/css">
<!--
 
#colleft {float:left; width:300px; }
#collright {float:left;  width:300px;   }
#container {width:600px; clear:both;background: url(backgd.gif) repeat-y 0% 0%; border:thin #CCCCCC solid}


 
-->
</style>


<%
  Search search = new Search();
  QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
  pageContext.setAttribute("search", search);
  TagManager tm = resourceResolver.adaptTo(TagManager.class);
  System.out.println("this is the title" +tm.resolve("marketing:interest/product").getTitle());
  System.out.println("this is the title" +tm.resolve("marketing:interest/product").getParent().getTitle());
%>

<%
    String q = request.getParameter("q");
    
    System.out.println("Request Parameter" +q);
    if(q!=null)
    {
    	search.searchWithQueryParams(q);
    }else
    {
        search.getMessage(slingRequest,queryBuilder);
    }
   %>
<%
   SearchResults searchResults = search.getsResults();
   Map<String,Map<String,Long>>facetsWithCounts = searchResults.getFacetsWithCount();
   Iterator iterator = facetsWithCounts.keySet().iterator();
 %>
   <div id="container">
   <div id="colleft">
     <form action="<%=currentPage.getPath()%>.html" method="get" id="form">
   <%
   while(iterator.hasNext()){
       String facetLabel = (String)iterator.next();
   %>
    <%=facetLabel %>
   <% 
     for(Map.Entry<String, Long> tags:facetsWithCounts.get(facetLabel).entrySet()){
         String title = tm.resolve(tags.getKey()).getTitle();
         
    %>
     
        <p> <input type="checkbox" name="<%=title %>" value="<%=tags.getKey()%>" id="<%=title %>">
           <label for="<%=title %>"><%=title %></label>&nbsp; (<%=tags.getValue() %>) </p>
        
    <%   
     }
   }
   

%>  <input value="submit" type="submit"/>
       </form> 
   </div>
   <div id="colright">
  <%
     Map<String,String> results =searchResults.getResults();
     for(Map.Entry<String,String> resuts: results.entrySet()){
   	%>
   	<div>
      <a href="<%=resuts.getValue()%>"><%=resuts.getKey() %></a> 	
   </div> 	 
     <%}
    	 
  
  
  %>
   
   </div>
   <div>
    
   </div>
   
</div>
<script>

 $("form").submit(function(e){
	 var checkBoxValues = "q=";
	 $("input:checked").each(function(){
		  checkBoxValues = checkBoxValues+$(this).val()+"|";
		 });
	 alert(checkBoxValues);
	 e.preventDefault();
	 location.href ="?"+checkBoxValues;
	});


</script>