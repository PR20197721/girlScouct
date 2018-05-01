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
						var content = $('.cq-dialog-content');
						var top = content.offset().top;
						var place = content.height();
						var updown;

						console.log(top,place,e.pageY, e.pageY + 100 < place * 0.30, place * 0.30)

						if( e.pageY-50 < place * 0.30){
								console.log('up',e.pageY)
								updown=-1;
						}


						if(e.pageY > place* 0.80){
								console.log('down',e.pageY)
								updown=1
						}
					
						if (clicked) {
							updateScrollPos(e, this, updown);
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

				var updateScrollPos = function(e, element, dir) {
					// $('html').css('cursor', 'row-resize');
				
					var top = $('.cq-dialog-content').scrollTop() + dir*60 ;

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
