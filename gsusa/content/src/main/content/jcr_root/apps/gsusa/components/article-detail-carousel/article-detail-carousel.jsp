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
	com.day.cq.search.QueryBuilder,
    com.day.cq.search.Query,
    com.day.cq.search.PredicateGroup,
    com.day.cq.search.result.SearchResult,
    com.day.cq.search.result.Hit,
    org.apache.sling.api.request.RequestPathInfo" %>
<%@page session="false" %>
<%
String path = "/content/gsusa/en/content-hub/articles";
String [] selectors = slingRequest.getRequestPathInfo().getSelectors();

String tag = selectors.length >= 1 ? selectors[0] : "articles";
tag = "gsusa:content-hub/" + tag.replaceAll("\\|", "/");

int num = 20;
try {
	if (selectors.length >= 2) {
		num = Integer.parseInt(selectors[1]);
	}
} catch (java.lang.NumberFormatException e) {}

QueryBuilder builder = sling.getService(QueryBuilder.class);
String output = "";
Map<String, String> map = new HashMap<String, String>();
map.put("type","cq:Page");
map.put("path",path);
map.put("tagid",tag);
map.put("tagid.property","jcr:content/cq:tags");
map.put("p.limit",num + "");
map.put("orderby","@jcr:content/tilePriority");
map.put("orderby.sort","desc");
map.put("2_orderby","@jcr:content/editedDate");
map.put("2_orderby.sort","desc");

Query query = builder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
SearchResult sr = query.getResult();
List<Hit> hits = sr.getHits();
%>
    
<div class="article-detail-carousel">
    <div class="article-slider">
    <%for (Hit h : hits){
        request.setAttribute("articlePath", h.getPath());%>
        <div>
            <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
        </div>
    <%}
    %>
    </div>
</div>
<script>
$(document).ready(function() {
    $(".article-detail-carousel .article-slider").slick({
        lazyLoad: 'ondemand',
        slidesToShow: 4,
        touchMove: true,
        slidesToScroll: 4,
        centerMode: true,
        // infinite: false,
        // responsive: [
        //  {
        //    breakpoint: 480,
        //    settings: {
        //     arrows: false,
        //     centerMode: true,
        //     centerPadding: '60px',
        //     slidesToShow: 1,
        //    }
        //  }
        // ]
    });
});
</script>