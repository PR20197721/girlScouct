<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch, com.day.cq.search.QueryBuilder,java.util.Map,java.util.List,org.girlscouts.web.events.search.SearchResultsInfo, org.girlscouts.web.events.search.FacetsInfo,com.day.cq.search.result.Hit, org.girlscouts.web.search.DocHit,java.util.HashSet"%> 
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%
// This is the path where the documents will reside
String path = properties.get("./srchLocation", "");
if(path.isEmpty()){  
	path = "/content/dam/girlscouts-shared/en/documents";
}
FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);
QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
String param ="";
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");
if(q!=null && !q.isEmpty()){
	//Add Parameter to option
	param="q="+escapedQueryForAttr;
}
String[] tags = new String[]{};

HashSet<String> set = new HashSet<String>();
boolean thisIsAdvanced = false;
if (request.getParameterValues("tags") != null) {
	thisIsAdvanced = true;
	tags = request.getParameterValues("tags");
	String tagParams="";
	set = new HashSet<String>();
	for (String words : tags){
		set.add(words);
		tagParams+="&tags="+words;
	}
	param+=param!=null?tagParams:tagParams.replaceFirst("&", " ").trim();
} 

formsDocuImpl.executeSearch(slingRequest, queryBuilder, q, path, tags);
Map<String,List<FacetsInfo>> facetsAndTags = formsDocuImpl.getFacets();
List<Hit> hits = formsDocuImpl.getSearchResultsInfo().getResultsHits();
String suffix = slingRequest.getRequestPathInfo().getSuffix();
if (suffix != null) {
	thisIsAdvanced = true;
}
String formAction = currentPage.getPath()+".html";
String placeHolder = "Keyword Search";
String advanceLink = currentPage.getPath()+".html"+"/advance";
String link = currentPage.getPath()+".html";

if(param!=null && !param.isEmpty()){
	link+="?"+param;
}

%>
<div class="expandable">
<div class="programLevel">
	<form action="<%=formAction%>" method="get">
		<div id="searchBox" class="baseDiv">
			<input type="text" name="q" class="searchField formsAndDocuments" placeholder="<%=placeHolder%>" value="<%=escapedQueryForAttr%>" />
		</div>
<script>
	function toggleOption() {
		if ($("#optionIndicatorId").attr("src") == "/etc/designs/girlscouts-usa-green/images/green-down-arrow.png") {
			$(".optionIndicator").attr("src", "/etc/designs/girlscouts-usa-green/images/green-up-arrow.png");
			$(".advancedSearch").hide();
		} else {
                        $(".optionIndicator").attr("src", "/etc/designs/girlscouts-usa-green/images/green-down-arrow.png");
                        $(".advancedSearch").show();
		}
	}
	$(document).ready(function() {
<%
	if (thisIsAdvanced) {
%>
		toggleOption();
<%
	}
%>
	});
</script>
		<div class="baseDiv toggleDisplay">
			<a href="#" onclick="toggleOption()"><img id="optionIndicatorId" class="optionIndicator" src="/etc/designs/girlscouts-usa-green/images/green-up-arrow.png" width="15" height="20">&nbsp;Options&nbsp;<img class="optionIndicator" src="/etc/designs/girlscouts-usa-green/images/green-up-arrow.png" width="15" height="20"></a>
		</div>
		<div class="options formsSearchOptions baseDiv advancedSearch">
			<div id="title">Categories</div>
			<ul class="checkbox-grid small-block-grid-1 medium-block-grid-1 large-block-grid-2">
<%
List fdocs = facetsAndTags.get("forms_documents");
for(int pi=0; pi<fdocs.size(); pi++){
	FacetsInfo fdocLevelList = (FacetsInfo)fdocs.get(pi);
%>  
				<li>
					<input type="checkbox" id="<%=fdocLevelList.getFacetsTagId()%>" value="<%=fdocLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(fdocLevelList.getFacetsTagId())){ %>checked <%} %>/>
					<label for="<%=fdocLevelList.getFacetsTagId() %>"><%=fdocLevelList.getFacetsTitle()%> (<%=fdocLevelList.getCounts()%>)</label>
				</li> 
<%	}
%>

			</ul>
		</div>
		
		<div class="searchButtonRow baseDiv advancedSearch">
			<input type="submit" value="Search" class="form-btn advancedSearchButton"/>
		</div>
	</form>
</div>
<% if((q!=null && !q.isEmpty()) ||  (request.getParameterValues("tags") != null)){ %>
<div class="search">
<%
	for(Hit hit: hits) {
		DocHit docHit = new DocHit(hit);
		String pth = docHit.getURL();
		int idx = pth.lastIndexOf('.');
		String extension = idx >= 0 ? pth.substring(idx + 1) : "";
%>
		<br/>
<%
		if(!extension.isEmpty() && !extension.equals("html")){
%>
		<span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
<%
		}
%>
		<a href="<%=pth%>"><%=docHit.getTitle() %></a>
		<div><%=docHit.getExcerpt()%></div>
		<br/>
<%
	}
%>
</div>
<%}%>
</div>
