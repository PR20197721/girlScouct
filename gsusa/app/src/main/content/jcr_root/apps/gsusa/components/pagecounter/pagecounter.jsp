<%@ page
	import="java.util.List,	
	java.util.ArrayList,
	java.util.Iterator"
   %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<cq:defineObjects />
<cq:includeClientLib categories="apps.gsusa.components.pagecounter" />
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
	

	ArrayList<String> pageCounterPaths;
	
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
	
	ArrayList<String> getPaths(ArrayList<Resource> councils,String componentPath){
		
		ArrayList<String> paths = new ArrayList<>();
		
		for(Resource resource : councils){
			paths.add(("/content/" + resource.getName() + componentPath + ".html").toString());
		}
		return paths;
	}
%>

<%
	

	Resource content = resourceResolver.getResource("/content");
	Node node = resourceResolver.getResource("/content").adaptTo(Node.class);
	//Page template = pageManager.getPage("/content/girlscouts-template");	

	String dialogPath = properties.get("pagecountercomponentpath", "");	
	
	councils = listCouncils(resourceResolver, content, dialogPath);	
	
	pageCounterPaths = getPaths(councils,dialogPath);
	
	%>

	<div id ="councilPath" data-council-paths= "<%=pageCounterPaths %>">

<div>
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
    Number of councils: <%= pageCounterPaths.size() %>

    <div id="pagesList" class="pagesList"></div>

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
			</tbody>
		</table>		
	</div>
	<br>
	<button id="thebutton" onClick="copy();"> CLICK TO COPY TABLE </button>
	<br>

</div>
</div>