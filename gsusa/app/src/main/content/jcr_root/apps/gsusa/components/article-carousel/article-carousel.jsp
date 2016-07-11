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
	java.lang.StringBuilder,
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
String titleLink = properties.get("titleLink", "");
if(!titleLink.isEmpty()) {
	titleLink = titleLink + ".html";
}
String seeMoreLink = titleLink;
String hasBorderLine = properties.get("borderLine", String.class);

int num = Integer.parseInt(properties.get("num","11"));
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();


String sortByPriority = properties.get("sortByPriority", "false");
if ("on".equals(hasBorderLine)) {
	%> <hr style="border-top: solid 1px #000000"><%
}

if(!title.isEmpty()){
    if(!titleLink.isEmpty()){
        %><h4><a href="<%=titleLink%>"> <%=title%> </a></h4> <%
    } else{
		%> <h4> <%=title%></h4> <%
    }
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

StringBuilder anchorsBuilder = new StringBuilder("#");

QueryBuilder builder = sling.getService(QueryBuilder.class);

List<String> tagIds = new ArrayList<String>();
List<String> cleanTags = new ArrayList<String>();
for(String tag : tags){
    String cleanTag = tag.replaceAll("gsusa:content-hub/", "");
    cleanTags.add(cleanTag);
    anchorsBuilder.append("|").append(cleanTag);
	tagIds.add(tag);
}
anchorsBuilder.deleteCharAt(1);
if(!"".equals(seeMoreLink)){
	anchorsBuilder.append("$$$");
	anchorsBuilder.append(resourceResolver.map(seeMoreLink));
}
request.setAttribute("linkTagAnchors", anchorsBuilder.toString());

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

    if (!"".equals(seeMoreLink)) {
    %>
	<div>
		<div class="article-tile last">
			<section><a href="<%= seeMoreLink %>"><img class="last" src="/etc/designs/gsusa/images/see-more.png" data-at2x="/etc/designs/gsusa/images/see-more@2x.png"/></a></section>
		</div>
	</div>
    <% } %>
</div>
<%
 }
%>