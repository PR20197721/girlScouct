(function($, window, document) {
    /* Adapting window object to foundation-registry */
    var registry = $(window).adaptTo("foundation-registry"),
        selector = '[name="./sling:vanityPath"]',
        extensionPattern = new RegExp("\\.[a-z]{1,5}$", "i"), // Matches any file ".ext" 1-5 chars, case-insensitive
        externalSitePattern = new RegExp('\\w+\\.\\w+\\/'); // Matches "sitename.ext/"

    /*Validator for TextField - Any Custom logic can go inside validate function - starts */
    registry.register("foundation.validation.validator", {
        selector: selector,
        validate: function(el) {
            var element = $(el),
                path = element.val().trim(),
                isLowerCase = path === path.toLowerCase(),
                containsExtension = extensionPattern.test(path),
				isUrl = externalSitePattern.test(path),
                isValidVanityPath = (isLowerCase || containsExtension) && !isUrl,
                pathList = $(selector).map(function() { return $(this).val().trim(); }).get(),
                isDuplicate = pathList.filter(function(p){ return p === path; }).length > 1;

            if (path.length == 0) {
                return "Please enter path";
            } else if (!isValidVanityPath) {
                return "Vanity paths can only be in the form of a relative path (e.g. /content/gsusa/camp, cannot be an external site) and must be completely lower case if not ending in a file extension (e.g. pdf)"; 
            } else if (isDuplicate) {
                return "Vanity paths cannot contain duplicates";
            }
        }
    });
}) ($, window, document);