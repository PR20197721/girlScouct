(function (document, Granite, $) {
    "use strict";
    var re = new RegExp("^(\/content\/.*\/en)$");
    
    $(document).on('click', 'button[value*=Deactivate]', function() {
    	var path = getUrlParameter("item");    	    		
	    generateWarning(path);
    });       
    	    
    $(document).on('mousedown', 'button.pageinfo-unpublish-activator', function(e) {
    	var path = $(this).data("path"); 
    	generateWarning(path);
    });
    function getUrlParameter(sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;
        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');
            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }        
    };
    function generateWarning(path){
    	if(re.test(path)){
	    	var dialog = new Coral.Dialog().set({
	    	    id: 'unpublish_warning',
	    	    backdrop:'static',
	    	    variant: 'error',
	    	    header: {
	    	      innerHTML: 'Warning!'
	    	    },
	    	    content: {
	    	      innerHTML: '<p>Unpublishing this page ('+path+') will also unpublish all other pages on your site.</p>'
	    	    },
	    	    footer: {
	    	      innerHTML: '<button is="coral-button" variant="warning" coral-close>Ok</button>'
	    	    }
	    	  });
	    	dialog.on('coral-overlay:close', function(event) {
	    		dialog.remove();
	    		$(".pageinfo-unpublish-confirm")[0].show();
	    	});
	    	document.body.appendChild(dialog);
	    	dialog.show();
    	}
    }
})(document, Granite, Granite.$);