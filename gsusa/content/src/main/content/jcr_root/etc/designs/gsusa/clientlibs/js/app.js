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
        $(".story").hide("slow");
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
          $('.main-slider').slick({
            autoplay: true
          });
          //console.log('main carousel automated');
        });
      }
      if (target.closest('.final-comp').length === 0
          && target.closest(".final-comp").css('display') !== 'none') {
        $(".final-comp").hide();
        $('.hero-text.first').show();
      }
      if (target.closest('.standalone-volunteer').length === 0
          && target.closest('.button.arrow').siblings('form').css('display') !== 'none') {
        $('.button.arrow').siblings('form').addClass('hide');
      }
    });
  }

  function slide_search_bar() {
    var searchSlider = {
      form: $(".tab-bar .search-form"),
      input: $(".tab-bar .search-form input"),
      button: $(".tab-bar .search-form span")
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
    headerjoin.button.click(function (event) {
      event.stopPropagation();
      headerjoin.input.stop().animate({
        width: 'toggle',
      }, 500, function () {
        if (headerjoin.input.is(':visible')) {
          headerjoin.input.focus();
          headerjoin.button.addClass('on');
        } else {
          // searchSlider.button.focus();
          headerjoin.button.removeClass('on');
        }
      });
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
    headervolunteer.button.click(function (event) {
      event.stopPropagation();
      headervolunteer.input.stop().animate({
        width: 'toggle',
      }, 500, function () {
        if (headervolunteer.input.is(':visible')) {
          headervolunteer.input.focus();
          headervolunteer.button.addClass('on');
        } else {
          // searchSlider.button.focus();
          headervolunteer.button.removeClass('on');
        }
      });
      event.preventDefault();
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
      });
    });
  }

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
          target.show("slow");
          try {
            gsusa.functions.ToggleParsysAll.toggleAll(true);
          } catch (ignore) {}
          if ($(window).width() <= 640) {
            target.css({
              "min-height" : "100",
              "bottom" : "auto",
              "top" : $(document).scrollTop()
            });
            $(".featured-stories").css('position', 'static');
          }
          return false;
        });
        //closing the section by clicking on the cross
        target.find('.icon-cross').on("click", function (e) {
          target.hide("slow");
          try {
            gsusa.functions.ToggleParsysAll.toggleAll(false);
          } catch (ignore) {}
          e.stopPropagation();
          return false;
        });
      });
    }
  }

  $('.main-slider').slick({
    dots: false,
    infinite: true,
    speed: 1000,
    fade: true,
    autoplay: true,
    arrows: false,
    cssEase: 'linear',
  });

  function explore_button() {
    $(".hero-text .button").on("click", function () {
      $('.inner-sliders .inner').slick({
        dots: false,
        infinite: false,
        speed: 500,
        fade: false,
        dotsClass: 'slick-dots',
        cssEase: 'linear',
        arrows: true,
        // responsive: [
        //   {
        //     breakpoint: 640,
        //     settings: "unslick"
        //   },
        //   {
        //     breakpoint: 480,
        //     settings: "unslick"
        //   }
        //   You can unslick at a given breakpoint now by adding:
        //   settings: "unslick"
        //   instead of a settings object
        // ]
      });
      $('.inner-sliders .slide-1').slick({
        dots: true,
        fade: true,
        dotsClass: 'slick-dots',
        cssEase: 'linear',
        arrows: false,
        customPaging: function (slick, index) {
          return slick.$slides.eq(index).find('.slide-thumb').prop('outerHTML');
        },
      });
      $('.inner-sliders .slide-2, .inner-sliders .slide-3').slick({
        dots: false,
        fade: true,
        cssEase: 'linear',
        arrows: false,
      });
      $('.main-slider').slick('slickPause');
      $('.main-slider').slick({
        autoplay: false
      });
      console.log('main carousel paused');
      $('.inner-sliders .inner').slick('init');
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
  }
  function scroll_feeds() {
    $('.scroll-more').bind("click", function () {
      // var target = $(e.target);
      var scroll_area = $(this).siblings('.social-block');

      var feed_height = scroll_area.scrollTop() + scroll_area.outerHeight();
      var inner_height = scroll_area.parents().find('.twitter-timeline-rendered').height();

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
  function equilize_gs_stories() {
    var blocks = $('.gs-stories-block li div');
    var maxHeight = Math.max.apply(Math, blocks.map(function () {
      console.log($(this).height());
      return $(this).height();

    }).get());
    blocks.height(maxHeight);
  }
  // function show_final() {
  $('.inner-sliders .inner').on('afterChange', function () {
    // if ($('inner-sliders .slick-slider .slick-active').last()) {
    $('.slick-next.slick-disabled').on('click', function (event) {
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
      $('.inner-sliders .inner').slick('unslick');
    });
    // }
  });
  $(window).resize(function () {
    if ($(window).width() < 640) {
      $('.overlay').hide();
      // $('.inner-sliders').css({
      //   opacity: 0,
      //   "z-index": 1
      // });
    }
  });
  
  $('.button.arrow').on("click", function () {
    event.preventDefault();
    var this_form = $(this).siblings("form");
    this_form.removeClass('hide');
  });
  fix_bottom_footer();
  slide_search_bar();
  show_hide_features();
  document_close_all();
  explore_button();
  join_now();
  scroll_feeds();
  equilize_gs_stories();
  //show_final();

}(jQuery));
