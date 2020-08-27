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
        
    });

    // email-signup
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: "input[name='./source']",
  		validate: function(el) {
  			
  			var source = $(el).val().trim();
  			var $form = $(el).closest("form.foundation-form");
  			var $sourceoption = $form.find("[name='./sourceoption']");
  			
  			if ($sourceoption.val() == "pair" && source == "") {
  				return "Please enter source field name";
  			}
  			
  		},
  		show: function (el, message) {
  		    showError(el, message);
  		},
  		clear: function (el) {
  			clearError(el);
  		}
	});
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: "input[name='./sourcevalue']",
  		validate: function(el) {
  			
  			var sourcevalue = $(el).val().trim();
  			var $form = $(el).closest("form.foundation-form");
  			var $sourceoption = $form.find("[name='./sourceoption']");
  			
  			if ($sourceoption.val() == "pair" && sourcevalue == "") {
  				return "Please enter source field value";
  			}
  			
  		},
  		show: function (el, message) {
  		    showError(el, message);
  		},
  		clear: function (el) {
  			clearError(el);
  		}
	});
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: "input[name='./triggersendkey']",
  		validate: function(el) {
  			
  			var tskey = $(el).val().trim();
  			var $form = $(el).closest("form.foundation-form");
  			var $sfmc = $form.find("[name='./sfmc']");
  			
  			if ($sfmc.val() == "ts" && tskey == "") {
  				return "Please enter Triggered Send Key";
  			}
  		},
  		show: function (el, message) {
  		    showError(el, message);
  		},
  		clear: function (el) {
  			clearError(el);
  		}
	});
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: "input[name='./dataextensionkey']",
  		validate: function(el) {
  			
  			var dataextensionkey = $(el).val().trim();
  			var $form = $(el).closest("form.foundation-form");
  			var $sfmc = $form.find("[name='./sfmc']");
  			
  			if ($sfmc.val() == "de" && dataextensionkey == "") {
  				return "Please enter Data Extension Key";
  			}
  		},
  		show: function (el, message) {
  		    showError(el, message);
  		},
  		clear: function (el) {
  			clearError(el);
  		}
	});
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: "input[name='./dataextensionemail']",
  		validate: function(el) {
  			
  			var dataextensionemail = $(el).val().trim();
  			var $form = $(el).closest("form.foundation-form");
  			var $sfmc = $form.find("[name='./sfmc']");
  			
  			if ($sfmc.val() == "de" && dataextensionemail == "") {
  				return "Please enter Data Extension Email";
  			}
  		},
  		show: function (el, message) {
  		    showError(el, message);
  		},
  		clear: function (el) {
  			clearError(el);
  		}
	});
    
    
    function showError(el, message) {
		var fieldErrorEl,
		    field,
		    error,
		    arrow;
		
		var $form = $(el).closest("form.foundation-form");
		var $sfmctab = $form.find("a.coral-TabPanel-tab:contains('SFMC')");
		$sfmctab.addClass("is-invalid", true);
		
		fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");
		field = el.closest(".coral-Form-field");
		
		$(field).attr("aria-invalid", "true")
		  .toggleClass("is-invalid", true);
		
		$(field).nextAll(".coral-Form-fieldinfo")
		  .addClass("u-coral-screenReaderOnly");
		
		error = $(field).nextAll(".coral-Form-fielderror");
		
		if (error.length === 0) {
		  arrow = $(field).closest("form").hasClass("coral-Form--vertical") ? "right" : "top";
		
		  fieldErrorEl
		    .attr("data-quicktip-arrow", arrow)
		.attr("data-quicktip-content", message)
		    .insertAfter($(field));
		} else {
		  error.data("quicktipContent", message);
		}
	}
    function clearError(el) {
    	var field = el.closest(".coral-Form-field");
    	
			$(field).removeAttr("aria-invalid").removeClass("is-invalid");

			$(field).nextAll(".coral-Form-fielderror").tooltip("hide").remove();
			$(field).nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
    }
    
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
    
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
  		selector: "[data-validationpattern]",
  		validate: function(el) {
    		var value = el.value;
            var validation = $(el).data("validationpattern");
            var isVisible = $(el).is(":visible");
            var patterns = {
                numeric: 	/^(\d)+(-(\d)+)*$/,
                string:		/^.+$/i
            };

            if (isVisible) {

                switch (validation) {
                    case 'numeric':
                        console.log("testing against numeric");
                        if (!patterns["numeric"].test(value)) {
                            return "Please input numerics";
                        }
                        break;
                    case 'string':
                    default:
                        console.log("testing against string or default");
                        if (!value) {
                            return "Please enter alphanumeric value";
                        }
                        else if (!patterns["string"].test(value)) {
                            return "Please input string.";
                        }
                        break;

                }
            }
  		}
	});
    
})(document, Granite.$, Granite.author);
      