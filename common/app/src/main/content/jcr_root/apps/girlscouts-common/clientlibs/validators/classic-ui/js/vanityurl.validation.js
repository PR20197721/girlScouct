(function($, window, document) {
	var extensionPattern = new RegExp("\\.[a-z]{1,5}$", "i"), // Matches any file ".ext" 1-5 chars, case-insensitive
		externalSitePattern = new RegExp('\\w+\\.\\w+\\/'); // Matches "sitename.ext/"

	// Set vtype for ExtJS validator
	CQ.Ext.form.VTypes.vanityPathValidator = function(value, field) {
        var path = value,
			isLowerCase = path === path.toLowerCase(),
			containsExtension = extensionPattern.test(path),
			isExternalRedirect = externalSitePattern.test(path),
			isValidVanityPath = isLowerCase || containsExtension || isExternalRedirect;
		return isValidVanityPath;
    };

	// Set vtype message
	CQ.Ext.form.VTypes.vanityPathValidatorText = 
		CQ.I18n.getMessage("Vanity paths not ending in a file extension or redirecting to an external site must be completely lower case");
}) ($, window, document);