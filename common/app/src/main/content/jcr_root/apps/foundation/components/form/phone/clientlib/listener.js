/**
 * Extension to the standard dropdown/select component. It enabled hidding/unhidding of other components based on the
 * selection made in the dropdown/select.
 *
 * How to use:
 *
 * - add the class cq-dialog-dropdown-showhide to the dropdown/select element
 * - add the data attribute cq-dialog-dropdown-showhide-target to the dropdown/select element, value should be the
 *   selector, usually a specific class name, to find all possible target elements that can be shown/hidden.
 * - add the target class to each target component that can be shown/hidden
 * - add the class hidden to each target component to make them initially hidden
 * - add the attribute showhidetargetvalue to each target component, the value should equal the value of the select
 *   option that will unhide this element.
 */
(function (document, $, ns) {
    "use strict";
    $(document).on("click", ".cq-dialog-submit", function (e) {
        var message = null;
		var $form = $(this).closest("form.foundation-form");
        var elName = $form.find("[name='./name']").val();
        if(elName.indexOf(' ') >= 0){
			message = "Element Name must not contain spaces";
        }
        if(message) {
        		e.stopPropagation();
            	e.preventDefault();
            	ns.ui.helpers.prompt({
		        title: Granite.I18n.get("Invalid Input"),
			    message: message,
			    actions: [{
			        id: "CANCEL",
			        text: "OK",
			        className: "coral-Button"
			    }],
				callback: function (actionId) {
				    if (actionId === "CANCEL") {
				    }
				}
			 });
        }
    });


})(document, Granite.$, Granite.author);