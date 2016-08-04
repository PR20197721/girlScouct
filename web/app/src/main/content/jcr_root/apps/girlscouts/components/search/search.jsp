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
    //-mapFullText.put("group.p.or","true");
    mapFullText.put("fulltext", escapedQuery);
    //mapFullText.put("type", pType);//"dam:Asset");//
    mapFullText.put("path",path);
    mapFullText.put("group.1_fulltext.relPath", "jcr:content");
    mapFullText.put("p.limit","-1");
    mapFullText.put("boolproperty","jcr:content/hideInNav");
    mapFullText.put("boolproperty.value","false");

	PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
	Query query = queryBuilder.createQuery(predicateFullText,session);
	System.out.println("test: "+predicateFullText.toString());
	
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


if(q == null) q = "[[empty search criteria]]";
if(q.length() <= 2) q = "[[too short search criteria]]";

// pagination init
String start = request.getParameter("start");
if (start == null) start = "1";
if (start.length() == 0) start = "1";
if (Integer.parseInt(start) < 0) start = "1";
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
    if (matcher.find()) {
        theseDamDocuments = "/" + matcher.group(1) + "/dam/girlscouts-" +  matcher.group(2) + "/documents";
            
    }
}

hits.addAll(getHits(queryBuilder,session,searchIn,java.net.URLDecoder.decode(query, "UTF-8")));
hits.addAll(getHits(queryBuilder,session,theseDamDocuments,java.net.URLDecoder.decode(query, "UTF-8")));
//hits.addAll(getHits(queryBuilder,session,documentLocation,java.net.URLDecoder.decode(escapedQuery, "UTF-8")));


String numberOfResults = String.valueOf(hits.size());
if (startIdx + pageSize > hits.size()) {
	endIdx = hits.size(); //last page
} else {
	endIdx = startIdx + pageSize; //all other page
}
totalPage = Math.ceil((double)hits.size()/pageSize);

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
	for(int i = startIdx; i < endIdx ; i++) {
        try{
            DocHit docHit = new DocHit(hits.get(i));
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
    
    
<script>
jQuery('#searchForm').bind('submit', function(event){
    if (jQuery.trim(jQuery(this).find('input[name="q"]').val()) === ''){
        event.preventDefault();
    }
});
</script>
