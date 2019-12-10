(function ($, ns, channel, window, undefined) {
    "use strict";
    var is_safari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
    if(is_safari){
        $(window).bind("pageshow", function(event) {
            if (event.originalEvent.persisted) {                
                window.location.reload(); 
            }
        });    	
    }    
}(jQuery, Granite.author, jQuery(document), this));