(function (document, Granite, $) {
    "use strict";
    var isJson;
    $(document).on('change','coral-chunkfileupload.coral-chunkfileupload', function(event, isJson) {
        event.stopPropagation();
        event.preventDefault();
        var fileNames = "";
        $.each(this.uploadQueue,function(index, item){
            fileNames+=item.name;
        });
        isJson = fileNames.endsWith(".json") && fileNames.includes(".");
        if(isJson){
            var prevDialog = document.querySelector('#uploadListDialog');
            prevDialog.remove();
			var dialog = new Coral.Dialog().set({
		    	    id: 'json_warning',
		    	    backdrop:'static',
		    	    variant: 'error',
		    	    header: {
		    	      innerHTML: 'Warning!'
		    	    },
		    	    content: {
		    	      innerHTML: fileNames + " is invalid. Please remove all ' . 's from the file name before uploading"
		    	    }
		    	  });
		    	var footer = dialog.querySelector('coral-dialog-footer');
		        var okButton = new Coral.Button();
		        okButton.label.textContent = Granite.I18n.get('OK');
		        okButton.variant = 'primary';
		        footer.appendChild(okButton).on('click', function (){
		            dialog.hide();
		            dialog.remove();
		        });
		    	document.body.appendChild(dialog);
		    	dialog.show();
        }

    });
    $(document).on('coral-overlay:open','#json_warning', function(event, isJson) {
        var dialog = document.querySelector('#uploadListDialog');
        $(document.querySelector('#uploadListDialog').footer).find("button.dam-asset-upload-button").hide()
        //dialog.footer.remove();
    });
})(document, Granite, Granite.$);