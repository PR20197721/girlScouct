<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch, com.day.cq.search.QueryBuilder,java.util.Map,java.util.List,org.girlscouts.web.events.search.SearchResultsInfo, org.girlscouts.web.events.search.FacetsInfo,com.day.cq.search.result.Hit, org.girlscouts.web.search.DocHit,java.util.HashSet,java.util.*"%> 
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

String formDocumentContentPath = properties.get("./form-document-path","");
if(formDocumentContentPath.isEmpty()){
	formDocumentContentPath = "/content/gateway/en/about-our-council/forms-documents";
}

FormsDocumentsSearch formsDocuImpl = sling.getService(FormsDocumentsSearch.class);
QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
String q = request.getParameter("q");
String param ="";

// xss escaped query string
final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");

// user selected tags
String[] tags = new String[]{};

Set<String> set = new HashSet<String>();
boolean thisIsAdvanced = false;
if (request.getParameterValues("tags") != null) {
	thisIsAdvanced = true;
	tags = request.getParameterValues("tags");
	String tagParams="";
	for (String words : tags){
		set.add(words);
	}
	
}
try{
	formsDocuImpl.executeSearch(slingRequest, queryBuilder, q, path, tags, currentPage.getAbsoluteParent(1).getName(),formDocumentContentPath);
}catch(Exception e){}
Map<String,List<FacetsInfo>> facetsAndTags = formsDocuImpl.getFacets();
List<Hit> hits = formsDocuImpl.getSearchResultsInfo().getResultsHits();
String suffix = slingRequest.getRequestPathInfo().getSuffix();
if (suffix != null) {
	thisIsAdvanced = true;
}
String formAction = currentPage.getPath()+".html";
String placeHolder = "Keyword Search";

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
// Here if we don't have forms_document page shouldn't blow-up
try {
	for(int pi=0; pi<fdocs.size(); pi++){
		FacetsInfo fdocLevelList = (FacetsInfo)fdocs.get(pi);
%>  
			<li>
				<input type="checkbox" id="<%=fdocLevelList.getFacetsTagId()%>" value="<%=fdocLevelList.getFacetsTagId()%>" name="tags" <%if(set.contains(fdocLevelList.getFacetsTagId())){ %>checked <%} %>/>
				<label for="<%=fdocLevelList.getFacetsTagId() %>"><%=fdocLevelList.getFacetsTitle()%></label>
			</li> 
<%	}
}catch(Exception e){}
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
try{
	for(Hit hit: hits) {
		DocHit docHit = new DocHit(hit);
		String pth = docHit.getURL();
		String title = docHit.getTitle();
		String description = docHit.getDescription();

		Node node = resourceResolver.resolve(hit.getPath()).adaptTo(Node.class);

		//GSWS-132: Prevents unwanted (folder) results
		if(!node.getPrimaryNodeType().getName().equals("dam:Asset") && !node.getPrimaryNodeType().getName().equals("cq:Page")){
			continue;
		}

		else if(node.hasNode("jcr:content/metadata")){
			Node metadata = node.getNode("jcr:content/metadata");
            //The title set in the dam is dc:title, and the description is dc:description
            
            // Temporary Hit fix for handling multiple description and title
            if(metadata.hasProperty("dc:title")){
            	if(metadata.getProperty("dc:title").isMultiple()) {
					Value[] value = null;

					value = metadata.getProperty("dc:title").getValues();
					if((!value[0].getString().isEmpty()) && (value[0].getString()!=null)) {
						title = value[0].getString();

					}
				}
            	else{
                	title = metadata.getProperty("dc:title").getString();
            	}
            }
            // In case title wasn't set. This usually includes the file extension
            else if(metadata.hasProperty("jcr:title")){
                title = metadata.getProperty("jcr:title").getString();
            }
            if(metadata.hasProperty("dc:description")){
                // Hotfix for description been multivalue- If description is empty we will try-to
				//identify if dc:description is multivalued
				if(metadata.getProperty("dc:description").isMultiple()) {
					Value[] value = null;
					value = metadata.getProperty("dc:description").getValues();
					if((!value[0].getString().isEmpty()) && (value[0].getString()!=null)) {
						description = value[0].getString();
					}
				}
                else{
                    Property fileDesc = metadata.getProperty("dc:description");
                    description = fileDesc.getString();
                }
            }
        }

		int idx = pth.lastIndexOf('.');
		String extension = idx >= 0 ? pth.substring(idx + 1) : "";
		String newWindow = "";
%>
		<br/>
<%
		if(!extension.isEmpty() && !extension.equals("html")){
			newWindow = "target=_blank";
%>
		<span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
<%
		}
%>
		<a href="<%=pth%>" <%=newWindow %>><%=title %></a>
<%
		if (q != null && !q.isEmpty()) {
			String excerpt = docHit.getExcerpt();
			if(excerpt!=null && !"".equals(excerpt) && (description==null || "".equals(description))) {
%>
				<div><%= excerpt %></div>
<%
			} else {
%>
                <div><%=description%></div>
<%
			}
		} else {

%>
		<div><%= description %></div>
<%
		}
%>
		<br/>
<%
	}
}catch(Exception e){ System.err.println(e.getMessage()); }
%>
</div>
<%}%>
</div>
