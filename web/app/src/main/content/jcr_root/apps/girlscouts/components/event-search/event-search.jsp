<%@ page import="com.day.cq.tagging.TagManager,
                java.util.ArrayList,java.util.HashSet,
                java.util.Locale,java.util.Map,java.util.Iterator,
                java.util.HashMap,java.util.List,java.util.Set,
                com.day.cq.search.result.SearchResult,
                java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
                javax.jcr.PropertyIterator,
                com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%
   
   long RESULTS_PER_PAGE = 10;
   
   String path = currentSite.get("eventPath",String.class);
  
   QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
   
   EventsSrch searchQuery = new EventsSrch(slingRequest,queryBuilder);
   Map<String, ArrayList<String>> tagsToCheck = new HashMap<String, ArrayList<String>>();
   String[] tags = new String[]{};
   if (request.getParameterValues("tags") != null) {
	tags = request.getParameterValues("tags");
   } 
   String q = request.getParameter("q");
   String offset = request.getParameter("offset");
   String region = request.getParameter("regions");
   String month = request.getParameter("month");
   String startdtRange = request.getParameter("startdtRange");
   String enddtRange = request.getParameter("enddtRange");
   String year=request.getParameter("year");
  
   Boolean includeCart = false;
   if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
   	if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
   		includeCart = true;
   	}
   }
   
   SearchResultsInfo searchResultsInfo = null;
   SearchResult searchResults = null;
   if(path != null){
	   if(includeCart){
		   searchQuery.search(q,tags,offset,month,year,startdtRange,enddtRange,region,path,"sf-activities");
	   }else{
	   		searchQuery.search(q,tags,offset,month,year,startdtRange,enddtRange,region,path,currentPage.getAbsoluteParent(1).getName());
	   }
	   searchResultsInfo = searchQuery.getSearchResultsInfo();
	   searchResults = searchResultsInfo.getSearchResults();
   }
   Map<String,List<FacetsInfo>> facetsAndTags =  searchQuery.getFacets();
  // List<String> results = searchResultsInfo.getResults();
  // long hitCounts = searchResultsInfo.getHitCounts();  
   request.setAttribute("searchResults", searchResults);
   request.setAttribute("facetsAndTags", facetsAndTags);
   request.setAttribute("eventresults", searchResultsInfo);
%>
