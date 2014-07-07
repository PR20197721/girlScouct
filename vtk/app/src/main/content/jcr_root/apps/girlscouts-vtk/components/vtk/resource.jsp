<%@page import="java.util.Iterator, java.util.Map, java.util.HashMap, javax.jcr.Session, org.apache.sling.api.resource.ResourceResolver, org.apache.sling.api.resource.Resource, com.day.cq.wcm.api.PageManager, com.day.cq.wcm.api.Page, com.day.cq.dam.api.Asset, com.day.cq.search.PredicateGroup, com.day.cq.search.Query, com.day.cq.search.QueryBuilder, com.day.cq.search.result.SearchResult, org.girlscouts.vtk.dao.*, org.girlscouts.vtk.models.user.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<%
	final String RESOURCE_SEARCH_PROMPT = "type in a search word or term here";
	final String MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
	final String TYPE_MEETING_AIDS = "meeting-aids";

	final QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
%>
<%@include file="include/session.jsp"%>
<%-- VTK tab --%>
<%!
    String activeTab = "resource";
    boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.0/themes/smoothness/jquery-ui.css">
<script>
	$(function() {
		var cache = {};
		$( "#resourceSearchField" ).autocomplete({
			minLength: 3,
			minChar: 3,
			source: function( request, response ) {
				var term = request.term;
				$("#searchResults").html("");
				$("#resourceSearchField" ).addClass('ui-autocomplete-loading');
				$.getJSON( 
					"/content/girlscouts-vtk/controllers/vtk.getdata.html?q="+term,
					request, function() {
                                                $( "#resourceSearchField" ).removeClass('ui-autocomplete-loading');
					}
				).done(function() {
					$("#searchResults").load("/content/girlscouts-vtk/controllers/vtk.searchR.html?rand="+Date.now());
				}).fail(function() {
                                        $("#searchResults").load("/content/girlscouts-vtk/controllers/vtk.searchR.html?rand="+Date.now());
					$( "#resourceSearchField" ).removeClass('ui-autocomplete-loading');
				});
			}
		});
	});
  
  function applyAids(aid, aidName){


	 		var link = "/content/girlscouts-vtk/controllers/vtk.asset.html?aidId="+ aid+"&aidName="+encodeURIComponent(aidName);
	      
	 		$( "#schedModal" ).load(link, function( response, status, xhr ) {
	                if ( status == "error" ) {
	                        var msg = "Sorry but there was an error: ";
	                        $( "#error" ).html( msg + xhr.status + " " + xhr.statusText );
	                }else{
				$( "#schedModal" ).dialog({
					width:920,
					modal:true,
					dialogClass:"modalWrap"
				});
				$(".ui-dialog-titlebar").hide();
			}
		});
	}

	function xClose() {
		$("#schedModal").dialog( "close" );
	}
</script>

<div id="schedModal"></div>

<h1>Search For Resources</h1>
<div class="ui-widget">
	<input type="text" id="resourceSearchField" name="q" placeholder="<%=RESOURCE_SEARCH_PROMPT%>" class="vtkSearchField" />
</div>
<div id="searchResults"></div>

<%-- categories --%>
<h1>Browse Resources by Category</h1>

<%
final PageManager manager = (PageManager)resourceResolver.adaptTo(PageManager.class);
try {
	// TODO: this field should come from Salesforce
	final String ROOT_PAGE_PATH = "/content/gateway/en/resources";

	final Page rootPage = manager.getPage(ROOT_PAGE_PATH);

	Iterator<Page> majorIter = rootPage.listChildren();

	int majorCount = 0;
	while (majorIter.hasNext()) {
		majorIter.next();
		majorCount++; 
	}
	majorIter = rootPage.listChildren();
%>

<ul class="small-block-grid-<%= majorCount %>">
<% 
	while (majorIter.hasNext()) { 
		Page currentMajor = majorIter.next();
%>
	<li>
		<div><%= currentMajor.getTitle() %></div>
<%
		Iterator<Page> minorIter = currentMajor.listChildren();
		minorIter = currentMajor.listChildren();
		while (minorIter.hasNext()) {
			Page currentMinor = minorIter.next();
			// TODO: encode URL
			String link = "?category=" + currentMinor.getPath();
			String title = currentMinor.getTitle();
			long minorCount;
			if (currentMinor.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
				minorCount = countAidAssets(
					MEETING_AID_PATH,
					queryBuilder,
					resourceResolver.adaptTo(Session.class)
				);
			} else {
				minorCount = countAllChildren(currentMinor) - 1;
			} 
%>
		<div><a href="<%= link %>"><%= title %> (<%= minorCount %>)</a></div>
<%
		}
%>
	</li>
<%
	} 
%>
</ul>
<%
} catch (Exception e) {
log.error("Cannot get VTK meeting aid categories: " + e.getMessage());
}
%>

