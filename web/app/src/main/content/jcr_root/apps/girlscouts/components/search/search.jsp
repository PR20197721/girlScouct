<%@ page import="com.day.cq.wcm.foundation.Search, org.girlscouts.web.search.DocHit, com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator, com.day.cq.tagging.TagManager, java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node, java.util.ResourceBundle,com.day.cq.search.PredicateGroup, com.day.cq.search.Predicate,com.day.cq.search.result.Hit, com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult, java.util.Map,java.util.HashMap,java.util.List, java.util.regex.*, java.text.*, java.util.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="page" />
<%

  

final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

    QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
//Map<String, String> query = new HashMap<String, String>();
String searchIn = (String) properties.get("searchIn");
if (null==searchIn)
{
searchIn = currentPage.getAbsoluteParent(2).getPath();
}

final String escapedQuery = xssAPI.encodeForHTML(q != null ? q : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

pageContext.setAttribute("escapedQuery", escapedQuery);
pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

int pos = 0;
if( request.getParameter("pos")!=null ){
	try{ pos= Integer.parseInt(request.getParameter("pos")); }catch(Exception e){e.printStackTrace();}
}


Map mapFullText = new HashMap();
mapFullText.put("fulltext", escapedQuery);


mapFullText.put("group.1_path", currentPage.getAbsoluteParent(2).getPath()); // returns /content/gateway/en
//mapFullText.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");

String regexStr = "/(content)/([^/]*)/(en)$";
Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
String theseDamDocuments = ""; // needs to return /content/dam/gateway/en/documents
if (matcher.find()) {
	theseDamDocuments = "/" + matcher.group(1) + "/dam/" +  matcher.group(2) + "/" + matcher.group(3) + "/documents";
}
mapFullText.put("group.2_path", theseDamDocuments); 

mapFullText.put("group.p.or","true");
PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
Query query = queryBuilder.createQuery(predicateFullText, slingRequest.getResourceResolver().adaptTo(Session.class));
query.setStart(pos);
query.setHitsPerPage(10);
query.setExcerpt(true);
SearchResult result = query.getResult();
List<Hit> hits = result.getHits();
Set<String> processedResults = new HashSet<String>();
List<DocHit> hitDisplay = new ArrayList<DocHit>();
for(Hit hit: hits) {
	try{
		if(hit.getPath().contains("textimage_"))  {
			continue;
		}
		DocHit thisHit = new DocHit(hit);
		String thisHitUrl = thisHit.getURL();
		if(!processedResults.contains(thisHitUrl)){
			processedResults.add(thisHitUrl);
			hitDisplay.add(thisHit);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
int xx=pos;
%>
<center>
     <form action="${currentPage.path}.html" id="searchForm">
        <input type="text" name="q" value="${escapedQueryForAttr}" class="searchField" />
     </form>
</center>
<br/>
<%if(hitDisplay.isEmpty()){ %>
    <fmt:message key="noResultsText">
      <fmt:param value="${escapedQuery}"/>
    </fmt:message>
 <%} else{ %>
    <%=properties.get("resultPagesText","Results for")%> "${escapedQuery}"
  <br/>
<%
}

for (DocHit hitItem: hitDisplay) {
	String extension = hitItem.getExtension();
%>
	<br/>
<% if(!extension.isEmpty() && !extension.equals("html")){ %><span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span><%} %>
	<a href="<%=hitItem.getURL() %>"><%=hitItem.getTitle()%></a>
       <div><%=hitItem.getExcerpt()%></div>
       <br/>
<%
}
%>
<%if( hits.size()> 9 ){ %>
<br/><br/>
<a href="/content/gateway/en/site-search.html?q=<%= escapedQuery %>&pos=<%= ( (pos-10)<0 ) ? 0 : (pos-10)%>">PREV</a> ||
 <a href="/content/gateway/en/site-search.html?q=<%= escapedQuery %>&pos=<%= (pos+10)%>">NEXT</a>
<br/><br/>
<%} %>

<script>
jQuery('#searchForm').bind('submit', function(event)
{
if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === '')
{
event.preventDefault();
}
});
</script>
