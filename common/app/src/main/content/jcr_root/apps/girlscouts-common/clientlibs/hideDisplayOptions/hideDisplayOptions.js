(function($, $document) {
	$(document).on("cq-layer-activated", function(e) {
		var displayLimitedOptions;
		$.ajax({
			url: '/bin/getAssetFilter.html',
			dataType: 'json',
			success: function(data, status, xhr) { // success callback function
				setDisplayLimitedOptions(data.displayLimitedOptions);
			},
			error: function(jqXhr, textStatus, errorMessage) { // error callback 
			}
		});
		function setDisplayLimitedOptions(displayLimitedOptions_) {
			displayLimitedOptions = displayLimitedOptions_;
		}
		function showDisplayOptions(displayLimitedOptions_) {
			displayLimitedOptions = displayLimitedOptions_;
			var hideList = ["Products", "Paragraphs", "Content Fragments", "Experience Fragments", "Paragraphs", "Design Packages", "Adaptive Forms", "Manuscript", "UGC", "Interactive Communications"];
			var showList = ["Images", "Videos", "Documents", "Pages"];
			if (displayLimitedOptions) {
				var parent = $('[name="assetfilter_type_selector"]');
				var children = parent[0].getElementsByClassName('coral3-SelectList-item');
				for (var i = 0; i < children.length; i++) {
					if (showList.indexOf(children[i].value) > -1) {
						$(children[i]).show();
					} else {
						$(children[i]).hide();
					}
				}
                /*
				$(".coral3-SelectList-item").each(function() {
					$(".coral3-SelectList-item").each(function() {
						if (showList.indexOf($(this).attr('value')) > -1) {
							$(this).show();
                        }else{
                            $(this).hide();
                        }
					});
				});*/
			}
		}
		$('[name="assetfilter_type_selector"]').click(function(event) {
			event.preventDefault();
			showDisplayOptions(displayLimitedOptions);
		});
	});
})(jQuery, jQuery(document));