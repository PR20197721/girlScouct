(function (document, $, ns) {
    "use strict";
    $(document).on("dialog-ready", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if("girlscouts/components/advertisement" == $resourceType){
        	var $multifield = $("label:contains('Ads Selection')").parent();
            $("input[type='checkbox'][name='./customized']").change(function() {
				var $isCustomizedChecked = $("input[type='checkbox'][name='./customized']").is(':checked');
                if($isCustomizedChecked){
                	$multifield.find("input").prop( "disabled", true );
                	$multifield.find("button").prop( "disabled", true );
                }else{
                	$multifield.find("input").prop( "disabled", false );
                	$multifield.find("button").prop( "disabled", false );
                }
            });
            var $isCustomizedChecked = $("input[type='checkbox'][name='./customized']").is(':checked');
            if($isCustomizedChecked){
            	$multifield.find("input").prop( "disabled", true );
            	$multifield.find("button").prop( "disabled", true );
            }
        }
    });
})(document, Granite.$, Granite.author);