/*jslint browser: true, eqeq: true*/
/*global $, jQuery, gsusa, alert, Handlebars, YT, Vimeo, console, Modernizr */
/*global shopautoscroll, shoptimedelay, redirectCampFinderURL, currentCampFinderURL, joinRedirectAutoplaySpeed, joinRedirectSpeed */

//
//
// Follow these Naming Conventions
// https://google.github.io/styleguide/javascriptguide.xml#Naming
//
//
var isRetina = (
    window.devicePixelRatio > 1 ||
    (window.matchMedia && window.matchMedia("(-webkit-min-device-pixel-ratio: 1.5),(-moz-min-device-pixel-ratio: 1.5),(min-device-pixel-ratio: 1.5)").matches)
);

var boundHashForms = {};

function bindSubmitHash(form) {
    "use strict";
    // Check if form exists and ensure a single form binding
    if (boundHashForms[form.formElement] || !$(form.formElement)) {
        return false;
    }
    boundHashForms[form.formElement] = true;

    $(form.formElement).submit(function (event) {
        // Stop other events
        if (event.preventDefault) {
            event.preventDefault();
        } else {
            event.stop();
        }
        event.returnValue = false;
        event.stopPropagation();

        var hashElement = $(this).find(form.hashElement),
            hash = hashElement.val(),
            pattern = hashElement.attr("pattern") || false,
            match = !pattern || (pattern && hash.match(pattern));

        // Prevent double submit on redirect and invalid data (if a pattern is specified)
        if (form.submitted || !match) {
            return false;
        }

        // Do ajax request instead of redirect
        if (form.ajax) {
            if (form.edit) {
                alert("This tool can only be used on a live page");
                return false;
            }
            $.ajax({
                method: form.ajax.method || "POST",
                url: form.ajax.url || "",
                data: form.ajax.data || $(this).serialize(),
                async: form.ajax.async || false,
                success: form.ajax.success || function () {}
            });
            return false;
        }

        // Redirect to the results page while maintaining query
        if (form.currentUrl == form.redirectUrl) {
            location.hash = hash;
            window.location.reload();
        }else{
        	window.location = form.redirectUrl + ".html" + (window.location.search != "" ? window.location.search : "?") + "#" + hash;
        }

        form.submitted = true;
        return false;
    });
}

function fixColorlessWrapper() {
    'use strict';
    // inkoo - this crazy code is to accommodate the initial hidden state of the slick layer for videos
    var colorlessWrappers = $(".story.colorless .bg-wrapper"),
        thisWrapperStyle,
        i;
    for (i = 0; i < colorlessWrappers.length; i += 1) {
        thisWrapperStyle = $(colorlessWrappers[i]).attr("style");
        if (thisWrapperStyle) {
            $(colorlessWrappers[i]).attr("style", thisWrapperStyle.replace(/, ?[0-9\.]*\)/, ", 1)"));
        }
    }
}

function fixSlickListTrack() {
    'use strict';
    // inkoo - this crazy code is to accommodate the initial hidden state of the slick layer for videos
    // don't use each function in jquery because ie doesn't support it
    // $(".slick-list .slick-track").each(function() {if($(this).attr("style")) {$(this).attr("style", $(this).attr("style").replace(/width: ?0px;/, "width: 100%;"))}});
    // $(".slick-slide.slick-active").each(function() {if($(this).attr("style")) {$(this).attr("style", $(this).attr("style").replace(/width: ?0px;/, "width: 100%;"))}});
    var slickListTrack = $(".slick-list .slick-track"),
        thisWrapperStyle,
        i;
    for (i = 0; i < slickListTrack.length; i += 1) {
        thisWrapperStyle = $(slickListTrack[i]).attr("style");
        if (thisWrapperStyle) {
            $(slickListTrack[i]).attr("style", thisWrapperStyle.replace(/width: ?0px;/, "width: 100%;"));
        }
    }
}

function fixSlickSlideActive() {
    'use strict';
    var slickSlideActive = $(".slick-slide.slick-active"),
        thisWrapperStyle,
        i;
    for (i = 0; i < slickSlideActive.length; i += 1) {
        thisWrapperStyle = $(slickSlideActive[i]).attr("style");
        if (thisWrapperStyle) {
            $(slickSlideActive[i]).attr("style", thisWrapperStyle.replace(/width: ?0px;/, "width: 100%;"));
        }
    }
}

(function bindButtonForms() {
    "use strict";
    //Detect ipad
    var touchOrClick = (navigator.userAgent.match(/iPad/i)) ? "touchstart" : "click";

    $(document).on(touchOrClick, function (event) {
        var target = $(event.target),
            form = target.closest(".button-form-target.open").length,
            button = target.is(".button-form");

        // Close all other open forms, including when clicking on a button
        if (!form) {
            //console.log("clicked outside form");
            $(".button-form-target.open").removeClass("open").addClass("hide");
        }
        /*
        if (form) {
            console.log("clicked form");
        }
        */
        // Open child form
        if (button) {
            //console.log("clicked button");
            target.find(".button-form-target.hide").removeClass("hide").addClass("open");
            target.find("input[type='text']").focus();
        }
    });
}());

//
//
// Main
//
//

