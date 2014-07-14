<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,
                java.util.HashSet, java.util.Locale,java.util.Map,
                java.util.Iterator,java.util.HashMap,java.util.List,
                java.util.Set,com.day.cq.search.result.SearchResult,
                java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
                javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, 
                com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,
                org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,
                java.util.Collections"
                %>
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%  
    HashMap<String,List<FacetsInfo>> facetsAndTags = (HashMap<String, List<FacetsInfo>>) request.getAttribute("facetsAndTags");
    if(null==facetsAndTags) {
%>
<cq:include path="content/left/par/event-search" resourceType="girlscouts/components/event-search" />
<%
    }
    Set <String> regions = new HashSet<String>();
    SearchResultsInfo srchResults = (SearchResultsInfo)request.getAttribute("eventresults");
    List<String> sresults = srchResults.getResults();
    for(String result: sresults){
        Node node =  resourceResolver.getResource(result).adaptTo(Node.class);
        try{
            Node propNode = node.getNode("jcr:content/data");
            if(propNode.hasProperty("region"))
                {
                  regions.add(propNode.getProperty("region").getString());
            
                 }
        }catch(Exception e){}
    }  
    List<String> sortList = new ArrayList<String>(regions);
    Collections.sort(sortList);
   
    facetsAndTags = (HashMap<String, List<FacetsInfo>>) request.getAttribute("facetsAndTags");
    String homepagePath = currentPage.getAbsoluteParent(2).getPath();
    //String REGIONS = currentSite.get("locationsPath", homepagePath + "/locations");
    String YEARS = currentSite.get("eventsPath", homepagePath + "/events");
    long RESULTS_PER_PAGE = Long.parseLong(properties.get("resultsPerPage", "10"));
    String[] tags = request.getParameterValues("tags");
    HashSet<String> set = new HashSet<String>();
    if(tags!=null) {
        set = new HashSet<String>();
        for (String words : tags){
            set.add(words);
        }
    }
    String year=request.getParameter("year");
    String month = request.getParameter("month");
    String startdtRange = request.getParameter("startdtRange");
    String enddtRange = request.getParameter("enddtRange");

   
    ArrayList<String> years = new ArrayList<String>();
    Iterator<Page> yrs= resourceResolver.getResource(YEARS).adaptTo(Page.class).listChildren();
    String formAction = currentPage.getPath()+".html";
    if(properties.get("formaction", String.class)!=null && properties.get("formaction", String.class).length()>0){
        formAction = properties.get("formaction", String.class);
    }
    while(yrs.hasNext()) {
        years.add(yrs.next().getTitle());
    }
    SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
    List<String> results = srchInfo.getResults();
    request.setAttribute("formAction", formAction);
    String m = request.getParameter("m"); 
    String eventSuffix = slingRequest.getRequestPathInfo().getSuffix();
  
%>
<% if(null!=eventSuffix){ %>

<div class="baseDiv anActivity small-24 large-24 medium-24 columns">
   <div id="title">Find an Activity</div>
</div>

<form action="<%=formAction%><%=eventSuffix %>" method="get" id="form">
<div class="baseDiv programLevel">
  <div class="item">
    <div id="title"> By Keyword </div>
      <input type="text" name="q" placeholder="Keywords"/>
  </div>
  <div class="item"> 
    <div id="title"> Region  </div>
      <select name="regions" id="regions">
        <option value="choose">Choose</option>
            <%for(String str: sortList) {%>
                <option value="<%=str%>"><%=str%></option>
            <%} %>
      </select>
  </div>
  <div class="item withoutPadding">
    <div id="title"> By Date  </div>
      <div class="inputField">
        <input id="date" type="text" name="startdtRange" <%if((enddtRange!=null && !enddtRange.isEmpty()) && (startdtRange.isEmpty())){%>style="border: 1px solid red"<%}%> placeholder="From Today"/>
      </div>
       <div class="inputField withoutPadding">  
        <input type="text" id="date" name="enddtRange" <%if((startdtRange!=null && !startdtRange.isEmpty()) && (enddtRange.isEmpty())){%>style="border: 1px solid red"<%}%> placeholder="To"/>
    </div>
  </div>

</div>
<div class="baseDiv programLevel small-8 large-8 medium-8 columns" >
   <div id="title"> By Program  </div>
        <%
         List programLevel = facetsAndTags.get("program-level");
        for(int pi=0; pi<programLevel.size(); pi++){
            FacetsInfo programLevelList = (FacetsInfo)programLevel.get(pi);
            %>      
                <input type="checkbox"  id="<%=programLevelList.getFacetsTagId()%>" value="<%=programLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(programLevelList.getFacetsTagId())){ %>checked <%} %>/>
                <label for="<%=programLevelList.getFacetsTitle() %>"><%=programLevelList.getFacetsTitle()%></label>
            <%
           }
        %>
</div>
<div class="baseDiv programLevel small-8 large-8 medium-8 columns" >
   <div id="title">
  Categories </div>
<%
    // Get the categories
    List categoriesList = facetsAndTags.get("categories");
    for(int i=0;i<categoriesList.size();i++) {
        FacetsInfo facetsTags = (FacetsInfo)categoriesList.get(i);
%>
        <input type="checkbox" id="<%=facetsTags.getFacetsTagId()%>" value="<%=facetsTags.getFacetsTagId()%>" name="tags" <%if(set.contains(facetsTags.getFacetsTagId())){ %>checked <%} %>/>
        <label for="<%=facetsTags.getFacetsTitle() %>"><%=facetsTags.getFacetsTitle()%></label>
        
<%
    }
%>
</div>
<div class="baseDiv programLevel">
  <input type="submit" value="Search" id="sub" class="form-btn pull-right">
</div>      
</form>
<%}if(null==eventSuffix){%>
<div class="row">
    <div class="small-24 large-24 medium-24 columns">&nbsp;</div>

</div>

<div class="row">
     <div class="small-12 large-12 medium-12 columns">
         <cq:include path="search-box" resourceType="girlscouts/components/search-box" />
     </div>
     <div class="small-12 large-12 medium-12 columns">
        <span id="advSearch">
           <a href="<%=currentPage.getPath()%>.html/advanced">Advanced Search</a>
       </span>
     </div>
</div>
<%}%>


