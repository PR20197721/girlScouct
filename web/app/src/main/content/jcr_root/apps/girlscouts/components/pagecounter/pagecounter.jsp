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
	org.girlscouts.web.events.search.*,
	java.text.SimpleDateFormat,
	java.util.Calendar
	"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />

<%!
    
	/************************** Page Counter Component ************************
	** This components lists pages that count towards page count
	** Dialog is used to filter pages that should not count
	**
	** Pages that count are:
	** 	* pages inherited from national template pages
	**  * pages that do not belong to below
	**
	** Pages that do not count are:
	**	* Homepage
	**  * Thank You pages
	**  * Terms and Conditions, Privacy Policy, Content Monitoring
	**	  Policy, Social Media Policy (in footer)
	**  * Individual forms, events, news
	**
	*************************************************************************/
	
	public static final String RESOURCE_TYPES = "foundation/components/page, girlscouts/components/homepage, girlscouts/components/one-column-page, girlscouts/components/three-column-page, girlscouts/components/placeholder-page";
	public static final String FOOTER_LINK_FILTERs = "Terms, Conditions, Privacy, Policy, Social";
	
	ArrayList<String> nationalTemplatePages;
	ArrayList<String> councilTemplatePages;
	ArrayList<String> councilAddedPages;
	ArrayList<String> noncountPages;
	ArrayList<String> allPages;	

	ArrayList<String> countedResourceTypes;
	ArrayList<String> exceptionPages;
	ArrayList<String> exceptionDirectories; 
	ArrayList<String> thankYouPages;	
	ArrayList<String> defaultValues; 	
		
	Long before;
	Long after;	
	
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
			String path = values.length > 1 ? values[1] : ""; 
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
	
	String linkify(String path) {
		String[] values = path.split("\\|");
		String reason = "";
		for (int i = 1; i < values.length; i++) {
			reason += values[i] + " ";
		}
		
		String html = "<a  target=\"_blank\"  href=\"" + values[0].trim() + ".html\">" + trimTopLevel(values[0],2) + "</a>";  
		
		if (reason.length() > 0) {
			html += " (" + reason.trim() + ")";
		}
		
		return html;
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

	
	String format(String label, String path, String page, String subdir) {
		String str = label.trim() + "|||" + path.trim() + "|||"
				+ page.trim() + "|||" + subdir.trim();		
		return str;
	}
	
	
	void checkRedirect(ResourceResolver rr, Page currentPage) {
		String nodePath = currentPage.getPath() + "/jcr:content/content/middle/par/start";
		Resource res = rr.getResource(nodePath);		
		if (res != null) {
			String redirect = res.getValueMap().get("redirect", "");
			if (!redirect.isEmpty()) {
				thankYouPages.add(redirect + " " + currentPage.getPath());
			}
		}
		
		nodePath = currentPage.getPath() + "/jcr:content/content/middle/par/form_start";
		res = rr.getResource(nodePath);		
		if (res != null) {
			String redirect = res.getValueMap().get("redirect", "");
			if (!redirect.isEmpty()) {
				thankYouPages.add(redirect + " " + currentPage.getPath());
			}
		}
		
		nodePath = currentPage.getPath() + "/jcr:content/content/middle/par/web-to-case-form_start";
		res = rr.getResource(nodePath);		
		if (res != null) {
			String redirect = res.getValueMap().get("redirect", "");
			if (!redirect.isEmpty()) {
				thankYouPages.add(redirect + " " + currentPage.getPath());
			}
		}
	}
	
	
	void recurse(ResourceResolver rr, Page currentPage) {
		String path = currentPage.getPath();
		ValueMap properties = currentPage.getProperties();		
		
		// If it is a page, then its resourceType must be one of the below
		//  foundation/components/page (/content/<council>)
		//  girlscouts/components/homepage
		// 	girlscouts/components/one-column-page
		// 	girlscouts/components/three-column-page
		//  girlscouts/components/placeholder-page
		String resourceType = properties.get("sling:resourceType", "");
		if (countedResourceTypes.contains(resourceType)) {
			allPages.add(path.trim());
			
			// Check for Thank You Page
			checkRedirect(rr, currentPage);
			
			for (Iterator<Page> iterator = currentPage.listChildren(); iterator.hasNext();) {
				recurse(rr, iterator.next());
			}
		}
	}
	
	
	
	
	void processPage(ResourceResolver rr, String path) {
		
		Page page = rr.getResource(path).adaptTo(Page.class);
		String reason;
		ValueMap properties = page.getProperties();
		
		// Active Pages
		String lastReplicationAction = properties.get("cq:lastReplicationAction", "");
		if (!lastReplicationAction.equals("Activate")) {
			noncountPages.add(path + " | NonActive");
			return;
		}
		
		// Exception Pages
		if (exceptionPages.contains(path)) {			
			noncountPages.add(path + " | ExceptionPages");
			return;
		}		
		
		// Exception Directories
		for (int i = 0; i < exceptionDirectories.size(); i++) {
			String dir = exceptionDirectories.get(i);
			if (!path.equals(dir) && path.contains(dir)) {				
				noncountPages.add(path + " | ExceptionDirectories");
				return;
			}
		}		
		
		// Belongs to Template Pages
		if (nationalTemplatePages.contains(path)) {
			councilTemplatePages.add(path);
			return;
		}
				
		// Placeholder Page
		String resourceType = properties.get("sling:resourceType", "");
		if (resourceType.equals("girlscouts/components/placeholder-page")) {
			//noncountPages.add(path + " PlaceHolder"); // not *really* a page
			return;
		}
				
		// If a page has jcr:mixinTypes of either LiveRelationship or LiveSync, 
		// it's inherited from national templates 
		ArrayList<String> mixinTypes = new ArrayList<String>();
		String[] mTypes = properties.get("jcr:mixinTypes", String[].class);
		Boolean addedMixin = false;
		String pathMixin = "";
		for (String m: mTypes) {
			mixinTypes.add(m.trim());
		}			
		if (mixinTypes.contains("cq:LiveSync") ||
			mixinTypes.contains("cq:LiveRelationship") ||
			mixinTypes.contains("cq:PropertyLiveSyncCancelled")) {
			councilTemplatePages.add(path);
			return;
		}	
		
		// Embedded Form
		/*
		Resource resource = rr.getResource(page.getPath() + "/jcr:content/content/middle/par/embedded");
		if (resource != null) {
			String html = resource.getValueMap().get("html", "");
			int qw = html.indexOf("wufoo");
			if (qw > 0) {
				noncountPages.add(path + " Embedded Form: wufoo");
				return;
			} else {
				noncountPages.add(path + " Embedded Form");
			}
			
		}
		*/
		
		// Thank You Pages
		for (int i = 0; i < thankYouPages.size(); i++) {
			String[] val = thankYouPages.get(i).split(" ");
			if (path.equals(val[0])) {				
				noncountPages.add(path + " | ThankYou " + val[1]);
				return;
			}
		}	
		
		
		councilAddedPages.add(path);
	}
%>

<%
	Calendar calendar = Calendar.getInstance();
	before = System.currentTimeMillis();

	Page top = currentPage.getAbsoluteParent(1);		// /content/<council>
	Page en = currentPage.getAbsoluteParent(2);			// /content/<council>/en
	
	String councilTitle = top.getTitle();
	String councilName = top.getName(); 
	String councilPath = top.getPath();
	
	nationalTemplatePages = new ArrayList<String>();
	councilTemplatePages = new ArrayList<String>();
	councilAddedPages = new ArrayList<String>();
	
	exceptionPages = new ArrayList<String>();
	exceptionDirectories = new ArrayList<String>();
	
	countedResourceTypes = new ArrayList<String>();
	
	noncountPages = new ArrayList<String>();
	allPages = new ArrayList<String>();
	
	thankYouPages = new ArrayList<String>();
	
	defaultValues = new ArrayList<String>();
	
	ArrayList<String> links = new ArrayList<String>();
	ArrayList<String> footerLinkFilters = new ArrayList<String>();

	// If paths value is empty set default
	// Retrieve paths set by homepage component in en/jcr:content to set as defaults
	
	ValueMap enProperties = en.getProperties();
	
	String resourceTypes = properties.get("resourceTypes", "");
	String[] filters = properties.get("paths", String[].class);
	
	String nodePath = resource.getPath(); //top.getPath() + "/en/jcr:content/content/middle/par/pagecounter";
	Node node = resourceResolver.getResource(nodePath).adaptTo(Node.class);
		
	
	if (resourceTypes.isEmpty()) {
		resourceTypes = RESOURCE_TYPES;
		node.setProperty("resourceTypes", resourceTypes);
		node.getSession().save();
	}
	if ((filters == null) || (filters.length == 0)) {		
		
		String eventRepoURL = enProperties.get("eventPath", "");		
		String eventCalendarURL = enProperties.get("calendarPath", "");
		String newsURL = enProperties.get("newsPath", "");		
		String sitesearchURL = enProperties.get("globalLanding", "");
		
		defaultValues.add(format("Homepage", councilPath + "/en", "true", "false"));
		defaultValues.add(format("Resources", councilPath + "/en/resources", "true", "true"));
		defaultValues.add(format("Event Repository", eventRepoURL, "true", "true"));
		defaultValues.add(format("News", newsURL, "false", "true"));
		defaultValues.add(format("Site Search", sitesearchURL, "true", "false"));
		defaultValues.add(format("Email Templates", councilPath + "/en/email-templates", "true", "false"));
		defaultValues.add(format("Redirects", councilPath + "/en/redirects", "true", "false"));
		
		
		// Get some links from homepage footer such as Terms and Conditions, Policy
		footerLinkFilters = listToArray(FOOTER_LINK_FILTERs);
		String footernavnodepath = top.getPath() + "/en/jcr:content/footer/nav";
		Node fnode = resourceResolver.getResource(footernavnodepath).adaptTo(Node.class);
		
		Value[] linkValues = fnode.getProperty("links").getValues();;
		for (int i = 0; i < linkValues.length; i++) {
			links.add(linkValues[i].toString());
		}
		for (String s: links) {
			String[] values = s.split("\\|\\|\\|");
			String label = values[0];
	        String path = values.length >= 2 ? values[1] : "";
	        
	        // If the label or path/url contains words in footer link filters,
	        // Add to filter. Else discard
	        for (int j = 0; j < footerLinkFilters.size(); j++) {
	        	if (label.contains(footerLinkFilters.get(j))) {
	        		defaultValues.add(format(label, path, "true", "false"));  
	        		break;
	        	} else if (path.contains(footerLinkFilters.get(j).toLowerCase())) {
	        		defaultValues.add(format(label, path, "true", "false"));
	        		break;
	        	} 
	        }
					
		}
		
		// Save the properties		
		filters = new String[defaultValues.size()];
		defaultValues.toArray(filters);  
		node.setProperty("paths", filters);
		node.getSession().save();
	}
	
	countedResourceTypes = listToArray(resourceTypes);	
	processPaths(filters);

	// These pages belong to council template pages 
	// They aren't inherited so no mixins to check
	String eventlist = enProperties.get("eventLanding", ""); 
	String eventcalendar = enProperties.get("calendarPath", ""); 
	nationalTemplatePages.add(eventlist);
	nationalTemplatePages.add(eventcalendar);
	
	
	// Go through council directory to count pages
	recurse(resourceResolver, top);	
	
	allPages.remove(0); // removes /content/<council>
	for (int i = 0; i < allPages.size(); i++) {
		processPage(resourceResolver, allPages.get(i));
	}
	
	after = System.currentTimeMillis();
%>


<div id="pagecount">
	<h1>Page Counter</h1>
	<p>This component aims to help page count for councils and GSUSA by filtering pages that count towards page count.</p>

	<h4>Pages that count</h4>
	<p>
	* Pages that are part of National Template Pages <br>
	* Pages that are added by councils <br>
	</p>
	
	<h4>Pages that do not count</h4>
	<p>
	* Individual events, news <br>
	* Forms under Forms and Documents <br>
	* Terms and Conditions, Privacy Policy, Content Monitering Policy, Social Media Policy <br>
	</p>
	
	<h4>Instructions</h4>	
	<p>
	This component goes through a council's pages and determines which pages count towards the total page count and which pages do not. 
	There are three categories that a page falls into - Template Pages: pages inherited from National Templates, Added Pages: 
	pages added by councils and Noncount Pages: pages that should not count (ex. non-active pages, forms, etc). Total page count 
	is the sum of Template Pages and Added Pages and does not count pages from Noncount Pages. Please look over each categories to 
	make sure pages are in the correct categories to ensure an accurate page count. If any pages are filed into wrong categories or 
	if there are any changes that need to made, please contact us.  
 	<br><br>
	Total Page Count<br>
	Up to 100 pages 						 $ 8,000<br>
	Between 100 - 200 pages                  $10,000<br>
	Between 201 - 300 pages                  $12,000<br>
	Between 301 - 400 pages                  $15,000<br>
	Each addition 100 pages over 400  		 $ 4,000
	</p>
	<br><br>
	
	<!-- # -->
	Total Page Count (<%= councilTemplatePages.size() + councilAddedPages.size() %>) = Template Pages (<%= councilTemplatePages.size() %>) + Council Pages (<%= councilAddedPages.size() %>) 

	<br><br>
	<div><!-- # -->
		Council Template Page Count: <%= councilTemplatePages.size() %>
		<a id="<%= councilName %>TemplatePages" class="showlist">Show List</a> 
	</div>
	<div id="<%= councilName %>TemplatePagesList" class="pagelist">
	<% for(String str: councilTemplatePages) { %>
		<br><%= linkify(str) %>
	<% } %> 
	</div>	
	<br>
	<div> <!-- # -->
		Council Added Page Count: <%= councilAddedPages.size() %>
		<a id="<%= councilName %>AddedPages" class="showlist">Show List</a> 
	</div>
	<div id="<%= councilName %>AddedPagesList" class="pagelist">
	<% for(String str: councilAddedPages) { %>
		<br><%= linkify(str) %> 
	<% } %> <br>
	</div>
	<br>
	<div> <!-- # -->
		Council Non Page Count: <%= noncountPages.size() %>
		<a id="<%= councilName %>NoncountPages" class="showlist">Show List</a> 
	</div>
	<div id="<%= councilName %>NoncountPagesList" class="pagelist">
	<% for(String str: noncountPages) { %>
		<br><%= linkify(str) %>
	<% } %><br>
	</div> 
	<br>
	
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
					<td><%= councilTitle %><br>(<%= councilName %>)</td>
					<td style="white-space:nowrap;">
						<% for(String str: councilTemplatePages) { %>
							<br><%= linkify(str) %>
						<% } %> 
					</td>
					<td><%= councilTemplatePages.size() %></td>
					<td style="white-space:nowrap;">
						<% for(String str: councilAddedPages) { %>
							<br><%= linkify(str) %>
						<% } %> 
					</td>
					<td><%= councilAddedPages.size() %></td>
					<td style="white-space:nowrap;">
						<% for(String str: noncountPages) { %>
							<br><%= linkify(str) %>
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
