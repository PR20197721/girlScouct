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
	java.io.ByteArrayOutputStream,
	com.day.cq.contentsync.handler.util.RequestResponseFactory,
	com.day.cq.wcm.api.WCMMode,
	com.day.cq.tagging.TagManager,
	com.day.cq.tagging.Tag,
	com.day.cq.dam.api.Asset,
	org.apache.sling.api.SlingHttpServletRequest,
	org.apache.sling.api.SlingHttpServletResponse,
	org.apache.sling.engine.SlingRequestProcessor,
	org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpServletRequest,
	javax.servlet.http.HttpServletResponse,
	javax.servlet.http.HttpSession,
	org.girlscouts.common.events.search.*
	"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />

<%!
    
	/************************** Page Counter Component ************************
	** This components lists pages that count towards page count
	** for all councils that are AEM
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
	
	RequestResponseFactory requestResponseFactory;
	SlingRequestProcessor requestProcessor;
	
	ArrayList<Resource> councils;
	ArrayList<String> scouncils;
	
	
	ArrayList<Resource> listCouncils (ResourceResolver resourceResolver, Resource content, String componentPath) {
		ArrayList<Resource> list = new ArrayList<Resource>();
		
		for (Iterator<Resource> iterator = content.listChildren(); iterator.hasNext();) {
			Resource res = iterator.next();
			
			String path = res.getPath() + componentPath;
			
			Resource pagecounter = resourceResolver.getResource(path);
			if ((pagecounter != null) && (!path.contains("gsusa"))) {
				list.add(res);
			}

		}
		
		return list;
	}
	
	
	String render(RequestResponseFactory requestResponseFactory, SlingRequestProcessor requestProcessor, 
			ResourceResolver resourceResolver, Resource resource, String componentPath) {
		
		/* The resource path to resolve. Use any selectors or extension. */
	    String requestPath = "/content/" + resource.getName() + componentPath + ".html"; //"/en/jcr:content/content/styled-subpar/pagecounter.html";

	    /* Setup request */
	    HttpServletRequest req = requestResponseFactory.createRequest("GET", requestPath);
	    WCMMode.DISABLED.toRequest(req);

	    /* Setup response */
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    HttpServletResponse resp = requestResponseFactory.createResponse(bout);

	    /* Process request through Sling */
	    String html;
	    try {
	    	requestProcessor.processRequest(req, resp, resourceResolver);
		    html = bout.toString();
	    } catch (Exception e) {
	    	html = e.getMessage();
	    }
	    
		return html;
	}
	
	String processHTMLToList(String html) {
		String[] sections = html.split("#");
		String result = "<!-- " + sections[1] + sections[2] + sections[3] + sections[4] + " -->";
		return result;
	}
	
	String processHTMLToTable(String html) {
		String[] sections = html.split("#");
		String result = "<!-- " + sections[6] + " -->";
		return result;
	}
	
%>

<%
	requestResponseFactory = sling.getService(RequestResponseFactory.class);
	requestProcessor = sling.getService(SlingRequestProcessor.class);

	Resource content = resourceResolver.getResource("/content");
	Node node = resourceResolver.getResource("/content").adaptTo(Node.class);
	//Page template = pageManager.getPage("/content/girlscouts-template");

	

	String dialogPath = properties.get("pagecountercomponentpath", "");
	
	
	councils = listCouncils(resourceResolver, content, dialogPath);	
	

	ArrayList<String> pagesList = new ArrayList<String>();
	ArrayList<String> pagesTable = new ArrayList<String>();
	ArrayList<String> errorList = new ArrayList<String>();
	
	for (Resource council: councils) {
		String html = "";
		String render;
		Page tpage = council.adaptTo(Page.class);
		html += "<h3>" + tpage.getTitle() + " (" + council.getName() + ")</h3>";
		render = render(requestResponseFactory, requestProcessor, resourceResolver, council, dialogPath);
		try {
			html += processHTMLToList(render);
			pagesList.add(html);
			html = processHTMLToTable(render);
			pagesTable.add(html);
		} catch (Exception e) {
			errorList.add(council.getName() + ": " + render);
		}
	}
	
	
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
	This component goes through all councils that contain Page Counter component and counts all pages 
	that are part of National Templates and are added by councils. Upon adding the component, it will 
	automatically generate three lists per council - a list of pages that are part of National 
	Templates which counts, a list of pages that are added by councils that counts and a list of pages 
	added by councils that do not count. Filter that decides whether a page counts or not can be 
	accessed by the dialog of Page Counter component located on each council's end.
	</p>


	<br><br>
	Number of councils: <%= pagesList.size() %>
	<% for (String str: pagesList) { %>
		<%= str %>
	<% } %>
	
	
	<div style="height:400px; overflow:scroll;">
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
				<% for (String str: pagesTable) { %>
					<%= str %> 
				<% } %>
			</tbody>
		</table>		
	</div>
	<br>
	<button id="thebutton" onClick="copy();"> CLICK TO COPY TABLE </button>
	<br>
	
	
	<% if (!errorList.isEmpty())  { %>
	<% for (String str: errorList) { %>
		<%= str %> <br>
	<% } } %>
	
	
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
