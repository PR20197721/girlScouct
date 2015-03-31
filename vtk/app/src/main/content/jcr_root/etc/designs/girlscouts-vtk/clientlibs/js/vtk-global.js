var $ = jQuery.noConflict();
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
  function vtk_accordion() {
    $('.accordion dt > :first-child').on('click', function() {
      var target = $(this).parent().data('target');
      var toggle = $(this);
      $('#' + target).slideToggle('slow');
      $(toggle).toggleClass('on');
        return false;
    });
  }

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
		});
	}
	
	function modal_height_resize() {
  	var window_h = $(window).height();
  	var popup_h = (window_h - 75);
		$('.scroll').css('max-height' , popup_h + 'px');
		$('.modalWrap').css('max-height' , $(window).height()+'px');
	}

	function validate_image() {
		$('form#frmImg').submit(function(e) {
		   var $this = $(this);
		   var $input =  $this.find('input[type="file"]').val();
		 if($input == '') {
		   alert ("you must choose a image");
		   return false; 
		   e.preventDefault(); 
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
  function placeholder_IE9() {
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
//all function calls should go here
  $(document).ready(function() {
  	 $(document).foundation({
  	  reveal : {
  	     animation: 'fade',
  	     root_element: 'window',
  	     close_on_background_click: false,
  	     open: function () { 
  	     	$('body').css({'overflow':'hidden'});
  	     },
  	     close: function () {
  	     	$('body').css({'overflow':'inherit'})
  	     },
  	 	}
  	 });
  	 //select_tabs();
  	 modal_height_on_open();
  	 vtk_accordion();
  	 validate_image();
  	 resizeWindow();
  	 if($('.tabs dd').length == 6) {
  	 	$('.tabs dd').css('width','100%');
  	 } 	
  	 // if (navigator.userAgent.match(/msie/i) ) {
  	 //   // alert(navigator.userAgent.match(/msie/i));
  	 //   placeholder_IE9();
  	 //   $('select').css('background-image', 'none');
  	 // }
  });
	$(window).resize(function() {
		modal_height_resize()
		// if($(window).width() < 420) {
		// 	$('.vtk-body .reveal-modal').css('top','0');
		// }
	});

})($);
