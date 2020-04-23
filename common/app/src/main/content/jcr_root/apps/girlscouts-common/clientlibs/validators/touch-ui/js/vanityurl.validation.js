(function($, window, document) {
    /* Adapting window object to foundation-registry */
    var registry = $(window).adaptTo("foundation-registry"),
        selector = '[name="./sling:vanityPath"]',
        ignoredExtensions = [
            'html',
            'pdf',
            'doc',
            'docx',
            'ppt',
            'pptx',
            'xls',
            'xlsx',
            'jpg',
            'png',
            'gif'
        ],
        ignoredExtensionsStr = ignoredExtensions.join(', '),
        extensionPattern = new RegExp(ignoredExtensions.join('$|') + '$'),
        externalSitePattern = new RegExp('\w+\.\w+\/'); // Matches "sitename.ext/"

    /*Validator for TextField - Any Custom logic can go inside validate function - starts */
    registry.register("foundation.validation.validator", {
        selector: selector,
        validate: function(el) {
            var element = $(el),
                path = element.val().trim(),
                isLowerCase = path === path.toLowerCase(),
                containsIgnoredExtension = extensionPattern.test(path),
				isExternalRedirect = externalSitePattern.test(path),
                isValidVanityPath = isLowerCase || containsIgnoredExtension || isExternalRedirect,
                pathList = $(selector).map(function() { return $(this).val().trim(); }).get(),
                isDuplicate = pathList.filter(function(p){ return p === path; }).length > 1;

            if (path.length == 0) {
                return "Please enter path";
            } else if (!isValidVanityPath) {
                return "Vanity paths not ending in the following extensions (" + ignoredExtensionsStr + 
                    ") or redirecting to an external site must be completely lower case"; 
            } else if (isDuplicate) {
                return "Vanity paths cannot contain duplicates";
            }
        }
    });
}) ($, window, document);