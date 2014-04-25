<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,java.util.HashSet,
                 java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult,
                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>

<%

   
   
   String REGIONS = "/content/girlscouts-usa/en/locations";
   String YEARS="/content/girlscouts-usa/en/events";
   long RESULTS_PER_PAGE = 10;
   
   
   
   List<String> regions = new ArrayList<String>();
   List<String> years = new ArrayList<String>();
  
   QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
   
   EventsSrch searchQuery = new EventsSrch(slingRequest,queryBuilder);
 
   Map<String, ArrayList<String>> tagsToCheck = new HashMap<String, ArrayList<String>>();
   String[] tags = request.getParameterValues("tags");
   Set<String> set = new HashSet<String>();
   if(tags!=null)
   {
        set = new HashSet<String>();
        for (String words : tags){
            set.add(words);
        }
   }
   String q = request.getParameter("q");
   String offset = request.getParameter("offset");
   String region = request.getParameter("region");
   String month = request.getParameter("month");
   String startdtRange = request.getParameter("startdtRange");
   String enddtRange = request.getParameter("enddtRange");
   String year=request.getParameter("year");
  
   searchQuery.search(q,tags,offset,month,year,startdtRange,enddtRange,region);
   Map<String,List<FacetsInfo>> facetsAndTags =  searchQuery.getFacets();
   
  
   SearchResultsInfo searchResultsInfo = searchQuery.getSearchResultsInfo();
   SearchResult searchResults = searchResultsInfo.getSearchResults();
   
  
   List<String> results = searchResultsInfo.getResults();
   long hitCounts = searchResultsInfo.getHitCounts();
   Iterator<Page> pages= resourceResolver.getResource(REGIONS).adaptTo(Page.class).listChildren();
   while(pages.hasNext()){
       regions.add(pages.next().getTitle());
       
   }
   Iterator<Page> yrs= resourceResolver.getResource(YEARS).adaptTo(Page.class).listChildren();
   while(yrs.hasNext()){
       years.add(yrs.next().getTitle());
       
   }
   request.setAttribute("searchResults", searchResults);
   request.setAttribute("facetsAndTags", facetsAndTags);
   request.setAttribute("eventresults", searchQuery.getSearchResultsInfo());
  
%>
