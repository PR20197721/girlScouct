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
				//contentElement.promise().then(function(){
					activeToggleParsys.showParsys();
				//});
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
		
		var headerElements = element.find('.accordionComponentHeader');
		var contentElements = element.find('.accordion-navigation .content');

		var isEdit = false;
		try{
			isEdit = readCookie('wcmmode') == 'edit';
		}catch(err){}
		
		if(isEdit && window.toggleParsys){
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