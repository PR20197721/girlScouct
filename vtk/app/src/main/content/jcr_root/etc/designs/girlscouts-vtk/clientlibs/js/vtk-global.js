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

	//all function calls should go here
	  $(document).ready(function(){
	  	 $(document).foundation({
	  	   reveal : {
	  	     animation: 'fade'
	  	   },
	  	   // accordion: {
	      // 		callback : function (accordion) {
	      // 		}
    			// }
	  	 });
  	  $('.accordion dt h3').on('click', function() { 
  	  	var target = "#" + $(this).parent().data('target');
  	  	if(target).find('content').hasClass('active') {
  	  		$(target).slideUp();
  	  	}
  	  	$(target).slideDown();
  	  });

	  });
	  $(window).load(function(){
	  //  $("#yearPlanMeetings").load(loadMeetings());
	  })
 })($);