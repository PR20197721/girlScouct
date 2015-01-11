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
	//all function calls should go here
	  $(document).ready(function() {
	  	 $(document).foundation({
	  	   reveal : {
	  	     animation: 'fade'
	  	   },
	  	 });
	  	 vtk_accordion();
  });
 })($);