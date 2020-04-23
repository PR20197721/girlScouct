(function($, window, document) {
	var ignoredExtensions = [
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

	// Set vtype for ExtJS validator
	CQ.Ext.form.VTypes.vanityPathValidator = function(value, field) {
        var path = value,
			isLowerCase = path === path.toLowerCase(),
			containsIgnoredExtension = extensionPattern.test(path),
			isExternalRedirect = externalSitePattern.test(path),
			isValidVanityPath = isLowerCase || containsIgnoredExtension || isExternalRedirect;
		console.log("Validating...");
		return isValidVanityPath;
    };

	// Set vtype message
	CQ.Ext.form.VTypes.vanityPathValidatorText = CQ.I18n.getMessage("Vanity paths not ending in the following extensions (" + 
		ignoredExtensionsStr + ") or redirecting to an external site must be completely lower case");
}) ($, window, document);