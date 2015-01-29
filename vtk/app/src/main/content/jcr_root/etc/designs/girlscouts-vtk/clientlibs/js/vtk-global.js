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
			if($('.accordion .accordion-navigation').children(':visible').length == 0) {
			 resizeWindow();
			}
		});
	}
		function modal_height_on_open() {
		  $(document).on('opened.fndtn.reveal', '[data-reveal]', function () {
				var window_h = $(window).height();
				var popup_h = (window_h - 50);
				$(this).find('.scroll').css('max-height' , ($(window).height()-50)+'px');
			});
			$('#gsModal').css({
  			'max-height': $(window).height()+'px !important',
  			'height': '100% !important',
  		});
		}
		function modal_height_resize() {
    	var window_h = $(window).height();
    	var popup_h = (window_h - 50);
  		$('.scroll').css('max-height' , ($(window).height()-50)+'px');
  		$('.modalWrap').css('max-height' , $(window).height()+'px');
			$('#gsModal').css({
  			'max-height': $(window).height()+'px !important',
  			'height': '100% !important',
  		});
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
	  	 select_tabs();
	  	 modal_height_on_open();
	  	 vtk_accordion();
	  	 validate_image();
  });
  $(window).resize(function() {
  	modal_height_resize()
  });
 })($);