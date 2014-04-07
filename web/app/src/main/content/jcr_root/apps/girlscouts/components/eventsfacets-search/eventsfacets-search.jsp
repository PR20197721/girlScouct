<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,java.util.HashSet,
                 java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult,
                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>

<%

   
   String COUNTS = "/content/girlscouts-usa/en/events/tag-counts";
   String REGIONS = "/content/girlscouts-usa/en/locations";
   String YEARS="/content/girlscouts-usa/en/events";
   long RESULTS_PER_PAGE = 10;
   
   
   
   ArrayList<String> regions = new ArrayList<String>();
   ArrayList<String> years = new ArrayList<String>();
  
   QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
   
   EventsSrch searchQuery = new EventsSrch(slingRequest,queryBuilder);
 
   Map<String, ArrayList<String>> tagsToCheck = new HashMap<String, ArrayList<String>>();
   String[] tags = request.getParameterValues("tags");
   HashSet<String> set = new HashSet<String>();
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
   HashMap<String,List<FacetsInfo>> facetsAndTags =  searchQuery.getFacets();
   
   
   SearchResultsInfo searchResultsInfo = searchQuery.getSearchResultsInfo();
   SearchResult searchResults = searchResultsInfo.getSearchResults();
   
  
   Map<String,String> results = searchResultsInfo.getResults();
   long hitCounts = searchResultsInfo.getHitCounts();
   Iterator<Page> pages= resourceResolver.getResource(REGIONS).adaptTo(Page.class).listChildren();
   while(pages.hasNext()){
	   regions.add(pages.next().getTitle());
	   
   }
   Iterator<Page> yrs= resourceResolver.getResource(YEARS).adaptTo(Page.class).listChildren();
   while(yrs.hasNext()){
       years.add(yrs.next().getTitle());
       
   }
   
   request.setAttribute("results", searchQuery.getSearchResultsInfo());
  
%>
<div style="width:100%;"> 
 
   <div style="float:left; width:20%;">
    <form action="<%=currentPage.getPath()%>.html" method="get" id="form">
       <div>
         <p>
              KeyWord : <input type="text" name="q" id="q"/>
         </p>
       </div> 
 
 
        <div>
         <Strong>By Month and Year </Strong>
        <select name="month" id="month"  <%if((year!=null && !year.isEmpty()) && (month.isEmpty())){%> style="border: 1px solid red" <%}%>>
            <option value="">Choose</option>
            <option value="0">January</option>
            <option value="1">February</option>
            <option value="2">March</option>
            <option value="3">April</option>
            <option value="4">May</option>
            <option value="5">June</option>
            <option value="6">July</option>
            <option value="7">August</option>
            <option value="8">September</option>
            <option value="9">October</option>
            <option value="10">November</option>
            <option value="11">December</option>
        </select>
    </div>
    <div>
        
       
       <select name="year" id="year"  <%if((month!=null && !month.isEmpty()) && (year.isEmpty())){%>style="border: 1px solid red" <%}%>>
        <option value="">Choose</option>
          <%for(String yr: years){ %>
              <option value="<%=yr %>"><%=yr %></option>       
            
            <%} %>
        </select>
    </div>
    
    <div id="day_range">       
         By Date Range<br/>
        <input type="text" name="startdtRange" id="startdtRange"   <%if((enddtRange!=null && !enddtRange.isEmpty()) && (startdtRange.isEmpty())){%>style="border: 1px solid red"<%}%>/>
         <input type="text" name="enddtRange" id="enddtRange"  <%if((startdtRange!=null && !startdtRange.isEmpty()) && (enddtRange.isEmpty())){%>style="border: 1px solid red"<%}%>/>
            
    
    </div>
    <div>
      <Strong> Program Level</Strong><br/>
      <%
        List programLevel = facetsAndTags.get("program-level");
        
        for(int pi=0; pi<programLevel.size(); pi++){
        
             FacetsInfo programLevelList = (FacetsInfo)programLevel.get(pi);
           %>  
            <input type="checkbox"  id="<%=programLevelList.getFacetsTagId()%>" value="<%=programLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(programLevelList.getFacetsTagId())){ %>checked <%} %>/>
             <label for="<%=programLevelList.getFacetsTitle() %>"><%=programLevelList.getFacetsTitle()%>&nbsp;(<%=programLevelList.getCounts()%>)</label>
        	<br/>
        <%}%>  
    </div>
    <div>
      <br/><Strong>Region</Strong><br/>
            <select name="regions" id="regions">
                <option value="choose">Choose</option>
            <%for(String str: regions) {%>
                <option value="<%=str%>"><%=str%></option>
            <%} %>
            </select>   
    </div>
    <div>
       <br/><Strong>Categories</Strong><br/>
       <%

        // Get the categories
       List categoriesList = facetsAndTags.get("categories");
       
       
       for(int i=0;i<categoriesList.size();i++)
    	   {
    	    FacetsInfo facetsTags = (FacetsInfo)categoriesList.get(i);
    	    System.out.println("Title" +facetsTags.getFacetsTitle() +"Counts " +facetsTags.getCounts());
    	     %>
    	      <input type="checkbox" id="<%=facetsTags.getFacetsTagId()%>" value="<%=facetsTags.getFacetsTagId()%>" name="tags" <%if(set.contains(facetsTags.getFacetsTagId())){ %>checked <%} %>/>
              <label for="<%=facetsTags.getFacetsTitle() %>"><%=facetsTags.getFacetsTitle()%>&nbsp;(<%=facetsTags.getCounts()%>)</label>
              <br/>
    
    <%}%>
    </div>
    <input value="submit" type="submit"/>
    
    </form>
   
   
   
    </div>
   <div style="float:right; width:80%; ">
      <cq:include script="event-lists.jsp"/>
    </div>
    
    <div id="pagination">
       <%if(searchResults.getResultPages().size() > 1) 
    	 { 
    	    
    	    for(int i=0;i<searchResults.getResultPages().size(); i++)
    	    {   long offst = i*10;
    	   	  if(searchResults.getResultPages().get(i).isCurrentPage()){%>
    	   	  
       	         <%=i+1 %>
              <%}
   	    	  else{
   	    	    if(q!=null){%>
   	    	    	 <a x-cq-linkchecker="skip" href="<%=currentPage.getPath()%>.html?q=<%=q%>&offset=<%=offst%>"><%=i+1%></a>
   	    	    <%}else{%>
   	    	        <a x-cq-linkchecker="skip" href="<%=currentPage.getPath()%>.html?offset=<%=offst%>"><%=i+1%></a>
   	    	  <%}%>
   	    	      
    	         	   
                <%}
    	      }
    	    
    	  }  
        %>
    </div>
 
 
 
 
 </div>  
