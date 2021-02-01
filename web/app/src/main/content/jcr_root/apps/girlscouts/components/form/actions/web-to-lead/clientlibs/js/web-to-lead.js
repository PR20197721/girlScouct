$(document).ready(function () {
    var form = $('form#web-to-lead');
    var isAuthor = $("input[name='isAuthor']").val();

    form.find("input[type='submit']").click(form, function (e) {
        e.stopPropagation();
        submitForm(form);
        document.body.scrollTop = 0; // For Safari
        document.documentElement.scrollTop = 0; // For Chrome, Firefox, IE and Opera
        return false;
    });
    setUTMParamValues(form);




    function setUTMParamValues(form) {
        var utm_campaign = getUrlParam("utm_campaign");
        var utm_medium = getUrlParam("utm_medium");
        var utm_source = getUrlParam("utm_source");
        if(!isBlank(utm_campaign)){
            var utmCampaignField = form.find("input[name='UTM_Campaign']");
            if(utmCampaignField){
                $(utmCampaignField[0]).val(utm_campaign);
            }
        }
        if(!isBlank(utm_medium)){
            var utmMediumField = form.find("input[name='UTM_Medium']");
            if(utmMediumField){
                $(utmMediumField[0]).val(utm_medium);
            }
        }
        if(!isBlank(utm_source)){
            var utmSourceField = form.find("input[name='UTM_Source']");
            if(utmSourceField){
                $(utmSourceField[0]).val(utm_source);
            }
        }
    }

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
    
    function checkFormConfiguration(form) {
        var leadTypeVal = getFormFieldValue(form, "LeadType");
        var emailField = form.find("input[name='Email']");
        var councilCode = getFormFieldValue(form, "CouncilCode");
        var formErrors = [];
        if (isBlank(leadTypeVal)) {
            formErrors.push("LeadType");
        }
        if (emailField.length == 0) {
            formErrors.push("Email");
        }
        if (isBlank(councilCode)) {
            formErrors.push("CouncilCode");
        }
        if (leadTypeVal == "DirectContact" || leadTypeVal == "General") {
            var zipCode = form.find("input[name='ZipCode']");
            var firstName = form.find("input[name='FirstName']");
            var lastName = form.find("input[name='LastName']");
            var campaignId = form.find("input[name='CampaignID']");
            if (zipCode.length == 0) {
                formErrors.push("ZipCode");
            }
            if (firstName.length == 0) {
                formErrors.push("FirstName");
            }
            if (lastName.length == 0) {
                formErrors.push("LastName");
            }
            if (campaignId.length == 0) {
                formErrors.push("CampaignID");
            }
        }
        if (formErrors.length > 0) {
            var errorMessage = "";
            if (!isBlank(leadTypeVal)) {
                errorMessage += "<div>Lead type " + leadTypeVal + " requires fields with following names:</div>";
            } else {
                errorMessage += "<div>Missing requires fields with following names:</div>";
            }
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
        action = action.replace(".html", ".webtolead.html");
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
            .fail(function (xhr, status, error) {
                $("#validation-errors").html(xhr.responseText);
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
