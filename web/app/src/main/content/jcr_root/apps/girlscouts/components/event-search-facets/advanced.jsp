<%@ page import="com.day.cq.tagging.TagManager,java.util.ArrayList,
                java.util.HashSet, java.util.Locale,java.util.Map,
                java.util.Iterator,java.util.HashMap,java.util.List,
                java.util.Set,com.day.cq.search.result.SearchResult,
                java.util.ResourceBundle,com.day.cq.search.QueryBuilder,
                javax.jcr.PropertyIterator, 
                com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,                
                java.util.Collections, javax.jcr.Node,
				org.girlscouts.common.search.formsdocuments.FormsDocumentsSearch,
				java.util.Map,java.util.List,
				org.girlscouts.common.events.search.FacetsInfo"
                %>
<%@include file="/libs/foundation/global.jsp"%>
<!--GSWP-2056: Created clientlibs and added categories -->
<cq:includeClientLib categories="cq.jquery.ui,apps.girlscouts,app.girlscouts.eventSearchFacets" />
<cq:defineObjects/>
<%@include file="/apps/girlscouts/components/global.jsp"%>


<%  
    Map<String,List<FacetsInfo>> facetsAndTags = (HashMap<String,List<FacetsInfo>>)request.getAttribute("facetsAndTags");
    if(null==facetsAndTags) {
    
%>
	<cq:include path="content/left/par/event-search" resourceType="girlscouts/components/event-search" />
<%
    }
    String returnAction = currentPage.getPath()+".html";
    String formAction = currentPage.getPath()+".advanced.html";
    request.setAttribute("formAction", formAction);
    Set <String> regions = new HashSet<String>();
    SearchResultsInfo srchResults = (SearchResultsInfo)request.getAttribute("eventresults");
    if(srchResults != null){
	    List<String> sresults = srchResults.getResults();
	    List<String> setOfRegions = srchResults.getRegion();
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
	    String YEARS = currentSite.get("eventPath", homepagePath + "/events");
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
	    String region = "";
	    try{
	    	region = request.getParameter("regions");
	    }catch(Exception e){}
	   
	    ArrayList<String> years = new ArrayList<String>();
	    Iterator<Page> yrs= resourceResolver.getResource(YEARS).adaptTo(Page.class).listChildren();
	   
	    if(properties.get("formaction", String.class)!=null && properties.get("formaction", String.class).length()>0){
	        formAction = properties.get("formaction", String.class);
	    }
	    while(yrs.hasNext()) {
	        years.add(yrs.next().getTitle());
	    }
	    SearchResultsInfo srchInfo = (SearchResultsInfo)request.getAttribute("eventresults");
	    List<String> results = srchInfo.getResults();
	    String m = request.getParameter("m"); 
	    String eventSuffix = slingRequest.getRequestPathInfo().getSuffix();
	    String placeHold = slingRequest.getParameter("search") != null ? slingRequest.getParameter("search") : "Keywords";
	    String placeholder = slingRequest.getParameter("q") != null ? slingRequest.getParameter("q") : placeHold;

%>

<%
    //GSWP-2003 : custom tags event search facets
    FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);

	List<FacetsInfo> programLevel = new ArrayList<FacetsInfo>();
	        List<FacetsInfo> categoryLevel = new ArrayList<FacetsInfo>();
	
	boolean useEventCustomTagList = properties.get("useEventCustomTagList", false);
	String[] eventPgmTagList = properties.get("eventPgmTagList", String[].class);
	String[] eventCtgTagList = properties.get("eventCtgTagList", String[].class);
	try{
		//get program and category level custom tags
	    if (useEventCustomTagList){
			if(null != eventPgmTagList){
	
				programLevel = formsDocuImpl.loadFacetsFromList(slingRequest, eventPgmTagList);
			}
			if(null != eventCtgTagList){
				categoryLevel = formsDocuImpl.loadFacetsFromList(slingRequest, eventCtgTagList);
			}
	
	    } 
	    //get program and category level default tags 
	    else {
			programLevel = facetsAndTags.get("program-level");
	        categoryLevel = facetsAndTags.get("categories");
	    }
	} catch(Exception e){}
      //GSWP-2003 : custom tags event search facets
 %>

<div class="baseDiv anActivity small-24 large-24 medium-24 columns">
   <div class="row collapse">
        <div class="small-1 large-1 medium-1 columns">
        	<div><a href="#" onclick="toggleWhiteArrow()"><img id="whiteArrowImg" src="/etc/designs/girlscouts-usa-green/images/white-down-arrow.png" width ="10" height="15"/></a></div>
        </div>
    	 <div class="small-23 large-23 medium-23 columns">
   			<div class="title" id="eventsTitle"><span class="activity-color">Find an Activity</span></div>
   		</div>
   </div>
