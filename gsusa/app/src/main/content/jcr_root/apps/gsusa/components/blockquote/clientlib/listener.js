(function (document, $, ns) {
    "use strict";
    
    function findSiblingCheckboxes(sourceCheckBox){
    		var container = $(sourceCheckBox).closest('.BlockQuoteEditDialog');
    		return {
    			makeQuoteCheckbox: container.find('input[name="./isQuote"]'),
    			makeQuoteDeleteCheckbox: container.find('input[name="./isQuote@Delete"]'),

    			hasQuoteeCheckbox: container.find('input[name="./hasQuotee"]'),
    			hasQuoteeDeleteCheckbox: container.find('input[name="./hasQuotee@Delete"]'),
    			
    			quoteeField: container.find('input[name="./quotee"]')
    		};
    };
    
    function processMakeQuoteCheckbox(){
		var siblingCheckBoxElements = findSiblingCheckboxes(this);
		if($(this).is(':checked')){
			siblingCheckBoxElements.hasQuoteeCheckbox.closest('.coral-Form-fieldwrapper').addClass('fieldAvailable');
		}else{
			siblingCheckBoxElements.hasQuoteeCheckbox.prop('checked', false).trigger('change').closest('.coral-Form-fieldwrapper').removeClass('fieldAvailable');
		}
    };
    
    function processHasQuoteeCheckbox(){
		var siblingCheckBoxElements = findSiblingCheckboxes(this);
		if($(this).is(':checked')){
			siblingCheckBoxElements.quoteeField.closest('.coral-Form-fieldwrapper').addClass('fieldAvailable');
		}else{
			siblingCheckBoxElements.quoteeField.val('').closest('.coral-Form-fieldwrapper').removeClass('fieldAvailable');
		}
    };
    
    $(document).on("dialog-ready", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if($resourceType != 'gsusa/components/blockquote'){
        		return;
        }
        var checkBoxElements = findSiblingCheckboxes('coral-dialog-content .BlockQuoteEditDialog');
        
        // Set the initial classes
        checkBoxElements.hasQuoteeCheckbox.add(checkBoxElements.quoteeField).each(function(){
        		$(this).closest('.coral-Form-fieldwrapper').addClass('fadeAwayField');
        });
        
        // Initialize with the right values.
        checkBoxElements.hasQuoteeCheckbox.each(processHasQuoteeCheckbox);
        checkBoxElements.makeQuoteCheckbox.each(processMakeQuoteCheckbox);
        
        // Add the listeners.
        checkBoxElements.makeQuoteCheckbox.off('change.blockQuote').on('change.blockQuote', processMakeQuoteCheckbox);
        checkBoxElements.hasQuoteeCheckbox.off('change.blockQuote').on('change.blockQuote', processHasQuoteeCheckbox);
        
    });
})(document, Granite.$, Granite.author);

