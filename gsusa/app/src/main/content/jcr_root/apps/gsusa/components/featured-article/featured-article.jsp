<%--
  blockquote component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode,
     			com.day.cq.search.result.Hit,
				java.util.List,
				java.util.ArrayList,
				java.lang.StringBuilder,
				com.day.cq.search.QueryBuilder" %>
<%@page session="false" %>

<%


	String title = properties.get("title", "");
	String listingPage = properties.get("listingPage", "");
	String titleLink = listingPage;

	if(!titleLink.isEmpty())
		titleLink = titleLink + ".html";

	String[] tags = (String[])properties.get("tag",String[].class);

	int num = Integer.parseInt(properties.get("num","10"));
	String [] selectors = slingRequest.getRequestPathInfo().getSelectors();


	String sortByPriority = properties.get("sortByPriority", "false");



	QueryBuilder builder = sling.getService(QueryBuilder.class);



	List<Hit> hits = new ArrayList<Hit>();
	if(tags == null || tags.length == 0){
		hits = getAllArticles(num, resourceResolver, builder, sortByPriority);
    } else{
        StringBuilder anchorsBuilder = new StringBuilder("#");
        List<String> tagIds = new ArrayList<String>();
		for(String tag : tags){
    		String cleanTag = tag.replaceAll("gsusa:content-hub/", "");
    		anchorsBuilder.append("|").append(cleanTag);
			tagIds.add(tag);
		}
		anchorsBuilder.deleteCharAt(1);
        if(!listingPage.isEmpty())
        	anchorsBuilder.append("$$$").append(titleLink);

		request.setAttribute("linkTagAnchors", anchorsBuilder.toString());
		hits = getTaggedArticles(tagIds, num, resourceResolver, builder, sortByPriority);
    }


if(title.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>
<h4>##Configure Featured Article Title##</h4>
<% }else{
    if(!titleLink.isEmpty()) {
        %>  <h4><a href="<%=titleLink%>"><%=title%></a></h4><%
    } else {
		%> <h4><%=title%></h4> <%
    }
} %>

	<%
    if(hits.size() > 0) {
    	Hit h = hits.get(0);

    	request.setAttribute("articlePath", h.getPath());%>
	<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />

    <%} else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
	<div class="article-tile"><h3>LARGE ARTICLE</h3></div>
	<%} else{%>
	<div class="article-tile"></div>
	<%}%>
	<div class="block-grid">
		<h4>FEATURED PROFILE</h4> 
    	<h3>How to Use Social Media to Improve Your Career</h3>
    	<p> Emily Miethner believes there's magic in connecting people. And whether working as CEO of FindSpark, an online community
    	for young professionals seeking career success, or teaching social media courses at the Fashion Institute of Technology 
    	in New York City, she makes the magic happen.</p>
    </div>

