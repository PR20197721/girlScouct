$(document).ready(function () {
    var form = $('form#web-to-case');
    var isAuthor = $("input[name='isAuthor']").val();

    if (isAuthor == "true") {
        checkFormConfiguration(form);
    }
    form.find("input[type='submit']").click(form, function (e) {
        e.stopPropagation();
        validateAndSubmit(form);
        return false;
    });

    function validateAndSubmit(form) {
        try {
            var formValCollection = new Array();
            var formValues = form.serialize();
            if(formValues != null){
                var formValuesArr = formValues.split("&");
                for(var i=0; i<formValuesArr.length; i++){
                    var field = formValuesArr[i].split("=");
                    formValCollection.push({name:field[0], value:field[1]});
                }
            }
            var dataValidationErrors = [];
            //Always required
            if (!isValidFieldValue(formValCollection, "email")) {
                dataValidationErrors.push("Missing value for required field: email");
            }
            if (!isValidFieldValue(formValCollection, "name")) {
                dataValidationErrors.push("Missing value for required field: name");
            }
            if (!isValidFieldValue(formValCollection, "phone")) {
                dataValidationErrors.push("Missing value for required field: phone");
            }
            if (!isValidFieldValue(formValCollection, "type")) {
                dataValidationErrors.push("Missing value for required field: type");
            }
            if (!isValidFieldValue(formValCollection, "subject")) {
                dataValidationErrors.push("Missing value for required field: subject");
            }
            if (!isValidFieldValue(formValCollection, "description")) {
                dataValidationErrors.push("Missing value for required field: description");
            }
            if (!isValidFieldValue(formValCollection, "g-recaptcha-response")) {
                dataValidationErrors.push("Missing value for required field: Google reCAPTCHA");
            }
            if (dataValidationErrors.length == 0) {
                submitForm(form);
            } else {
                displayErrors(dataValidationErrors, form);
            }
        } catch (err) {
            console.log(err);
        }
    }
    function isValidFieldValue(formValCollection, name){
        if(formValCollection != null){
            for(var i=0; i<formValCollection.length; i++){
                if(formValCollection[i].name == name){
                    return !isBlank(formValCollection[i].value);
                }
            }
        }
        return false;
    }
    function displayErrors(errors, form){
        var errorMessage = "";
        var formErrorTitle = form.find(".form_error_title");
        var formErrorContainer = form.find(".form-error-container");
        if(formErrorTitle.length == 0 ){
            errorMessage += "<p class=\"form_error form_error_title\">Please correct the errors and submit your information again.</p>";
        }
        for (var i = 0; i < errors.length; i++) {
            console.log(errors[i]);
            errorMessage += "<div class=\"form_row form_error\">" + errors[i] + "</div>";
        }

        $(formErrorContainer[0]).html(errorMessage);

    }
    function checkFormConfiguration(form) {
        var emailField = form.find("input[name='email']");
        var nameField = form.find("input[name='name']");
        var phoneField = form.find("input[name='phone']");
        var typeField = form.find("select[name='type']");
        var subjectField = form.find("input[name='subject']");
        var descriptionField = form.find("textarea[name='description']");
        var g_recaptcha_response = form.find("div[name=':g-recaptcha-response']");

        var formErrors = [];
        if (emailField.length == 0) {
            formErrors.push("email");
        }
        if (nameField.length == 0) {
            formErrors.push("name");
        }
        if (phoneField.length == 0) {
            formErrors.push("phone");
        }
        if (typeField.length == 0) {
            formErrors.push("type");
        }
        if (subjectField.length == 0) {
            formErrors.push("subject");
        }
        if (descriptionField.length == 0) {
            formErrors.push("description");
        }
        if (g_recaptcha_response.length == 0) {
            formErrors.push("Google reCAPTCHA");
        }

        if (formErrors.length > 0) {
            var errorMessage = "<div>Missing requires fields with following names:</div>";
            errorMessage += "<ul>";
            for (var i = 0; i < formErrors.length; i++) {
                errorMessage += "<li> " + formErrors[i] + "</li>";
            }
            errorMessage += "</ul>";
            $("#validation-errors").html(errorMessage);
        }
    }

    function submitForm(form) {
        var action = form.attr("action");
        var formId = form.attr("id");
        action = action.replace(".html", ".webtocase.html");
        $.post(action, form.serialize())
            .success(function (response, status, xhr) {
                var ct = xhr.getResponseHeader("content-type") || "";
                if (ct.indexOf('html') > -1) {
                    var responseForm = $(response).find("#" + formId);
                    if (responseForm != null && responseForm.length > 0) {
                        form.html($(responseForm[0]).html());
                        if (isAuthor == "true") {
                            checkFormConfiguration(form);
                        }
                    }
                }
                if (ct.indexOf('json') > -1) {
                    if (response.status == "success") {
                        console.log("form submitted successfully");
                        var redirect = form.find("input[name=':gsredirect']");
                        if (redirect != null && redirect.length > 0) {
                            console.log("redirect to " + $(redirect).val());
                            window.location.href=$(redirect).val();
                        }
                    } else {
                        var errors = response.errors;
                        displayErrors(errors, form);
                    }
                }

            })
            .fail(function () {
                $("#validation-errors").html("Unexpected error occurred.");
            });
    }

    function isBlank(val) {
        return val == null || val == "" || val.trim() == "";
    }
    function getUrlParam(name){
        try {
            var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
            return results[1] || 0;
        }catch(error){

        }
        return "";
    }
    function getFormFieldValue(form, name) {
        var value = "";
        var field = form.find("input[name='" + name + "']");
        if (field != null && field.length > 0) {
            value = $(field[0]).val();
        }
        return value;
    }
});
