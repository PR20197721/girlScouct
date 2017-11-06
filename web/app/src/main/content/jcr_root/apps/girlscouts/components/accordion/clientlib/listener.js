var deleteAccordionFlag = false;
(function (document, $, ns) {
    "use strict";
    $(document).on("coral-component:attached", "[name='idField']", function (e) {
		var id = $(this).val();
        if(id == null || id == ""){
            var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
            for( var i=0; i < 8; i++ ){
                id += possible.charAt(Math.floor(Math.random() * possible.length));
            }
			$(this).val(id);
        }
    });
    $(document).on("coral-component:attached", "[is='coral-button'][handle='remove']", function (e) {
        e.stopPropagation();
        e.stopImmediatePropagation();
        e.preventDefault();
        $(this).click(function(){
        	if(deleteAccordionFlag == false){
            	deleteAccordionFlag = true;
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
    });
})(document, Granite.$, Granite.author);