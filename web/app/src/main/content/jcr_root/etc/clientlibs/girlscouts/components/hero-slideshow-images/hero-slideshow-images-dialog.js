(function (document, $, ns) { 
    "use strict";
	jQuery.validator.register({
      selector: "coral-fileupload",
      validate: function(el) {
          var url = el.find("input[name='fileReference']").val() +"/jcr:content/metadata.1.json";
          if(el.attr('isValidSize') == "false"){
              showDialog(el, width, length);
			  var name = el.attr("name");
              if(name.includes("regular") || name.includes("medium")){
                  if(springPosition == "right"){
						return "Size invalid. Correct dimensions: 655 x 360";
                  }else{
					    return "Size invalid. Correct dimensions: 960 x 420";
                  }
              }
              else if(name.includes("small")){
				return "Size invalid. Correct dimensions: 500 x 655";
              }
          }
      }
    });
	var width;
    var length;
    var springPosition;
    var files = "";
    function showDialog(el){
        var url = el.find("input[name='fileReference']").val();
        var name = el.attr("name");
        name = name.substring(0, name.indexOf("/"));
		var message;
		files = files + "<li>" + url + "</li>";
        if(springPosition == "right"){
			message = "The " + name + " asset size is invalid, your dimensions are: "+width+" x "+length+" <br><ul>"+files +  "</ul>It is detected that you are using the wrong size image. For design options with the springboards under hero, use 655 x 360 for regular and medium, 500 x 655 for small.";
        } else{
			message = "The " + name + " asset size is invalid, your dimensions are: "+width+" x "+length+" <br>"+files +  "It is detected that you are using the wrong size image. For design options with the springboards under hero, use 960 x 420 for regular and medium, 500 x 655 for small.";
        }
        var prevDialog = document.querySelector('#fileSize-warning');
        if(prevDialog != null){
            prevDialog.set({
                content: {
                  innerHTML: message
              }
            });
        } else{
        	var dialog = new Coral.Dialog().set({
              id: 'fileSize-warning',
              backdrop:'static',
              variant: 'error',
              header: {
                  innerHTML: 'Warning!'
              },
              content: {
                  innerHTML: message
              }
          	});
         	 var footer = dialog.querySelector('coral-dialog-footer');
         	 var okButton = new Coral.Button();
          	okButton.label.textContent = Granite.I18n.get('OK');
          	okButton.variant = 'primary';
          	footer.appendChild(okButton).on('click', function (){
              files = "";
              dialog.hide();
              dialog.remove();
         	 });
         	document.body.appendChild(dialog);
          	dialog.show();
        }
    }
    $(document).on("assetselected", function(e){
        var url = e.path +"/jcr:content/metadata.1.json";
         $.getJSON(url, function (data) {
				try{
                    var regWidth = data["tiff:ImageWidth"];
                    width = regWidth;
                    var regLength = data["tiff:ImageLength"];
                    length = regLength;
                    springPosition = document.getElementsByName("./spplacement")[0].value;
                    if($(e.target).attr("name").includes("regular") || $(e.target).attr("name").includes("medium")){
                        if(springPosition == "right"){
							if(regWidth != 655 || regLength != 360){
                            	$(e.target).attr('isValidSize',false);
                    		}
                        } else{
							if(regWidth != 960 || regLength != 420){
                            	$(e.target).attr('isValidSize',false);
                    		}
                        }
                    }
                    else if($(e.target).attr("name").includes("small")){
						if(regWidth != 500 || regLength != 655){
                        	$(e.target).attr('isValidSize',false);
                    	}
                    }
                }catch(err){}
        	});
    });
})(document, Granite.$, Granite.author);