</div>

<div id="events-display">



<form action="<%=formAction%>" method="get" id="form" onsubmit="return validateForm()">
	<div class="baseDiv programLevel row collapse topSearchRow">

	   <div class="small-24 medium-6 large-7 columns">
				<div class="title"> By Keyword </div>
				<!--GSWP-2056- added value attribute to pass last search text from simple search to adv search -->
				<input id="keywordInput" type="text" name="q" placeholder="Keywords" class="searchField" style="width:140px;height:25px;" <%if(placeholder != null && !"Keywords".equals(placeholder)){ %>value="<%=placeholder%>"<%}%> />
		</div>
		<div class="small-12 medium-6 large-6 event-region columns">
			<div class="title"> Region </div>
			<div class="styled-select">
				<select name="regions" id="regions">
					<option value="choose">Choose Region</option>
					<%for(String str: setOfRegions) {%>
						<option value="<%=str%>" <%if(region!=null && region.equalsIgnoreCase(str)){%> selected <%} %>><%=str%></option>
					<%} %>
				</select>
			</div>	
		</div>
		<div class="small-11 medium-12 large-10 columns">
			<div class="title" id="dateTitle">By Date</div>
			<div class="row event-activity collapse">
				<div class="small-12 medium-12 large-12 columns">
					<input type="text" name="startdtRange" id="startdtRange" class="searchField" <%if((enddtRange!=null && !enddtRange.isEmpty()) && (startdtRange.isEmpty())){%>style="border: 1px solid red"<%}%> placeholder="From Today"/>
				</div>
				<div class="small-11 medium-11 large-11 columns">
					<input type="text" name="enddtRange" id="enddtRange" class="searchField" <%if((startdtRange!=null && !startdtRange.isEmpty()) && (enddtRange.isEmpty())){%>style="border: 1px solid red"<%}%> placeholder="To"/>
				</div>
			</div>
			<p id ="dateErrorBox" ></p>
		</div>
	</div>
	
	<div class="baseDiv sortBy programLevel" style="width: 100%; padding: 10px; padding-top: 0px;">
        <div class="title"> Sort By </div>
        <div class="styled-select sort-select-bar">
            <select name="dateAdded" id="addDate">
                <option value="none">Event Date (Default)</option>
                <option value="descending">Date Added: Descending</option>
                <option value="ascending">Date Added: Ascending</option>
            </select>
        </div>
    </div>
	
	<%	if(!programLevel.isEmpty()){ %>
		<div class="baseDiv programLevel" >
	   <div class="title"> By Program Level </div>
	    <ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2 categoriesList">
	        <%

	        try {
            //List programLevel = facetsAndTags.get("program-level");
		        for(int pi=0; pi<programLevel.size(); pi++){
		            FacetsInfo programLevelList = (FacetsInfo)programLevel.get(pi);
		            %> 
		            <li>     
		                <input type="checkbox"  id="<%=programLevelList.getFacetsTagId()%>" value="<%=programLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(programLevelList.getFacetsTagId())){ %>checked <%} %>/>
		                <label for="<%=programLevelList.getFacetsTitle() %>"><%=programLevelList.getFacetsTitle()%></label>
		            </li>
		            <%
		           }
	        }catch(Exception e){}
	        %>
		</ul>        
	</div>
	<% }
	
	if(!categoryLevel.isEmpty()){
		%>
		<div class="baseDiv programLevel" >
		<div class="title">By Category </div>
		<ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2 categoriesList">
	<%
	    // Get the categories

		try{
                //List<FacetsInfo> facetsInfoList = facetsAndTags.get("categories");
			for (FacetsInfo facetsInfo: categoryLevel) {
	%>
	    	<li>
	    		<input type="checkbox" id="<%=facetsInfo.getFacetsTagId()%>" value="<%=facetsInfo.getFacetsTagId()%>" name="tags" <%if(set.contains(facetsInfo.getFacetsTagId())){ %>checked <%} %>/>&nbsp;<label for="<%=facetsInfo.getFacetsTitle() %>"><%=facetsInfo.getFacetsTitle()%></label>
	    	</li>
	<%
			}
		}catch(Exception e){}
		
	%>
		</ul>
	</div>
	<% }	%>
	

	<div class="searchButtonRow baseDiv programLevel">
            <a id="smplSearch"style="width: 180px; margin: 0; height: 43px; padding-top: 10.5px; padding-right: 15px;"href="<%=returnAction%>" >Simple Search</a>
	    	<input type="submit" value="Search" id="sub" class="form-btn advancedSearchButton"/>
	</div>
  


</form>

</div>
<% } %>
