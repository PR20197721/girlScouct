(function(document, $, ns) {
	'use strict';

	$(document).on('dialog-loaded', function(s) {
		try {
			setTimeout(function() {
				var clicked = false,
					clickY,
					elementinDrag;

			
				var updateScrollPos = function(e, element, dir) {
					var top = $('.cq-dialog-content').scrollTop() + dir * 25;
					if (top < max - $(element).height()) {
						$('.cq-dialog-content').scrollTop(top);
					}
				};


				$('.coral-Multifield-item').on({
					mousemove: function(e) {
						var content = $('.cq-dialog-content');
						var top = content.offset().top;
						var place = content.height();
						var updown;



						console.log(top,place,e.pageY)

						if (e.pageY-160 < (place+top) * 0.15) {
							updown = -1;
						}

						if (e.pageY > (place+top) * 0.85) {
							updown = 1;
						}

						if (clicked) {
							updateScrollPos(e, this, updown);
						}
					},
					mousedown: function(e) {
						clicked = true;
						clickY = e.pageY;

						$('coral-multifield-item.is-dragging').css(
							'margin-top',
							-44
						);
					},
					mouseup: function() {
						clicked = false;
						$('coral-multifield-item.is-dragging').css(
							'margin-top',
							''
						);
					},
				});

				$(document).on('coral-dragaction:dragstart', function(e) {
					// $('._drophere').remove();
					$('coral-multifield-item')
						.eq(0)
						.before('<div   class="_drophere"></div>');
					$('coral-multifield-item').after(
						'<div  class="_drophere"></div>'
					);

					e.detail.dragElement.$.next().remove();
					e.detail.dragElement.$.css('top', '0px');
				});

				$(document).on('coral-dragaction:drag', function(e) {
					$('coral-multifield-item').css('top', '0px');
				});

				$(document).on('coral-dragaction:dragend', function(e) {
					$('._drophere').remove();
				});

				var max = $('.coral-Multifield').height();

				$(document).on('dialog-closed', function(s) {
					$('.coral-Multifield-item').off('mousemove');
					$('.coral-Multifield-item').off('mousedown');
					$('.coral-Multifield-item').off('mouseup');
					$('._drophere').remove();
					$(document).off('coral-dragaction:dragstart');
					$(document).off('coral-dragaction:drag');
					$(document).off('coral-dragaction:dragend');
				});
			}, 50);
		} catch (err) {}
	});
})(document, Granite.$, Granite.author);
