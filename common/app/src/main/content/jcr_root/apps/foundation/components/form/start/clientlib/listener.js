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

    $(document).on("dialog-ready", function () {
        $("[name='./fileUploadSizeLimit']").keydown(function (e) {
            if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
                return false;
            }
    
        });
    });

    $(document).on("click", ".cq-dialog-submit", function (e) {
        var message = null;

		var $form = $(this).closest("form.foundation-form");

        var actionType = $form.find("[name='./actionType']").val();

        var isMailAction = actionType === "girlscouts/components/form/actions/gsmail" || actionType === "girlscouts/components/form/actions/spiceworks";
        var isStoreAction = actionType === "girlscouts/components/form/actions/gsstore";
        var isWebToCaseAction = actionType === "girlscouts/components/form/actions/web-to-case";

		var $mailtoparent = $form.find("[data-granite-coral-multifield-name='./mailto']");


        var $contentPath = $form.find("[name='./action']");

        $contentPath = $contentPath.find("input");



        if(isStoreAction && $contentPath.val().trim() < 1){

			message = "Content path field is required."
        }


        if(isMailAction && !$mailtoparent.find("coral-multifield-item").length){
			message = "Mail to field is required and must contain at least one email address";
        }

        var patterns = {
             emailadd: /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i
        };


		var $mailto = $form.find("[name='./mailto']");
        var $cc = $form.find("[name='./cc']");
        var $bcc = $form.find("[name='./bcc']");
        var $mailFrom = $form.find("[name='./from']");
        var $confirmationBcc = $form.find("[name='./confirmationmailto']");
        var $confirmationFrom = $form.find("[name='./confirmationFrom']");

        if(isMailAction){
            $mailto.each(function(){
                var tempVal = $(this).val().trim();
                if(!patterns.emailadd.test(tempVal)){
                    message = "A Mail To email field is invalid please correct the error before proceeding";
                }
                if(tempVal.length < 1){
                    message = "A Mail To email field cannot be left empty please remove unused fields";
                }
            });
            $cc.each(function(){
                var tempVal = $(this).val().trim();
                if(!patterns.emailadd.test(tempVal)){
                    message = "A CC email field is invalid please correct the error before proceeding";
                }
                if(tempVal.length < 1){
                    message = "A CC To email field cannot be left empty please please remove unused fields";
                }
            });
            $bcc.each(function(){
                var tempVal = $(this).val().trim();
                if(!patterns.emailadd.test(tempVal)){
                    message = "A BCC To email field is invalid please correct the error before proceeding";
                }
                if(tempVal.length < 1){
                    message = "A BCC To email field cannot be left empty please remove unused fields";
                }
            });
            $mailFrom.each(function(){
                var tempVal = $(this).val().trim();
                if(!patterns.emailadd.test(tempVal)){
                    message = "A From Email email field is invalid please correct the error before proceeding";
                }
                if(tempVal.length < 1){
                    message = "A From Email email field cannot be left empty please remove unused fields";
                }
            });
        }
        if(isMailAction || isStoreAction || isWebToCaseAction){
			$confirmationBcc.each(function(){
                var tempVal = $(this).val().trim();
                if(!patterns.emailadd.test(tempVal)){
                    message = "A Confirmation BCC email field is invalid please correct the error before proceeding";
                }
                if(tempVal.length < 1){
                    message = "A Confirmation BCC email field cannot be left empty please remove unused fields";
                }
            });
            $confirmationFrom.each(function(){
                var tempVal = $(this).val().trim();
                if(tempVal.length !== 0){ 
                    if(!patterns.emailadd.test(tempVal)){
                        message = "A Confirmation From email field is invalid please correct the error before proceeding";
                    }
                }
            });
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



    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: ".gs-email-field",
  		validate: function(el) {
    		var emailContent = el.value;
			var emailPattern = /^([a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+(\.[a-z\d!#$%&'*+\-\/=?^_`{|}~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+)*|"((([ \t]*\r\n)?[ \t]+)?([\x01-\x08\x0b\x0c\x0e-\x1f\x7f\x21\x23-\x5b\x5d-\x7e\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|\\[\x01-\x09\x0b\x0c\x0d-\x7f\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))*(([ \t]*\r\n)?[ \t]+)?")@(([a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\d\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.)+([a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]|[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF][a-z\d\-._~\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]*[a-z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])\.?$/i;

            if(emailContent){
            	if(!emailPattern.test(emailContent)){
					return "Email field is invalid. Please correct the email address format";
            	}
            }
  		}
	});




    // when dialog gets injected
    $(document).on("foundation-contentloaded", function (e) {
        // if there is already an inital value make sure the according target element becomes visible
        showHideHandler($(".cq-dialog-dropdown-showhide", e.target));
    });

    $(document).on("dialog-ready", function() {

        var actionField = $("[name='./action']");
        actionField = actionField.find("input");
        var f = actionField.closest("form");
        var pageEditorPath = f.attr("data-cq-dialog-pageeditor");

        if(pageEditorPath && !actionField.val()){
            console.log("Inside if");
			actionField.val(pageEditorPath.replace("/editor.html/content", "/content/usergenerated").replace(".html", "") + "/");
			console.log("Set Val");	
        }

	});

    $(document).on("selected", ".cq-dialog-dropdown-showhide", function (e) {
        showHideHandler($(this));
    });



    function showHideHandler(el) {
        el.each(function (i, element) {
            console.log(element);
            if($(element).is("coral-select")) {
                // handle Coral3 base drop-down
                Coral.commons.ready(element, function (component) {
                    showHide(component, element);
                    component.on("change", function () {
                        showHide(component, element);
                    });
                });
            } else {
                // handle Coral2 based drop-down
                var component = $(element).data("select");
                if (component) {
                    showHide(component, element);
                }
            }
        })
    }

    function showHide(component, element) {
        // get the selector to find the target elements. its stored as data-.. attribute
        var target = $(element).data("cqDialogDropdownShowhideTarget");
        var $target = $(target);
        if (target) {
            var value;
            if (component.value) {
                value = component.value;
            } else {
                value = component.getValue();
            }


            value = value.replace(/^.*\/(.*)$/,"$1");

            // make sure all unselected target elements are hidden.
            $target.not(".hide").addClass("hide");

            // unhide the target element that contains the selected value as data-showhidetargetvalue attribute
            $target.each(function(index){
                if($(this).hasClass(value)){
					$(this).removeClass("hide");
                }

            });
        }
    }

})(document, Granite.$, Granite.author);