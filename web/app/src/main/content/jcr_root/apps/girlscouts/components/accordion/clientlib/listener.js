(function (document, $, ns) {
    "use strict";
    var DELETE_ACCORDION_FLAG = false;
    $(document).on("dialog-ready", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if("girlscouts/components/accordion" == $resourceType){
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
            el.val(id);
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
