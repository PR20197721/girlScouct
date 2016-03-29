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
<script>
$(document).ready(function() {
    var TILES_SELECTOR = '.article-detail-carousel .article-tile';

    // Add "see more" tile if link is found
    var hash = window.location.hash;

    if (hash) {
        hash = hash.indexOf('#') == 0 ? hash.substring(1) : hash;
    } else {
        hash = "";
    }
    var hashSplitResult = hash.split('$$$');
    var seeMoreLink = hashSplitResult.length >= 2 ? hashSplitResult[1] : null;

    var seeMoreTileSelector = '.article-detail-carousel .article-tile.last';
    if (!seeMoreLink) {
        seeMoreLink = $('#dynamic-tag-carousel-listing-page').attr('data');
    }

    if (seeMoreLink) {
        $(seeMoreTileSelector + ' a').attr('href', seeMoreLink);
    } else {
        $(seeMoreTileSelector).parent().remove();
    }

    var slides = $(TILES_SELECTOR);
    var currentSlideIndex = -1;
    for (var slideIndex = 0; slideIndex < slides.length; slideIndex++) {
        var slide = slides[slideIndex];
        var link = $(slide).find('a').attr('href');

        if (typeof link !== 'string') {
            return;
        }

        var clazz = $(slide).find('a').attr('class');
        if (clazz == 'photo' || clazz == 'video non-click') {
            if (link.indexOf('#') == -1) {
                link += '#';
            }
            if (seeMoreLink) {
                $(slide).find('a').attr('href', link + '$$$' + seeMoreLink);
            }
        }

        var hashIndex = link.lastIndexOf('#');
        if (hashIndex == -1) {
            hashIndex = link.length;
        }
        var questionMarkIndex = link.lastIndexOf('?');
        if (questionMarkIndex == -1) {
            questionMarkIndex = link.length;
        }

        var hashOrQuestionMarkIndex = Math.min(hashIndex, questionMarkIndex);
        if (hashOrQuestionMarkIndex != -1) {
            link = link.substring(0, hashOrQuestionMarkIndex);
        }
        if (link == window.location.pathname) {
            $(slide).addClass('current');
            currentSlideIndex = slideIndex;

        }
    }

    var slider = $(".dynamic-tag-carousel .article-detail-carousel .article-slider");

    slider.slick({
        lazyLoad: 'ondemand',
        slidesToShow: 4,
        touchMove: true,
        slidesToScroll: 4,
        infinite: false
    });

    // Initial Slide does not work. Use this instead.
    $(function() {
        if (currentSlideIndex != -1) {
            $(".dynamic-tag-carousel .article-detail-carousel .article-slider").slick('slickGoTo', currentSlideIndex, true);
        }
    });
});
</script>