(function ($) {
    'use strict';

    // Foundation JavaScript
    // Documentation can be found at: http://foundation.zurb.com/docs
    $(document).foundation();

    var isIE11 = false,
        isNewerThanIE9 = true,
        MEDIUM_ONLY = 768,
        mobile = false,
        homepageScrollTopPos,
        lastAfterSlick = null,
        carouselSliderPropogate = true,
        ImageMap,
        imageMap,
        SlickPlayer,
        Underbar;

    if (navigator.userAgent.indexOf("Trident\/7") != -1 && parseFloat($.browser.version) >= 11) {
        isIE11 = true;
    }

    function isMobile() { //https://stackoverflow.com/questions/19291873/window-width-not-the-same-as-media-query
        return Modernizr.mq('(max-width: ' + MEDIUM_ONLY + 'px)');
    }
    mobile = isMobile();
    $(window).on("resize", function () {
        if (isMobile() !== mobile) { // Trigger once when the breakpoint is passed
            mobile = !mobile;
            $(window).trigger("breakpoint");
            //console.log("Mobile is: " + mobile);
        }
    });

    // YouTube API loaded
    function ytLoaded() {
        return window['YT'] && YT.Player;
    }
    
    function vimeoLoaded(){
    		return window['Vimeo'] && Vimeo.Player;
    }

    if (ytLoaded()) {
        $(window).trigger("ytLoaded");
    } else {
        window.onYouTubeIframeAPIReady = function () {
            $(window).trigger("ytLoaded");
        };
    }
    
    if (vimeoLoaded()) {
        $(window).trigger("vimeoLoaded");
    } else {
    		// Vimeo doesn't provide a callback so we need to poll.
    		var vimeoIntervalId = window.setInterval(function(){
    			if(vimeoLoaded()){
    		        $(window).trigger("vimeoLoaded");
    		        window.clearInterval(vimeoIntervalId);
    			}
    		}, 100);
    }

    //add height to the content for the footer to be always at the bottom.
    function fixBottomFooter() {
        var footer_height = $("footer").outerHeight(),
            header_height = $(".header").outerHeight(),
            total_height = "calc((100vh - " + (footer_height + header_height) + "px))";
        //$(".main-content, #main .vtk").css({
        	
        $("#main .vtk").css({
            "min-height": total_height
        });
        $(".join-volunteer").css({
            "min-height": "initial"
        });
    }

    function document_close_all() {

        //Detect ipad
        var touchOrClick = (navigator.userAgent.match(/iPad/i)) ? "touchstart" : "click";

        //when clicking outside of the form it will close the input.
        $(document).on(touchOrClick, function (event) {
            event.stopPropagation();
            var target = $(event.target);
            if (target.closest('.tab-bar .search-form').length === 0 && $(".tab-bar .search-form input").css('display') != 'none') {
                $(".tab-bar .search-form span").removeClass('hide');
                $(".tab-bar .search-form input").hide('slow');
            }
            if (target.closest('.featured-stories li').length === 0 && target.closest(".story").css('display') != 'none') {
                $(".story").fadeOut('slow');
                $("body").css('overflow', '');
                $(".featured-stories").css('position', '');
            }
        });

    }

    //header join now volunteer forms.
    function headercomptrigger(item, event) {
        event.stopPropagation();
        item.input.stop().animate({
            width: 'toggle'
        }, 500, function () {
            if (item.input.is(':visible')) {
                item.input.focus();
                item.button.addClass('on');
            } else {
                item.button.removeClass('on');
                item.input.hide();
            }
        });
        event.preventDefault();
    }

    function slide_search_bar() {
        var searchSlider = { //search slider for the mobile.
                form: $(".tab-bar .search-form"),
                input: $(".tab-bar .search-form input"),
                button: $(".tab-bar .search-form span")
            },
            joinNow = { //hero final comp join now button
                form: $(".hero-text .join-now-form"),
                input: $(".hero-text .join-now-form .join-text"),
                button: $(".hero-text .button.join-now")
            },
            headerjoin = {
                form: $('.formHeaderJoin'),
                input: $('.formHeaderJoin input[type="text"]'),
                button: $('.formHeaderJoin .button')
            },
            headervolunteer = {
                form: $('.formHeaderVolunteer'),
                input: $('.formHeaderVolunteer input[type="text"]'),
                button: $('.formHeaderVolunteer .button')
            };
        /*searchjoin = { //stand-alone volunteer and join now buttons.
            form: $('.formJoin'),
            input: $('.formJoin input[type="text"]'),
            button: $('.formJoin .button')
        },
        searchvolunteer = {
            form: $('.formVolunteer'),
            input: $('.formVolunteer input[type="text"]'),
            button: $('.formVolunteer .button')
        };*/
        //on ESC keypress close the input
        searchSlider.input.keyup(function (e) {
            if (e.which == 27) {
                searchSlider.button.click();
            }
        });
        //if the input is not shown display on clicking the looking glass.
        searchSlider.button.click(function (event) {
            event.stopPropagation();
            searchSlider.input.stop().animate({
                width: 'toggle'
            }, 500, function () {
                if (searchSlider.input.is(':visible')) {
                    searchSlider.input.focus();
                    searchSlider.button.addClass('hide');
                } else {
                    // searchSlider.button.focus();
                    searchSlider.button.removeClass('hide');
                }
            });
        });
        //Join button on the final comp.
        joinNow.button.click(function (event) {
            event.stopPropagation();
            event.preventDefault();
            joinNow.input.stop().animate({
                height: 'toggle'
            }, 500, function () {
                if (joinNow.input.is(':visible')) {
                    joinNow.input.focus();
                    joinNow.button.addClass('hide');
                    joinNow.form.submit(function () {
                        if (joinNow.input.val() !== "") {
                            //joinNow.form.submit();
                            joinNow.input.val('');
                        } else {
                            return false;
                        }
                    });
                } else {
                    // searchSlider.button.focus();
                    joinNow.button.removeClass('hide');
                }
            });
        });
        headerjoin.button.click(function (event) {
            headervolunteer.input.hide();
            headervolunteer.button.removeClass('on');
            headercomptrigger(headerjoin, event);
        });
        headerjoin.button.keyup(function (event) {
            if (event.keyCode == 13) {
                headercomptrigger(headerjoin, event);
            }
        });
        headervolunteer.button.click(function (event) {
            headerjoin.input.hide();
            headerjoin.button.removeClass('on');
            headercomptrigger(headervolunteer, event);
        });
        headervolunteer.button.keyup(function (event) {
            if (event.keyCode == 13) {
                headercomptrigger(headervolunteer, event);
            }
        });
        /*searchvolunteer.form.submit(function () {
            if (searchvolunteer.input.val() != "") {
                searchvolunteer.form.submit();
                searchvolunteer.input.val('');
            } else {
                return false;
            }
        });
        searchSlider.form.submit(function () {
            if (searchSlider.input.val() != "") {
                searchSlider.form.submit();
                searchSlider.input.val('');
            } else {
                return false;
            }
        });*/
    }

    (function getInternetExplorerVersion() {
        //Returns the version of Internet Explorer or a -1
        //(indicating the use of another browser).

        var rv = -1, // Return value assumes failure.
            ua,
            re;
        if (navigator.appName == 'Microsoft Internet Explorer') {
            ua = navigator.userAgent;
            re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
            if (re.exec(ua) != null) {
                rv = parseFloat(RegExp.$1);
            }
        }
        if (rv > -1 && rv <= 9.0) {
            console.log("No auto slide due to browser incompatibility");
            isNewerThanIE9 = false;
        }
        return rv;
    }());

    function getJSONFromAttr(el, attr) {
        var jsonData = el.attr(attr);
        if (jsonData) {
            return JSON.parse(jsonData);
        } else {
            return {};
        }
    }

    function getSlickOptions(el) {
        var options = getJSONFromAttr(el, "slick-options");
        if (!isNewerThanIE9) {
            options.autoplay = false;
        }
        return options;
    }

    $('.shop-slider').slick({
        dots: false,
        infinite: false,
        speed: 500,
        autoplay: false,
        arrows: false,
        slidesToShow: 3,
        slidesToScroll: 1,
        responsive: [{
            breakpoint: MEDIUM_ONLY,
            settings: {
                slidesToShow: 1
            }
        }]
    });
    $('.shop-slider').on('init', function () {
        $(this).find('.slick-active:eq(1)').addClass('shadow-box');
    });
    $('.shop-slider').on('afterChange', function () {
        $(this).removeClass('shadow-box');
        $(this).find('.slick-active').each(function (i) {
            $(this).removeClass('shadow-box');
            if (i % 3 == 1) {
                $(this).addClass('shadow-box');
            }
        });
    });

    function show_hide_features() {
        if ($(".featured-stories").length > 0) {
            if ($(".featured-stories li").length <= 3) {
                $(".featured-stories li .story").css({
                    "bottom": "-17.5rem",
                    "top": 'auto'
                });
            }
            $(".featured-stories li").each(function () {
                var elem = $(this),
                    target = elem.find('.story');
                //clicking on the LI will open the section with content.
                elem.on("click", function (e) {
                    e.stopPropagation();
                    // target.addClass("shown");
                    target.fadeIn('slow');
                    if (target[0] && target[0].children[0]) {
                        if (target[0].children[0].id == "tag_tile_social") {
                            $(window).trigger("fb-lazy-load");
                        }
                    }
                    $('.shop-slider').slick('setPosition');
                    try {
                        gsusa.functions.ToggleParsysAll.toggleAll(true);
                    } catch (ignore) {}
                    if (mobile) {
                        /*
                        $(".off-canvas-wrap").css({
                            'position': 'fixed'
                        });
                        */
                        homepageScrollTopPos = document.documentElement.scrollTop || document.body.scrollTop;
                        window.scrollTo(0, 0);
                        target.css({
                            "bottom": "auto",
                            "top": 0
                        });
                        $(".featured-stories").css('position', 'static');
                        /*
                        if ($(window).height() < target.find('.contents').height()) {
                            target.find('.contents').css({
                                'max-height': ($(window).height() - 100) + 'px',
                                'overflow-y': 'scroll'
                            });
                        }
                        */
                        fixColorlessWrapper();
                    }
                    fixSlickListTrack();
                    fixSlickSlideActive();
                    return true;
                });
                //closing the section by clicking on the cross
                target.find('.icon-cross').on("click", function (e) {
                    if (mobile) {
                        window.scrollTo(0, homepageScrollTopPos); // go back to previous window Y position
                    }
                    target.removeClass("shown");
                    target.fadeOut('slow');
                    $("body").css('overflow', '');
                    /*
                    $(".off-canvas-wrap").css({
                      'position': ''
                    });
                    */
                    $(".featured-stories").css('position', '');
                    try {
                        gsusa.functions.ToggleParsysAll.toggleAll(false);
                    } catch (ignore) {}
                    e.stopPropagation();
                    return false;
                });
            });
        }
    }
    //the "homeCarouselTimeDelay" parameter is defined in the jsp in carousel.jsp, which allows the user to set its value
    //inkoo added slide alternate view for carousel for ie9 and under because it breaks
    /*
    if ($.browser.msie && parseFloat($.browser.version) < 10) {
        $('.main-slider').slick({
            dots: false,
            infinite: true,
            speed: (typeof homeCarouselTimeDelay != 'undefined') ? homeCarouselTimeDelay : 1000,
            fade: false,
            autoplay: (typeof homeCarouselAutoScroll != 'undefined') ? homeCarouselAutoScroll : false,
            arrows: false,
            cssEase: 'linear',
        });
    }
    */

    $('.main-slider').each(function () {
        var slickOptions = getSlickOptions($(this));
        $(this).slick({
            dots: false,
            speed: slickOptions.speed || 1000,
            fade: false,
            autoplay: slickOptions.autoplay || false,
            arrows: true,
            autoplaySpeed: slickOptions.autoplaySpeed || 2000,
            cssEase: 'linear',
            slidesToShow: 1,
            infinite: true,
            responsive: [{
                breakpoint: 480,
                settings: {
                    arrows: true,
                    centerMode: true,
                    centerPadding: '30px'
                }
            }]
        });
    });

    $(".article-carousel .article-slider").slick({
        lazyLoad: 'ondemand',
        slidesToShow: 3,
        touchMove: true,
        slidesToScroll: 3,
        infinite: false,
        responsive: [{
            breakpoint: 480,
            settings: {
                arrows: false,
                centerMode: true,
                centerPadding: '60px',
                slidesToShow: 1
            }
        }]
    });

    function explore_button() {
        $(".hero-text .button.explore").on("click", function () {
            $('.inner-sliders .inner').slick({
                dots: false,
                infinite: false,
                speed: 500,
                fade: false,
                dotsClass: 'slick-dots',
                cssEase: 'linear',
                arrows: true
            });
            // mike's fix for ie11 Windows 8
            if (isIE11) {
                $('.inner-sliders .inner').on('beforeChange', function (event, slick, index) {
                    var slides = slick.$slides,
                        i;
                    for (i = 0; i < slides.length; i += 1) {
                        $(slides[i]).css('opacity', '1');
                    }
                });
                $('.inner-sliders .inner').on('afterChange', function (event, slick, index) {
                    var slides = slick.$slides,
                        i;
                    lastAfterSlick = slides;
                    for (i = 0; i < slides.length; i += 1) {
                        if (i != index) {
                            $(slides[i]).css('opacity', '0');
                        } else {
                            $(slides[i]).css('opacity', '1');
                        }
                    }
                });
            }
            $('.inner-sliders .slide-1, .inner-sliders .slide-2').slick({
                dots: true,
                fade: true,
                dotsClass: 'slick-dots',
                cssEase: 'linear',
                arrows: false,
                customPaging: function (slick, index) {
                    return slick.$slides.eq(index).find('.slide-thumb').prop('outerHTML');
                }
            });
            $('.inner-sliders .slide-4').slick({
                dots: true,
                fade: true,
                dotsClass: 'slick-dots',
                cssEase: 'linear',
                arrows: false,
                customPaging: function (slick, index) {
                    var thumbnailText = $("#hiddenThumbnail" + index).text();
                    if (thumbnailText.trim() !== "") {
                        thumbnailText = "<p>" + thumbnailText + "</p>";
                    }
                    return slick.$slides.eq(index).find('.slide-thumb').prop('outerHTML') + thumbnailText;
                }
            });
            $('.inner-sliders .slide-3').slick({
                dots: false,
                fade: true,
                cssEase: 'linear',
                arrows: false
            });
            $('.main-slider').slick('slickPause');
            if ($(window).width() > 640) {
                $('.overlay').fadeIn();
                if ($(".position").css("opacity") == '0') {
                    $(".position").animate({
                        'opacity': 1
                    }, 1000);
                    $(".position").css('z-index', '1000');
                    $('.zip-council').addClass('change');
                }
            }
        });
    } //END OF EXPLORER CLICK FUNCTION

    $('.inner-sliders .inner').on('init reInit afterChange', function (slick, currentSlide, index) {
        var item_length = $('.inner-sliders .inner > .slick-list > .slick-track > li').length - 1;
        if (item_length == index) {
            carouselSliderPropogate = false;
            $('.slick-disabled').on('click', function (event) {
                if (!carouselSliderPropogate) {
                    event.stopPropagation();
                    $('.position').animate({
                        'opacity': 0
                    }, 100, function () {
                        $('.hero-feature .overlay').fadeOut();
                        $('.position').css('z-index', '-1');
                        $('.zip-council').removeClass('change').hide();
                        $('.main-slider').css('opacity', '-1');
                        $('.hero-text.first').hide();
                    });
                    $('.final-comp').show();
                    $('.inner-sliders .inner').slick('unslick');
                }
            });
        } else {
            carouselSliderPropogate = true;
        }
    });
    $('.feature-video-slider .slide-5').slick({
        dots: true,
        fade: true,
        dotsClass: 'slick-dots',
        cssEase: 'linear',
        arrows: false,
        customPaging: function (slick, index) {
            return slick.$slides.eq(index).find('.slide-thumb').prop('outerHTML');
        }
    });

    function equilize_our_stories() {
        var blocks = $('.our-stories-block li div'),
            maxHeight = Math.max.apply(Math, blocks.map(function () {
                //console.log($(this).height());
                return $(this).height();
            }).get());
        blocks.height(maxHeight);
    }

    function small_screens() {
        if (mobile) {
            $('.overlay').hide();
            $(".hero-text .button").hide();
        } else {
            $(".hero-text .button").show();
        }
    }

    ImageMap = function (map, img) {
        var n, areas = map.getElementsByTagName('area'),
            len = areas.length,
            coords = [],
            currentWidth = img.clientWidth,
            previousWidth = parseInt(img.style.width, 10);
        for (n = 0; n < len; n += 1) {
            coords[n] = areas[n].coords.split(',');
        }
        this.resize = function () {
            currentWidth = img.clientWidth;
            var n, m, clen, x = currentWidth / previousWidth;
            for (n = 0; n < len; n += 1) {
                clen = coords[n].length;
                for (m = 0; m < clen; m += 1) {
                    coords[n][m] *= x;
                }
                areas[n].coords = coords[n].join(',');
            }
            previousWidth = currentWidth;
            return true;
        };
        window.onresize = this.resize;
    };
    if (document.getElementById('council-map') && document.getElementById('council-map-img')) {
        imageMap = new ImageMap(document.getElementById('council-map'), document.getElementById('council-map-img'));
        imageMap.resize();
    }

    $('.video-slider-wrapper').each(function () {
        var slickOptions = getSlickOptions($(this));
        $(this).slick({
            dots: false,
            speed: 500,
            fade: false,
            autoplay: slickOptions.autoplay || false,
            autoplaySpeed: slickOptions.autoplaySpeed || 2000,
            cssEase: 'linear',
            centerMode: true,
            slidesToShow: 1,
            centerPadding: '100px',
            touchMove: true,
            responsive: [{
                breakpoint: 480,
                settings: {
                    arrows: true,
                    centerMode: true,
                    centerPadding: '30px'
                }
            }]
        });
    });

    $('.join-redirect-slider').slick({
        dots: false,
        autoplay: true,
        autoplayspeed: (typeof joinRedirectAutoplaySpeed != 'undefined') ? joinRedirectAutoplaySpeed : 2000,
        speed: (typeof joinRedirectSpeed != 'undefined') ? joinRedirectSpeed : 500,
        arrows: false,
        slidesToShow: 1,
        slidesToScroll: 1,
        fade: true
    });

    function shop_rotator() {
        /*
        $('.rotator .button.arrow').on("click", function (event) {
            this.delegateEvents();
        });
        */
        $('.shop-carousel').slick({
            dots: false,
            speed: 500,
            autoplay: (typeof shopautoscroll != 'undefined') ? shopautoscroll : false,
            arrows: true,
            slidesToShow: 1,
            autoplaySpeed: (typeof shoptimedelay != 'undefined') ? shoptimedelay : 2000,
            slidesToScroll: 1
                /*responsive: [{
                    breakpoint: MEDIUM_ONLY,
                    settings: {
                        slidesToShow: 1
                    }
                }]*/
        });
    }

    function welcome_cookie_slider() {
        /*
        $('.rotator .button.arrow').on("click", function (event) {
            this.delegateEvents();
        });
        */
        $('.welcome-video-slider').slick({
            dots: false,
            speed: 500,
            autoplay: true,
            arrows: false,
            slidesToShow: 1,
            slidesToScroll: 1,
            fade: true
                /*responsive: [{
                    breakpoint: MEDIUM_ONLY,
                    settings: {
                        slidesToShow: 1
                    }
                }]*/
        });
    }

    $.fn.lazyLoad = function () {
        var self = this,
            attr = {
                "src": "data-src",
                "href": "data-href"
            },
            src;

        for (src in attr) {
            if (attr.hasOwnProperty(src) && (!self.attr(src) || self.attr(src) === "") && self.attr(attr[src])) {
                // If the key is undefined or empty and the value exists, set the key to the value
                self.attr(src, self.attr(attr[src]));
            }
        }

        return self;
    };


	var vimeoPlayerLoadStarted = false;
	function loadVimeoPlayer(){
		if(!vimeoPlayerLoadStarted){
			$.getScript('https://player.vimeo.com/api/player.js', {cache: true});
			vimeoPlayerLoadStarted = true;
		}
	}
	
	var youtubePlayerLoadStarted = false;
	function loadYoutubePlayer(){
		if(!youtubePlayerLoadStarted){
			$.getScript('https://www.youtube.com/iframe_api', {cache: true});
			youtubePlayerLoadStarted = true;
		}
	}
    //
    //
    // SLICK CAROUSEL VIDEO PLAYER
    //
    //
    SlickPlayer = {
        init: function (params) {
            var self = this; // Lexical closure

            self.iframe = params.iframe;
            self.slick = params.slick;
            self.autoplay = params.autoplay;
            self.type = self.iframe.attr('id').toLowerCase();
            self.underbar = params.underbar;
            self.placeholder = self.iframe.siblings(".vid-placeholder");
            self.thumbnail = self.placeholder.find("img");

            // Set config from component
            self.config = { // Determine how player behaves
                thumbnail: { // If there is a thumbnail, do not interact with the player until the user requests it
                    desktop: false,
                    mobile: false,
                    isActive: self.isActive
                },
                link: { // If the thumbnail opens the video in a new tab, there is no need to interact with the player
                    desktop: false,
                    mobile: false,
                    isActive: self.isActive
                }
            };
            switch (params.config.desktop) {
            case "thumbnail":
                self.config.thumbnail.desktop = true;
                self.config.link.desktop = false;
                break;
            case "link":
                self.config.thumbnail.desktop = true;
                self.config.link.desktop = true;
                break;
            }
            switch (params.config.mobile) {
            case "thumbnail":
                self.config.thumbnail.mobile = true;
                self.config.link.mobile = false;
                break;
            case "link":
                self.config.thumbnail.mobile = true;
                self.config.link.mobile = true;
                break;
            }

            // Lazy load thumbnail and link if used
            if (self.config.thumbnail.desktop || self.config.thumbnail.mobile) {
                self.thumbnail.lazyLoad();
                self.toggleThumbnail();
                $(window).on("breakpoint", function () {
                    self.toggleThumbnail();
                });
            } else {
                self.createPlayer(); // Load video right away if no thumbnail
            }
            if (self.config.link.desktop || self.config.link.mobile) {
                self.placeholder.lazyLoad();
            }

            // Underbar events
            self.underbar.input.on("focus", function () {
                self.stopSlider();
            }).on("focusout", function () {
                self.startSlider();
            });

            // Placeholder events
            self.placeholder.on("click", function (event) {
                if (!self.config.link.isActive()) { // If using the thumbnail as a lazyload placeholder
                    event.preventDefault(); // Prevent anchor tag from opening link
                    event.stopPropagation();
                    self.play();
                }
            });
        },
        playing: false,
        playVideo: function () {}, // Wrapper for API call
        unloadVideo: function () {}, // Wrapper for API call
        isActive: function () {
            return (this.desktop && !mobile) || (this.mobile && mobile);
        },
        toggleThumbnail: function () {
            if (this.config.thumbnail.isActive()) {
                this.slick.addClass("thumbnail");
            } else {
                this.slick.removeClass("thumbnail");
                this.createPlayer(); // Load video right away if no thumbnail
            }
        },
        stopSlider: function () {
            if (this.autoplay) {
                this.slick.slick('slickPause');
                this.slick.slick('slickSetOption', 'autoplay', false, false);
                this.slick.slick('autoPlay', $.noop);
            }
            if (!this.underbar.isFocused() && !this.config.link.isActive()) {
                this.underbar.hide();
            }
        },
        startSlider: function () {
            if (this.autoplay) {
                this.slick.slick('slickPlay');
                this.slick.slick('slickSetOption', 'autoplay', true, false);
                this.slick.slick('autoPlay', $.noop);
            }
            if (!this.underbar.isFocused() && !this.config.link.isActive()) {
                this.underbar.show();
            }
        },
        createPlayer: function () {
            var self = this;

            if (self.iframe.attr("src")) { // Prevent player reinstantiation 
                return;
            }

            // Assign embed url
            self.iframe.lazyLoad();

            // Load event
            self.iframe.on("playerLoad", function () {
                if (self.config.thumbnail.isActive()) { // If using thumbnail for lazy load, call play functions when thumbnail is clicked
                    self.play();
                }
            });

            // Play event
            self.iframe.on("play", function () {
                if (!self.config.thumbnail.isActive()) { // If not using a thumbnail for lazy load, call play functions when video is played
                    self.play();
                }
            });

            // Unload event
            self.slick.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
                if (self.playing) {
                    self.unload();
                }
            });

            // Instantiate player
            if (self.type.indexOf('vimeo') > -1) { // Check for a Vimeo player
            		loadVimeoPlayer();
	        		if(vimeoLoaded()){
	    	            self.createVimeoPlayer();
	        		}else {
	        			$(window).on('vimeoLoaded', function(){
	        	            self.createVimeoPlayer();
	        			});
	        		}
            } else if (self.type.indexOf('youtube') > -1) { // Check for a YouTube player
            		loadYoutubePlayer();
                if (ytLoaded()) {
                    self.createYTPlayer();
                } else {
                    $(window).on("ytLoaded", function () { // Wait until API script loads if it has not already
                        self.createYTPlayer();
                    });
                }
            }
        },
        play: function () {
            var self = this;

            self.createPlayer(); // Load player if not already loaded
            self.stopSlider();
            if (!mobile) { // Browsers will block programmatic playback on mobile anyway, prevent Vimeo bug by manually prohibiting
                self.playVideo();
            }
            self.slick.addClass("playing");
            self.playing = true;
        },
        unload: function () {
            var self = this;

            self.startSlider();
            self.unloadVideo();
            self.slick.removeClass("playing");
            self.playing = false;
        },
        createVimeoPlayer: function () {
            var self = this;
            self.player = new Vimeo.Player(self.iframe.attr('id'));

            self.player.on("loaded", function () {
                // State functions
                self.playVideo = function () {
                    self.player.play();
                };
                self.unloadVideo = function () {
                    self.player.unload();
                };

                // Listener events
                self.player.on('play', function () {
                    self.iframe.trigger("play");
                });

                // Load
                self.iframe.trigger("playerLoad");
            });
        },
        createYTPlayer: function () {
            var self = this;
            self.player = new YT.Player(self.iframe.attr('id'));

            self.player.addEventListener("onReady", function () {
                // State functions
                self.playVideo = function () {
                    self.player.playVideo();
                };
                self.unloadVideo = function () {
                    self.player.stopVideo();
                };

                // Listener events
                self.player.addEventListener("onStateChange", function (event) {
                    if (event.data == YT.PlayerState.BUFFERING) { // Bind to buffering event to prevent delay before triggering play state
                        self.iframe.trigger("play");
                    }
                });

                // Load
                self.iframe.trigger("playerLoad");
            });
        }
    };

    Underbar = {
        init: function (el) {
            this.el = el;
            this.input = el.find('input');
        },
        show: function () {
            if (this.el.length && !mobile) { // Desktop only
                this.el.slideDown(1000);
            }
        },
        hide: function () {
            if (this.el.length && !mobile) {
                this.el.slideUp(0);
            }
        },
        isFocused: function () {
            if (this.input.length) {
                return this.input.is(":focus");
            }
        }

    };

    // Instantiate SlickPlayers and Underbars
    $('.slick-slider').each(function () {
        // For each embed, create player events (Make sure player.js is loaded first)
        var slick = $(this),
            autoplay = slick.slick('slickGetOption', 'autoplay'),
            underbar = Object.create(Underbar),
            config = getJSONFromAttr(slick, "player-config");
        underbar.init(slick.parent().find('.zip-council'));

        slick.find("iframe").each(function () {
            Object.create(SlickPlayer).init({
                iframe: $(this),
                slick: slick,
                autoplay: autoplay,
                underbar: underbar,
                config: config
            });
        });
    }).on('swipeMove', function (event) {
        // Disable mouse/click events for slides when swiping (prevent videos from playing after swipe)
        $(this).addClass("dragging");
    }).on('swipeEnd', function (event) {
        $(this).removeClass("dragging");
    });

    function hide_show_cookie() {
        $('#meet-cookie-layout section').hide();
        $('#meet-cookie-layout .wrapper h4').on('click', function (e) {
            $(this).siblings('section').slideToggle();
            $(this).toggleClass('on');
        });
    }
    fixBottomFooter();
    slide_search_bar();
    small_screens();
    show_hide_features();
    document_close_all();
    explore_button();
    //join_now();
    shop_rotator();
    welcome_cookie_slider();
    //camp_finder();
    $(window).resize(function () {
        small_screens();
    });
    $(window).resize(function (event) {
        //if(!mobile) {
        //  iframeClick();
        //} else {
        $("iframe").off("mouseenter mouseleave");
        //$("iframe").unbind("mouseenter,mouseleave");
        //}
    });
    $(window).load(function () {
        equilize_our_stories();
        hide_show_cookie();
    });
    // form on the Donate Tile.
    $("#tag_tile_button_local").on('click', function (e) {
        e.preventDefault();
        $('#tag_tile_button_donate').toggle();
        $('.formDonate').toggleClass('hide');
        //$(this).toggleClass('hide');
        $('.formDonate input[type="text"]').focus();
    });
    $(document).on('closed.fndtn.reveal', '[data-reveal]', function () {
        $(".off-canvas-wrap").removeClass('noprint');
    });
    // Setup "contact local council" form
    $('.booth-finder form.contactlocalcouncil').submit(function () {
        $.post($(this).attr('action'), $(this).serialize(), function (response) {
            // Remove blank lines
            response = response.replace(/^\s*\n/gm, '').trim();
            if (response.toUpperCase() == 'OK') {
                $('.contactlocalcouncil').html('Thank you. A representative will contact you shortly.');
            } else {
                $('.contactlocalcouncil div.error').html(response);
            }
        });
        // Prevent default
        return false;
    });

    //
    //
    // Sticky Nav
    //
    //
    (function () {
        var desktopHeader = $(".header"),
            mobileHeader = $(".tab-bar"),
            header,
            headerHeight,
            desktopPlaceholder = $(".header-placeholder"),
            mobilePlaceholder = $(".tab-bar-placeholder"),
            placeholder,
            trigger = false,
            fixed = false,
            fixedClass = "sticky-nav-fixed",
            offset = 0,
            desktopStickyOffset = 0;

        function setOffset() {
            // Set placeholders
            headerHeight = header.height();
            placeholder.height(headerHeight);

            // Set offset
            if (!mobile) { // Desktop header
                header.addClass(fixedClass);
                desktopStickyOffset = headerHeight - header.height(); // Change header to sticky and change back to get height difference
                header.removeClass(fixedClass);

                offset = header.offset().top + desktopStickyOffset;
            } else { // Mobile header
                offset = header.offset().top;
            }

            // Reset fix
            $(window).trigger("scroll");
        }

        function switchHeader() {
            // Unfix previous header
            if (header && placeholder) {
                header.removeClass(fixedClass);
                placeholder.hide();
                fixed = false;
            }

            // Set new header
            header = mobile ? mobileHeader : desktopHeader;
            placeholder = mobile ? mobilePlaceholder : desktopPlaceholder;

            // Set offset
            setOffset();
        }

        if ($(".header.sticky-nav").length) {
            // On load
            switchHeader();

            // Change header for desktop vs. mobile
            $(window).on("breakpoint", function () {
                switchHeader();
            });

            // Reset offset on resize
            $(window).on("resize", function () {
                if (Math.abs(header.height() - headerHeight) > 1) { // Trigger once when the height changes
                    //console.log("Old height: " + headerHeight);
                    //console.log("New height: " + header.height());
                    setOffset();
                }
            });

            var addFixedHeader = function(topLocation){
				trigger = topLocation > offset;
				if (trigger && !fixed) { // Trigger once to fix header
					fixed = true;
					header.addClass(fixedClass);
					placeholder.show();
				} else if (!trigger && fixed) { // Trigger once to unfix header
					fixed = false;
					header.removeClass(fixedClass);
					placeholder.hide();
				}
            }

            // Sticky header
            if(window !== window.top){
                try {
                    var contentScrollWrapper = window.top.$('#ContentScrollView');
					contentScrollWrapper.off('scroll.editorFixedHeader').on('scroll.editorFixedHeader', function(){
					    try{
					        var scrollLocation = $(this).scrollTop();
							addFixedHeader(scrollLocation);
							header.css({top: scrollLocation + 'px'});
                        }catch(er1){}
                    });
				}catch(er){}
            }

            $(window).off("scroll.fixedHeader").on("scroll.fixedHeader", function(){
				addFixedHeader($(window).scrollTop());
			});
        }
    }());
}(jQuery));

