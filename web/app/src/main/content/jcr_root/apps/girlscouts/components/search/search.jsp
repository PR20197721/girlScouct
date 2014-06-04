<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List" %>
<%@include file="/libs/foundation/global.jsp" %>

<cq:setContentBundle source="page" />
<%

  

final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

    QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
//Map<String, String> query = new HashMap<String, String>();
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

   Map mapPath = new HashMap();
   mapPath.put("group.p.or","true");
   mapPath.put("group.1_path","/content/girlscouts-usa/en");
   mapPath.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");
 
   
   PredicateGroup predicatePath =PredicateGroup.create(mapPath);
   
   Map mapFullText = new HashMap();
   
    mapFullText.put("group.p.or","true");
    mapFullText.put("group.1_fulltext", q);
    mapFullText.put("group.1_fulltext.relPath", "jcr:content");
    mapFullText.put("group.2_fulltext", q);
    mapFullText.put("group.2_fulltext.relPath", "jcr:content/@jcr:title");
    mapFullText.put("group.3_fulltext", q);
    mapFullText.put("group.3_fulltext.relPath", "jcr:content/@jcr:description");
   

   PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
   
   
   Map otherMap  = new HashMap();
   otherMap.put("type","nt:hierarchyNode" );
   otherMap.put("boolproperty","jcr:content/hideInNav");
   otherMap.put("boolproperty.value","false");
   otherMap.put("p.limit","-1");
   
   
   PredicateGroup other =   PredicateGroup.create(otherMap);
   PredicateGroup master = new PredicateGroup();
 
   master.add(predicatePath);
   master.add(predicateFullText);
   master.add(other);
   


		
		
		
Query query = queryBuilder.createQuery(master,slingRequest.getResourceResolver().adaptTo(Session.class));

query.setExcerpt(true);
 SearchResult result = query.getResult();
List<Hit> hits = result.getHits();

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
         DocHit docHit = new DocHit(hit);
         String path = docHit.getURL();

int idx = path.lastIndexOf('.');
String extension = idx >= 0 ? path.substring(idx + 1) : "";
%>
<br/>
<%
if(!extension.isEmpty() && !extension.equals("html")){
%>

            <span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
     <%} %>
<a href="<%=path%>"><%=docHit.getTitle() %></a>
       <div><%=docHit.getExcerpt()%></div>
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