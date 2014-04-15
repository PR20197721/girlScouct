<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,java.util.HashSet,
                 java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult,
                 java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo,
                 com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo" %>

<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%@include file="/apps/girlscouts/components/site-conf.jsp"%>

<%  


  HashMap<String,List<FacetsInfo>> facetsAndTags = (HashMap<String, List<FacetsInfo>>) request.getAttribute("facetsAndTags");
  
  if(null==facetsAndTags)
  {
%>
    <cq:include path="content/left/par/event-search" resourceType="girlscouts/components/event-search" />
<%  }
    facetsAndTags = (HashMap<String, List<FacetsInfo>>) request.getAttribute("facetsAndTags");

%>


<%
   String homepagePath = currentPage.getAbsoluteParent(2).getPath();
   String REGIONS = currentSite.get("locationsPath", homepagePath + "/locations");
   String YEARS = currentSite.get("eventsPath", homepagePath + "/events");
   long RESULTS_PER_PAGE = Long.parseLong(properties.get("resultsPerPage", "10"));

   String[] tags = request.getParameterValues("tags");
   HashSet<String> set = new HashSet<String>();
   if(tags!=null)
   {
        set = new HashSet<String>();
        for (String words : tags){
            set.add(words);
        }
   }

   String year=request.getParameter("year");
   String month = request.getParameter("month");
   String startdtRange = request.getParameter("startdtRange");
   String enddtRange = request.getParameter("enddtRange");

   ArrayList<String> regions = new ArrayList<String>();
   ArrayList<String> years = new ArrayList<String>();


   Iterator<Page> yrs= resourceResolver.getResource(YEARS).adaptTo(Page.class).listChildren();

   String formAction = currentPage.getPath()+".html";
   if(properties.get("formaction", String.class)!=null && properties.get("formaction", String.class).length()>0){
	   formAction = properties.get("formaction", String.class);
   }
		   
		   
   Iterator<Page> pages= resourceResolver.getResource(REGIONS).adaptTo(Page.class).listChildren();
   while(pages.hasNext()){
       regions.add(pages.next().getTitle());

   }
   while(yrs.hasNext())
   {
       years.add(yrs.next().getTitle());
   }

   SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
   Map<String, String> results = srchInfo.getResults();
   request.setAttribute("formAction", formAction);


%>
<div> 

   <div>
    <form action="<%=formAction%>" method="get" id="form">
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
             %>
              <input type="checkbox" id="<%=facetsTags.getFacetsTagId()%>" value="<%=facetsTags.getFacetsTagId()%>" name="tags" <%if(set.contains(facetsTags.getFacetsTagId())){ %>checked <%} %>/>
              <label for="<%=facetsTags.getFacetsTitle() %>"><%=facetsTags.getFacetsTitle()%>&nbsp;(<%=facetsTags.getCounts()%>)</label>
              <br/>
    
    <%}%>
    </div>
    <input value="submit" type="submit"/>
    
    </form>
   
   
   
    </div>
   
 
 
 
 </div>  

