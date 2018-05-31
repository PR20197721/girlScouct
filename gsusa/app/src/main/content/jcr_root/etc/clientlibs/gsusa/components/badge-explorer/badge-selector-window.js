window.BadgeSelectorFocusWindow = (function(){
	
	var allBadgeData = null;
	var badgeGrid = null;
	var badgeFocusGrid = null;
	var overlay = null;

	/*
	 * Returns a list of badge JSON data based on what is currently visible in the UI.
	 */
	function getAvailableBadges(){
		var returner = [];
		$('.badge-grid .badge-block').not('.hide').each(function(){
			returner.push($(this).data('badge-info'));
		});
		return returner;
	};
	
	function init(){
		badgeGrid = $('.badge-grid').wrap($('<div>').addClass('badge-grid-wrapper'));
		badgeFocusGrid = $('<div>').addClass('badge-focus-grid').hide();
		badgeFocusGrid.insertAfter(badgeGrid);
	}
	
	function _open(){
		$('.badge-focus-grid').show();
		allBadgeData = getAvailableBadges();
		badgeFocusGrid = $('.badge-focus-grid').append('<badge-selector :badge-data="badgeData" @pdf="createPdf" @close="close">');
		var vm = new Vue({
			el: badgeFocusGrid[0],
			data: {
				badgeData: allBadgeData
			},
			methods: {
				createPdf: function(badgeData){
					vm.$destroy();
					var rawBadgeData = [];
					for(var i = 0; i < badgeData.length; i++){
						rawBadgeData = [badgeData[i].$props].concat(rawBadgeData);
					}
					BadgePdfGenerator.setSelectedBadges(rawBadgeData);
					BadgePdfGenerator.generateBadgePdf();
					BadgeSelectorFocusWindow.close();
				},
				close: function(){
					vm.$destroy();
					BadgeSelectorFocusWindow.close();
				}
			},
			mounted: function(){
				badgeGrid.hide();
				overlay = $('<div>').addClass('BadgeSelectionOverlay').hide().appendTo($('.BadgeSelectorModal').parent()).show();
				$('.BadgeSelectorModal').css({
					zIndex: 5, 
					backgroundColor: 'white',
					opacity: 1
				});
				if($(window).width() <= 750){
					$('.middle-col').css({paddingLeft: 0, paddingRight: 0});
				}
				$('.badge-selector').parent().children('.section').filter(function(){
					return $(this).find('.badge-grid-wrapper').length == 0;
				}).hide();
			}
		});
	}
	
	function _close(){
		badgeGrid.show();
		$('.badge-focus-grid').hide();
		$('.BadgeSelectionOverlay').hide();
		$('.badge-selector').parent().children('.section').show();
		$('.BadgeSelectorModal').css({
			zIndex: '', 
			backgroundColor: '',
			opacity: ''
		});
		if($(window).width() <= 750){
			$('.middle-col').css({paddingLeft: '', paddingRight: ''});
		}
		badgeFocusGrid = $('.badge-focus-grid').replaceWith($('<div>').addClass('badge-focus-grid').hide());
	}
	
	$(init);
	return{
		open: _open,
		close: _close
	};
	
})();
