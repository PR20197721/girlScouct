var $ = jQuery.noConflict();
//all function calls should go here
(function($) {
	function showError(msg, msgId) {
		var targetNode = "#error_msg";
		if (msgId) {
			targetNode = msgId;
		}
		if (msg) {
			$(targetNode).html(msg);
			$(targetNode).show();
		} else {
			// clear
			$(targetNode).html("");
			$(targetNode).hide();
		}
	}
	// function modal_height() {
 //  	var window_h = $(window).height();
 //  	// var popup_top = $(window_h -$('.reveal-modal').height()/2);
 //  	var popup_h = (window_h - 50);
	// 	$('.reveal-modal').css('top', 0);
	// 	$('.reveal-modal').css('height' , window_h + 'px');
	// 	$('.scroll').css('max-height' , popup_h +' px');
	// }
/* In Koo removed this because it is duplicated in /etc/designs/girlscouts/clientlibs/js/footer.js
  function vtk_accordion() {
	if ($(".accordion").length > 0) {
	    $('.accordion dt > :first-child').on('click', function(e) {
	    e.stopPropagation();
	      var target = $(this).parent().data('target');
	      var toggle = $(this);
	      $('#' + target).slideToggle('slow');
	      $(toggle).toggleClass('on');
	      return false;
		});
	  }
  }
*/


	function modal_height_on_open() {
	  $(document).on('opened.fndtn.reveal', '[data-reveal]', function () {
			var window_h = $(window).height();
			var popup_h = (window_h - 75);
			$(this).find('.scroll').css('max-height' , popup_h + 'px');
			var browser =navigator.userAgent.match(/(msie\ [0-9]{1})/i);
			if ( browser!=null && browser[0].split(" ")[1] == 9) {
			  // alert(navigator.userAgent.match(/msie/i));
			  placeholder_IE9();
			  $('select').css('background-image', 'none');
			}

			//adding a heights to popups with two scrollable content.
			$('.scroll_2').css('max-height' , popup_h -$('.scroll_1').height() + 'px');

		});
	}

	function modal_height_resize() {
  		var window_h = $(window).height();
  		var popup_h = (window_h - 75);
		$('.scroll').css('max-height' , popup_h + 'px');
		$('.modalWrap').css('max-height', $(window).height() + 'px');
		$('.ui-dialog').css('left', $(window).width()<920?0:($(window).width() - 920) / 2 )
		//adding a heights to popups with two scrollable content.
		$('.scroll_2').css('max-height', ($(window).height()-75)-$('.scroll_1').height() + 'px');
	}
	function validate_image() {
		$('form#frmImg').submit(function(e) {
		   var $this = $(this);
		   var $input =  $this.find('input[type="file"]').val();
		 if($input == '') {
		   alert ("you must choose a image");
		  
		   e.preventDefault();
		   return false; 
		 }
		});
	}
	//used if using select instead of tabs for small, screens was removed.
	function select_tabs() {
		$("select.tabs").on('change', function(index) {
		    if ($(this).index() !==0 ) {
		      window.location.href = $(this).val();
		    }
		});
		var path = location.pathname;
		$('select.tabs option').each(function() {
			if($(this).val() == path) {
				$(this).prop("selected", true);
			}
		});
  }

  function add_placeholdersIE9() {
		function add() {
		  if($(this).val() === ''){
		    $(this).val($(this).attr('placeholder')).addClass('placeholder');
		  }
		}

		function remove() {
		  if($(this).val() === $(this).attr('placeholder')){
		    $(this).val('').removeClass('placeholder');
		  }
		}
	  // Create a dummy element for feature detection
	  if (!('placeholder' in $('<input>')[0])) {

	    // Select the elements that have a placeholder attribute
	    $('input[placeholder], textarea[placeholder]').blur(add).focus(remove).each(add);

	    // Remove the placeholder text before the form is submitted
	    $('form').submit(function(){
	      $(this).find('input[placeholder], textarea[placeholder]').each(remove);
	    });
	  }
  }

//	  $(document).ready(function() {
	  	 $(document).foundation({
	  	  reveal : {
				  animation: 'fade',
	  	     root_element: 'window',
				 close_on_background_click: false,
				 opened: function () {
					var window_h = $(window).height();
					var popup_h = (window_h - 75);
					$('#modal_popup').find('.scroll').css('max-height' , popup_h + 'px'); 
				  },
	  	     open: function () {
	  	     	$('body').css({'overflow':'hidden'});
     		  	if (navigator.userAgent.match(/msie/i) ) {
     		  		// alert(navigator.userAgent.match(/msie/i));
     	        	add_placeholdersIE9();
						}
	  	     },
	  	     close: function () {
	  	     	$('body').css({'overflow':'inherit'})
	  	     },
	  	 	}
	  	 });
	  	 modal_height_on_open();
	  	 validate_image();
	  	 // resizeWindow();
	  	 if($('.tabs dd').length == 6) {
	  	 	$('.tabs dd').css('width','100%');
	  	 }
	  	 if($('.tabs dd').length == 5) {
	  	 	$('.tabs dd').css('min-width','20%');
	  	 }
//  });

	$(window).resize(function() {
		modal_height_resize();
	});

})($);

