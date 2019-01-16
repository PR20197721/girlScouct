(function (document, $, ns) { 
    "use strict";
	jQuery.validator.register({
      selector: "coral-fileupload",
      validate: function(el) {
          console.log($(el));
          var url = el.find("input[name='fileReference']").val() +"/jcr:content/metadata.1.json";
          if(el.attr('isValidSize') == "false"){
              showDialog();
              return "invalid";
          }
      }
    });
    function isValid(el, url){
         $.getJSON(url, function (data) {
				try{
                    console.log(data);
                    var regWidth = data["tiff:ImageWidth"];
                    var regLength = data["tiff:ImageLength"];
                    var test = el.attr("name");
                    if(el.attr("name").includes("regular") || el.attr("name").includes("medium")){
						if(regWidth != 960 || regLength != 420){
                        	//dialog.show();
                        	//return false;
                            el.attr('isValidSize',false);
                    	}
                    }
                    else if(el.attr("name").includes("small")){
						if(regWidth != 500 || regLength != 655){
                        	//dialog.show();
                        	el.attr('isValidSize',false);
                    	}
                    }

                }catch(err){}
        	});
    }
    function showDialog(){
        var dialog = new Coral.Dialog().set({
              id: 'fileSize-warning',
              backdrop:'static',
              variant: 'error',
              header: {
                  innerHTML: 'Warning!'
              },
              content: {
                  innerHTML: "It is detected that you are using the wrong size image. For design options with the springboards under hero, use 960 x 420 for large and medium, 500 x 655 for small."
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
    $(document).on("assetselected", function(e){
       // isValid(e.target, e.path);
        var url = e.path +"/jcr:content/metadata.1.json";
         $.getJSON(url, function (data) {
				try{
                    console.log(data);
                    var regWidth = data["tiff:ImageWidth"];
                    var regLength = data["tiff:ImageLength"];
                    if($(e.target).attr("name").includes("regular") || $(e.target).attr("name").includes("medium")){
						if(regWidth != 960 || regLength != 420){
                        	//dialog.show();
                        	//return false;
                            $(e.target).attr('isValidSize',false);
                    	}
                    }
                    else if($(e.target).attr("name").includes("small")){
						if(regWidth != 500 || regLength != 655){
                        	//dialog.show();
                        	$(e.target).attr('isValidSize',false);
                    	}
                    }

                }catch(err){}
        	});



    });

})(document, Granite.$, Granite.author); 