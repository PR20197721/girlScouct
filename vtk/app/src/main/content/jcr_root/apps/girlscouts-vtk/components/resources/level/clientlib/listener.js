(function (document, $, ns) {
    "use strict"; 
    var myObserver;
    $(document).on("dialog-ready", function() {
    	 try {
 	    	if("girlscouts-vtk/components/resources/level" == ns.DialogFrame.currentDialog.editable.type){  
 	    		if ((ns.ContentFrame.contentURL.indexOf('/content/vtkcontent/') == -1)) {  	
 	    			var title = ns.DialogFrame.currentFloatingDialog.find(".coral-Textfield[name='./title']")[0];
 	    			$(title).prop('disabled', true);
	    			var $typeSelects = ns.DialogFrame.currentFloatingDialog.find("coral-select[name='type']");
 	    			$typeSelects.each(function(index){
 	    				removeAdminOptions(this); 	    					    				
 	    			});
 	    	    }  
 	        	var $multifield = ns.DialogFrame.currentFloatingDialog.find("coral-multifield[data-granite-coral-multifield-name='./items']");  
        		var myObserver = new MutationObserver (mutationHandler);
 	   	 		var obsConfig = { childList: true, characterData: true, attributes: true, subtree: true };
 	   	 		myObserver.observe ($multifield[0], obsConfig);
 	    	}
    	 } catch(err) {
    		 console.log(err);
    	 } 	        
    });
    $(document).on("dialog-closed", function() {
   	 	try{
   	 		observer.disconnect();
   	 	}catch(e){
   	 	}
   	});
    function mutationHandler (mutationRecords) {
        mutationRecords.forEach(function (mutation) {        	
	        if("childList" == mutation.type){
	    		var $addedNodes = $(mutation.addedNodes[0]);
	    		if($addedNodes.is("coral-multifield-item")){
	    			$addedNodes.find("input[type='hidden'][name='id']").each(function(index, item){
	    				setId(item);
	    			}); 
	    			if((ns.ContentFrame.contentURL.indexOf('/content/vtkcontent/') == -1)){
		    			$addedNodes.find("coral-select[name='type']").each(function(index, item){
		    				removeAdminOptions(item);    				
		    			}); 
	    			}
	        	}
	        }
        });
    }
    function setId(el){
    	var $el = $(el);
    	var id = $el.val();
    	if(id == null || id == ""){
            var id = "vtkrtid" + (new Date()).getTime() + Math.floor(Math.random() * Math.floor(999));
            $(el).val(id);
        }
    }
    function removeAdminOptions(el){
    	var select = $(el);
    	var items = select.find("coral-select-item");
		items.each(function(index){
			if(this.value == "list" || this.value == "meeting-overview"){
				this.remove();
			} 	    					
		}); 
    }            
})(document, Granite.$, Granite.author);
$.validator.register({
	  selector: ".coral-Form-field[name='uri']",
	  validate: function(el) {
	    var field,
	        value;

	    var multiFieldItem = el.closest("coral-multifield-item-content");
	    var type = multiFieldItem.find("coral-select[name='type']")[0].value;
	    field = el.find("input");
	    value = el.val();

	    if ((type == 'pdf' || type == 'link' || type == 'video' || type == 'download') && (value == null || value.trim() == "" )) {
	      return Granite.I18n.get('Please fill out this field for '+type+' type.');
	    }
	  },
	  show: function (el, message) {
	    var fieldErrorEl,
	        field,
	        error,
	        arrow;

	    fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");
	    field = el.closest(".coral-Form-field");

	    field.attr("aria-invalid", "true")
	      .toggleClass("is-invalid", true);

	    field.nextAll(".coral-Form-fieldinfo")
	      .addClass("u-coral-screenReaderOnly");

	    error = field.nextAll(".coral-Form-fielderror");

	    if (error.length === 0) {
	      arrow = field.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

	      fieldErrorEl
	        .attr("data-quicktip-arrow", arrow)
	        .attr("data-quicktip-content", message)
	        .insertAfter(field);
	    } else {
	      error.data("quicktipContent", message);
	    }
	  },
	  clear: function (el) {
	    var field = el.closest(".coral-Form-field");

	    field.removeAttr("aria-invalid").removeClass("is-invalid");

	    field.nextAll(".coral-Form-fielderror").tooltip("hide").remove();
	    field.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
	  }
	});