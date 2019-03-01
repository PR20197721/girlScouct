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
	java.text.SimpleDateFormat,
	java.util.Calendar,
	com.day.cq.wcm.msm.api.*,
	javax.jcr.query.*,
	org.apache.sling.api.resource.ResourceUtil
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
	
	ArrayList<String> councilTemplatePages;
	ArrayList<String> councilAddedPages;
	//ArrayList<String> noncountPages;
	ArrayList<String> allPages;	
	//Map<String, String> councilTemplatePages;
	//Map<String, String> councilAddedPages;
	Map<String, String> noncountPages;
	//Map<String, String> allPages;	

	ArrayList<String> nationalTemplatePages;
	ArrayList<String> countedResourceTypes;
	ArrayList<String> exceptionPages;
	ArrayList<String> exceptionDirectories; 
	ArrayList<String> thankYouPages;	
	ArrayList<String> defaultValues; 	
	private static final String formQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([{path}]) and s.[sling:resourceType]= 'foundation/components/form/start'";
	
	ArrayList<String> overrides;
		
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
	
	void processFilterPaths(Node filtersNode) {	
		try {
			NodeIterator iterator = filtersNode.getNodes();
			while(iterator.hasNext()){
				try {
					Node filter = iterator.nextNode();
					String path = filter.getProperty("path").getString().trim();
					if(path.length() > 0){
						String pageOnly = filter.getProperty("pageOnly").getString();
						String subDirOnly = filter.getProperty("subDirOnly").getString();
						if (pageOnly.equals("true")) {
							exceptionPages.add(path.trim());
						}
						if (subDirOnly.equals("true")) {
							exceptionDirectories.add(path.trim());
						}
					}
				}catch(Exception e){}
			}
		}catch(Exception e){}
	}
	
	void processOverridePaths(String[] list) {
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				//String[] values = list[i].split("\\|\\|\\|");
				//String label = values[0];
				//String path = values.length > 1 ? values[1] : ""; 
				//String page = values.length > 2 ? values[2] : "";
				//String subdir = values.length > 3 ? values [3] : "";
								
				overrides.add(list[i].trim());
			}
		}
	}
	
	
	String linkify(String path) {
		return "<a  target=\"_blank\"  href=\"" + path.trim() + ".html\">" + trimTopLevel(path,2) + "</a>";  
	}
	String linkify(String path, String reason) {		
		return linkify(path) + " (" + reason.trim() + ")";		
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
	
	void checkForms(ResourceResolver rr, Page currentPage) {
		try {
			QueryResult forms = searchForms(rr,currentPage.getContentResource());
			if(forms != null){
				RowIterator rowIter = forms.getRows();
				while (rowIter.hasNext()) {
					try {
						Row row = rowIter.nextRow();
						Node node = row.getNode();
						if(node.hasProperty("redirect")){
							String redirect = node.getProperty("redirect").getString();
							if (!redirect.isEmpty()) {
								thankYouPages.add(redirect + " " + currentPage.getPath());
							}							
						}
						if(node.hasProperty("actionType")){
							String action = node.getProperty("actionType").getString();
							if (action != null &&  action.equals("girlscouts/components/form/actions/web-to-case")) {
								exceptionPages.add(currentPage.getPath());
							}							
						}
					}catch(Exception e){}
				}
			}
		}catch(Exception e){}
	}
	
	private QueryResult searchForms(ResourceResolver rr, Resource currentPageContent) {
		QueryResult result = null;
		try {
			Session session = rr.adaptTo(Session.class);
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			Query sql2Query = queryManager.createQuery(formQuery.replace("{path}",currentPageContent.getPath()), "JCR-SQL2");
			return sql2Query.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
			
			// Check forms for Thank You Page and web-to-case forms
			checkForms(rr, currentPage);
			
			for (Iterator<Page> iterator = currentPage.listChildren(); iterator.hasNext();) {
				recurse(rr, iterator.next());
			}
		}
	}
	
	
	
	
	void processPage(ResourceResolver rr, String path, LiveRelationshipManager lrm) {
		
		Page page = rr.getResource(path).adaptTo(Page.class);
		String reason;
		ValueMap properties = page.getProperties();
		
		// Active Pages
		String lastReplicationAction = properties.get("cq:lastReplicationAction", "");
		if (!lastReplicationAction.equals("Activate")) {
			//noncountPages.add(path + " | NonActive");
			noncountPages.put(path, "NonActivate");
			return;
		}
		
		// Exception Pages
		if (exceptionPages.contains(path)) {			
			//noncountPages.add(path + " | ExceptionPages");
			noncountPages.put(path, "ExceptionPages");
			return;
		}		
		
		// Exception Directories
		for (int i = 0; i < exceptionDirectories.size(); i++) {
			String dir = exceptionDirectories.get(i);
			if (!path.equals(dir) && path.contains(dir)) {				
				//noncountPages.add(path + " | ExceptionDirectories");
				noncountPages.put(path, "ExceptionDirectories");
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
		
		// Redirect Page
		if (resourceType.endsWith("/redirect")) {
			//noncountPages.add(path + " Redirect"); // not *really* a page
			return;
		}
				
		// If a page has jcr:mixinTypes of either LiveRelationship or LiveSync, 
		// it's inherited from national templates 
		try{ 
			Resource pageRes = page.adaptTo(Resource.class);
			LiveRelationship relationship = lrm.getLiveRelationship(pageRes, false);
			if(relationship != null){
				String srcPath = relationship.getSourcePath();
				Resource srcRes = rr.resolve(srcPath);
				if(srcRes != null && !ResourceUtil.isNonExistingResource(srcRes)){
					if(srcPath != null && srcPath.startsWith("/content/girlscouts-template")){
						councilTemplatePages.add(path);
						return;
					}else{
						if(srcPath != null && srcPath.startsWith("/content/webtocase")){
							noncountPages.put(path, "ExceptionPages");
							return;
						}
					}
				}
			}
		}catch(Exception e){
			
		}
		
		// Thank You Pages
		for (int i = 0; i < thankYouPages.size(); i++) {
			String[] val = thankYouPages.get(i).split(" ");
			if (path.equals(val[0])) {				
				//noncountPages.add(path + " | ThankYou " + val[1]);
				noncountPages.put(path, "ThankYou " + val[1]);
				return;
			}
		}	
		
		
		councilAddedPages.add(path);
	}
	
	void overridePages() {
		
		for (String path: overrides){
			if (noncountPages.containsKey(path)) {
				councilAddedPages.add(path);
				noncountPages.remove(path);
			}
		}
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
	
	noncountPages = new HashMap<String,String>(); // new ArrayList<String>();
	allPages = new ArrayList<String>();
	councilTemplatePages = new ArrayList<String>();
	councilAddedPages = new ArrayList<String>();
	
	nationalTemplatePages = new ArrayList<String>();
	exceptionPages = new ArrayList<String>();
	exceptionDirectories = new ArrayList<String>();	
	
	countedResourceTypes = new ArrayList<String>();
	thankYouPages = new ArrayList<String>();	
	defaultValues = new ArrayList<String>();	
	overrides = new ArrayList<String>();
	
	ArrayList<String> links = new ArrayList<String>();
	ArrayList<String> footerLinkFilters = new ArrayList<String>();

	// If paths value is empty set default
	// Retrieve paths set by homepage component in en/jcr:content to set as defaults
	
	ValueMap enProperties = en.getProperties();
	Node node = resource.adaptTo(Node.class);
	Node filtersNode = null;
	if(node.hasNode("filters")){
		filtersNode = node.getNode("filters");
	}
	String resourceTypes = properties.get("resourceTypes", "");
	String[] overridePaths = properties.get("overrides", String[].class);
	
		
	if (resourceTypes.isEmpty()) {
		resourceTypes = RESOURCE_TYPES;
		node.setProperty("resourceTypes", resourceTypes);
		node.getSession().save();
	}
	if (filtersNode == null || !filtersNode.hasNodes()) {		
		
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
		if(!node.hasNode("filters")){
			filtersNode = node.addNode("filters", "nt:unstructured");
	    }else{
	    	filtersNode = node.getNode("filters");
	    }
		for(int i = 0; i < defaultValues.size(); i++){
			try{
				String filter = defaultValues.get(i);
	            String[] filterProperties = filter.split("\\|\\|\\|");
				Node itemNode = null;
	            if(!filtersNode.hasNode("item" + i)){
	                itemNode = filtersNode.addNode("item" + i, "nt:unstructured");
	            } else{
	                itemNode = filtersNode.getNode("item" + i);
	            }	            
	            itemNode.setProperty("label", filterProperties[0]);
	            itemNode.setProperty("path", filterProperties[1]);
	            itemNode.setProperty("pageOnly", filterProperties[2]);
	            itemNode.setProperty("subDirOnly", filterProperties[3]);  
	            filtersNode.getSession().save();
			}catch(Exception e){
				
			}
		}
	}
	
	countedResourceTypes = listToArray(resourceTypes);	
	processFilterPaths(filtersNode);
	processOverridePaths(overridePaths);

	// These pages belong to council template pages 
	// They aren't inherited so no mixins to check
	String eventlist = enProperties.get("eventLanding", ""); 
	String eventcalendar = enProperties.get("calendarPath", ""); 
	nationalTemplatePages.add(eventlist);
	nationalTemplatePages.add(eventcalendar);
	
	
	// Go through council directory to count pages
	recurse(resourceResolver, top);	
	
	allPages.remove(0); // removes /content/<council>
	LiveRelationshipManager lrm = resourceResolver.adaptTo(LiveRelationshipManager.class);
	for (int i = 0; i < allPages.size(); i++) {
		processPage(resourceResolver, allPages.get(i), lrm);
	}
	
	// If overrides are set, add override paths
	overridePages();
	
		
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
	<% for(Map.Entry<String,String> entry: noncountPages.entrySet()) { %>
		<br><%= linkify(entry.getKey(), entry.getValue()) %>
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
						<% for(Map.Entry<String,String> entry: noncountPages.entrySet()) { %>
							<br><%= linkify(entry.getKey(), entry.getValue()) %>
						<% } %><br>
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
