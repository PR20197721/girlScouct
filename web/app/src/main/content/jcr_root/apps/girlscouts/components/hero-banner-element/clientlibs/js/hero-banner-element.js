var HeroBannerElementManager = (function($, ns, channel, document, window){

	function getContentElement(buttonElement){
		return $(buttonElement).parent().find('.heroBannerElementConfigContents');
	}

	function _show(contentElement){
		contentElement = $(contentElement);
		if(!contentElement.hasClass('heroBannerElementConfigContents')){
			contentElement = getContentElement(contentElement);
		}
		if(contentElement.data('displayed') !== true){
			contentElement.data('displayed', true).parent()
				.addClass('heroBannerElementContentDisplayed')
				.find('.heroBannerElementTriangle')
				.toggleClass('hideTriangle');
		}
	}

	function _hide(contentElement){
		contentElement = $(contentElement);
		if(!contentElement.hasClass('heroBannerElementConfigContents')){
			contentElement = getContentElement(contentElement);
		}
		if(contentElement.data('displayed') === true){
			contentElement.data('displayed', false).parent()
				.removeClass('heroBannerElementContentDisplayed')
				.find('.heroBannerElementTriangle')
				.toggleClass('hideTriangle');
		}
	}

	function _disable(contentElement){
		contentElement.addClass("heroBannerElementConfigDisabled");
	}

	function _enable(contentElement){
		contentElement.removeClass("heroBannerElementConfigDisabled");
	}

	function _toggle(buttonElement){
		var contentElement = getContentElement(buttonElement);
		var displayed = contentElement.data('displayed')
		if(displayed !== true){
			_show(contentElement);
		}else{
			_hide(contentElement);
		}
	}

	$(document).on("dialog-ready", function() {
		var dialog = $("coral-dialog-content");
		if(dialog.data('disabled-checkbox-initialized') === true){
			return;
		}
		dialog.data('disabled-checkbox-initialized', true);

		dialog.find('input[name="enabled"]').on('change.disable-container', function(){
			var heroContainer = $(this).parents('.heroBannerElementContainer').find('.heroBannerElementImagePreview');
			if($(this).is(':checked')){
				HeroBannerElementManager.enable(heroContainer)
			}else{
				HeroBannerElementManager.disable(heroContainer);
			}
		}).trigger('change.disable-container');
	});

	return {
		toggle: _toggle,
		show: _show,
		hide: _hide,
		enable: _enable,
		disable: _disable
	};

})(Granite.$, Granite, jQuery(document), document, this);