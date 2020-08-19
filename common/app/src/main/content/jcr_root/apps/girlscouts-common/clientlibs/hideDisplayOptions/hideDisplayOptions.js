(function($, $document) {
	$(document).on("cq-layer-activated", function(e) {
		var showAllOptions;
		$.ajax({
			url: '/bin/getAssetFilter.html',
			dataType: 'json',
			success: function(data, status, xhr) { // success callback function
				hideDisplayOptions(data.displayAllOptions);
			},
			error: function(jqXhr, textStatus, errorMessage) { // error callback 
			}
		});
		function hideDisplayOptions(displayAllOptions) {
			showAllOptions = displayAllOptions;
			//var hideList = ["Products", "Paragraphs", "Content Fragments", "Experience Fragments", "Paragraphs", "Design Packages", "Adaptive Forms", "Manuscript", "UGC", "Interactive Communications"];
            var hideList = ["Images", "Videos", "Documents", "Pages"];
            if (displayAllOptions) {
				$(".coral3-SelectList-item").each(function() {
					$(".coral3-SelectList-item").each(function() {
						if (hideList.indexOf($(this).attr('value')) > -1) {
							$(this).show();
                        }else{
                            $(this).hide();
                        }
					});
				});
			}
		}
		$('[name="assetfilter_type_selector"]').click(function(event) {
			event.preventDefault();
			hideDisplayOptions(showAllOptions);
		});
	});
})(jQuery, jQuery(document));