/**
 * VTK Data Worker
 * 
 * Fetch VTK data first and then call the callback
 * if the returned status code is not 304.
 */

var VTKDataWorker;
(function() {
	var BASE_PATH = '/vtk-data';
	
    function _getTroopDataToken() {
    	// Ref: https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie
    	// Get cookie: troopDataToken
    	var hash = document.cookie.replace(/(?:(?:^|.*;\s*)troopDataToken\s*\=\s*([^;]*).*$)|^.*$/, "$1");
    	return hash;
    }
    
    function _checkShouldSkipFirst() {
    	// Get cookie: troopDataToken
    	var readonly = document.cookie.replace(/(?:(?:^|.*;\s*)VTKReadonlyMode\s*\=\s*([^;]*).*$)|^.*$/, "$1");
    	return !(readonly == 'true');
    }
    
    function _VTKDataWorker(path, that, success, interval) {
    	this.path = path;
    	this.that = that;
    	this.successResponse = success;
    	this.interval = interval;
    	this.url = BASE_PATH + '/' + _getTroopDataToken() + '/' + path;
    	this.shouldSkipFirst = _checkShouldSkipFirst();
    	this.eTag = null;
    }
    	
    _VTKDataWorker.prototype.getData = function(shouldSkip) {
		var url = this.url;
		if (shouldSkip) {
			url += "?_=" + (new Date()).getTime();
		    if (this.intervalId) {
		    	clearInterval(this.intervalId);
		    	this.setInterval();
		    }
		}
		
		if (typeof VTKDataWorkerShouldSkipNextPoll !== 'undefined' && VTKDataWorkerShouldSkipNextPoll) {
			VTKDataWorkerShouldSkipNextPoll = false;
			return;
		}
		
	
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data, textStatus, jqXHR){   
                var eTag = jqXHR.getResponseHeader("ETag");
                if (eTag) {
                	this.eTag = eTag;
                }
                // Only call the callback if the status code is 200.
                if (this.successResponse && jqXHR.status == 200) {
                    this.successResponse(data)
                }
            }.bind(this),
            beforeSend: function(request) {
                if (this.eTag != null) {
                    request.setRequestHeader('If-None-Match', this.eTag);
                }
            }.bind(this)
        });
    };
    
    _VTKDataWorker.prototype.setInterval = function() {
    	this.intervalId = setInterval(this.getData.bind(this), this.interval);
    }

    _VTKDataWorker.prototype.start = function() {
    	this.getData(true);
    	this.setInterval();
    };

    // Expose the function to global namespace
    VTKDataWorker = _VTKDataWorker;
})();



/* 	
	CALL ABND APPEND THE  DOME WITH HTML ELEMENTS AND TRIGGER THE SLIDER
*/

