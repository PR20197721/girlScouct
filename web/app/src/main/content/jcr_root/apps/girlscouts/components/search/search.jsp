<%@ page import="com.day.cq.wcm.foundation.Search,
org.girlscouts.web.search.DocHit,
com.day.cq.search.eval.JcrPropertyPredicateEvaluator,com.day.cq.search.eval.FulltextPredicateEvaluator,
com.day.cq.tagging.TagManager,
java.util.Locale,com.day.cq.search.QueryBuilder,javax.jcr.Node,
java.util.ResourceBundle,com.day.cq.search.PredicateGroup,
com.day.cq.search.Predicate,com.day.cq.search.result.Hit,
com.day.cq.i18n.I18n,com.day.cq.search.Query,com.day.cq.search.result.SearchResult,
java.util.Map,java.util.HashMap,java.util.List, java.util.ArrayList, 
java.util.Arrays, java.util.regex.*, java.text.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:setContentBundle source="page" />

<%!
public List<Hit> getHits(QueryBuilder queryBuilder, Session session, String path, String escapedQuery, String pType){
    Map mapFullText = new HashMap();
    mapFullText.put("fulltext", escapedQuery);
    mapFullText.put("type", pType);
    mapFullText.put("p.limit","-1");
    mapFullText.put("path",path);
    mapFullText.put("group.1_fulltext.relPath", "jcr:content");
    mapFullText.put("boolproperty","jcr:content/hideInNav");
    mapFullText.put("boolproperty.value","false");


    PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
    Query query = queryBuilder.createQuery(predicateFullText,session);

    query.setExcerpt(true);
    return query.getResult().getHits(); 
}

%>

<%
final Locale pageLocale = currentPage.getLanguage(true);
final ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = slingRequest.getParameter("q");
//String documentLocation = "/content/dam/girlscouts-shared/documents";
String searchIn = (String) properties.get("searchIn");
List<Hit> hits = new ArrayList<Hit>();
if (null==searchIn){
    searchIn = currentPage.getAbsoluteParent(2).getPath();
}

final String query = java.net.URLEncoder.encode(q != null ? q.replaceAll("[^a-zA-Z0-9]"," ") : "","UTF-8");
final String escapedQuery = xssAPI.encodeForHTML(q != null ? q.replaceAll("%","%25") : "");
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q.replaceAll("%","%25") : "");

pageContext.setAttribute("escapedQuery", java.net.URLDecoder.decode(escapedQuery, "UTF-8"));
pageContext.setAttribute("escapedQueryForAttr", java.net.URLDecoder.decode(escapedQueryForAttr, "UTF-8"));

String theseDamDocuments = properties.get("docusrchpath","");
if(theseDamDocuments.equals("")){
    String regexStr = "/(content)/([^/]*)/(en)$";
    Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
	String[] councils = new String[]{"gssjc", "gateway","gssem" };
	
    if (matcher.find()) {
        theseDamDocuments = "/" + matcher.group(1) + "/dam/girlscouts-" +  matcher.group(2) + "/documents";
		if (Arrays.asList(councils).contains(matcher.group(2))){
            theseDamDocuments = "/" + matcher.group(1) + "/dam/" +  matcher.group(2) + "/documents";
        }

    }
}

hits.addAll(getHits(queryBuilder,session,theseDamDocuments,java.net.URLDecoder.decode(query, "UTF-8"), "dam:Asset"));
hits.addAll(getHits(queryBuilder,session,searchIn,java.net.URLDecoder.decode(query, "UTF-8"), "cq:Page"));
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
            String description = docHit.getDescription();

            %>
            <br/>
        <%
        if(!extension.isEmpty() && !extension.equals("html")){
        %>
            <span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
        <%}%>
            <a href="<%=path%>"><%=docHit.getTitle() %></a>
        <%
        if(description!=null &&  !description.isEmpty()) {
        %>  
            <div><%=description%></div>
        <%
        }else{
        %><div><%=docHit.getExcerpt()%></div>   
        <%} %>  
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
