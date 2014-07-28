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
mapFullText.put("fulltext", q);


mapFullText.put("group.1_path", currentPage.getAbsoluteParent(2).getPath());
mapFullText.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");
mapFullText.put("group.p.or","true");
PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
System.err.println("***SQL: "+predicateFullText.toString());
Query query = queryBuilder.createQuery(predicateFullText, slingRequest.getResourceResolver().adaptTo(Session.class));
query.setStart(pos);
query.setHitsPerPage(10);
query.setExcerpt(true);
SearchResult result = query.getResult();
List<Hit> hits = result.getHits();
System.err.println("TESt123: "+ hits.size() +" : "+result.getExecutionTimeMillis()  );
Map unq= new java.util.TreeMap();
for(Hit hit: hits)
{
	try{
		if(hit.getPath().contains("textimage_")) 
		continue;
		DocHit docHit = new DocHit(hit);
    	String path = docHit.getURL();
  		String obj[] =(String[])unq.get( docHit.getURL() );
		if( obj ==null ){
			obj = new String[3];
			obj[0]= docHit.getURL();
			obj[1]= docHit.getTitle();
			try{
				obj[2]= hit.getExcerpt();
			}catch(Exception e){
				e.printStackTrace();
			}
	}else{
		
	}
		unq.put( docHit.getURL() , obj);
	}catch(Exception e){}
}
int xx=pos;
java.util.Iterator itr= unq.keySet().iterator();

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

    while(itr.hasNext())
{
    	String u=(String) itr.next();
    	String obj[] = (String[]) unq.get(u);
		int idx = obj[0].indexOf('.');
		String extension = idx >= 0 ? obj[0].substring(idx + 1) : "";
%>
<br/>
<%
if(!extension.isEmpty() && !extension.equals("html")){
%>

            <span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
     <%} %>
<a href="<%=obj[0] %>" data-geo=""><%=obj[1]%></a>
       <div><%=obj[2]%></div>
       <br/>
   <%}
}
   
%>
<%if( hits.size()> 9 ){ %>
<br/><br/>
<a href="/content/gateway/en/site-search.html?q=girls&pos=<%= ( (pos-10)<0 ) ? 0 : (pos-10)%>">PREV</a> ||
 <a href="/content/gateway/en/site-search.html?q=girls&pos=<%= (pos+10)%>">NEXT</a>
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
