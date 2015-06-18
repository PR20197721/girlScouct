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
  
	window.fbAsyncInit = function(){
		FB.init({
			appId:"419540344831322",
			status:true,
			cookie:true,
			xfbml:true,
			oauth:true,
			//channelUrl:"https://www.girlscouts.org/gogoldonlinechannel.ashx"
			channelUrl:"http://girlscouts.org"
		});
	};
  
  var addthis_config = addthis_config || {};
  addthis_config.data_track_addressbar = false;
  addthis_config.data_track_clickback = false;
  
  addthis.addEventListener('addthis.ready', function (e) {
      feederize('419540344831322',
          "Registration",
          "");
  });

}(jQuery));
