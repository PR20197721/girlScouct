(function(document, $) {
	"use strict";
	//download action
	$(document).on('foundation-selections-change', '.foundation-collection', function() {
		var currentLocation = window.location.href;
		var urlPaths = ["/content/dam/girlscouts-shared"];
		var enabelAssetTrackinButton = showAssetTracking(currentLocation, urlPaths);
		var collection = $(this);
		var downloadAssetTrackingReportActivator = ".cq-damadmin-admin-actions-download-assettracking-report-activator";
		if (enabelAssetTrackinButton) {
			$(downloadAssetTrackingReportActivator).show()
			$(downloadAssetTrackingReportActivator).on("click", function handler(e) {
				var activator = $(this);
				var items = collection.find('.foundation-selections-item');
				var paths = "";
				if (items.length) {
					items.each(function(i) {
						var item = $(this);
						var itemPath = item.data("foundation-collection-item-id");
						paths += "path=" + itemPath + "&";
					});
					//remove the last '&'
					paths = paths.substr(0, paths.length - 1);
				}
				var url = Granite.HTTP.externalize(activator.data("href"));
				url = url + "?" + paths;
				var hiddenIFrameID = 'assettracking-report-iframe',
					iframe = document.getElementById(hiddenIFrameID);
				if (iframe === null) {
					iframe = document.createElement('iframe');
					iframe.id = hiddenIFrameID;
					iframe.style.display = 'none';
					document.body.appendChild(iframe);
				}
				iframe.src = url;
			});
		} else {
			$(downloadAssetTrackingReportActivator).hide()
		}

	});
})(document, Granite.$);

function showAssetTracking(url, folderPath) {
	var flag = false;
	for (var i = 0; i < folderPath.length; i++) {
		if (url.indexOf(folderPath[i]) > -1) {
			flag = true;
		}
	}
	return flag;
}
