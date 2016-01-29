/*jslint browser: true*/
/*global $, jQuery*/
/*global alert */
(function ($) {
  'use strict';
// Foundation JavaScript
// Documentation can be found at: http://foundation.zurb.com/docs
  $(document).foundation();
  //add height to the content for the footer to be always at the bottom.
  function fix_bottom_footer() {
    var footer_height = $("footer").outerHeight();
    var header_height = $(".header").outerHeight();
    var total_height = "calc((100vh - " + (footer_height + header_height) + "px))";
    $(".main-content, #main .vtk").css({
      "min-height" : total_height
    });
    $(".join-volunteer").css({
      "min-height" : "initial"
    });
  }

  var isIE11 = false;
  if (navigator.userAgent.indexOf("Trident\/7") !== -1 && parseFloat($.browser.version) >= 11) {
    isIE11 = true;
  }

  function pauseAllCarouselVideos() {
    var i;
    for (i = 0; i < 4; i++) {
      if (document.getElementById("vimeoPlayer" + i)) {
        $(document.getElementById("vimeoPlayer" + i)).api('pause');
      }
      if (document.getElementById("youtubePlayer" + i)) {
        document.getElementById("youtubePlayer" + i).contentWindow.postMessage('{"event":"command","func":"pauseVideo","args":""}', '*');
      }
      if ($(document.getElementsByClassName("vid" + i)).find("video").length > 0) {
        $(document.getElementsByClassName("vid" + i)).find("video")[0].pause();
      }
    }
  }

  function document_close_all() {
    //Detect ipad
    var touchOrClick = (navigator.userAgent.match(/iPad/i)) ? "touchstart" : "click";
    //when clicking outside of the form it will close the input.
    $(document).on(touchOrClick, function (event) {
      event.stopPropagation();
      var target = $(event.target);

      if (target.closest('.tab-bar .search-form').length === 0
          && $(".tab-bar .search-form input").css('display') !== 'none') {
        $(".tab-bar .search-form span").removeClass('hide');
        $(".tab-bar .search-form input").hide('slow');
      }
      if (target.closest('.featured-stories li').length === 0
          && target.closest(".story").css('display') !== 'none') {
        $(".story").fadeOut('slow');
        $("body").css('overflow', '');
        $(".featured-stories").css('position', '');
      }
      if (target.closest('.join .wrapper').length === 0
          && target.closest(".join section").css('display') !== 'none') {
        $('.join section').fadeOut('500', function () {
          $('.join a').fadeIn('slow');
        });
      }
      // if (target.closest('.hero-feature').length === 0
      //     && target.closest(".hero-feature").css('display') !== 'none') {
      //   $('.position').animate({
      //     'opacity': 0
      //   }, 100, function () {
      //     $('.hero-feature .overlay').fadeOut();
      //     $('.position').css('z-index', '-1');
      //     $('.zip-council').removeClass('change');
      //     $('.main-slider').slick('slickPlay');
      //     pauseAllCarouselVideos();
      //     // release opacity for mike's fix
      //     if (isIE11 && lastAfterSlick) {
      //       for (var x in lastAfterSlick) {
      //         for (var i = 0; i < lastAfterSlick.length; i++) {
      //           $(lastAfterSlick[i]).css('opacity', '1');
      //         }
      //       }
      //     }
      //   });
      // // }
      // if (target.closest('.final-comp').length === 0
      //     && target.closest(".final-comp").css('display') !== 'none') {
      //   $(".final-comp").fadeOut('slow');
      //   $('.hero-text.first').show();
      //   $('.zip-council').fadeIn('slow');
      //   $('.main-slider').css('opacity', '');
      //   $("#tag_explore_final input[type=\"text\"]").hide();
      // }
      if ((target.closest('.standalone-volunteer').length === 0 && target.closest('.footer-volunteer').length ===0)
          && target.closest('.vol.button.arrow').siblings('form').css('display') !== 'none') {
        $('.vol.button.arrow').siblings('form').addClass('hide');
      }
      if ((target.closest('.standalone-join').length === 0 && target.closest('.footer-join').length ===0)
              && target.closest('.join.button.arrow').siblings('form').css('display') !== 'none') {
            $('.join.button.arrow').siblings('form').addClass('hide');
          }
      if (target.closest('.standalone-donate').length === 0 && target.closest('a.button.form').siblings('form').css('display') !== 'none') {
            $('a.button.form').siblings('form').addClass('hide');
            $('a.button.form').removeClass('hide');
          }
    });
  }

 //header join now volunteer forms.
  function headercomptrigger(item, event) {
    event.stopPropagation();
    item.input.stop().animate({
      width : 'toggle',
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
    //search slider for the mobile.
    var searchSlider = {
      form: $(".tab-bar .search-form"),
      input: $(".tab-bar .search-form input"),
      button: $(".tab-bar .search-form span")
    };
    //hero final comp join now button
    var joinNow = {
      form: $(".hero-text .join-now-form"),
      input: $(".hero-text .join-now-form .join-text"),
      button: $(".hero-text .button.join-now")
    };
    var headerjoin = {
      form : $('.formHeaderJoin'),
      input: $('.formHeaderJoin input[type="text"]'),
      button: $('.formHeaderJoin .button'),
    };
    var headervolunteer = {
      form : $('.formHeaderVolunteer'),
      input: $('.formHeaderVolunteer input[type="text"]'),
      button: $('.formHeaderVolunteer .button'),
    };
    //stand-alone volunteer and join now buttons.
    var searchjoin = {
      form : $('.formJoin'),
      input: $('.formJoin input[type="text"]'),
      button: $('.formJoin .button'),
    };
    var searchvolunteer = {
      form : $('.formVolunteer'),
      input: $('.formVolunteer input[type="text"]'),
      button: $('.formVolunteer .button'),
    };

    //on ESC keypress close the input
    searchSlider.input.keyup(function (e) {
      if (e.which === 27) {
        searchSlider.button.click();
      }
    });

    //if the input is not shown display on clicking the looking glass.
    searchSlider.button.click(function (event) {
      event.stopPropagation();
      searchSlider.input.stop().animate({
        width: 'toggle',
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
        height: 'toggle',
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
      if (event.keyCode === 13) {
        headercomptrigger(headerjoin, event);
      }
    });
    headervolunteer.button.click(function (event) {
      headerjoin.input.hide();
      headerjoin.button.removeClass('on');
      headercomptrigger(headervolunteer, event);
    });
    headervolunteer.button.keyup(function (event) {
      if (event.keyCode === 13) {
        headercomptrigger(headervolunteer, event);
      }
    });
    searchvolunteer.form.submit(function () {
      if (searchvolunteer.input.val() !== "") {
        searchvolunteer.form.submit();
        searchvolunteer.input.val('');
      } else {
        return false;
      }
    });
    searchSlider.form.submit(function () {
      if (searchSlider.input.val() !== "") {
        searchSlider.form.submit();
        searchSlider.input.val('');
      } else {
        return false;
      }
    });
  }
  //join now and volunteer form for standalone
  $('.vol.button.arrow, .join.button.arrow').on("click", function (event) {
    event.preventDefault();
    var this_form = $(this).siblings("form");
    this_form.removeClass('hide');
    if (this_form.find('input[name="ZipJoin"]').length > 0) {
      this_form.find('input[name="ZipJoin"]').focus();
    }
    if (this_form.find('input[name="ZipVolunteer"]').length > 0) {
      this_form.find('input[name="ZipVolunteer"]').focus();
    }
  });
  //home page join now link will open the email form.
  function join_now() {
    $('.zip-council > .join a').on('click', function (e) {
      e.preventDefault();
      $(this).fadeOut(500, function () {
        $(this).siblings('section').fadeIn('slow');
        $(this).siblings('section').find("input[name='zipcode']").focus();
      });
    });
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
      breakpoint: 768,
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
      if (i % 3 === 1) {
        $(this).addClass('shadow-box');
      }
    });
  });

  function show_hide_features() {
    if ($(".featured-stories").length > 0) {
      if ($(".featured-stories li").length <= 3) {
        $(".featured-stories li .story").css({
          "bottom" : "-17.5rem",
          "top" : 'auto'
        });
      }
      $(".featured-stories li").each(function () {
        var elem = $(this);
        var target = elem.find('.story');
        //clicking on the LI will open the section with content.
        elem.on("click", function (e) {
          e.stopPropagation();
          // target.addClass("shown");
          target.fadeIn('slow');
          $('.shop-slider').slick('setPosition');
          try {
            gsusa.functions.ToggleParsysAll.toggleAll(true);
          } catch (ignore) {}
          if ($(window).width() <= 768) {
            // $(".off-canvas-wrap").css({
            //   'position': 'fixed'
            // });
            window.scrollTo(0,0);
            target.css({
              "bottom" : "auto",
              "top" : 0
            });
            $(".featured-stories").css('position', 'static');
            // if ($(window).height() < target.find('.contents').height()) {
            //   target.find('.contents').css({
            //     'max-height': ($(window).height() - 100) + 'px',
            //     'overflow-y' : 'scroll'
            //   });
            // }
            fixColorlessWrapper();
          }
          fixSlickListTrack();
          fixSlickSlideActive();
          return true;
        });
        //closing the section by clicking on the cross
        target.find('.icon-cross').on("click", function (e) {
          target.removeClass("shown");
          target.fadeOut('slow');
          $("body").css('overflow', '');
          // $(".off-canvas-wrap").css({
          //   'position': ''
          // });
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
  // inkoo added slide alternate view for carousel for ie9 and under because it breaks
  // if($.browser.msie && parseFloat($.browser.version)<10) {
  //   $('.main-slider').slick({
  //     dots: false,
  //     infinite: true,
  //     speed: (typeof homeCarouselTimeDelay !== 'undefined') ? homeCarouselTimeDelay : 1000,
  //     fade: false,
  //     autoplay: (typeof homeCarouselAutoScroll !== 'undefined') ? homeCarouselAutoScroll : false,
  //     arrows: false,
  //     cssEase: 'linear',
  //   });
  // } else {
    $('.main-slider').slick({
      dots: false,
      speed: (typeof homeCarouselTimeDelay !== 'undefined') ? homeCarouselTimeDelay : 1000,
      fade: false,
      autoplay: (typeof homeCarouselAutoScroll !== 'undefined') ? homeCarouselAutoScroll : false,
      arrows: true,
      autoplaySpeed: (typeof homeCarouselAutoPlaySpeed !== 'undefined') ? homeCarouselAutoPlaySpeed : 2000,
      cssEase: 'linear',
      slidesToShow: 1,
      infinite: true,
      responsive: [
       {
         breakpoint: 480,
         settings: {
          arrows: true,
          centerMode: true,
          centerPadding: '30px',
         }
       }
      ]
    });
    $(".article-carousel .article-slider").slick({
      lazyLoad: 'ondemand',
      slidesToShow: 3,
      touchMove: true,
      slidesToScroll: 3,
      responsive: [
       {
         breakpoint: 480,
         settings: {
          arrows: false,
          centerMode: true,
          centerPadding: '60px',
          slidesToShow: 1,
         }
       }
      ]
    });

  var lastAfterSlick = null;

  function explore_button() {
    $(".hero-text .button.explore").on("click", function () {
      $('.inner-sliders .inner').slick({
        dots: false,
        infinite: false,
        speed: 500,
        fade: false,
        dotsClass: 'slick-dots',
        cssEase: 'linear',
        arrows: true,
      });

      // mike's fix for ie11 Windows 8
      if (isIE11) {
        $('.inner-sliders .inner').on('beforeChange', function(event, slick, index) {
          var slides = slick.$slides;
          for (var i = 0; i < slides.length; i++) {
              $(slides[i]).css('opacity', '1');
          }
            });

            $('.inner-sliders .inner').on('afterChange', function(event, slick, index){
          var slides = slick.$slides;
          lastAfterSlick = slides;
          for (var i = 0; i < slides.length; i++) {
            if (i !== index) {
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
        },
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
        },
      });
      $('.inner-sliders .slide-3').slick({
        dots: false,
        fade: true,
        cssEase: 'linear',
        arrows: false,
      });
      $('.main-slider').slick('slickPause');

      if ($(window).width() > 640) {
        $('.overlay').fadeIn();
        if ($(".position").css("opacity") === '0') {
          $(".position").animate({
            'opacity': 1
          }, 1000);
          $(".position").css('z-index', '1000');
          $('.zip-council').addClass('change');
        }
      }
    });
  }//END OF EXPLORER CLICK FUNCTION

  // $('.inner-sliders .slide-4').on('afterChange', function (event, slick, currentSlide) {
  //   pauseAllCarouselVideos();
  // });

  var carouselSliderPropogate = true;
  $('.inner-sliders .inner').on('init reInit afterChange', function (slick, currentSlide, index) {
    var item_length =  $('.inner-sliders .inner > .slick-list > .slick-track > li').length - 1;
    if (item_length === index) {
      carouselSliderPropogate = false;
      $('.slick-disabled').on('click', function (event) {
        if (!carouselSliderPropogate) {
          event.stopPropagation();
          $('.position').animate({
            'opacity': 0,
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
    },
  });

  function scroll_feeds() {
    $('.scroll-more').bind("click", function () {
      // var target = $(e.target);
      var scroll_dir;
      var scroll_area = $(this).siblings('.social-block');
      if ($(this).hasClass('down')) {
        scroll_dir = "+=";
      } else {
        scroll_dir = "-=";
      }
      scroll_area.animate({
        scrollTop: scroll_dir + scroll_area.height() + "px"
      }, 500);
    });
  }

  function equilize_our_stories() {
    var blocks = $('.our-stories-block li div');
    var maxHeight = Math.max.apply(Math, blocks.map(function () {
      console.log($(this).height());
      return $(this).height();
    }).get());
    blocks.height(maxHeight);
  }

  function small_screens() {
    if ($(window).width() < 769) {
      $('.overlay').hide();
      $(".hero-text .button").hide();
    } else {
      $(".hero-text .button").show();
    }
  }

  var ImageMap = function (map, img) {
    var n,
      areas = map.getElementsByTagName('area'),
      len = areas.length,
      coords = [],
      currentWidth = img.clientWidth,
      previousWidth = parseInt(img.style.width, 10);
    for (n = 0; n < len; n++) {
      coords[n] = areas[n].coords.split(',');
    }
    this.resize = function () {
      currentWidth = img.clientWidth;
      var n, m, clen,
        x = currentWidth / previousWidth;
      for (n = 0; n < len; n++) {
        clen = coords[n].length;
        for (m = 0; m < clen; m++) {
          coords[n][m] *= x;
        }
        areas[n].coords = coords[n].join(',');
      }
      previousWidth = currentWidth;
      return true;
    };
    window.onresize = this.resize;
  }

  if (document.getElementById('council-map') && document.getElementById('council-map-img')){
    var imageMap = new ImageMap(document.getElementById('council-map'), document.getElementById('council-map-img'));
    imageMap.resize();
  }

  $('.video-slider-wrapper').slick({
    dots: false,
    speed: 500,
    fade: false,
    autoplay: (typeof videoSliderAuto !== 'undefined') ? videoSliderAuto : false,
    autoplaySpeed: (typeof videoSliderDelay !== 'undefined') ? videoSliderDelay : 2000,
    cssEase: 'linear',
    centerMode: true,
    slidesToShow: 1,
    centerPadding: '100px',
    touchMove: true,
    responsive: [
     {
       breakpoint: 480,
       settings: {
         arrows: false,
         centerMode: true,
         centerPadding: '30px',
       }
     }
    ]
  });

  function shop_rotator() {
    // $('.rotator .button.arrow').on("click", function (event) {
    //   this.delegateEvents();
    // }
    $('.shop-carousel').slick({
      dots: false,
      speed: 500,
      autoplay: (typeof shopautoscroll !== 'undefined') ? shopautoscroll : false,
      arrows: true,
      slidesToShow: 1,
      autoplaySpeed: (typeof shoptimedelay !== 'undefined') ? shoptimedelay : 2000,
      slidesToScroll: 1,
      // responsive: [{
      //   breakpoint: 768,
      //   settings: {
      //     slidesToShow: 1
      //   }
      // }]
    });
  }
  function welcome_cookie_slider() {
    // $('.rotator .button.arrow').on("click", function (event) {
    //   this.delegateEvents();
    // }
    $('.welcome-video-slider').slick({
      dots: false,
      speed: 500,
      autoplay: true,
      arrows: false,
      slidesToShow: 1,
      slidesToScroll: 1,
      fade: true,
      // responsive: [{
      //   breakpoint: 768,
      //   settings: {
      //     slidesToShow: 1
      //   }
      // }]
    });
  }



/*window.onYouTubeIframeAPIReady = function() {
		loadYoutubeAPI();
		$('.lazyYT').lazyYT('AIzaSyD5AjIEx35bBXxpvwPghtCzjrFNAWuLj8I');
    };*/

	function loadYTScript() {
	    if (typeof(YT) == 'undefined' || typeof(YT.Player) == 'undefined') {
	    	var tag = document.createElement('script');
	        tag.src = "https://www.youtube.com/iframe_api";
	        var firstScriptTag = document.getElementsByTagName('script')[0];
	        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
	    }
	}



  //camp-finder vaidation and submittion function
  function camp_finder() {
    var campFormSubmitted = false;
    $('.find-camp').submit(function (event) {
       if(event.preventDefault) {
         event.preventDefault();
       } else {
         event.stop();
       }
       event.returnValue = false;
       event.stopPropagation();

       if (campFormSubmitted) {
         return;
       }

      event.preventDefault();

      var zip = $(this).find('input[name="zip-code"]').val();
      var zip_field = $(this).find('input[type="text"]');
      var redirectUrl = loc; //passed in from standalone-camp-finder.jsp

      if(zip != zip.match("[0-9]{5}")) {

        zip_field.attr("value", "invalid zip code");
        zip_field.css({
            "font-size" : "12px",
            "color": "red"
        });
        zip_field.on( "blur, focus", function() {
          $(this).css({
              "font-size" : "1.125rem",
              "color": "#000"
          });
          if($(this).attr('value') == "invalid zip code") {
            $(this).val("").attr("placeholder", "ZIP Code");
          }
        });
      } else {
	  	  var currentUrl = window.location.href;
	  	  var isSameUrl = currentUrl.substring(0, currentUrl.indexOf('.html')) == redirectUrl.substring(0, redirectUrl.indexOf('.html'));

	      if (window.location.search != undefined && window.location.search != "") {
	    	  redirectUrl += window.location.search;
	      }

	      redirectUrl = redirectUrl + '#' + zip;

		  if (isSameUrl) {
			  window.location.hash = "#" + zip;
		      window.location.reload();
		  }else{
			  window.location.href = redirectUrl;
		  }
      }
    });
  }

  function hide_show_cookie() {
    $('#meet-cookie-layout section').hide();
    $('#meet-cookie-layout .wrapper h4').on('click', function (e) {
      $(this).siblings('section').slideToggle();
      $(this).toggleClass('on');
    });
  }
  fix_bottom_footer();
  slide_search_bar();
  small_screens();
  show_hide_features();
  document_close_all();
  explore_button();
  join_now();
  scroll_feeds();
  shop_rotator();
  welcome_cookie_slider();

  loadYTScript();
  $('.lazyYT').lazyYT('AIzaSyD5AjIEx35bBXxpvwPghtCzjrFNAWuLj8I');
  camp_finder();

  $(window).resize(function() {
    small_screens();
  });
  $(window).resize(function (event) {
    //if($(window).width() > 768) {
    //  iframeClick();
    //} else {
      $("iframe").off( "mouseenter mouseleave" );
      //$("iframe").unbind("mouseenter,mouseleave");
    //}
  });
  $(window).load(function () {
    equilize_our_stories();
    hide_show_cookie();
  });

  // form on the Donate Tile.
  $("#tag_tile_button_local, .standalone-donate a.button.form").on('click', function (e) {
    e.preventDefault();
    $('#tag_tile_button_donate').toggle();
    $('.formDonate').toggleClass('hide');
    $(this).toggleClass('hide');
    $('.formDonate input[type="text"]').focus();
  });
  $(document).on('closed.fndtn.reveal', '[data-reveal]', function () {
    $(".off-canvas-wrap").removeClass('noprint');
  });
  // $(document).ready(function() {
    // Setup "contact local council" form
    $('.booth-finder form#contactlocalcouncil').submit(function(){
      $.post($(this).attr('action'), $(this).serialize(), function(response) {
        // Remove blank lines
        response = response.replace(/^\s*\n/gm, '').trim();
        if (response.toUpperCase() == 'OK') {
          $('#contactlocalcouncil').html('Thank you. A representative will contact you shortly.');
        } else {
          $('#contactlocalcouncil div.error').html(response);
        }
      });
      // Prevent default
      return false;
    });
  // });
// $('#videoModal').foundation('reveal', 'open', '//www.youtube-nocookie.com/embed/wnXCopXXblE?rel=0');

}(jQuery));

function attachListenerToVideoSlider () {
    for (var i = 0; i < $('.vid-slide-wrapper iframe').length; i ++) {
        var iframe = $('.vid-slide-wrapper iframe')[i],
            player;
        if ($(iframe).hasClass("vimeo")) {
            player = $f(iframe);
            player.addEvent('ready', function() {
                player.addEvent('playProgress', function() {
                    stopSlider();
                });
            });
        }
    }
}

function stopSlider() {
    var slick = $('.video-slider-wrapper');
    if(slick != undefined && slick.slick != undefined){
        slick.slick('slickPause');
        slick.slick('slickSetOption', 'autoplay', false, false);
        slick.slick('autoPlay',$.noop);
    }
};

function fixColorlessWrapper() {
  // inkoo - this crazy code is to accommodate the initial hidden state of the slick layer for videos
  var colorlessWrappers = $(".story.colorless .bg-wrapper");
  for (var i = 0; i < colorlessWrappers.length; i++) {
    var thisWrapperStyle = $(colorlessWrappers[i]).attr("style");
    if(thisWrapperStyle) {
      $(colorlessWrappers[i]).attr("style", thisWrapperStyle.replace(/, ?[0-9\.]*\)/, ", 1\)"));
    }
  }
  //$(".story.colorless .bg-wrapper").each(function() {if($(this).attr("style")) {$(this).attr("style", $(this).attr("style").replace(/, ?[0-9\.]*\)/, ", 1\)"))}});
}

function fixSlickListTrack() {
  // inkoo - this crazy code is to accommodate the initial hidden state of the slick layer for videos
  // don't use each function in jquery because ie doesn't support it
  //          $(".slick-list .slick-track").each(function() {if($(this).attr("style")) {$(this).attr("style", $(this).attr("style").replace(/width: ?0px;/, "width: 100%;"))}});
  //          $(".slick-slide.slick-active").each(function() {if($(this).attr("style")) {$(this).attr("style", $(this).attr("style").replace(/width: ?0px;/, "width: 100%;"))}});

  var slickListTrack = $(".slick-list .slick-track");
  for (var i = 0; i < slickListTrack.length; i++) {
    var thisWrapperStyle = $(slickListTrack[i]).attr("style");
    if(thisWrapperStyle) {
      $(slickListTrack[i]).attr("style", thisWrapperStyle.replace(/width: ?0px;/, "width: 100%;"));
    }
  }
}
function fixSlickSlideActive() {
  var slickSlideActive = $(".slick-slide.slick-active");
  for (var i = 0; i < slickSlideActive.length; i++) {
    var thisWrapperStyle = $(slickSlideActive[i]).attr("style");
    if(thisWrapperStyle) {
      $(slickSlideActive[i]).attr("style", thisWrapperStyle.replace(/width: ?0px;/, "width: 100%;"));
    }
  }
}


// useful utility printer of object properties
function printObjectProperties(objectToInspect) {
  for (var key in objectToInspect) {
    if (objectToInspect.hasOwnProperty(key)) {
      var obj = objectToInspect[key];
      for (var prop in obj) {
        if(obj.hasOwnProperty(prop)){
          console.log(prop + " = " + obj[prop]);
        }
      }
    }
  }
}

function populateVideoIntoModal(divId, videoLink, color, e) {
  var parent = $("#" + divId + " " + ".video-popup");
  $("#" + divId).css("background-color", "#" + color);
  parent.html(videoLink);
  e.preventDefault();
  return false;
}

function setupContactLocalCouncilForm() {
	// Setup "contact local council" form
	$('.booth-finder form#contactlocalcouncil').submit(function(){
		$.post($(this).attr('action'), $(this).serialize(), function(response) {
			// Remove blank lines
			response = response.replace(/^\s*\n/gm, '').trim();
			if (response.toUpperCase() == 'OK') {
				$('#contactlocalcouncil').html('Thank you. A representative will contact you shortly.');
			} else {
				$('#contactlocalcouncil div.error').html(response);
			}
		});

		// Prevent default
		return false;
	});
}

$(document).ready(setupContactLocalCouncilForm);

// Needed for "View Detail" data
Handlebars.registerHelper('json', function(context) {
    return JSON.stringify(context).replace(/'/g, "&#39;");
});
Handlebars.registerHelper('escapeDoubleQuotes', function(context) {
	if (typeof context == 'string') {
    	return context.replace(/"/g, '\\\"');
	}
	return '';
});

