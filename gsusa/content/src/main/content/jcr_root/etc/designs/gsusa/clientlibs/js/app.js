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

    $(".main-content").css({
      "min-height" : total_height
    });
  }

  function document_close_all() {
    //when clicking outside of the form it will close the input.
    $(document).click(function (event) {
      event.stopPropagation();
      var target = $(event.target);

      if (target.closest('.tab-bar .search-form').length === 0
          && $(".tab-bar .search-form input").css('display') !== 'none') {
        $(".tab-bar .search-form span").removeClass('hide');
        $(".tab-bar .search-form input").hide('slow');
      }
      if (target.closest('.featured-stories li').length === 0
          && target.closest(".story").css('display') !== 'none') {
        $(".story").removeClass("shown");
        $("body").css('overflow', '');
      }
      if (target.closest('.join .wrapper').length === 0
          && target.closest(".join section").css('display') !== 'none') {
        $('.join section').fadeOut('500', function () {
          $('.join a').fadeIn('slow');
        });
      }
      if (target.closest('.hero-feature').length === 0
          && target.closest(".hero-feature").css('display') !== 'none') {
        $('.position').animate({
          'opacity': 0,
        }, 100, function () {
          $('.hero-feature .overlay').fadeOut();
          $('.position').css('z-index', '-1');
          $('.join').removeClass('change');
          $('.main-slider').slick('slickPlay');
        });
      }
      if (target.closest('.final-comp').length === 0
          && target.closest(".final-comp").css('display') !== 'none') {
        $(".final-comp").hide();
        $('.hero-text.first').show();
        $('.join').show();
    //    $('.inner-sliders .inner').slick('unslick');
      }
      if (target.closest('.standalone-volunteer').length === 0
          && target.closest('.button.arrow').siblings('form').css('display') !== 'none') {
        $('.button.arrow').siblings('form').addClass('hide');
      }
    });
  }

  function headercomptrigger(item, event) {
    event.stopPropagation();
    item.input.stop().animate({
      width: 'toggle',
    }, 500, function () {
      if (item.input.is(':visible')) {
        item.input.focus();
        item.button.addClass('on');
      } else {
        item.button.removeClass('on');
      }
    });
    event.preventDefault();
  }

  function slide_search_bar() {
    var searchSlider = {
      form: $(".tab-bar .search-form"),
      input: $(".tab-bar .search-form input"),
      button: $(".tab-bar .search-form span")
    };
    var joinNow = {
      form: $(".join-now-form .formJoin"),
      input: $(".join-now-form .join-text .formJoin"),
      button: $(".join-now-form .button.join-now .formJoin")
    };
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
    joinNow.button.click(function (event) {
      event.stopPropagation();
      event.preventDefault();
      joinNow.input.stop().animate({
        width: 'toggle',
      }, 500, function () {
        if (joinNow.input.is(':visible')) {
          joinNow.input.focus();
          joinNow.button.addClass('hide');
          joinNow.form.submit(function () {
            if (joinNow.input.val() !== "") {
              joinNow.form.submit();
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

    searchjoin.button.click(function (event) {
      event.stopPropagation();
      searchjoin.input.stop().animate({
        width: 'toggle',
      }, 500, function () {
        if (searchjoin.input.is(':visible')) {
          searchjoin.input.focus();
          searchjoin.button.addClass('on');
        } else {
          // searchSlider.button.focus();
          searchjoin.button.removeClass('on');
        }
      });
    });

    function headercomptrigger(item) {
      event.stopPropagation();
      item.input.stop().animate({
        width: 'toggle',
      }, 500, function () {
        if (item.input.is(':visible')) {
          item.input.focus();
          item.button.addClass('on');
        } else {
          item.button.removeClass('on');
        }
      });
      event.preventDefault();
    }


    headerjoin.button.click(function () {
      headercomptrigger(headerjoin);
    });
    headerjoin.button.keyup(function (event) {
      if (event.keyCode === 13) {
        headercomptrigger(headerjoin);
      }
    });

    searchvolunteer.button.click(function (event) {
      event.stopPropagation();
      searchvolunteer.input.stop().animate({
        width: 'toggle',
      }, 500, function () {
        if (searchvolunteer.input.is(':visible')) {
          searchvolunteer.input.focus();
          searchvolunteer.button.addClass('on');
        } else {
          // searchSlider.button.focus();
          searchvolunteer.button.removeClass('on');
        }
      });
      event.preventDefault();
    });
    headervolunteer.button.click(function () {
      headercomptrigger(headervolunteer);
    });
    headervolunteer.button.keyup(function (event) {
      if (event.keyCode === 13) {
        headercomptrigger(headervolunteer);
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
    searchvolunteer.form.submit(function () {
      if (searchvolunteer.input.val() !== "") {
        searchvolunteer.form.submit();
        searchvolunteer.input.val('');
      } else {
        return false;
      }
    });
    headervolunteer.form.submit(function () {
      if (headervolunteer.input.val() !== "") {
        headervolunteer.form.submit();
        headervolunteer.input.val('');
      } else {
        return false;
      }
    });
  }
  //home page join now link will open the email form.
  function join_now() {
    $('.join a').on('click', function (e) {
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
      breakpoint: 640,
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
          target.addClass("shown");
          $('.shop-slider').slick('setPosition');
          try {
            gsusa.functions.ToggleParsysAll.toggleAll(true);
          } catch (ignore) {}
          if ($(window).width() <= 768) {
            target.css({
              "bottom" : "auto",
              "top" : $(document).scrollTop()
            });
            $(".featured-stories").css('position', 'static');
            if ($(window).height() < target.find('.contents').height()) {
              target.find('.contents').css({
                'max-height': ($(window).height() - 100) + 'px',
                'overflow' : 'scroll'
              });
            }

            $("body").css('overflow', 'hidden');
          }
          return true;
        });
        //closing the section by clicking on the cross
        target.find('.icon-cross').on("click", function (e) {
          target.removeClass("shown");
          $("body").css('overflow', '');
          try {
            gsusa.functions.ToggleParsysAll.toggleAll(false);
          } catch (ignore) {}
          e.stopPropagation();
          return false;
        });
      });
    }
  }

  //the "interval" parameter is defined in the jsp in carousel.jsp, which allows the user to set its value
  $('.main-slider').slick({
    dots: false,
    infinite: true,
    speed: (typeof interval !== 'undefined') ? interval : 1000,
    fade: true,
    autoplay: true,
    arrows: false,
    cssEase: 'linear',
  });

  // $('.inner-sliders .inner').slick({
  //   dots: false,
  //   infinite: false,
  //   speed: 500,
  //   fade: false,
  //   dotsClass: 'slick-dots',
  //   cssEase: 'linear',
  //   arrows: true,
  // });

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
          return slick.$slides.eq(index).find('.slide-thumb').prop('outerHTML') + "<p>" + $("#hiddenThumbnail" + index).text(); + "</p>";
        },
      });
      $('.inner-sliders .slide-3').slick({
        dots: false,
        fade: true,
        cssEase: 'linear',
        arrows: false,
      });
      $('.main-slider').slick('slickPause');
      // $('.main-slider').slickPause();
      console.log('main carousel paused');

      if ($(window).width() > 640) {
        $('.overlay').fadeIn();
        if ($(".position").css("opacity") === '0') {
          $(".position").animate({
            'opacity': 1
          }, 1000);
          $(".position").css('z-index', '100');
          $('.join').addClass('change');
        }
      }
    });
  }//END OF EXPLOER CLICK FUNCTION


    $('.inner-sliders .inner').on('init reInit afterChange', function (slick, currentSlide, index) {
      var item_length =  $('.inner-sliders .inner > .slick-list > .slick-track > li').length -1;
      if (item_length === index) {
        $('.slick-disabled').on('click', function (event) {
          event.stopPropagation();
          $('.position').animate({
            'opacity': 0,
          }, 100, function () {
            $('.hero-feature .overlay').fadeOut();
            $('.position').css('z-index', '-1');
            $('.join').removeClass('change');
            $('.hero-text.first').hide();
          });
          $('.final-comp').show();
          $('.join').hide();
         // $('.inner-sliders .inner').slick('unslick');
        });
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
      var scroll_area = $(this).siblings('.social-block');

      var feed_height = scroll_area.scrollTop() + scroll_area.outerHeight();
      var inner_height = scroll_area.parent('.wrapper').find('.block-area').height();

      if (feed_height >= inner_height) {
        scroll_area.animate({
          scrollTop: 0
        }, 500);
        return false;
      }
      scroll_area.animate({
        scrollTop: "+=" + scroll_area.height() + "px"
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
    if ($(window).width() < 768) {
      $('.overlay').hide();
      $(".hero-text .button").hide();
      // $('.inner-sliders').css({
      //   opacity: 0,
      //   "z-index": 1
      // });
    } else {
      $(".hero-text .button").show();
    }
  }

  $(window).resize(function () {
    small_screens();
  });

  $('.button.arrow').on("click", function (event) {
    event.preventDefault();
    var this_form = $(this).siblings("form");
    this_form.removeClass('hide');
  });

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

  fix_bottom_footer();
  slide_search_bar();
  small_screens();
  show_hide_features();
  document_close_all();
  explore_button();
  join_now();
  scroll_feeds();
  //show_final();

  $(window).load(function () {
    equilize_our_stories();
  });

}(jQuery));
