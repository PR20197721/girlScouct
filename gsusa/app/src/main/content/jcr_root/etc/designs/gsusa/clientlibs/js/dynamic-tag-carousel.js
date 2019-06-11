(function(){
	var END_POINT = '/content/gsusa/en/components_old/article-hub/dynamic-tag-carousel';

	DynamicTagCarousel = function(id, num, defaultTag) {
		this.id = id;
		this.num = num;
		this.defaultTag = defaultTag;
	};

	DynamicTagCarousel.prototype.load = function() {
		var hash = window.location.hash;

		if (hash) {
			hash = hash.indexOf('#') == 0 ? hash.substring(1) : hash;
		} else {
			hash = "";
		}

		var result = hash.split('$$$');
		var tags = hash && result.length >= 1 ? result[0] : this.defaultTag;

		var selector = '#' + this.id;
		var url = END_POINT + '.' + tags + '.' + this.num + '.html';

		// After Load the content to the selector trigger the callback
		//  in this case going to be the article style in app.js
		$(selector).load(url, loadArticleCarousel);
	};
	function loadArticleCarousel() {
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
    }
})();
