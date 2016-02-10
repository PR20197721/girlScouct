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
String seeMoreLink = "";
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


List<String> tagIds = new ArrayList<String>();
for (String singleTag : tag.split("\\|")) {
	tagIds.add("gsusa:content-hub/" + singleTag);
}
List<Hit> hits = getTaggedArticles(tagIds, num, resourceResolver, sling.getService(QueryBuilder.class), "true");

//now query for the page
String categoryPagePath = getArticleCategoryPagePath(tag.split("\\|"), resourceResolver.adaptTo(Session.class));
if (categoryPagePath != null) {
	seeMoreLink = categoryPagePath + ".html";
}
%>

<div class="article-detail-carousel">
    <div class="article-slider">
    <%for (Hit h : hits){
        request.setAttribute("articlePath", h.getPath());%>
        <div>
            <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
        </div>
    <% } %>
    <% if (seeMoreLink != null) { %>
		<div>
			<div class="article-tile last">
				<section><a href="<%= seeMoreLink %>">See More</a></section>
			</div>
		</div>
	<% } %>
    </div>
</div>
<script>
$(document).ready(function() {
	var TILES_SELECTOR = '.article-detail-carousel .article-tile';
	var slides = $(TILES_SELECTOR);
	var currentSlideIndex = -1;
	for (var slideIndex = 0; slideIndex < slides.length; slideIndex++) {
		var slide = slides[slideIndex];
		var link = $(slide).find('a').attr('href');
		if (typeof link !== 'string') {
			return;
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

	function adjustSlidesAndSlick() {
		slides = $(TILES_SELECTOR);
		var currentSlideHtml;
		for (var slideIndex = 0; slideIndex < slides.length; slideIndex++) {
			if (slideIndex == currentSlideIndex) {
				currentSlideHtml = '<div><div class="article-tile current">' + $(slides[slideIndex]).html() + '</div></div>';
				$(slides[slideIndex]).parent().remove();
				break;
			}
		}

		slides = $(TILES_SELECTOR);
		var middleSlideIndex = parseInt(slides.length / 2, 10);
		if (slides.length == 0) {
			$('.article-detail-carousel .article-slider').prepend(currentSlideHtml);
		} else {
			for (var slideIndex = 0; slideIndex < slides.length; slideIndex++) {
				if (slideIndex == middleSlideIndex - 1) {
					$(slides[slideIndex]).parent().after(currentSlideHtml);
				}
			}
		}

        var initialSlide = slides.length + 1 > 4 ? middleSlideIndex : 0;
        $(".article-detail-carousel .article-slider").slick({
            lazyLoad: 'ondemand',
            slidesToShow: 4,
            touchMove: true,
            slidesToScroll: 4,
            infinite: false,
        });
        // Initial Slide does not work. Use this instead.
        $(function(){
       		$('.article-detail-carousel .article-slider').slick('slickGoTo', initialSlide, true);
        });
	}

	if (currentSlideIndex == -1) {
		$.get(
			'/content/gsusa/en/components/article-hub/article-tile.html',
			{articlePath: window.location.pathname},
			function(html) {
				html = '<div><div class="article-tile current">' + html + '</div></div>';
				$('.article-detail-carousel .article-slider').append(html);
				currentSlideIndex = slides.length;
				adjustSlidesAndSlick();
			}
		)
	} else {
		adjustSlidesAndSlick();
	}
});
</script>