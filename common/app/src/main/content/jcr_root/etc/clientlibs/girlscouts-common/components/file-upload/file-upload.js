(function (document, Granite, $) {
    "use strict";
    $(document).on('change','coral-chunkfileupload.coral-chunkfileupload', function(event) {
        event.stopPropagation();
        event.preventDefault();
        var fileNames = [];
        var baseUri = this.baseURI;
        var regex = new RegExp("((?=(.+\.json))(^[^\.]+\.json$))|((?!(.+\.json))(^.+$))");
        $.each(this.uploadQueue,function(index, item){
            if(!regex.test(item.name)){
                fileNames.push(item.name);
            }
        });

        var folderRegex = new RegExp("^.+\/dam[^\.]+$");
        var folderPath = false;
        if(!baseUri.endsWith("/dam") && folderRegex.test(baseUri)){
			folderPath = true;
        }
        if(fileNames.length > 0 || !folderPath){
            var printString = "";
            if(fileNames.length > 0){
                for(var i = 0; i<fileNames.length; i++){
                    printString = printString + baseUri+"/"+fileNames[i] + " is invalid. Please remove all ' . 's from the file/folder name and replace with ' - ' before uploading";
            	    printString = printString + "<br>";
                }
            } else{
				printString = baseUri + " Folder name is invalid, please remove all ' . 's from the folder name and replace with ' - '";
            }

            var prevDialog = document.querySelector('#uploadListDialog');
            prevDialog.hide();
			var dialog = new Coral.Dialog().set({
		    	    id: 'json_warning',
		    	    backdrop:'static',
		    	    variant: 'error',
		    	    header: {
		    	      innerHTML: 'Warning!'
		    	    },
		    	    content: {
		    	      innerHTML: printString
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