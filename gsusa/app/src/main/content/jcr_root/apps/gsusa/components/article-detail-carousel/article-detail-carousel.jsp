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
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();
boolean editMode = false;
String tag = selectors.length >= 1 ? selectors[0] : "articles";
if(!tag.equals("articles"))
    request.setAttribute("linkTagAnchors", "#" + tag);

if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
    editMode = true;
}

int num = 20;
try {
    if (selectors.length >= 2) {
        num = Integer.parseInt(selectors[1]);
    }
} catch (java.lang.NumberFormatException e) {}

// If num is less than zero, it means it should be sorted by priority.
String sortByPriority = "false";
if (num < 0) {
    sortByPriority = "true";
    num = num * -1;
}

List<String> tagIds = new ArrayList<String>();
for (String singleTag : tag.split("\\|")) {
    tagIds.add("gsusa:content-hub/" + singleTag);
}
List<Hit> hits = getTaggedArticles(tagIds, num, resourceResolver, sling.getService(QueryBuilder.class), sortByPriority);
%>

<div class="article-detail-carousel">
    <div class="article-slider">
    <%for (Hit h : hits){
        request.setAttribute("articlePath", h.getPath());%>
        <div>
            <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
        </div>
    <% } %>
        <div>
            <div class="article-tile last">
                <section><a>See More</a></section>
            </div>
        </div>
    </div>
</div>
