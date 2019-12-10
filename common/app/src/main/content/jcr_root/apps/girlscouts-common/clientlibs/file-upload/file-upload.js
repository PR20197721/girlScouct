(function (document, Granite, $) {
    "use strict";
    $(document).on('change','coral-chunkfileupload.coral-chunkfileupload', function(event) {
        event.stopPropagation();
        event.preventDefault();
        var fileNames = [];
        var baseUri = this.baseURI;
        var regex = new RegExp("^(.+[\.].+\.json)$");
        $.each(this.uploadQueue,function(index, item){
            if(regex.test(item.name)){
                fileNames.push(item.name);
            }
        });

        var folderRegex = new RegExp("^.+\/dam.*[\.].+$");
        if(fileNames.length > 0 || folderRegex.test(baseUri)){
            var printString = "";
            if(fileNames.length > 0){
                printString = "File name(s) invalid. Please remove all ' . 's from the file name:";
                printString = printString + "<br><ul>"
                for(var i = 0; i<fileNames.length; i++){
                    printString = printString + "<li>"+fileNames[i];
            	    printString = printString + "</li>";
                }
                printString = printString + "</ul>";
            } else{
				printString = "Folder name invalid. Please remove all ' . 's from the folder name:";
                printString = printString + "<br><ul>"
                printString = printString + "<li>" + baseUri + "</li>";
                printString = printString + "</ul>";

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