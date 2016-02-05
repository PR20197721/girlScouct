<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*,
    java.io.*, java.util.regex.*,
	java.net.*,
	org.apache.sling.commons.json.*,
	org.apache.sling.api.request.RequestDispatcherOptions,
	com.day.cq.wcm.api.components.IncludeOptions,
	org.apache.sling.jcr.api.SlingRepository,
	java.util.Map,
	java.util.HashMap,
	java.util.List,
	com.day.cq.search.QueryBuilder,
    com.day.cq.search.Query,
    com.day.cq.search.PredicateGroup,
    com.day.cq.search.result.SearchResult,
    com.day.cq.search.result.Hit,
    org.apache.sling.api.request.RequestPathInfo,
	com.day.cq.wcm.api.WCMMode" %>
<%@page session="false" %>
<%
	String tag = properties.get("tag","");
	String title = properties.get("componentTitle","");
	int num = Integer.parseInt(properties.get("num","10"));
	String [] selectors = slingRequest.getRequestPathInfo().getSelectors();


	String sortByPriority = properties.get("sortByPriority", "false");

	if(!title.isEmpty()){
    	%> <h4> <%=title%></h4> <%
	}

	if (tag.isEmpty()) {
	    if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
			<div class="article-slider">
				<p>###Configure Article Carousel</p>
			</div>
	<%  } else{ %>
			<div class="article-slider">
			</div>
	<%	}
	} else{
		String linkTagAnchors = "#" + tag.replaceAll("gsusa:content-hub/", "").replaceAll("/", "|");
		request.setAttribute("linkTagAnchors", linkTagAnchors);
		
		QueryBuilder builder = sling.getService(QueryBuilder.class);
		String output = "";
		Map<String, String> map = new HashMap<String, String>();
		map.put("type","cq:Page");
		map.put("tagid",tag);
		map.put("tagid.property","jcr:content/cq:tags");
		map.put("p.limit",num + "");
		if (sortByPriority.equals("true")) {
			map.put("orderby","@jcr:content/articlePriority");
			map.put("orderby.sort","desc");
		    map.put("2_orderby","@jcr:content/editedDate");
		    map.put("2_orderby.sort","desc");
		} else {
			map.put("orderby","@jcr:content/editedDate");
			map.put("orderby.sort","desc");
		}
		Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
		SearchResult sr = query.getResult();
		List<Hit> hits = sr.getHits();
    %>
	<div class="article-slider">
    	<%for (Hit h : hits) {
        	request.setAttribute("articlePath", h.getPath());%>
        	<div>
            	<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
        	</div>
    	<%}
    %>
	</div>
<%}%>
