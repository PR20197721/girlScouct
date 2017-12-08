<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,
    org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	com.day.text.Text,
	org.girlscouts.web.events.search.*,
	java.util.Collections" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%
	String q = request.getParameter("q");
	String[] tags = new String[]{};
	if (request.getParameterValues("tags") != null) {
		tags = request.getParameterValues("tags");
	} 
	String offset = request.getParameter("offset");
	String regions = request.getParameter("regions");
	String month = request.getParameter("month");
	String startdtRange = request.getParameter("startdtRange");
	String enddtRange = request.getParameter("enddtRange");
	String year  =request.getParameter("year");
	if(q == null && tags.length == 0 && offset == null && month == null && startdtRange == null && enddtRange == null && year == null){
		%>
		<div id="eventListWrapper"></div>
		<script>
			var jsonPath = '<%=resource.getPath()%>';
			$(document).ready(function() {
				var eventLoader = new EventLoader(jsonPath, $("#eventListWrapper"));
			});		
		</script>
		<%
	}else{
		%>
		<cq:include script="events-from-search.jsp"/>
		<%
	}
%>