(function (document, Granite, $) {
	$(document).on("dialog-ready", function(e){
		generateWarning("test");
	});
	function generateWarning(path){
    	var dialog = new Coral.Dialog().set({
    	    id: 'unpublish_warning',
    	    backdrop:'static',
    	    variant: 'error',
    	    header: {
    	      innerHTML: 'Warning!'
    	    },
    	    content: {
    	      innerHTML: '<pAre you sure?</p>'
    	    },
    	    footer: {
    	      innerHTML: '<button is="coral-button" variant="warning" coral-close>Yes</button><button is="coral-button" variant="warning" coral-close>No</button>'
    	    }
    	});
    	dialog.on('coral-overlay:close', function(event) {
    		dialog.remove();
    		//$(".pageinfo-unpublish-confirm")[0].show();
    	});
    	document.body.appendChild(dialog);
    	dialog.show();
    }
})(document, Granite, Granite.$);
