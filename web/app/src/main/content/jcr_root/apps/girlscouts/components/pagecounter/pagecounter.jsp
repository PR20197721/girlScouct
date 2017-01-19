<%@ page
	import="java.text.Format,
	java.text.ParseException,
	java.lang.Exception,
	java.util.Map,
	java.util.HashMap,
	java.util.List,
	java.util.Arrays,
	java.util.ArrayList,
	java.util.Iterator,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.dam.api.Asset,
	org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	org.girlscouts.web.events.search.*
	"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />

<%!
    
	/************************** Page Counter Component ************************
	** This components lists pages that count towards page count
	** Dialog is used to filter pages that do not count
	**
	** Pages that count are:
	** 	* pages that belong to 35 national template pages
	**  * pages that do not belong to below
	**
	** Pages that do not count are:
	**	* Homepage
	**  * Terms and Conditions, Privacy Policy, Content Monitoring
	**	  Policy, Social Media Policy (in footer)
	**  * Individual forms, events, news
	**
	*************************************************************************/
	
	public static final String TEMPLATE_PATH = "/content/girlscouts-template";
	public static final String RESOURCE_TYPES = "girlscouts/components/homepage, girlscouts/components/one-column-page, girlscouts/components/three-column-page, girlscouts/components/placeholder-page";
	
	
	ArrayList<String> nationalTemplatePages;
	ArrayList<String> councilTemplatePages;
	ArrayList<String> councilAddedPages;
	
	ArrayList<String> exceptionPages;
	ArrayList<String> councilExceptionPages;
	ArrayList<String> exceptionDirectories; 
	ArrayList<String> councilExceptionDirectories;
	ArrayList<String> countedResourceTypes;
	ArrayList<String> countedResourceTypesList;
	
	ArrayList<String> countPages;
	ArrayList<String> noncountPages;
	ArrayList<String> allPages;
	ArrayList<String> nonactivePages;
		
	ArrayList<String> liveRelationships;
	ArrayList<String> liveSyncs;
	ArrayList<String> propertyLiveSyncCancelled;
	ArrayList<String> neither;
	ArrayList<String> both;
	ArrayList<String> inherited;

	ArrayList<String> dValues; 	
		
	void listAllPages(ArrayList<String> list, Page current, String path) {	
		String name = path + "/" + current.getName();
		name = name.trim();
		ValueMap properties = current.getProperties();
		
		// In order for a resource to be a page, its resourceType must be
		// 	girlscouts/components/one-column-page
		// 	girlscouts/components/three-column-page
		//  girlscouts/components/homepage
		//  girlscouts/components/placeholder-page
		String resourceType = properties.get("sling:resourceType", "");
		if (countedResourceTypes.contains(resourceType)) {
			list.add(name);
		}
		
		for (Iterator<Page> iterator = current.listChildren(); iterator.hasNext();) {
			listAllPages(list, iterator.next(), name);
		}
	}
	
	ArrayList<String> listChildren(Page current) {
		ArrayList<String> list = new ArrayList<String>();
		String name = "/" + current.getName();
		name = name.trim();
		ValueMap properties = current.getProperties();
		
		for (Iterator<Page> iterator = current.listChildren(); iterator.hasNext();) {
			list.add(iterator.next().getPath());
		}
		
		return list;
	}
	
	void listChildrenPages(ArrayList<String> list, Page current) {
		String name = "/" + current.getName();
		name = name.trim();
		
		for (Iterator<Page> iterator = current.listChildren(); iterator.hasNext();) {
			Page page = iterator.next();
			ValueMap properties = page.getProperties();
			
			list.add(page.getPath() + ": " + properties.get("cq:lastReplicationAction")
						+ " " + properties.get("sling:resourceType")
						+ " " + page.isValid()
					);
		}	
	}
	
	void listChildrenPages(ArrayList<String> list, String path, ResourceResolver rr) {
		Page current = rr.getResource(path).adaptTo(Page.class);
		
		for (Iterator<Page> iterator = current.listChildren(); iterator.hasNext();) {
			Page page = iterator.next();
			ValueMap properties = page.getProperties();
			
			list.add(page.getPath() + ": " + properties.get("cq:lastReplicationAction")
						+ " " + properties.get("sling:resourceType")
						+ " " + page.isValid()
					);
		}	
	}
	
	ArrayList<String> listToArray(String list) {
		String[] lists = list.split(",");
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String str: lists) {
			arrayList.add(str.trim());
		}
		return arrayList;
	}
	
	void processPaths(String[] list) {		
		for (int i = 0; i < list.length; i++) {
			String[] values = list[i].split("\\|\\|\\|");
			String label = values[0];
			String path = values.length > 1 ? trimTopLevel(values[1], 2) : "";
			String page = values.length > 2 ? values[2] : "";
			String subdir = values.length > 3 ? values [3] : "";
			
			if (page.equals("true")) {
				exceptionPages.add(path.trim());
			}
			if (subdir.equals("true")) {
				exceptionDirectories.add(path.trim());
			}			
		}		
	}
	
	String linkify(String councilName, String path) {
		String[] values = path.split(" ");
		String reason = "";
		for (int i = 1; i < values.length; i++) {
			reason += values[i] + " ";
		}
		String url = "/content/" + councilName + values[0];
		String fpath = "<a  target=\"_blank\"  href=\"" + url + ".html\">" + values[0] + "</a>";  
		
		if (reason.length() > 0) {
			fpath += " (" + reason.trim() + ")";
		}
		
		return fpath;
	}
	
	String trimTopLevel(String path, int num) {
		
		String[] values = path.split("/");
		String newPath = "";
		
		if (values[0].isEmpty()) {
			num++;
		}
		
		if (num <= values.length) {
			for (int i = 0; i < values.length; i++) {
				if (i >= num) {
					newPath += "/" + values[i];
				}
			}
		} else {
			newPath = path;
		}
		return newPath;
	}
	
	String trimBottomLevel(String path, int num) { 
		String[] values = path.split("/");
		String str = "";
		//num++;
		for (int i = 1; i < values.length - num; i++) {
			str = str + "/" + values[i];
		}
		return str;
	}
	
	ArrayList<String> trimLevel(ArrayList<String> list, int num) {
		ArrayList<String> newList = new ArrayList<String>();
		for (String str: list) {
			str = trimTopLevel(str, num);
			newList.add(str);
		}
		return newList;
	}
	
	String format(String label, String path, String page, String subdir) {
		String str = label.trim() + "|||" + path.trim() + "|||"
				+ page.trim() + "|||" + subdir.trim();
		
		return str;
	}
	
	
	
	// Recurses through the children and selects pages that counts into a list
	void recurse(ResourceResolver rr, Page current, String prevPath, String councilName, String componentPagePath) {
		String fullpath = prevPath + "/" + current.getName();
		String path = trimTopLevel(fullpath, 1);
		ValueMap properties = current.getProperties();
		Boolean isNonCountPage = false;
		Boolean isInherited = false;
		Boolean isNonPlaceholderPage = false;
		Boolean hasEmbeddedForm = false;
		Boolean isCurrentPage = false;
		Boolean doRecurse = true;
		String reason = "";
				
		// If it is a page, then its resourceType must be one of the below
		// 	girlscouts/components/one-column-page
		// 	girlscouts/components/three-column-page
		//  girlscouts/components/homepage
		//  girlscouts/components/placeholder-page
		// Do not count non page resources		
		String resourceType = properties.get("sling:resourceType", "");
		if (!countedResourceTypes.contains(resourceType)) {
			countedResourceTypesList.add(path);
			isNonCountPage = true;
			doRecurse = true;
		} else {
			
			// If not active, 
			// do not count, do not recurse
			String lastReplicationAction = properties.get("cq:lastReplicationAction", "");
			if (!lastReplicationAction.equals("Activate")) {
				nonactivePages.add(path);
				noncountPages.add(path + " Non Active");
				return;
			}
		
			
			// If it exists in ExceptionPages
			// do not count		
			if (exceptionPages.contains(path)) {
				if (!resourceType.equals("girlscouts/components/placeholder-page")) {
					noncountPages.add(path + " in exceptionPages");
					councilExceptionPages.add(path + " Page in exceptionPages");
				} else {
					noncountPages.add(path + " nonPage in exceptionPages");
				}
				isNonCountPage = true;
			}
					
			// If it exists in ExceptionDirectories,
			// do not recurse		
			if (exceptionDirectories.contains(path) && exceptionPages.contains(path)) {
				noncountPages.add(path + " in exceptionPages and exceptionDir");
				//councilAddedPages.add(path);
				councilExceptionDirectories.add(path + " in exceptionPages and exceptionDir");
				doRecurse = false;
				return;
			} else if (exceptionDirectories.contains(path) && !exceptionPages.contains(path)) {
				if (!resourceType.equals("girlscouts/components/placeholder-page")) { // If this is a page
					countPages.add(path + " in exceptionPages but not exceptionDir");
					councilAddedPages.add(path); // + " in exceptionPages but not exceptionDir");
				} else {
					noncountPages.add(path + " nonPage in exceptionPages but not exceptionDir");
				}
				
				councilExceptionDirectories.add(path + " in exceptionPages but not exceptionDir");
				doRecurse = false;
				return;
			}
			
			// If a page has jcr:mixinTypes of either LiveRelationship or LiveSync, 
			// it's inherited from national templates 
			ArrayList<String> mixinTypes = new ArrayList<String>();
			String[] mTypes = properties.get("jcr:mixinTypes", String[].class);
			Boolean addedMixin = false;
			for (String m: mTypes) {
				mixinTypes.add(m.trim());
			}			
			if (mixinTypes.contains("cq:PropertyLiveSyncCancelled")) {
				propertyLiveSyncCancelled.add(path);
				addedMixin = true;
			}
			if (mixinTypes.contains("cq:LiveRelationship")) {
				liveRelationships.add(path);
				addedMixin = true;
			}
			if (mixinTypes.contains("cq:LiveSync")) {
				liveSyncs.add(path);
				addedMixin = true;
			}
			if (addedMixin == true) {
				inherited.add(path);
				isInherited = true;
			}
			
			// Test for embedded forms
			Resource tester = rr.getResource(current.getPath() + "/jcr:content/content/middle/par/embedded");
			if (tester != null) {
				String html = tester.getValueMap().get("html", "");
				int qw = html.indexOf("wufoo");
				if (qw > 0) {
					hasEmbeddedForm = true;
				}
			}
			
			// Page that holds this component do not count
			String currentPath = current.getPath();
			if (componentPagePath.equals(currentPath)) {
				isCurrentPage = true;
			}
			
			if (resourceType.equals("girlscouts/components/one-column-page") || resourceType.equals("girlscouts/components/three-column-page")) {
				// If the page name is in the national template
				// it counts
				
				String scaffolding = properties.get("cq:scaffolding", "");
				
				if (isCurrentPage) {
					noncountPages.add(path + " Component Containing Page");
				} else if (nationalTemplatePages.contains(path)) {
					councilTemplatePages.add(path);
				} else if (isInherited) {
					councilTemplatePages.add(path);
				} else if (hasEmbeddedForm) {
					noncountPages.add(path + " Embedded form");
				} else if (scaffolding.equals("/etc/scaffolding/" + councilName + "/contact")) {
					noncountPages.add(path + " Scaffolding");
				} else if (isNonCountPage){
					noncountPages.add(path + " isNonCountPage");
				} else {
					councilAddedPages.add(path);// + " else else");
				}
			}
			
			
			allPages.add(path);
		}
		
		if (doRecurse) {
			for (Iterator<Page> iterator = current.listChildren(); iterator.hasNext();) {
				recurse(rr, iterator.next(), fullpath, councilName, componentPagePath);
			}
		}
		
	}	
	
