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
		function modal_height() {
	  	var window_h = $(window).height();
	  	// var popup_top = $(window_h -$('.reveal-modal').height()/2);
	  	var popup_h = (window_h - 50);
			$('.reveal-modal').css('top', $(window).height()-$('.reveal-modal').height/2);
			$('.reveal-modal').css('max-height' , window_h + 'px');
			//$('.scroll').css('max-height' , popup_h +' px');
			$('.scroll').css('max-height' , ($(window).height()-50)+'px');
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
	//all function calls should go here
	  $(document).ready(function() {
	  	$('body').css('overflow','auto');
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
	  	 modal_height();
	  	 vtk_accordion();
	  	 validate_image();
				$(document).on('opened.fndtn.reveal', '[data-reveal]', function () {
					var window_h = $(window).height();
					var popup_h = (window_h - 50);
					$(this).find('.scroll').css('max-height' , ($(window).height()-50)+'px');
				});
  });
	  $(window).resize(function() {
	  	modal_height();
	  });
 })($);