window.AccordionWidgetManager = (function(window, $){
	
	function _initAccordionComponent(element){
		
		var headerElements = element.find('.accordionComponentHeader');
		var contentElements = element.find('.accordion-navigation .content');
		
		function toggleAccordion(){
			
			// Close everything else
			headerElements.add(contentElements).removeClass('on');
			
			var contentElement = $(this).next('.accordion-navigation .content');
			
			// Add the classes.
			$(this).add(contentElement).addClass('on');
			
			// Hide other elements and show the selected.
			contentElements.not(contentElement).slideUp('slow');
			contentElement.slideDown('slow');
		}
		
		headerElements.on('click.accordionHeader', toggleAccordion);
		
	}
	
	function init(){
		// Setup click functions for all the sliders on the page.
		$('body').on('click.accordion', '.accordion.accordionComponent ', function(){
			
		});
		
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