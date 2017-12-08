<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List, java.util.ArrayList, java.util.regex.*, java.text.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="page" />
<%!
public List<Hit> getHits(QueryBuilder queryBuilder, Session session, String path, String escapedQuery){
  Map mapFullText = new HashMap();
  mapFullText.put("path",path);
  mapFullText.put("fulltext", escapedQuery);
  mapFullText.put("fulltext.relPath", "jcr:content");
  mapFullText.put("type","nt:hierarchyNode" );
  mapFullText.put("boolproperty","jcr:content/hideInNav");
  mapFullText.put("boolproperty.value","false");
  mapFullText.put("p.limit","-1");
  mapFullText.put("orderby","@jcr:content/cq:lastModified");	// order by latest first (pbae)
  mapFullText.put("orderby.sort", "desc"); 
  PredicateGroup pg=PredicateGroup.create(mapFullText);
  Query query = queryBuilder.createQuery(pg,session);
  query.setExcerpt(true);
  return query.getResult().getHits(); 
} 

%>
<%
final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);

QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
String cleanedQ = request.getParameter("q").replaceAll("[^a-zA-Z0-9 ]+", "");
String start = request.getParameter("start");
int pageSize = 10;
int endIdx = pageSize; //this may change for the last page
double totalPage = 0;
int startIdx = 0;
try {
	startIdx = Integer.parseInt(start); 
} catch (NumberFormatException e) {
	startIdx = 0;
} 
int currentPageNo = startIdx/pageSize;


String documentLocation = "/content/dam/gsusa-shared/documents";
String searchIn = (String) properties.get("searchIn");
if (null==searchIn){
  searchIn = currentPage.getAbsoluteParent(2).getPath();
}

final String escapedQuery = xssAPI.encodeForHTML(cleanedQ != null ? cleanedQ : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

pageContext.setAttribute("escapedQuery", escapedQuery);
pageContext.setAttribute("escapedQueryForAttr", escapedQueryForAttr);

String theseDamDocuments = properties.get("docusrchpath","");
if(theseDamDocuments.equals("")){
  String regexStr = "/(content)/([^/]*)/(en)$";
  Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
  Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
  if (matcher.find()) {
    theseDamDocuments = "/" + matcher.group(1) + "/dam/gsusa-" +  matcher.group(2) + "/documents";

  }
}
long startTime = System.nanoTime();

List<Hit> hits = new ArrayList<Hit>();
Session session = slingRequest.getResourceResolver().adaptTo(Session.class);

//Since we have to seperate the query, we have to do pagination manually
hits.addAll(getHits(queryBuilder,session,searchIn,escapedQuery));
hits.addAll(getHits(queryBuilder,session,theseDamDocuments,escapedQuery));
hits.addAll(getHits(queryBuilder,session,documentLocation,escapedQuery));

String numberOfResults = String.valueOf(hits.size());
if (startIdx + pageSize > hits.size()) {
	endIdx = hits.size(); //last page
} else {
	endIdx = startIdx + pageSize; //all other page
}
totalPage = Math.ceil((double)hits.size()/pageSize);

%>

<form action="${currentPage.path}.html" id="searchForm" class="row">
    <input type="text" name="q" value="${escapedQueryForAttr}" pattern=".{3,}" required title="3 characters minimum" class="searchField" />
    <input type="submit" value="search" class="button" />
</form>

<%if(hits.isEmpty()){ %>
    <fmt:message key="noResultsText">
      <fmt:param value="${escapedQueryForAttr}"/>
    </fmt:message>
<% } else { %>
    <p><strong>
        <%= numberOfResults%> <%= properties.get("resultPagesText","Results for")%> "${escapedQueryForAttr}"
    </strong></p>
    <ul class="search-row">
<%
  
  
  
  for(int i = startIdx; i < endIdx ; i++) {
    try {
      DocHit docHit = new DocHit(hits.get(i));
      String path = docHit.getURL();
      int idx = path.lastIndexOf('.');
      String extension = idx >= 0 ? path.substring(idx + 1) : "";
      %>
            <li>
                <% if(!extension.isEmpty() && !extension.equals("html")) { %>
                <span class="icon type_<%=extension%>"><!-- <img src="/etc/designs/default/0.gif" alt="*"> --></span>
                <% } %>
                <h5><a href="<%=path%>"><%=docHit.getTitle() %></a></h5>
                <p><%=docHit.getRawExcerpt()%></p>
                <% 
                	// show last modified to confirm result sorting (pbae)
                	// String lastModified = docHit.getProperties().get("cq:lastModified").toString();
                	// out.println(lastModified); 
                %>
            </li>
        <% } catch(Exception w) {}
    } %>
    </ul>
    <ul class="search-page">
    	<%if (currentPageNo != 0) {  %>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=(currentPageNo - 1)*10%>"><</a></li>
    	<%}  %>
    <%for (int i = 0; i < totalPage; i++ ) { 
    	if (currentPageNo == i) {%>
    		<li class="currentPageNo"><%= i+1 %></li>
    	<%} else {%>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=i*10%>"><%= i+1 %></a></li>
    <%	}
    }%>
    <%if (currentPageNo != totalPage-1) {  %>
    		<li><a href="${currentPage.path}.html?q=<%= q%>&start=<%=(currentPageNo + 1)*10%>">></a></li>
    	<%}  %>
    </ul>
<% } %>

<script type="text/javascript">
jQuery('#searchForm').bind('submit', function(event){
  if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === ''){
    event.preventDefault();
  }
});
</script>
