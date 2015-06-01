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

  function slide_search_bar() {
    var searchSlider = {
      form: $(".tab-bar .search-form"),
      input: $(".tab-bar .search-form input"),
      button: $(".tab-bar .search-form span")
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

    searchSlider.form.submit(function () {
      searchSlider.input.keydown(function (e) {
        if (e.which === 13) {
          if (searchSlider.input.val() !== "") {
            searchSlider.form.submit();
            searchSlider.input.val('');
          } else {
            e.preventDefault();
          }
        }
      });
    });
    //when clicking outside of the form it will close the input.
    $(document).click(function (event) {
      var target = $(event.target);
      if (target.closest('.tab-bar .search-form').length === 0) {
        searchSlider.button.removeClass('hide');
         //searchSlider.button.click();
        searchSlider.input.hide('slow');
      }
    });
  }
  // function show_hide() {
  //   alert("hello");
  //   //if ($(".featured-stories.target").length > 0) {
  //   $("li.featured-story").each(function (index) {
  //     alert('inside' + index);
  //     $(this).click(function (e) {
  //       e.stopPropagation();
  //       var target = $(this).children().data('target');
  //       // var toggle = $(this);
  //       $('.' + target).show("slow");
  //       // target.show("slow");
  //       return false;
  //     });
  //   });
  //   //}
  // }

  function show_hide() {
    console.log('Mike is here');
    if ($("#_content_gsusa_en_jcr_content_content_featured-stories").length > 0) {
      console.log($(".featured-stories.target").length);

      $("ul.featured-stories li").each(function () {
        alert("each function");
        console.log($(this).length);
        var elem = $(this);
        var target = elem.find('section');
        var contents = elem.find(".thumb .contents").html();
        var section_header = target.find('.header');

        elem.on("click", function (e) {
          console.log($(this).length);
          e.stopPropagation();
          // var toggle = $(this);
          $("ul.featured-stories li section .header").empty();
          section_header.append(contents);
          target.show("slow");
        });
        target.find('.icon-cross').on("click", function (e) {
          target.hide("slow");
          e.stopPropagation();
          return false;
        });
      });
    }
  }
  $('.hero-feature ul').slick({
    dots: true,
    infinite: true,
    speed: 500,
    fade: true,
    cssEase: 'linear',
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 3,
          slidesToScroll: 3,
          infinite: true,
          dots: true
        }
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 2
        }
      },
      {
        breakpoint: 480,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1
        }
      }
      // You can unslick at a given breakpoint now by adding:
      // settings: "unslick"
      // instead of a settings object
    ]
  });
  fix_bottom_footer();
  slide_search_bar();
  show_hide();
}(jQuery));