//
//
// Helper Functions
//
//

// useful utility printer of object properties
function printObjectProperties(objectToInspect) {
    'use strict';
    var key,
        obj,
        prop;
    for (key in objectToInspect) {
        if (objectToInspect.hasOwnProperty(key)) {
            obj = objectToInspect[key];
            for (prop in obj) {
                if (obj.hasOwnProperty(prop)) {
                    console.log(prop + " = " + obj[prop]);
                }
            }
        }
    }
}

function populateVideoIntoModal(divId, videoLink, color, e) {
    'use strict';
    var parent = $("#" + divId + " " + ".video-popup");
    $("#" + divId).css("background-color", "#" + color);
    parent.html(videoLink);
    if (e) {
        e.preventDefault();
    }
    return false;
}

function setupContactLocalCouncilForm() {
    'use strict';
    // Setup "contact local council" form
    $('.booth-finder form.contactlocalcouncil').submit(function () {
        $.post($(this).attr('action'), $(this).serialize(), function (response) {
            // Remove blank lines
            response = response.replace(/^\s*\n/gm, '').trim();
            if (response.toUpperCase() == 'OK') {
                $('.contactlocalcouncil').html('Thank you. A representative will contact you shortly.');
            } else {
                $('.contactlocalcouncil div.error').html(response);
            }
        });
        // Prevent default
        return false;
    });
}