%>

<%
	Page template = pageManager.getPage(TEMPLATE_PATH);
	Page top = currentPage.getAbsoluteParent(1);
	Page en = currentPage.getAbsoluteParent(2);
	
	String councilName = currentPage.getAbsoluteParent(1).getName();
	String councilPath = currentPage.getAbsoluteParent(1).getPath();
	
	nationalTemplatePages = new ArrayList<String>();
	councilTemplatePages = new ArrayList<String>();
	councilAddedPages = new ArrayList<String>();
	
	exceptionPages = new ArrayList<String>();
	councilExceptionPages = new ArrayList<String>();
	exceptionDirectories = new ArrayList<String>();
	councilExceptionDirectories = new ArrayList<String>();
	countedResourceTypes = new ArrayList<String>();
	countedResourceTypesList = new ArrayList<String>();
	
	noncountPages = new ArrayList<String>();
	countPages = new ArrayList<String>();
	allPages = new ArrayList<String>();
	nonactivePages = new ArrayList<String>();
	
	dValues = new ArrayList<String>();
	
	
	liveRelationships = new ArrayList<String>();
	liveSyncs = new ArrayList<String>();
	neither = new ArrayList<String>();
	both = new ArrayList<String>();
	inherited = new ArrayList<String>();
	propertyLiveSyncCancelled = new ArrayList<String>();

	
	ArrayList<String> links = new ArrayList<String>();

	ArrayList<String> childrenResources = new ArrayList<String>();
	ArrayList<String> childrenNodes = new ArrayList<String>();

	// If paths value is empty set default
	// Retrieve paths set by homepage component in en/jcr:content to set as defaults
	
	ValueMap topProperties = top.getProperties();
	String eventURL1 = topProperties.get("eventPath", "");
	
	ValueMap enProperties = en.getProperties();
	
	String resourceTypes = properties.get("resourceTypes", "");
	String[] filters = properties.get("paths", String[].class);
	
	String nodePath = resource.getPath(); //top.getPath() + "/en/jcr:content/content/styled-subpar/pagecounter";
	Node node = resourceResolver.getResource(nodePath).adaptTo(Node.class);
		
	
	if (resourceTypes.isEmpty()) {
		resourceTypes = RESOURCE_TYPES;
		node.setProperty("resourceTypes", resourceTypes);
		node.getSession().save();
	}
	if ((filters == null) || (filters.length == 0)) {
		
		
		String eventRepoURL = enProperties.get("eventPath", "");		
		String eventCalendarURL = enProperties.get("calendarPath", "");
		//String eventListURL = enProperties.get("eventLanding", "/content/" + councilName + "/en/events");
		//String eventURL = trimBottomLevel(eventListURL, 1);
		String newsURL = enProperties.get("newsPath", "");		
		String sitesearchURL = enProperties.get("globalLanding", "");
		
		dValues.add(format("Homepage", councilPath + "/en", "true", "false"));
		dValues.add(format("Resources", councilPath + "/en/resources", "true", "true"));
		dValues.add(format("Event Repository", eventRepoURL, "true", "true"));
		//dValues.add(format("Events", eventURL, "true", "true"));
		//dValues.add(format("Event Calendar", eventCalendarURL, "false", "false"));
		//dValues.add(format("Event List", eventListURL, "false", "false"));
		dValues.add(format("News", newsURL, "false", "true"));
		dValues.add(format("Site Search", sitesearchURL, "true", "false"));
		
		// Get some links from footer
		String footernavnodepath = top.getPath() + "/en/jcr:content/footer/nav";
		Node fnode = resourceResolver.getResource(footernavnodepath).adaptTo(Node.class);
		
		// TODO
		Value[] linkValues = fnode.getProperty("links").getValues();;
		for (int i = 0; i < linkValues.length; i++) {
			links.add(linkValues[i].toString());
		}
		for (String s: links) {
			String[] values = s.split("\\|\\|\\|");
			String label = values[0];
	        String path = values.length >= 2 ? values[1] : "";
			dValues.add(format(label, path, "true", "false"));		
		}
		
		// Save the properties		
		filters = new String[dValues.size()];
		dValues.toArray(filters);
		node.setProperty("paths", filters);
		node.getSession().save();
	}
	
	countedResourceTypes = listToArray(resourceTypes);	
	processPaths(filters);
	
	
	// List national templates into tree format
	//templates.add("/en"); // only on template, en is marked as placeholder. else where it's homepage component
	listAllPages(nationalTemplatePages, template, "");
	nationalTemplatePages = trimLevel(nationalTemplatePages, 1);
	
	String eventlist = trimTopLevel(enProperties.get("eventLanding", ""), 2);
	String eventcalendar = trimTopLevel(enProperties.get("calendarPath", ""), 2); 

	nationalTemplatePages.add(eventlist);
	nationalTemplatePages.add(eventcalendar);
	
	
	// Go through council directory to count pages
	recurse(resourceResolver, top, "", councilName, currentPage.getPath());	

	
