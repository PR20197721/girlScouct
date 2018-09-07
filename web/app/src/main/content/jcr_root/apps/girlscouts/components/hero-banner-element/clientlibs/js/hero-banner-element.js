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

	function _toggle(buttonElement){
		var contentElement = getContentElement(buttonElement);
		var displayed = contentElement.data('displayed')
		if(displayed !== true){
			_show(contentElement);
		}else{
			_hide(contentElement);
		}
	}

	return {
		toggle: _toggle,
		show: _show,
		hide: _hide
	};

})(Granite.$, Granite, jQuery(document), document, this);