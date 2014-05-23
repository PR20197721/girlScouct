<%@ page import="com.day.cq.wcm.foundation.Search,
                 com.day.cq.tagging.TagManager,
                 java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
                 java.util.ResourceBundle,com.day.cq.search.PredicateGroup,com.day.cq.search.result.Hit,
                 com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
                 java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>

<cq:setContentBundle source="page" />
<%

  

final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

    QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
	String q = request.getParameter("q");
	Map<String, String> query = new HashMap<String, String>();
	String documentLocation = "/content/dam/girlscouts-shared/en/documents";
	String searchIn = (String) properties.get("searchIn");
	if (null==searchIn) 
	   {
	     searchIn = currentPage.getAbsoluteParent(2).getPath();
	   }
	
	final String escapedQuery = xssAPI.encodeForHTML(q != null ? q : "");
	final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

	pageContext.setAttribute("escapedQuery", escapedQuery);
	pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

//Path to search in the DAM path.
	query.put("fulltext", q);
	query.put("property","jcr:primaryType");
	query.put("property.1_value","dam:Asset");
	query.put("property.2_value","cq:Page");
	query.put("boolproperty","jcr:content/hideInNav");
    query.put("boolproperty.value","false");
	query.put("group.p.or","true");
	query.put("group.1_path","/content/girlscouts-usa/en");
	query.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");
	query.put("p.limit", "-1");
	
	Query searchQuery = queryBuilder.createQuery(PredicateGroup.create(query),slingRequest.getResourceResolver().adaptTo(Session.class));
	SearchResult results = searchQuery.getResult();
    List<Hit> hits = results.getHits();
	pageContext.setAttribute("results", results);		

%>
<center>
     <form action="${currentPage.path}.html" id="searchForm">
        <input type="text" name="q" value="${escapedQueryForAttr}" class="searchField" />
     </form> 
</center>
<br/>
<%if(hits.isEmpty()){ %>
    <fmt:message key="noResultsText">
      <fmt:param value="${escapedQuery}"/>
    </fmt:message>
 <%} else{ %>
    <%=properties.get("resultPagesText","Results For")%> "${escapedQuery}"   
  <br/>
<%
    for(Hit hit: hits)
	     {
		   String path = hit.getPath();
		   Node node = hit.getNode();
		  
		   if(node.isNodeType("cq:Page")){
			   path+=".html";
			   System.out.println("path" +path);
		   }
		   int idx = path.lastIndexOf('.');
		   String extension = idx >= 0 ? path.substring(idx + 1) : "";
	     %>
	   <br/>
	   <%
	      if(!extension.isEmpty() && !extension.equals("html")){
	   %>
	 
            <span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
     <%} %>
	   <a href="<%=path%>"><%=hit.getTitle() %></a>
       <div><%=hit.getExcerpt()%></div>
       <br/>
   <%}
}
   
%>
<script>
jQuery('#searchForm').bind('submit', function(event)
	{
		if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === '')
			   {
			       event.preventDefault();
			    }
		}); 
</script>