%>


<div id="pagecount">
	<h1>Page Counter</h1>
	<p>This component aims to help page count for councils and GSUSA by filtering pages that count towards page count.</p>

	<h4>Pages that count</h4>
	<p>
	* Pages that are part of 35 National Templates <br>
	* Pages that are added by councils <br>
	</p>
	
	<h4>Pages that does not count</h4>
	<p>
	* Individual events, forms, news <br>
	* Terms and Conditions, Privacy Policy, Content Monitering Policy, Social Media Policy <br>
	</p>
	
	<h4>Instructions</h4>	
	<p>
	This component goes through all active pages in a council's website to generate three lists to aid councils 
	in counting pages. It lists pages that are part of National Templates which counts, pages that are added 
	by councils which counts and pages that are added by councils but do not count. The pages that do not fit 
	the requirement are filtered to count only relevant pages. The filter criteria can be altered by editing 
	the dialog. Upon adding the component, it automatically generates a filter that collects various sites that 
	do not count, such as Privacy Policy and Terms and Conditions, or individual events. In case of pages that 
	are counted when they shouldn't, you can add them to the filter. In the dialog, add a label that explains 
	why and select the path of the site or the directory that needs to be filtered. By checking "Page" Only it 
	will only filter the specific page and will not filter its children pages. Checking "Sub Dir Only" will not 
	filter the specified page but will filter all its children pages. And checking both will result in the page 
	and its children pages being filtered. 
	</p>
		
	<br><br>

	<!-- # -->
	Template Pages (<%= councilTemplatePages.size() %>) + Council Pages (<%= councilAddedPages.size() %>) = <%= councilTemplatePages.size() + councilAddedPages.size() %>

	<br><br>
	<div><!-- # -->
		Council Template Page Count: <%= councilTemplatePages.size() %>
		<a id="<%= councilName %>TemplatePages" class="showlist">Show List</a> 
	</div>
	<div id="<%= councilName %>TemplatePagesList" class="pagelist">
	<% for(String str: councilTemplatePages) { %>
		<br><%= linkify(councilName, str) %>
	<% } %> 
	</div>	
	<br>
	<div> <!-- # -->
		Council Added Page Count: <%= councilAddedPages.size() %>
		<a id="<%= councilName %>AddedPages" class="showlist">Show List</a> 
	</div>
	<div id="<%= councilName %>AddedPagesList" class="pagelist">
	<% for(String str: councilAddedPages) { %>
		<br><%= linkify(councilName, str) %> 
	<% } %> <br>
	</div>
	<br>
	<div> <!-- # -->
		Council Non Page Count: <%= noncountPages.size() %>
		<a id="<%= councilName %>NoncountPages" class="showlist">Show List</a> 
	</div>
	<div id="<%= councilName %>NoncountPagesList" class="pagelist">
	<% for(String str: noncountPages) { %>
		<br><%= linkify(councilName, str) %>
	<% } %><br>
	</div> 
	
	<br><br><br>
	<!-- # -->
	<div style="height:400px; overflow:auto;">
		<table id="pagelistTable">
			<thead>
				<tr>
					<th>Council Name</th>
					<th>Template Pages List</th>
					<th>Count</th>
					<th>Added Pages List</th>
					<th>Count</th>
					<th>Noncount Pages List</th>
					<th>Count</th>
				</tr>
			</thead>
			<tbody>
			<!-- # -->
				<tr style="vertical-align:top;">
					<td><%= councilName %></td>
					<td style="white-space:nowrap;">
						<% for(String str: councilTemplatePages) { %>
							<br><%= linkify(councilName, str) %>
						<% } %> 
					</td>
					<td><%= councilTemplatePages.size() %></td>
					<td style="white-space:nowrap;">
						<% for(String str: councilAddedPages) { %>
							<br><%= linkify(councilName, str) %>
						<% } %> 
					</td>
					<td><%= councilAddedPages.size() %></td>
					<td style="white-space:nowrap;">
						<% for(String str: noncountPages) { %>
							<br><%= linkify(councilName, str) %>
						<% } %> 
					</td>
					<td><%= noncountPages.size() %></td>
				</tr>
			<!-- # -->
			</tbody>
		</table>
		
	</div>
	<button id="thebutton" onClick="copy();"> CLICK TO COPY TABLE </button>
	
	
	
</div>

<script type="text/javascript">

function toggle(id) {
	var text = $("#" + id).text();
	if (text == "Show List") {
		$("#" + id).text("Show less");
		$("#" + id + "List").show();
	} else {
		$("#" + id).text("Show List");
		$("#" + id + "List").hide();
	}
}

function copy() {
	var emailLink = document.querySelector('#pagelistTable');  
	var range = document.createRange();  
	range.selectNode(emailLink);  
	window.getSelection().addRange(range);

	try {  
		var successful = document.execCommand('copy');  
		var msg = successful ? 'Copied. Please paste to excel' : 'Copy failed. Please copy manually';  
		alert(msg);  
	} catch(err) {  
		alert('Oops, unable to copy');  
	}
	
	window.getSelection().removeAllRanges();  
}

$(document).ready(function() {
	
    $(".pagelist").hide();

	$(".showlist").click(function() {
		toggle(this.id);
	});
    
});
</script>
