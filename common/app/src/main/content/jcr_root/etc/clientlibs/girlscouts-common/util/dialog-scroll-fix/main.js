(function(document, $, ns) {
	'use strict';

	$(document).on('dialog-loaded', function(s) {
		try {
			setTimeout(function(){
				var clicked = false,
					clickY,
					elementinDrag;
				$('.coral-Multifield-item').on({
					mousemove: function(e) {
						if (clicked) {
							updateScrollPos(e, this);
						}
					},
					mousedown: function(e) {
						clicked = true;
						clickY = e.pageY;
						
					},
					mouseup: function() {
						clicked = false;
					
					},
				});

				var max = $('.coral-Multifield').height();

				var updateScrollPos = function(e, element) {
					// $('html').css('cursor', 'row-resize');

					var top = $('.cq-dialog-content').scrollTop() + (e.pageY - clickY);

					if(top < max - $(element).height()){
						$('.cq-dialog-content').scrollTop(top);
					}
				};

				$(document).on('dialog-closed', function(s) {
					$('.coral-Multifield-item').off('mousemove');
					$('.coral-Multifield-item').off('mousedown');
					$('.coral-Multifield-item').off('mouseup');
				});
			}, 50);
		} catch (err) {}
	});
})(document, Granite.$, Granite.author);
