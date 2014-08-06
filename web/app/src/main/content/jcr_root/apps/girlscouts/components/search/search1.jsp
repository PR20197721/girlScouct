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


int pos = 0;
if( request.getParameter("pos")!=null ){
	try{ pos= Integer.parseInt(request.getParameter("pos")); }catch(Exception e){e.printStackTrace();}
}
System.err.println("POs: "+ pos +" : "+ request.getParameter("pos"));


   Map mapPath = new HashMap();
   mapPath.put("group.p.or","true");
   mapPath.put("group.1_path", currentPage.getAbsoluteParent(2).getPath());

//   mapPath.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");

String regexStr = "/(content)/([^/]*)/(en)$";
Pattern pattern = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
Matcher matcher = pattern.matcher(currentPage.getAbsoluteParent(2).getPath());
String theseDamDocuments = ""; // needs to return /content/dam/gateway/en/documents
if (matcher.find()) {
        theseDamDocuments = "/" + matcher.group(1) + "/dam/" +  matcher.group(2) + "/" + matcher.group(3) + "/documents";
}
  mapPath.put("group.2_path", theseDamDocuments); 
   
   PredicateGroup predicatePath =PredicateGroup.create(mapPath);
   
   Map mapFullText = new HashMap();
   
    mapFullText.put("group.p.or","true");
    mapFullText.put("group.1_fulltext", escapedQuery);
    mapFullText.put("group.1_fulltext.relPath", "jcr:content");
    mapFullText.put("group.2_fulltext", escapedQuery);
    mapFullText.put("group.2_fulltext.relPath", "jcr:content/@jcr:title");
    mapFullText.put("group.3_fulltext", escapedQuery);
    mapFullText.put("group.3_fulltext.relPath", "jcr:content/@jcr:description");

   PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
   
   
   Map masterMap  = new HashMap();
   masterMap.put("type","nt:hierarchyNode" );
   masterMap.put("boolproperty","jcr:content/hideInNav");
   masterMap.put("boolproperty.value","false");
   masterMap.put("p.limit","-1");
   
   PredicateGroup master = PredicateGroup.create(masterMap);
 
   master.add(predicatePath);
   master.add(predicateFullText);
		
Query query = queryBuilder.createQuery(master,slingRequest.getResourceResolver().adaptTo(Session.class));
query.setExcerpt(true);

query.setStart(pos);
query.setHitsPerPage(10);

 SearchResult result = query.getResult();
 
List<Hit> hits = result.getHits();

%>


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

   
if( hits.size()>9){%>
 <a href="/content/gateway/en/site-search.simple.html?q=<%=escapedQuery%>&pos=<%= (pos+10)%>">next</a>
<%} %>

