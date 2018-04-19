(function (document, $, ns) {
    $(document).on("dialog-ready", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if($resourceType == 'gsusa/components/install-block'){
        		ns.isInstallBlock = true;
        }else{
        		ns.isInstallBlock = false;
        }
        
    });
        
    // Overwrite color options of CUI Color Picker
    try{
    		if(CUI.Colorpicker.hasGsOverride){
    			return;
    		}
    		CUI.Colorpicker.hasGsOverride = true;
    }catch(err){
    		// doesn't exist.
    		return;
    }

    var originalConstruct = CUI.Colorpicker.prototype.construct;
    CUI.Colorpicker.prototype.construct = function() {

    		// Let the original constructor happen.
        originalConstruct.apply(this, arguments);
        
        window.setTimeout(function(){
	        if(ns.isInstallBlock){
				// Set the GS colors.
				this.options.config.colors = {
					'Light Orange': '#FAA61A',
					'Dark Orange': '#F27536',
					'Magento': '#EC008C',
					'Purple': '#AB218E',
					'Blue': '#00AAE5',
					'Olive': '#C4D82E'
				};
				this.colorNames = ['Light Orange', 'Dark Orange', 'Magento', 'Purple', 'Blue', 'Olive'];
	        }
        }.bind(this), 50);
		
    };
})(document, Granite.$, Granite.author);

