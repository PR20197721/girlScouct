(function (document, Granite, $) {
    "use strict";
   
    $(document).on("foundation-contentloaded", function(e){
        var deleteActivator = ".cq-damadmin-admin-actions-delete-activator";
        $(document).on("mousedown", deleteActivator, function(e) {
            var activator = $(this);
            var type = "asset";
            if (activator.data("type")) {
                type = activator.data("type");
            }
            showDialog(activator, type);
        });
   
	    function showDialog(activator, type){
	    	var selectedItems = $(".foundation-selections-item");
	    	var folderList = "";
	    	selectedItems.each(function () {
	    		var type = $(this).find("div.foundation-collection-assets-meta").attr("data-foundation-collection-meta-type");
	    		if("directory"==type){
	    			folderList+="<li>"+$(this).attr('data-foundation-collection-item-id')+"</li>";
	    		}    		
	    	});
	    	if(folderList.trim() != ""){
		    	var dialog = new Coral.Dialog().set({
		    	    id: 'unpublish_warning',
		    	    backdrop:'static',
		    	    variant: 'error',
		    	    header: {
		    	      innerHTML: 'Warning!'
		    	    },
		    	    content: {
		    	      innerHTML: '<p>Are you sure you want to delete?</p><p>You will lose all of the assets stored in:</p><ul>'+folderList+'</ul>'
		    	    }
		    	  });
		    	var footer = dialog.querySelector('coral-dialog-footer');
		    	var cancel = new Coral.Button();
		        cancel.label.textContent = Granite.I18n.get('Cancel');
		        footer.appendChild(cancel).on('click', function (){
		        	dialog.hide();
		    		dialog.remove();
		        });
		        var deleteButton = new Coral.Button();
		        deleteButton.label.textContent = Granite.I18n.get('Delete');
		        deleteButton.variant = 'primary';
		        footer.appendChild(deleteButton).on('click', function (){
		        	$(deleteActivator).trigger( "click" );
		            dialog.hide();
		            dialog.remove();
		        });
		    	document.body.appendChild(dialog);
		    	dialog.show();
	    	}
		}
    });
})(document, Granite, Granite.$);