$(document).ready(setupContactLocalCouncilForm);
// Needed for "View Detail" data
Handlebars.registerHelper('json', function (context) {
    'use strict';
    return JSON.stringify(context).replace(/'/g, "&#39;");
});
Handlebars.registerHelper('escapeDoubleQuotes', function (context) {
    'use strict';
    if (typeof context == 'string') {
        return context.replace(/"/g, '\\\"');
    }
    return '';
});
Handlebars.registerHelper({
    eq: function (v1, v2) {
        'use strict';
        return v1 === v2;
    },
    ne: function (v1, v2) {
        'use strict';
        return v1 !== v2;
    },
    lt: function (v1, v2) {
        'use strict';
        return v1 < v2;
    },
    gt: function (v1, v2) {
        'use strict';
        return v1 > v2;
    },
    lte: function (v1, v2) {
        'use strict';
        return v1 <= v2;
    },
    gte: function (v1, v2) {
        'use strict';
        return v1 >= v2;
    },
    and: function (v1, v2) {
        'use strict';
        return v1 && v2;
    },
    or: function (v1, v2) {
        'use strict';
        return v1 || v2;
    }
});

function seeMoreScale() {
    'use strict';
    $.each($('.article-slider .article-tile.last section'), function (index, value) {
        var thisDiv = $(value),
            parentDiv = thisDiv.parents('.slick-slide')[0],
            siblingDiv;
        if ($(parentDiv).siblings()) {
            siblingDiv = $(parentDiv).siblings()[0];
            thisDiv.css('minHeight', $(siblingDiv).innerHeight());
            thisDiv.css('height', $(siblingDiv).innerHeight());
            thisDiv.css('width', $(siblingDiv).innerWidth());
        }
    });
}


$(document).ready(function() {
	$("input:hidden[name='file-upload-max-size']").each(function(index) {
		var maxSize = parseInt($(this).val(), 10);
		if(maxSize > -1){
			$(this).closest("form").find("input:file").change(function() {
				if(typeof this.files[0] !== 'undefined'){
		            if(maxSize > -1){
		                size = this.files[0].size;
		                if(maxSize * 1000000 < size){
		                	alert("File size cannot be larger than "+maxSize+" MB.");
		                	$(this).val('');
		                }
					}
				}
			});
		}
	});
});