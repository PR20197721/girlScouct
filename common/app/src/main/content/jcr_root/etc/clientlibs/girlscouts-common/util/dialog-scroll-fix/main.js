(function(document, $, ns) {
	'use strict';

	$(document).on('dialog-loaded', function(s) {
		try {
			setTimeout(function(){
				var clicked = false,
					clickY;
				$('.coral-Multifield-item').on({
					mousemove: function(e) {
						if (clicked) {
							updateScrollPos(e);
						}
					},
					mousedown: function(e) {
						console.log(e.target);
						clicked = true;
						clickY = e.pageY;
					},
					mouseup: function() {
						clicked = false;
						$('html').css('cursor', 'auto');
					},
				});

				var updateScrollPos = function(e) {
					$('html').css('cursor', 'row-resize');
					$('.cq-dialog-content').scrollTop(
						$('.cq-dialog-content').scrollTop() + (e.pageY - clickY)
					);
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
