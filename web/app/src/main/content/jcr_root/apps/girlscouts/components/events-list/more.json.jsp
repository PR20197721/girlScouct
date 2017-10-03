<%-- <%@ page import="com.day.cq.tagging.TagManager, --%>
<%--                 java.util.ArrayList,java.util.HashSet, --%>
<%--                 java.util.Locale,java.util.Map,java.util.Iterator, --%>
<%--                 java.util.HashMap,java.util.List,java.util.Set, --%>
<%--                 com.day.cq.search.result.SearchResult, --%>
<%--                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder, --%>
<%--                 javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, --%>
<%--                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo" %> --%>
<%@ page import="java.util.Locale,
java.util.ResourceBundle,
java.util.List, 
java.util.ArrayList,
java.util.regex.*, 
java.util.Arrays,
java.text.*, 
org.girlscouts.web.search.GSSearchResult, 
org.girlscouts.web.search.GSSearchResultManager,
org.girlscouts.web.search.GSJcrSearchProvider" %>          
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%
   	long RESULTS_PER_PAGE = 10;
	long offset = Long.parseLong(request.getParameter("offset"));
   	String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] FROM [cq:Page] AS s WHERE ISDESCENDANTNODE([%s]) ORDER BY [jcr:content/data/start] ASC";
   	String path = currentSite.get("eventPath",String.class);
   
   	String query = String.format(EXPRESSION, path);
   
   	GSSearchResultManager gsResultManager = new GSSearchResultManager();
   	List<GSSearchResult> results = new ArrayList<GSSearchResult>();
   	try {
		GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
	 	boolean searchMore = true;
	 	gsResultManager.add(searchProvider.searchWithOffset(query, RESULTS_PER_PAGE, offset));
	 	List<GSSearchResult> queryResults = gsResultManager.getResults();
	 	for(GSSearchResult qResult:queryResults){
	 		offset++;
	 		out.println(qResult.getTitle());
	 	}
   	} catch(Exception e){
   		e.printStackTrace();
   	}
%>
