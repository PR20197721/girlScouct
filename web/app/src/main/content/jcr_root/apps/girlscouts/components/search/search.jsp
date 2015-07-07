<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List, java.util.regex.*, java.text.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="page" />
<%
final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
String documentLocation = "/content/dam/girlscouts-shared/documents";
String searchIn = (String) properties.get("searchIn");
if (null==searchIn){
	searchIn = currentPage.getAbsoluteParent(2).getPath();
}

final String escapedQuery = xssAPI.encodeForHTML(q != null ? q : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

pageContext.setAttribute("escapedQuery", escapedQuery);
pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

Map mapPath = new HashMap();
mapPath.put("group.p.or","true");
mapPath.put("group.1_path", searchIn);

String theseDamDocuments = properties.get("docusrchpath","");
if(theseDamDocuments.equals("")){
	String regexStr = "/(content)/([^/]*)/(en)$";
	Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
	Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
	if (matcher.find()) {
		theseDamDocuments = "/" + matcher.group(1) + "/dam/girlscouts-" +  matcher.group(2) + "/documents";
			
	}
}
long startTime = System.nanoTime();
mapPath.put("group.2_path", theseDamDocuments); 
mapPath.put("group.4_path", documentLocation);
PredicateGroup predicatePath =PredicateGroup.create(mapPath);
Map mapFullText = new HashMap();

mapFullText.put("group.p.or","true");
mapFullText.put("group.1_fulltext", escapedQuery);
mapFullText.put("group.1_fulltext.relPath", "jcr:content");
mapFullText.put("group.2_fulltext", escapedQuery);
mapFullText.put("group.2_fulltext.relPath", "jcr:content/@jcr:title");
mapFullText.put("group.3_fulltext", escapedQuery);
mapFullText.put("group.3_fulltext.relPath", "jcr:content/@jcr:description");
mapFullText.put("group.4_fulltext", escapedQuery);
mapFullText.put("group.4_fulltext.relPath", "jcr:content/@cq:name");
mapFullText.put("group.5_fulltext", escapedQuery);
mapFullText.put("group.5_fulltext.relPath", "jcr:content/metadata/@dc:title");
mapFullText.put("group.6_fulltext", escapedQuery);
mapFullText.put("group.6_fulltext.relPath", "jcr:content/metadata/@pdf:title");

PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
Map masterMap  = new HashMap();
masterMap.put("type","nt:hierarchyNode" );
masterMap.put("boolproperty","jcr:content/hideInNav");
masterMap.put("boolproperty.value","false");
masterMap.put("p.limit","-1");
masterMap.put("orderby","type");
PredicateGroup master = PredicateGroup.create(masterMap);
master.add(predicatePath);
master.add(predicateFullText);
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
    <%=properties.get("resultPagesText","Results for")%> "${escapedQuery}"
  <br/>
<%
	for(Hit hit: hits){
		try{
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
		<%}%>
			<a href="<%=path%>"><%=docHit.getTitle() %></a>
			<div>
				<%=docHit.getExcerpt()%>
			</div>
			<br/>
		 <%}catch(Exception w){
			 w.printStackTrace();
		 }
	}	
}
%>
<script>
jQuery('#searchForm').bind('submit', function(event){
	if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === ''){
		event.preventDefault();
	}
});
</script>
