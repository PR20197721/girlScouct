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
    Map<String,List<FacetsInfo>> facetsAndTags = (HashMap<String,List<FacetsInfo>>)request.getAttribute("facetsAndTags");
    if(null==facetsAndTags) {
    
%>
	<cq:include path="content/left/par/event-search" resourceType="girlscouts/components/event-search" />
<%
    }


    Set <String> regions = new HashSet<String>();
    SearchResultsInfo srchResults = (SearchResultsInfo)request.getAttribute("eventresults");
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
    String formAction = currentPage.getPath()+".advanced.html";
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

<script>
function toggleWhiteArrow() {
	$('#events-display').toggle();
	if ($('#whiteArrowImg').attr('src') == "/etc/designs/girlscouts-usa-green/images/white-right-arrow.png") {
		$('#whiteArrowImg').attr('src', "/etc/designs/girlscouts-usa-green/images/white-down-arrow.png");
	} else {
		$('#whiteArrowImg').attr('src', "/etc/designs/girlscouts-usa-green/images/white-right-arrow.png");
	}
}
</script>
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
	<div class="baseDiv programLevel row collapse">

	   <div class="small-24 medium-6 large-7 columns">
				<div class="title"> By Keyword </div>
				<input type="text" name="q" placeholder="Keywords" class="searchField" style="width:140px;height:25px;" />
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
	<div class="baseDiv programLevel" >
	   <div class="title"> By Program Level </div>
	    <ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2 categoriesList">
	        <%
	         
	        try {
		        List programLevel = facetsAndTags.get("program-level");
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
	<div class="baseDiv programLevel" >
		<div class="title">By Category </div>
		<ul class="small-block-grid-1 medium-block-grid-2 large-block-grid-2 categoriesList">
	<%
	    // Get the categories
		
		try{
			List<FacetsInfo> facetsInfoList = facetsAndTags.get("categories");
			for (FacetsInfo facetsInfo: facetsInfoList) {
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

	<div class="searchButtonRow baseDiv programLevel">
	    	<input type="submit" value="Search" id="sub" class="form-btn advancedSearchButton"/>
	</div>
  


</form>
<script>


$(function() {

    $( "#startdtRange" ).datepicker({minDate: 0,
beforeShowDay: function(d) {


    if($('#enddtRange').val() == "" || $('#enddtRange').val() == undefined){
		return [true, "","Available"]; 
    }

    var dateString = (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear();

    //alert((new Date(dateString) < new Date($('#sch_endDate').val())) + " " + dateString + "<" + $('#sch_endDate').val());

    if(+(new Date(dateString)) <= +(new Date($('#enddtRange').val()))){
		return [true, "","Available"];
    }

	return [false, "","unAvailable"]; 

    }
                                    });

    $( "#enddtRange" ).datepicker({minDate: 0,
beforeShowDay: function(d) {


    if($('#startdtRange').val() == "" || $('#startdtRange').val() == undefined){
		return [true, "","Available"]; 
    }

    var dateString = (d.getMonth() + 1) + "/" + d.getDate() + "/" + d.getFullYear();

    //alert((new Date(dateString) < new Date($('#sch_endDate').val())) + " " + dateString + "<" + $('#sch_endDate').val());

    if(+(new Date(dateString)) >= +(new Date($('#startdtRange').val()))){
		return [true, "","Available"];
    }

	return [false, "","unAvailable"]; 

    }
                                  });

  });

    function validateForm(){


        if($('#enddtRange').val() == "" && $('#startdtRange').val() == ""){
			return true;
        }

		if(!isDate($('#enddtRange').val())){
			displayError("Invalid End Date");
			return false;
		}

		if(!isDate($('#startdtRange').val())){
			displayError("Invalid Start Date");
			return false;
		}
		

        if(new Date($('#enddtRange').val()) < new Date($('#startdtRange').val())){
			displayError("End Date cannot be less than Start Date");

            return false;
        }else{
            document.getElementById("dateErrorBox").innerHTML = "";
			return true;
        }

    }

function displayError(errorMessage){
	document.getElementById("dateErrorBox").innerHTML = errorMessage;
	document.getElementById("dateTitle").style.color = "#FF0000";
    document.getElementById("dateErrorBox").style.color = "#FF0000";
	document.getElementById("dateErrorBox").style.fontSize = "x-small";
	document.getElementById("dateErrorBox").style.fontWeight = "bold";
	document.getElementById("eventsTitle").scrollIntoView();

}

    
function isDate(txtDate)
{
    var currVal = txtDate;
    if(currVal == '')
        return false;
    
    var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/; //Declare Regex
    var dtArray = currVal.match(rxDatePattern); // is format OK?
    
    if (dtArray == null) 
        return false;
    
    //Checks for mm/dd/yyyy format.
    dtMonth = dtArray[1];
    dtDay= dtArray[3];
    dtYear = dtArray[5];        
    
    if (dtMonth < 1 || dtMonth > 12) 
        return false;
    else if (dtDay < 1 || dtDay> 31) 
        return false;
    else if ((dtMonth==4 || dtMonth==6 || dtMonth==9 || dtMonth==11) && dtDay ==31) 
        return false;
    else if (dtMonth == 2) 
    {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay> 29 || (dtDay ==29 && !isleap)) 
                return false;
    }
    return true;
}



</script>
</div>

