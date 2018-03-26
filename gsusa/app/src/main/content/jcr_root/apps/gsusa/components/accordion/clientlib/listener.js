(function (document, $, ns) {
    "use strict";
    var DELETE_ACCORDION_FLAG = false;
    $(document).on("dialog-ready", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if("gsusa/components/accordion" == $resourceType){
        	DELETE_ACCORDION_FLAG = false;
        	var $multifield = $("coral-dialog-content").find("coral-multifield[data-granite-coral-multifield-name='./children']");
        	//handle existing items in multifield
        	$multifield.find("input[name='idField']").each(function() {
        		setAccordionIdentifier(this);
        	});
        	$multifield.find("button[handle='remove']").each(function() {
        		setRemoveButtonMessage(this);
        	});
        	//attach event handlers for future items in multifield
        	$multifield.on("coral-component:attached", "input[name='idField']", function (e) {
            	setAccordionIdentifier(this);
            });
        	$multifield.on("coral-component:attached", "button[handle='remove']", function (e) {            	
        		setRemoveButtonMessage(this);
            });
        }
    });
    function setAccordionIdentifier(el){
    	var id = $(el).val();
        if(id == null || id == ""){
            var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            for( var i=0; i < 8; i++ ){
                id += possible.charAt(Math.floor(Math.random() * possible.length));
            }
            $(el).val(id);
        }
    }
    function setRemoveButtonMessage(el){
    	$(el).click(function(){
        	if(DELETE_ACCORDION_FLAG == false){
        		DELETE_ACCORDION_FLAG = true;
                ns.ui.helpers.prompt({
                    title: Granite.I18n.get("Warning"),
                    message: "<div>If you delete this field, all content contained within will be removed as well.</div>" +
                            "<div>You can RESTORE the content at a later point IN THIS ACCORDION COMPONENT ONLY by using the same identifier.</div>" +
                            "<div>If you do not want to delete this field, click 'X' above to undo all changes</div>",
                    actions: [{
                            id: "OK",
                            text: "OK",
                            className: "coral-Button"
                    }]
                });
             }
        });
    }
})(document, Granite.$, Granite.author);

// Pass the cq-editables-loaded event to the main content frame for use in hiding / showing parsys.
(function(){
	$(document).on('cq-editables-loaded', function(editablesObject){ 
	    var contentWindow = $('#ContentFrame')[0].contentWindow;
	    contentWindow.$(contentWindow.document).trigger('cq-editables-loaded', editablesObject);
	});
})();


// Makes accordions usable during Author.
(function(){
	
	function fixAccordionOverlays(){
		var graniteEditables = Granite.author.editables;
        var editableArr = graniteEditables.filter((itm) => itm.type == 'gsusa/components/accordion');
        
        var allEditableDoms = $();
        var allEditableOverlays = $();
        for(var i = 0; i < editableArr.length; i++){
            var editable = editableArr[i];
            allEditableDoms = allEditableDoms.add($(editable.dom))
            allEditableOverlays = allEditableOverlays.add($(editable.overlay.dom));
        }
        var editableOverlaysAndParentsDoms = allEditableOverlays.add(allEditableOverlays.parents('[data-type="Editable"]'));
	    $('#OverlayWrapper').css({maxHeight: 1});
        
        var editableAndParents = graniteEditables.filter((itm) => editableOverlaysAndParentsDoms.toArray().filter(eapd => eapd == itm.overlay.dom[0]).length > 0);
        for(var i = 0; i < editableAndParents.length; i++){
            var editable = editableAndParents[i];
            if(!editable.getAreaOverride){
                editable.getAreaOverride = true;
                editable.getArea = function(){
                    
                    if (!this.onPage() ) {
                        return null;
                    }
                    
                    if (!this.dom[0].getBoundingClientRect) {
                        return {top: 0, left: 0, width: 0, height: 0};
                    }
                    
                    var rect = this.dom[0].getBoundingClientRect();
                    var finalWidth = rect.width - 50 < 0 ? 0 : rect.width - 70;
                    
                    return {
                        top: rect.top,
                        left: rect.left,
                        width: finalWidth,
                        height: rect.height
                    };
                }
            }
        }
        
        for(var i = 0; i < editableArr.length; i++){
            var editable = editableArr[i];
            var overlay = editable.overlay.dom;
            var overlayWidth = editable.dom.outerWidth(true);
            if(overlayWidth > 0){
                overlay.add(overlay.parents('[data-type="Editable"]').not(allEditableDoms)).css({width: (overlayWidth - 70) + 'px'});
            }
        }
	}

	// Check for new accordions as the page changes.
	$(document).on('cq-editables-loaded cq-editables-loaded cq-editor-loaded cq-layer-activated', function(){ 
		// Check to be sure we have all the dom objects.
		for(var editable of Granite.author.editables){
			try{
				if(!editable.dom || !editable.overlay || !editable.overlay.dom){
					return;
				}
			}catch(err){
				// Something didn't exist.
				return;
			}
		}
		
		// All the dom is present so we can fix the overlays now.
	    fixAccordionOverlays();
	});

})();
