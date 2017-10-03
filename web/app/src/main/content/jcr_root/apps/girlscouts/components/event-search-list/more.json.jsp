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
java.util.Calendar,
java.util.TimeZone,
java.text.DateFormat,
java.text.SimpleDateFormat,
java.util.Date,
java.util.List, 
java.util.ArrayList,
javax.jcr.Node,org.girlscouts.web.events.search.*,
org.girlscouts.web.search.GSSearchResult, 
org.girlscouts.web.search.GSSearchResultManager,
org.girlscouts.web.search.GSJcrSearchProvider" %>          
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:defineObjects/>
<%
   	long RESULTS_PER_PAGE = 20;
	long offset = 0;
	String EVENTS_RESOURCE_TYPE = "girlscouts/components/event-page";
	Calendar cal = Calendar.getInstance();
	cal.setTimeZone(TimeZone.getDefault());
	DateFormat sql2DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	DateFormat printDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	sql2DateFormat.setTimeZone(TimeZone.getDefault());
	try{
		offset = Long.parseLong(request.getParameter("offset"));
	}catch(Exception e){}
   	String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] "
   			+ "FROM [cq:Page] AS s "
   			+ "WHERE ISDESCENDANTNODE([%s]) AND "
   				+ "(s.[jcr:content/data/visibleDate] IS NULL OR s.[jcr:content/data/visibleDate] <= CAST('%s' AS DATE)) AND "
   				+ "s.[jcr:content/data/start] >= CAST('%s' AS DATE) "
   			+ "ORDER BY s.[jcr:content/data/start] ASC";
   	String path = currentSite.get("eventPath",String.class);
   	String query = String.format(EXPRESSION, path, sql2DateFormat.format(cal.getTime()), sql2DateFormat.format(cal.getTime()));
   	try {
		GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
	 	boolean searchMore = true;
	 	long resultCount = 0;
	 	Calendar cale =  Calendar.getInstance();
	 	while(searchMore){
	 		long fetchSize = RESULTS_PER_PAGE - resultCount;
	 		GSSearchResultManager gsResultManager = new GSSearchResultManager();
	 		out.println("<br/><div>"+query+"</div><br/>");
		 	gsResultManager.add(searchProvider.searchWithOffset(query, fetchSize, offset));
		 	if(gsResultManager.size() == 0){
		 		searchMore = false;
	 			break;
		 	}
		 	List<GSSearchResult> queryResults = gsResultManager.getResults();
		 	for(GSSearchResult qResult:queryResults){
		 		offset++;
		 		Node resultNode = qResult.getResultNode();
		 		Node dataNode = null;
		 		try{
		 			dataNode = resultNode.getNode("jcr:content/data");
		 			String date="";
		 			try{
		 				Calendar visibleDate = dataNode.getProperty("visibleDate").getDate();
		 				date = printDateFormat.format(visibleDate.getTime());
		 			}catch(Exception e){}
			 		out.println("<div>"+offset+"   "+qResult.getTitle()+"   "+qResult.getPath()+" "+date+"</div>");
			 		resultCount ++;
		 		}catch(Exception e){}
		 		if(resultCount == RESULTS_PER_PAGE){
		 			searchMore = false;
		 			break;
		 		}
		 	}
	 	}
   	} catch(Exception e){
   		e.printStackTrace();
   	}
   	out.println("<div>offset:"+offset+"</div>");
%>
