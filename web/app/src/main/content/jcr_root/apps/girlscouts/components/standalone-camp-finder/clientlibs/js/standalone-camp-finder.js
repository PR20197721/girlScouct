var boundHashForms = {};

function bindSubmitHash(form) {
    "use strict";
    // Check if form exists and ensure a single form binding
    if (boundHashForms[form.formElement] || !$(form.formElement)) {
        return false;
    }
    boundHashForms[form.formElement] = true;

    $(form.formElement).submit(function (event) {
        // Stop other events
        if (event.preventDefault) {
            event.preventDefault();
        } else {
            event.stop();
        }
        event.returnValue = false;
        event.stopPropagation();

        var hashElement = $(this).find(form.hashElement),
            hash = hashElement.val(),
            pattern = hashElement.attr("pattern") || false,
            match = !pattern || (pattern && hash.match(pattern));

        // Prevent double submit on redirect and invalid data (if a pattern is specified)
        if (form.submitted || !match) {
            return false;
        }

        // Do ajax request instead of redirect
        if (form.ajax) {
            if (form.edit) {
                alert("This tool can only be used on a live page");
                return false;
            }
            $.ajax({
                method: form.ajax.method || "POST",
                url: form.ajax.url || "",
                data: form.ajax.data || $(this).serialize(),
                async: form.ajax.async || false,
                success: form.ajax.success || function () {}
            });
            return false;
        }

        // Redirect to the results page while maintaining query
        if (form.currentUrl == form.redirectUrl) {
            location.hash = hash;
            window.location.reload();
        }else{
        	window.location = form.redirectUrl + ".html" + (window.location.search != "" ? window.location.search : "?") + "#" + hash;
        }

        form.submitted = true;
        return false;
    });
}