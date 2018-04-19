(function (document, $, ns) {
    "use strict";    
    $(document).on("dialog-ready", function(event) {
	    try{
	    	if("foundation/components/form/tags" == ns.DialogFrame.currentDialog.editable.type){
	        	var jsonPath = ns.DialogFrame.currentDialog.editable.path+ '.namespacesoptions.json'        	        	
	        	var input = ns.DialogFrame.currentFloatingDialog.find("input[type=hidden][name='./namespaces-placeholder']");
	        	var nodeProperties = Granite.HTTP.eval(ns.DialogFrame.currentDialog.editable.path+".json");
	        	var namespaces = {};
        		if(nodeProperties.namespaces != null && typeof nodeProperties.namespaces != "undefined"){
        			namespaces=nodeProperties.namespaces;        	
            	}	        	
	    		var tagList = Granite.HTTP.eval(jsonPath);
	    		tagList.sort(function(tag1, tag2){
	    			var x = tag1.text.toLowerCase();
	    		    var y = tag2.text.toLowerCase();
	    		    if (x < y) {return -1;}
	    		    if (x > y) {return 1;}
	    		    return 0;
	    		});
	    		var checkboxes = new Array();
	    		tagList.forEach(function(tag, index) { 
	        		try {  
	        			var selected = $.inArray(tag.value, namespaces) > -1 ? true : false;
	        			var checkbox = new Coral.Checkbox().set({
	        				checked:selected,
	        				value: tag.value,
	        				name:"./namespaces",
	        				label:{
	        				    innerHTML: $("<p>").html(tag.text).text()
	        				}
	        			});
	        			checkboxes.push($("<div class=\"coral-Form-fieldwrapper\">").append(checkbox));        			
	                } catch(err){
	                	console.log(err);
	                }
	        	});
	    		input.after(checkboxes);
	        }
	    }catch(e){
	    	console.log(e);
	    }
    });
})(document, Granite.$, Granite.author);
