(function($, $document) {
	$(document).on("cq-layer-activated", function(e) {
		var displayLimitedOptions;
		$.ajax({
			url: '/bin/getAssetFilter.html',
			dataType: 'json',
			success: function(data, status, xhr) { // success callback function
				showDisplayOptions(data.displayLimitedOptions);
			},
			error: function(jqXhr, textStatus, errorMessage) { // error callback 
			}
		});
		function showDisplayOptions(displayLimitedOptions_) {
			displayLimitedOptions = displayLimitedOptions_;
			//var showList = ["Products", "Paragraphs", "Content Fragments", "Experience Fragments", "Paragraphs", "Design Packages", "Adaptive Forms", "Manuscript", "UGC", "Interactive Communications"];
            var showList = ["Images", "Videos", "Documents", "Pages"];
            if (displayLimitedOptions) {
				$(".coral3-SelectList-item").each(function() {
					$(".coral3-SelectList-item").each(function() {
						if (showList.indexOf($(this).attr('value')) > -1) {
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
			showDisplayOptions(displayLimitedOptions);
		});
	});
})(jQuery, jQuery(document));