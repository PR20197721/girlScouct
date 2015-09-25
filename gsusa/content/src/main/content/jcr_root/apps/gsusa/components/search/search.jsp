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
  mapFullText.put("orderby","type");
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
String documentLocation = "/content/dam/gsusa-shared/documents";
String searchIn = (String) properties.get("searchIn");
if (null==searchIn){
  searchIn = currentPage.getAbsoluteParent(2).getPath();
}

// pbae: adding ~ to enable stemming search
final String escapedQuery = xssAPI.encodeForHTML(q != null ? q : "") + "~";
final String escapedQueryPlain = xssAPI.encodeForHTML(q != null ? q : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

pageContext.setAttribute("escapedQuery", escapedQuery);
pageContext.setAttribute("escapedQueryPlain", escapedQueryPlain);
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

hits.addAll(getHits(queryBuilder,session,searchIn,escapedQuery));
hits.addAll(getHits(queryBuilder,session,theseDamDocuments,escapedQuery));
hits.addAll(getHits(queryBuilder,session,documentLocation,escapedQuery));

%>

<form action="${currentPage.path}.html" id="searchForm" class="row">
    <input type="text" name="q" value="${escapedQueryForAttr}" pattern=".{3,}" required title="3 characters minimum" class="searchField" />
    <input type="submit" value="search" class="button" />
</form>

<%if(hits.isEmpty()){ %>
    <fmt:message key="noResultsText">
      <fmt:param value="${escapedQuery}"/>
    </fmt:message>
<% } else { %>
    <p><strong>
        <%= properties.get("resultPagesText","Results for")%> "${escapedQueryPlain}"
    </strong></p>
    <ul class="search-row">
<%

  for(Hit hit: hits) {
    try {
      DocHit docHit = new DocHit(hit);
      String path = docHit.getURL();
      int idx = path.lastIndexOf('.');
      String extension = idx >= 0 ? path.substring(idx + 1) : "";
      %>
            <li>
                <% if(!extension.isEmpty() && !extension.equals("html")) { %>
                <span class="icon type_<%=extension%>"><!-- <img src="/etc/designs/default/0.gif" alt="*"> --></span>
                <% } %>
                <h5><a href="<%=path%>"><%=docHit.getTitle() %></a></h5>
                <p><%=docHit.getExcerpt()%></p>
            </li>
        <% } catch(Exception w) {}
    } %>
    </ul>
<% } %>

<script type="text/javascript">
jQuery('#searchForm').bind('submit', function(event){
  if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === ''){
    event.preventDefault();
  }
});
</script>