<%-- display aids --%>
<%
String categoryParam = (String)request.getParameter("category");
Page categoryPage = manager.getPage(categoryParam);

if (categoryPage != null) {
	if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
%>
<%= displayAidAssets(MEETING_AID_PATH, resourceResolver) %>
<%
	} else {
%>
<div><%= categoryPage.getTitle() %></div>
<ul>
<% 
		StringBuilder builder = new StringBuilder();
		Iterator<Page> resIter = categoryPage.listChildren();
		while (resIter.hasNext()) {
			Page resPage = resIter.next();
			displayAllChildren(resPage, builder);
		}
%>
<%= builder.toString() %>
</ul>
<%
	}
}
%>
	<%!
	private long countAllChildren(Page page) {
		// TODO: Need an effecient way.
		if (page == null) {
			return 0;
		}

		long count = 1;
		Iterator<Page> iter = page.listChildren();
		while (iter.hasNext()) {
			Page currentPage = iter.next();
			count += countAllChildren(currentPage);
		}
		return count;
	}

	private void displayAllChildren(Page rootPage, StringBuilder builder) {
		builder.append("<li>");
		String path = rootPage.getPath() + ".plain.html";
		// TODO: Get the link back once the dialog is styled
		builder.append(rootPage.getTitle());
		Iterator<Page> iter = rootPage.listChildren();
		while (iter.hasNext()) {
			Page childPage = iter.next();
			builder.append("<ul>");
			displayAllChildren(childPage, builder);
			builder.append("</ul>");
		}
		builder.append("</li>");
	}

	private long countAidAssets(String path, QueryBuilder builder, Session session) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("path", path);
		map.put("type", "dam:Asset");
		map.put("p.limit", "1");
		PredicateGroup predicate = PredicateGroup.create(map);
		Query query = builder.createQuery(predicate, session);

		SearchResult result = query.getResult();
		return result.getTotalMatches();
	}

                	builder.append("<li>");
                	builder.append("<a href=\"");
                	builder.append(asset.getPath());
                	builder.append("\">");
                	builder.append(title);
                	builder.append("</a>");
                	builder.append("<input type=\"button\" value=\"Add to Meeting\" onclick=\"applyAids('"+asset.getPath()+"', '"+title+"' )\" />");
                	builder.append("</li>");
                	
                	
                	
            		
            	
                }
            }
        }
        builder.append("</ul>");
        return builder.toString();
	}
	
	/*
	private Map<String, String> buildQueryMap(String... paths) {
	    Map<String, String> mapPath = new HashMap<String, String>();
	    mapPath.put("group.p.or","true");
	    mapPath.put("group.1_path","/content/gateway/en");
	    mapPath.put("group.2_path", "/content/dam/girlscouts-shared/en/documents");
	  
	    
	    PredicateGroup predicatePath =PredicateGroup.create(mapPath);
	    
	    Map mapFullText<String, String> = new HashMap<String, String>();
	    
	    mapFullText.put("group.p.or","true");
	    mapFullText.put("group.1_fulltext", q);
	    mapFullText.put("group.1_fulltext.relPath", "jcr:content");
	    mapFullText.put("group.2_fulltext", q);
	    mapFullText.put("group.2_fulltext.relPath", "jcr:content/@jcr:title");
	    mapFullText.put("group.3_fulltext", q);
	    mapFullText.put("group.3_fulltext.relPath", "jcr:content/@jcr:description");
	    
	    PredicateGroup predicateFullText = PredicateGroup.create(mapFullText);
	    
	    Map masterMap<String, String>  = new HashMap()<String, String>;
	    masterMap.put("type","nt:hierarchyNode" );
	    masterMap.put("boolproperty","jcr:content/hideInNav");
	    masterMap.put("boolproperty.value","false");
	}
%>