function callExecuteBannerSlider(tabNavLoaded) {

	tabNavLoaded = tabNavLoaded || new $.Deferred().resolve();

	function processVtkBannerResponse(result) {

		if ($("#vtk_banner2234").data('cached') === 'no') {
			$("#vtk_banner2234").show();
		}

		var htmlResults = $(result[0]);
		var vtkBannerSections = htmlResults.find('.vtk-banner.section');

		//REMOVE BANNERS THAT ARE DISABLED.
		vtkBannerSections.each(function (x, y) {
			if ($(y).find('.vtk-banner-disabled').length) {
				$(this).remove();
			}
		});

		//APPEND TO THE  BANNER
		$("#vtk_banner2234").append(htmlResults);

		//CLOSE BANNER
		$('.vtk-banner-button').click(function () {
			$.ajax({
				url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=hideVtkBanner',
				dataType: 'html',
			}).done(function () {
				$('.vtk-banner-image').slideUp();
				$(".slick-next").css('display', 'none');
                $(".slick-prev").css('display', 'none');
			})
		});


		//VTK BANNER SLIDER SETTING
		$('.vtk-banner-container').slick({
			slidesToScroll: 1,
			adaptiveHeight: true,
			autoplaySpeed: 10000,
			autoplay: true,
		});

		//Breaking News Maintenance Close:
        $('.vtk-breaking-news-button').click(function () {
             $.ajax({
                url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=hideVtkMaintenance',
                dataType: 'html',
            }).done(function () {
                $('#vtkBreakingNews').slideUp();
            })
        });

	}

	var bannerLoaded = $.ajax({
		url: '/content/vtkcontent/en/vtk-banner.simple.html',
		type: 'GET',
		dataType: 'html',
		data: {
			a: Date.now()
		}
	});

	$.when(bannerLoaded, tabNavLoaded).then(processVtkBannerResponse);

}

function callFoundationModal(e,id) {
	$(document).foundation();
	e.preventDefault();
	console.log('#' + id)

	// $(e.target).trigger('click')

		$('#'+id).foundation('reveal', 'open');
}



function setHeigthPropertiesToBanner(p){

	var image = $(p).find('.banner-image');
	var scroll = $(p).find('.scroll-banner');
	var height = $(window).height();
	var modalwidth = $(p).innerWidth();
	var	imageHeight = image.height();
	scroll.css(
		{
			'maxHeight':$(window).height()-imageHeight-75+'px',
			'overflow-y':'auto'
		}
	);
}


$(function(){

	var TIME_FOR_LOG_OUT_IN_MINUTES = 10;

	var signOutModal = new ModalVtk('Signout',true);

	signOutModal.init();

	function singOut(timeinminutes, _this) {

		var timeoutId = undefined, secondTimeOutId = undefined, timeinmillisecond = timeinminutes * 60 * 1000;

		function startTimeout() {
			timeoutId = setTimeout(goInactive, timeinmillisecond);
		}

		function init() {

			window.addEventListener("mousedown", resetTimeout, false);
			window.addEventListener("keypress", resetTimeout, false);
			window.addEventListener("DOMMouseScroll", resetTimeout, false);
			window.addEventListener("mousewheel", resetTimeout, false);
			startTimeout();
		}

		function goInactive() {
			clearTimeout(timeoutId);
			window.removeEventListener("mousedown", resetTimeout, false);
			window.removeEventListener("keypress", resetTimeout, false);
			window.removeEventListener("DOMMouseScroll", resetTimeout, false);
			window.removeEventListener("mousewheel", resetTimeout, false);
			secondTimeOutId = setTimeout(onCB, 30000 /* in millisecons */);
			signOutModal.confirm('LOGGING OUT', '<p>You are about to be automatically logged out due to inactivity. Please click "CANCEL" to continue working.</p>', onCB, cCBack);
		}

		function onCB(){
			girlscouts.components.login.signOut();
		}

		function cCBack() {
			
			clearTimeout(secondTimeOutId);
			init();
			signOutModal.close();
		}

		function resetTimeout() {
			clearTimeout(timeoutId);
			startTimeout();
		}

		init();
	}

	if(!~document.cookie.indexOf('wcmmode')){ //Return Boolean
		singOut(TIME_FOR_LOG_OUT_IN_MINUTES, this);
	}

})
$(window).load(function(){
         var data = $("#maintenanceNotify").attr("data-val");
         if(data == "true"){
            var notice = document.getElementById("maintenance");
  			notice.classList.toggle("show");

         }
    	 $(".maintenanceWarning").click(function(){
            if($(".maintenanceWarning").is(':visible')){
                var notice = document.getElementById("maintenance");
                notice.classList.toggle("show");
         }
 });
});
// Re-Foundation on page load and dom load for modals that were added dynamically.
$(function(){
	$(document).foundation();
});
$(window).load(function(){
	$(document).foundation();
});