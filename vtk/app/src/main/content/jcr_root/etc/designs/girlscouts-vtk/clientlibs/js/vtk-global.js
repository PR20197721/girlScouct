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
	  	   }
	  	 });
	  });
	  $(window).load(function(){
	    //$("#yearPlanMeetings").load(loadMeetings());
	  })
 })($);