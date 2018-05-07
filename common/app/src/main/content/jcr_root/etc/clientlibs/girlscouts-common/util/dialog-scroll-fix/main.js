(function(document, $, ns) {
	'use strict';

	$(document).on('dialog-loaded', function(s) {
		try {
			setTimeout(function() {
				var clicked = false,
					clickY,
					elementinDrag;



				var updateScrollPos = function(e, element, dir) {
					$('#__top').hide()
					$('#__footer').hide()

					var container = $('.cq-dialog-content').scrollTop();
					var top = $('.cq-dialog-content').scrollTop() + dir * 25;
					
					var min = $(element).parent('coral-multifield').offset().top + container;
					var max = $(element).parent('coral-multifield').height() + min;




					var allColumn = $(element).parents('.coral-Form-fieldwrapper').height();
					

					if(top < max){
						$('#__top').show()
					}
					if(top > min){	
						$('#__footer').show();
					}

					if(top < max && top > min){
						$('#__top').hide()
						$('#__footer').hide()
					}




					if (top + $(element).height() +200 < allColumn ) {
						$('.cq-dialog-content').scrollTop(top);
					}
				};

				// $('.cq-dialog-content').append('<div id="__top">TOP</div><div id="__footer">footer</div>') pending funtionality


				$('.coral-Multifield-item').on({
					mousemove: function(e) {
						
						var content = $('.cq-dialog-content');
						var top = content.offset().top;
						var place = content.height();
						var updown;

						if (e.pageY-160< (place+top) * 0.15) {
							updown = -1;
						}

						if (e.pageY > (place+top) * 0.85) {
							updown = 1;
						}

						if (clicked && updown) {
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
					mouseup: function(e) {
						clicked = false;
				
			
					},
				});



				$(document).on('coral-dragaction:dragstart', function(e) {
					
					e.detail.dragElement.$
					.parent()
					.find('coral-multifield-item')
					.eq(0)
					.before('<div   class="_drophere"></div>');


					e.detail.dragElement.$
					.parent()
					.find('coral-multifield-item')
					.after(
						'<div  class="_drophere"></div>'
					);

					e.detail.dragElement.$.next() && e.detail.dragElement.$.next().remove();
					e.detail.dragElement.$.css('top', '0px');
					e.detail.dragElement.$.parent('coral-multifield').addClass('__dropzoneOn');
				});

				$(document).on('coral-dragaction:drag', function(e) {
					$('coral-multifield-item').css('top', '0px');
				});

				$(document).on('coral-dragaction:dragend', function(e) {
					$('._drophere').remove();
					
					$('#__top').hide()
					$('#__footer').hide()

					$('coral-multifield-item').css(
						'margin-top',
						''
					);
					if(e.detail){
						e.detail.dragElement.$.parent('coral-multifield').removeClass('__dropzoneOn');
					}
					
				});

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
