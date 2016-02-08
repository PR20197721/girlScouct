<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
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
	java.util.ArrayList,
	com.day.cq.search.QueryBuilder,
    com.day.cq.search.Query,
    com.day.cq.search.PredicateGroup,
    com.day.cq.search.result.SearchResult,
    com.day.cq.search.result.Hit,
    org.apache.sling.api.request.RequestPathInfo,
	com.day.cq.wcm.api.WCMMode" %>
<%@page session="false" %>
<%

String[] tags = (String[])properties.get("tag",String[].class);
String title = properties.get("componentTitle","");
int num = Integer.parseInt(properties.get("num","10"));
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();

String sortByPriority = properties.get("sortByPriority", "false");

if(!title.isEmpty()){
	%> <h4> <%=title%></h4> <%
}

if(tags == null){
    if(WCMMode.fromRequest(request) == WCMMode.EDIT){
    %>
	<div class="article-slider">
        <p>###Configure Article Carousel</p>
    </div>
<%
    } else{ %>
	<div class="article-slider">
    </div>
 <% }
} else{

String linkTagAnchors = "#";
request.setAttribute("linkTagAnchors", linkTagAnchors);

QueryBuilder builder = sling.getService(QueryBuilder.class);

List<String> tagIds = new ArrayList<String>();
for(String tag : tags){
	tagIds.add(tag);
}

List<Hit> hits = getTaggedArticles(tagIds, num, resourceResolver, builder, sortByPriority);

    %>

<div class="article-slider">

    <%
    if(hits.size() > 0){
        for (Hit h : hits){
        request.setAttribute("articlePath", h.getPath());%>
        <div>
            <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
        </div>
    <%	}
    } else{
        if(WCMMode.fromRequest(request) == WCMMode.EDIT){
            %> <h4>##No Results Found For Specified Tags##</h4> <%
        }
    }

    %>
</div>

<% }%>

