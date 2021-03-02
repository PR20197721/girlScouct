$(document).ready(function () {
    var form = $('form#web-to-case');

    form.find("input[type='submit']").click(form, function (e) {
        e.stopPropagation();
        submitForm(form);
        document.body.scrollTop = 0; // For Safari
        document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
        return false;
    });

    function displayErrors(errors, form){
        var errorMessage = "";
        form.find("div.form_row").find("div.form_error").each(function( index ) {
            if($(this).html() != null && $(this).html().trim().length > 0){
                $(this).html("").hide();
            }
        });
        var formErrorContainer = form.find("div.form-error-container");
        if(formErrorContainer.length > 0){
            $(formErrorContainer[0]).html("");
        }
        errorMessage += "<p class=\"form_error form_error_title\">Please correct the errors and submit your information again.</p>";
        for (var i = 0; i < errors.length; i++) {
            console.log(errors[i]);
            errorMessage += "<div class=\"form_row form_error\">" + errors[i] + "</div>";
        }

        $(formErrorContainer[0]).html(errorMessage);
        $("#recaptcha-error").hide();
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
                        $('html, body').animate({
                            scrollTop: $("#validation-errors").offset().top
                        }, 2000);
                    }
                }

            })
            .fail(function (xhr, status, error) {
                $("#validation-errors").html(xhr.responseText);
                //GSAWDO-162-form-scroll-to-error starts
                $('html, body').animate({
                    scrollTop: $("#validation-errors").offset().top
                }, 2000);
                //GSAWDO-162-form-scroll-to-error ends
            });
    }

});
