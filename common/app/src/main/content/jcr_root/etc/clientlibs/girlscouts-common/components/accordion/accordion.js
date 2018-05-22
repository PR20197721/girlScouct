window.AccordionWidgetManager = (function(window, document, $){

	function toggleAccordionElement(){

		var accordionElement = $(this).data('accordion-element') || $(this).parent('.accordion');
		var headerElements = accordionElement.find('.accordionComponentHeader');
		var contentElements = accordionElement.find('.accordion-navigation .content');

		var show = !$(this).data('accordion-element-shown');

		// Get the target content elements.
		var contentElement = $(this).next('.accordion-navigation').children('.content');

		// Fix the classes.
		headerElements.add(contentElements).removeClass('on accordionHeaderShown').data('accordion-element-shown', false);
		if(show){
			$(this).add(contentElement).addClass('on accordionHeaderShown').data('accordion-element-shown', true);
		}

		// Toggle parsys if we have one after the animation is complete.
		headerElements.not($(this)).each(function(){
			var nonActiveToggleParsys = $(this).data('toggle-parsys');
			if(nonActiveToggleParsys && show){
				nonActiveToggleParsys.hideParsys();
			}
		});

		// Show the parsys after open.
		var activeToggleParsys = $(this).data('toggle-parsys');
		if(activeToggleParsys){
			if(show){
				activeToggleParsys.showParsys();
			}else{
				activeToggleParsys.hideParsys();
			}
		}

		// Hide other elements and show the selected.
		if(show){
			contentElements.not(contentElement).slideUp('slow');
			contentElement.slideDown('slow');
		}else{
			contentElements.slideUp('slow');
		}
	}

	function _initAccordionComponent(element){

		var headerElements = element.children('.accordionComponentHeader');

		var isEdit = false;
		try{
			isEdit = readCookie('wcmmode') == 'edit';
		}catch(err){}

		var toggleParsysExists = false;
		try{
			toggleParsysExists = !!TouchUI.Utils.ToggleParsys;
		}catch(er){}

		if(isEdit && toggleParsysExists){
			headerElements.each(function(){
				var parsysIdentifier = $(this).data('parsys-identifier');
				var toggleParsys;
				if($(this).data('toggle-parsys')){
					toggleParsys = $(this).data('toggle-parsys');
				}else{
					toggleParsys = TouchUI.Utils.ToggleParsys(parsysIdentifier);
					$(this).data('toggle-parsys', toggleParsys);
				}
				// Initialize hidden.
				toggleParsys.hideParsys();
			});
		}

		// Apply the header elements click functions.
		headerElements
			.data('accordion-element', $(element))
			.data('accordion-element-shown', false)
			.off('click.accordionHeader')
			.on('click.accordionHeader', toggleAccordionElement);

		// Open accordion with ID = hash.
		var currentHash = document.location.hash;
		if(currentHash && currentHash.length > 1){
			currentHash = currentHash.replace('#', '');
		}

		var thisHeader = headerElements.filter(function(){
			return $(this).attr('id') == currentHash;
		});

		var parentHeaders = thisHeader
			.parents('.accordion-navigation')
			.prev();

		for(var i = parentHeaders.length - 1; i >= 0; i--){
			$(parentHeaders[i]).trigger('click.accordionHeader');
		}

		if(thisHeader.length > 0){
			thisHeader.trigger('click.accordionHeader');
			window.setTimeout(function(){
				var targetHeader = $('#' + currentHash);
				targetHeader.next('.accordion-navigation').children('.content').promise().then(function(){
					$('body').animate({scrollTop: $('#' + currentHash).offset().top - 200});
				});
			}, 100);
		}

	}

	function init(){
		$('.accordionComponent').filter(function(){
			return !$(this).data('accordion-initialized');
		}).each(function(){
			AccordionWidgetManager.initAccordionComponent($(this).data('accordion-initialized', true));
		});
	}

	// Init everything that's already on the page.
	$(init);

	return {
		// Expose so this can be used for async accordions.
		initAccordionComponent : _initAccordionComponent
	}

})(window, document, $);