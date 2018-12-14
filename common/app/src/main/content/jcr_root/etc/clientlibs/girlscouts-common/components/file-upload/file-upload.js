(function (document, Granite, $) {
    "use strict";
    var isJson;
    $(document).on('change','coral-chunkfileupload.coral-chunkfileupload', function(event, isJson) {
        event.stopPropagation();
        event.preventDefault();
        var fileNames = "";
        var baseUri = this.baseURI;
        var folderPath = baseUri.substring(baseUri.indexOf("/dam") + "/dam".length );
        var regex = new RegExp("^[^\.]+\.json$");
        $.each(this.uploadQueue,function(index, item){
            //if((item.name.endsWith(".json") && item.name.includes("."))){
            var isValid = regex.test(item.name);
            if(isValid){
                fileNames+=item.name;
            }
        });
        isJson = fileNames.length > 0 || folderPath.includes(".");
        if(isJson){
            var prevDialog = document.querySelector('#uploadListDialog');
            prevDialog.hide();
            prevDialog.remove();
			var dialog = new Coral.Dialog().set({
		    	    id: 'json_warning',
		    	    backdrop:'static',
		    	    variant: 'error',
		    	    header: {
		    	      innerHTML: 'Warning!'
		    	    },
		    	    content: {
		    	      innerHTML: baseUri+"/"+fileNames + " is invalid. Please remove all ' . 's from the file/folder name and replace with ' - ' before uploading"
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
})(document, Granite, Granite.$);