(function (document, $, ns) { 
    "use strict";
    //validation check
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
    var names = new Array();
    var items = new Array();
    var files = new Array();
    function showDialog(el, w, l){
        //Parse element information for error message
        var url = el.find("input[name='fileReference']").val();
        var name = el.attr("name");
        name = name.substring(0, name.indexOf("/"));

        //find element slide number
        var slide = el.closest("div.heroBannerElementConfigContents").find("div.coral-TabPanel-pane.coral-FixedColumn.foundation-layout-util-vmargin.u-coral-noPadding.u-coral-noMargin").find("div.cq-FileUpload-thumbnail-img").children().attr("src");
		slide = slide.substring(slide.indexOf("item")+4, slide.indexOf("item")+5);
		var item = parseInt(slide) + 1;

		files.push(url);
        items.push(item);
        names.push(name);
        var printName = "";
        for(var i = 0; i < names.length; i++){
            printName = printName + "The " + names[i] + " asset size on slide element "+ items[i] +" is invalid, your dimensions are: "+w+" x "+l+":<ul><li>"+files[i]+"</li></ul>";
        }
        names.forEach(function(item){

        });

		var message;

        if(springPosition == "right"){
			message = printName + "It is detected that you are using the wrong sized image(s). For design options with the springboards under hero, use 655 x 360 for regular and medium, 500 x 655 for small.";
        } else{
			message = printName + "It is detected that you are using the wrong sized image(s). For design options with the springboards under hero, use 960 x 420 for regular and medium, 500 x 655 for small.";
        }
        //Query for dialog to edit message if exists
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
              files = [];
              names = [];
              items = [];
              dialog.hide();
              dialog.remove();
         	 });
         	document.body.appendChild(dialog);
          	dialog.show();
        }
    }
    $(document).on("assetselected", function(e){
        var url = e.path +"/jcr:content/metadata.1.json";

        //get image dimensions, and check with constraints
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