/*
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 * Copyright 2012 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
jQuery(function($){

    // disable 403 handler in granite
    $.ajaxSetup({
        statusCode: {
            403: $.noop
        }
    });

    function flushError() {
        // adds the class to hide the alert
        $('#error').addClass('hidden');
        // clears the text
        $('#error > .coral-Alert-message').text('');
    }

    function displayError(message) {
        // removes the hidden class to show the component
        $('#error').removeClass('hidden');
        // adds the text inside the coral-Alert-message
        $('#error > .coral-Alert-message').text(message);
    }

    /**
     * Clear all password fields.
     */
    function clearPasswords(passwordPlaceholder) {
        if (passwordPlaceholder) {
            // empty field and change placeholder
            $("#password").attr("placeholder", passwordPlaceholder).val("");
            $("#password-label span").html(passwordPlaceholder);
        } else {
            // empty field
            $("#password").val("");
        }
        $("#new_password").val("");
        $("#confirm_password").val("");
    }

    /**
     * Show the change password form.
     */
    function showChangeForm() {
        displayError($("#expired_message").val());
        $(".sign-in-title").html($("#change_title").val());
        clearPasswords($("change_password_placeholder").val());
        $("#change_fields").removeClass("hidden");
        $("#submit-button").html($("#change_submit_text").val());
        $("#back-button").removeClass("hidden");
        $("#password").focus();
    }

    /**
     * Show the login form.
     */
    function showLoginForm() {
        $(".sign-in-title").html($("#login_title").val());
        clearPasswords($("#login_assword_placeholder").val());
        $("#change_fields").addClass("hidden");
        $("#submit-button").html($("#login_submit_text").val());
        $("#back-button").addClass("hidden");
        $("#username").focus();
        flushError();
    }

    /**
     * Show the modal after the password has been changed successfully.
     */
    function showSuccessModal() {
        if ($.browser.msie === true && $.browser.version < 9) {
            // IE 8 does not render the Coral modal properly: use native alert
            alert($("#success_modal .coral-Modal-body p").html());
            redirect();
        } else {
            var modal = $("#success_modal");
            $("#success_backdrop").removeClass("hidden");
            modal.show(                                                                )
                .css("position", "absolute")
                .css("top", $(window).height() / 2 - modal.outerHeight() / 2)
                .css("left", $(window).width() / 2 - modal.outerWidth() / 2)
                // find Ok and minimize button
                .find("button")
                .on("click", redirect)
                // filter OK button
                .filter(".coral-Button--primary")
                .focus();
        }
    }

    /**
     * Redirects after successful login or password change.
     */
    function redirect() {
        var u = $("#resource").val();
        if (window.location.hash && u.indexOf('#') < 0) {
            u = u + window.location.hash;
        }
        document.location = u;
    }

    // click listener of the "Back" in the change password form; returns to the login form
    $("#back-button").on("click", function(e) {
        showLoginForm();
        e.preventDefault();
    });


    // Bind an event listener on login form to make an ajax call
    $("#login").submit(function(event) {
        event.preventDefault();
        var form = this;
        var path = form.action;
        var user = form.j_username.value;
        var pass = form.j_password.value;

        // if no user is given, avoid login request
        if (!user) {
            return true;
        }

        var data = {
            _charset_: "utf-8",
            j_username: user,
            j_password: pass,
            j_validate: true
        };

        if ($("#change_fields").hasClass("hidden") === false) {
            // change password: check new and confirm passwords
            if ($("#new_password").val().length === 0) {
                // new password empty: error
                clearPasswords();
                $("#password").focus();
                displayError($("#empty_message").val());
                return false;
            } else if ($("#new_password").val() !== $("#confirm_password").val()) {
                // passwords do not match: error
                clearPasswords();
                $("#password").focus();
                displayError($("#not_match_message").val());
                return false;
            } else {
                // passwords match: add new password to data
                data["j_newpassword"] = $("#new_password").val();
            }
        }

        // send user/id password to check and persist
        $.ajax({
            url: path,
            type: "POST",
            async: false,
            global: false,
            dataType: "text",
            data: data,
            success: function (data, code, jqXHR){
                if ($("#change_fields").hasClass("hidden")) {
                    // login without changing password
                    redirect();
                } else {
                    // login after changing password: show success modal
                    showSuccessModal();
                }
            },
            error: function(jqXHR) {
                if (jqXHR.getResponseHeader("X-Reason-Code") === "password_expired") {
                    // password expired
                    showChangeForm();
                } else {
                    // login invalid
                    displayError($("#invalid_message").val());
                    clearPasswords();
                    $("#username").focus();
                }
            }
        });
        return true;
    });

    // workaround for typekit which takes away any focus
    var typekitTries = 5;
    var $html = $('html');
    
    function checkForTypekit() {
        if (!$html.hasClass('wf-active') && typekitTries-- > 0) {
            setTimeout(checkForTypekit, 500);
        } else {
            if (!document.activeElement || $(document.activeElement).is('body')) {
                $('[autofocus]').trigger('focus');
            }
        }
    }

    $(document).on('ready', checkForTypekit);
});
