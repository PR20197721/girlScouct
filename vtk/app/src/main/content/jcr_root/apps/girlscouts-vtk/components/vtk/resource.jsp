<%@page import="java.util.Iterator, java.util.Map, java.util.HashMap, javax.jcr.Session, org.apache.sling.api.resource.ResourceResolver, org.apache.sling.api.resource.Resource, com.day.cq.wcm.api.PageManager, com.day.cq.wcm.api.Page, com.day.cq.dam.api.Asset, com.day.cq.search.PredicateGroup, com.day.cq.search.Query, com.day.cq.search.QueryBuilder, com.day.cq.search.result.SearchResult, org.girlscouts.vtk.dao.*, org.girlscouts.vtk.models.user.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<%
	final String RESOURCE_SEARCH_PROMPT = "enter search term";
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
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<style>
	.ui-autocomplete-loading {
		background: white url('images/ui-anim_basic_16x16.gif') right center no-repeat;
	}
</style>
<script>
$(function() {
	var cache = {};
	$( "#searchByCharField" ).autocomplete({
		minLength: 3,
		minChar: 3,
		source: function( request, response ) {
			var term = request.term;
			//console.log(term)
			$("#searchResults").html("");
			$.getJSON( "/content/girlscouts-vtk/controllers/vtk.getdata.html?q="+term, request, 
			function( data, status, xhr ) {
				//cache[ term ] = data;
				//var y =response( data );
				//console.log(1)
			})
			.done(function( json ) {
				$("#searchResults").load("/content/girlscouts-vtk/controllers/vtk.searchR.html?rand="+Date.now());
				//console.log( "JSON Data: " + json );
				//$( "div.caca" ).html("searching...");
				$(jQuery.parseJSON(JSON.stringify(json))).each(function() {  
				var ID = this.label;
				// var TITLE = this.Title;
				//console.log( "--- "+ID );
				//$( "#caca" ).append("<li>"+ID+"</li>");
				});
			});
		}
	});
});

function applyAids(aid){
	var link = "/content/girlscouts-vtk/controllers/vtk.asset.html?aidId="+ aid;
	$( "#gsModal" ).load(link, function( response, status, xhr ) {
		if ( status == "error" ) {
			var msg = "Sorry but there was an error: ";
			$( "#error" ).html( msg + xhr.status + " " + xhr.statusText );
		}else{
			$( "#gsModal" ).dialog({
				width:920,
				modal:true,
				dialogClass:"modalWrap"
			});
			$(".ui-dialog-titlebar").hide();
		}
	});
}
</script>
	<div id="schedModal"></div>
	<h1>Search for Resources</h1>
	<div class="ui-widget searchBox">
		<input type="text" id="searchByCharField" name="q" placeholder="<%=RESOURCE_SEARCH_PROMPT%>" class="searchField" />
	</div>
	<div id="searchResults"></div>
	<%-- categories --%>
	<p>Browse Resources by Category</p>
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
				minorCount = countAidAssets( MEETING_AID_PATH, queryBuilder, resourceResolver.adaptTo(Session.class));
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

	private String displayAidAssets(String path, ResourceResolver rr) {
		StringBuilder builder = new StringBuilder("<ul>");
		Resource root = rr.resolve(path);
		if (root != null) {
			Iterator<Resource> iter = root.listChildren();
			while (iter.hasNext()) {
				Asset asset = iter.next().adaptTo(Asset.class);
				if (asset != null) {
					// TODO: check importer. Use dc:title
					Map<String, Object> map = asset.getMetadata();
					//String title = asset.getMetadataValue("dc:title");
					String title = asset.getName();
					builder.append("<li>");
					builder.append("<a href=\"");
					builder.append(asset.getPath());
					builder.append("\">");
					builder.append(title);
					builder.append("</a></li>");
				}
			}
		}
		builder.append("</ul>");
		return builder.toString();
	}
%>
