(function (document, Granite, $) {
	$(document).on("coral-overlay:open", function(overlayEvent){
		var $multifield = $("coral-dialog-content").find("coral-multifield[data-granite-coral-multifield-name='./slides']");
		$multifield.find("button[handle='remove']").each(function() {
    		setRemoveButtonMessage(this);
    	});
		$multifield.on("coral-component:attached", "button[handle='remove']", function (buttonEvent) {   
    		setRemoveButtonMessage(this);
        });	
		$multifield.on("coral-component:attached", "coral-fileupload", function (fileUploadEvent) { 
			if($(fileUploadEvent.target).is("coral-fileupload")){
				$(fileUploadEvent.target).attr("data-cq-is-new","true");	
			}
        });			
	});
	function setRemoveButtonMessage(el){
		$(el).on('mousedown', function(e){
			e.stopPropagation();
			e.preventDefault();
    		generateWarning(el);
    	});
    }
	function generateWarning(el){
		if($('#unpublish_warning').length == 0){
	    	var dialog = new Coral.Dialog().set({
	    	    id: 'unpublish_warning',
	    	    backdrop:'static',
	    	    variant: 'warning',
	    	    header: {
	    	      innerHTML: 'Delete Banner'
	    	    },
	    	    content: {
	    	      innerHTML: '<p>Are you sure you want to delete this banner?</p>'
	    	    },
	    	    footer: {
	    	      innerHTML: '<button id="removeYesButton" is="coral-button" variant="warning">Delete</button><button is="coral-button" variant="warning" coral-close>Cancel</button>'
	    	    }
	    	});
	    	dialog.on('coral-overlay:close', function(event) {
	    		dialog.remove();
	    	});
	    	dialog.on('click', '#removeYesButton', function() {	   				
	    		$(el).closest("coral-multifield-item").remove();
	    		dialog.remove();
	    	});
	    	document.body.appendChild(dialog);
	    	dialog.show();
		}
    }
})(document, Granite, Granite.$);
