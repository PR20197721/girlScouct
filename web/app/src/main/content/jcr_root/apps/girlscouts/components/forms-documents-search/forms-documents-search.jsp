<%@ page import="org.girlscouts.common.search.formsdocuments.FormsDocumentsSearch, java.util.Map,java.util.List,org.girlscouts.common.events.search.SearchResultsInfo, org.girlscouts.common.events.search.FacetsInfo,com.day.cq.search.result.Hit, org.girlscouts.common.search.DocHit,java.util.HashSet,java.util.*"%> 
<%@page import="javax.jcr.query.*,
                javax.jcr.*,
                com.day.cq.wcm.api.WCMMode,
				java.util.HashMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts.components.forms-documents-search" />
<cq:defineObjects/>
<script>
    function checkLen(){
        var x = document.getElementById("frmSrch");
        if( x!=null  ){
            x= $.trim( x.value ) ;
        }
        if( x.length <3 ){
            var isTag=0;
            var coffee = document.frm.tags;
            for (i = 0; i < coffee.length; i++) {
              if (coffee[i].checked) {
                isTag = isTag +1 ; 
              }
            }
            if( isTag>0 ){return true;}
            alert("Search queries must be greater than 2 characters.");
            return false;
        }
        return true;
    }
    function toggleOption() {
        if ($("#optionIndicatorId").attr("src") == "/etc/designs/girlscouts-usa-green/images/green-down-arrow.png") {
            $(".optionIndicator").attr("src", "/etc/designs/girlscouts-usa-green/images/green-up-arrow.png");
            $(".advancedSearch").hide();
        } else {
            $(".optionIndicator").attr("src", "/etc/designs/girlscouts-usa-green/images/green-down-arrow.png");
            $(".advancedSearch").show();
        }
    }
</script>
<%
// This is the path where the documents will reside
String q = request.getParameter("q");
String[] tags = request.getParameterValues("tags");
String suffix = slingRequest.getRequestPathInfo().getSuffix();
String formAction = currentPage.getPath()+".html";
String placeHolder = "Keyword Search";
String damPath = "#";
boolean isWCMMode = (WCMMode.fromRequest(request) == WCMMode.EDIT);
boolean thisIsAdvanced = false;

q= q==null ? "" : q.trim();
// xss escaped query string
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");
Set<String> tagsSet = new HashSet<String>();
if (tags != null) {
    thisIsAdvanced = true;
    for (String words : tags){
    	tagsSet.add(words);
    }
}
if (suffix != null) {
    thisIsAdvanced = true;
}
FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);
Map<String, List<FacetsInfo>> facetsAndTags = formsDocuImpl.loadFacets(slingRequest, currentPage.getAbsoluteParent(1).getName());
List<FacetsInfo> fdocs = facetsAndTags.get("forms_documents");
String useCustomTagList = properties.get("useCustomTagList", String.class);
String[] tagList = properties.get("tagList", String[].class);

try {
    if (useCustomTagList != null && useCustomTagList.equals("true") && tagList != null){
        List<FacetsInfo> newFDocs = new ArrayList<FacetsInfo>();
        for (int tagListCounter=0; tagListCounter<tagList.length; tagListCounter++){
            Node tagNode = resourceResolver.resolve(tagList[tagListCounter]).adaptTo(Node.class);
            String tagTitle = tagNode.getProperty("jcr:title").getString();
            for (int facetCounter = 0; facetCounter<fdocs.size(); facetCounter++){
                FacetsInfo currentFacet = (FacetsInfo)fdocs.get(facetCounter);
                if (currentFacet.getFacetsTitle().equals(tagTitle)){
                    newFDocs.add(currentFacet);
               }
            }
        }
        fdocs = newFDocs;
    } else {

    }
} catch(Exception e){
    log.error(e.getMessage());
}
%>
<div class="expandable">
	<div class="programLevel">
    	<form action="<%=xssAPI.encodeForHTMLAttr(formAction) %>" method="get" name="frm" onsubmit="return checkLen()">
	        <div id="searchBox" class="baseDiv">
	            <input type="text" name="q" id="frmSrch" class="searchField formsAndDocuments" placeholder="<%=placeHolder%>" value="<%=escapedQueryForAttr%>" />
	        </div>
	        <%if (thisIsAdvanced) {%>
				<script>				
			    	$(document).ready(function() {
			    		toggleOption();
			    	});
				</script>
			<%}%>
	        <div class="baseDiv toggleDisplay">
	            <a href="#" onclick="toggleOption()"><img id="optionIndicatorId" class="optionIndicator" src="/etc/designs/girlscouts-usa-green/images/green-up-arrow.png" width="15" height="20">&nbsp;Options&nbsp;<img class="optionIndicator" src="/etc/designs/girlscouts-usa-green/images/green-up-arrow.png" width="15" height="20"></a>
	        </div>
	        <div class="options formsSearchOptions baseDiv advancedSearch">
	            <div id="title">Categories</div>
	            <ul class="checkbox-grid small-block-grid-1 medium-block-grid-1 large-block-grid-2">
					<%
					// Here if we don't have forms_document page shouldn't blow-up
					try {
					    for(int pi=0; pi<fdocs.size(); pi++){
					        FacetsInfo fdocLevelList = (FacetsInfo)fdocs.get(pi);
							%>  
				            <li>
				                <input type="checkbox" id="<%=fdocLevelList.getFacetsTagId()%>" value="<%=fdocLevelList.getFacetsTagId()%>" name="tags" <%if(tagsSet.contains(fdocLevelList.getFacetsTagId())){ %>checked <%} %>/>
				                <label for="<%=fdocLevelList.getFacetsTagId() %>"><%=fdocLevelList.getFacetsTitle()%></label>
				            </li> 
					<%  }
					}catch(Exception e){}
					%>
	            </ul>
	        </div>
	        <div class="searchButtonRow baseDiv advancedSearch">
	            <input type="submit" value="Search" class="form-btn advancedSearchButton"/>
	        </div>
    	</form>
	</div>
	<%
	
	if((q!=null && !q.isEmpty()) ||  (tags != null)){ %>
		<div class="search" id="formsDocsListWrapper"></div>
		<script>
			var tags = new Array();
			<%
			if(tags != null){
				for (String tag : tags){%>
					tags.push('<%=xssAPI.encodeForJSString(tag) %>');
			   	<% }
		   	}%>
		   	var q = "<%=xssAPI.encodeForJSString(q)%>";
			var jsonPath = '<%=resource.getPath()%>';
			$(document).ready(function() {
				var formsDocsLoader = new FormsDocsLoader(jsonPath, $("#formsDocsListWrapper"),q , tags);
			});		
		</script>
	<%}%>
</div>
