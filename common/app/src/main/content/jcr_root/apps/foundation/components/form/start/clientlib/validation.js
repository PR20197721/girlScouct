(function($, window, document) {

    /* Adapting window object to foundation-registry */
    var registry = $(window).adaptTo("foundation-registry");

    /*Validator for TextField - Any Custom logic can go inside validate function - starts */
    registry.register("foundation.validation.validator", {

        selector: "[data-validation=email-validate]",
        validate: function(el) {
            var element = $(el);
            var value = element.val();
            if (!element.parents('.actionType-showhide-target.coral-Form-fieldset').hasClass('hide')) {
                if (value.length == 0) {
                    if (!element.hasClass('coral3-Multifield')) {
                    	return "Please enter the email";
                    } else if (element.children('coral-multifield-item').length == 0) {
						return "Please enter the email";
                    }
                } else {
                    var patterns = {
                        email: /((([a-zA-Z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-zA-Z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-zA-Z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-zA-Z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-zA-Z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-zA-Z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-zA-Z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-zA-Z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-zA-Z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-zA-Z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?/
                    }
                    if (patterns["email"]) {
                        error = !patterns["email"].test(value);
                    } else {
                        error = !(new RegExp("email")).test(value);
                    }
                    if (error) {
                        return "The field must match the pattern: email";
                    }
                }
            }
        }
    });
})
($, window, document);