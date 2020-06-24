(function (document, $, ns) {
    "use strict";
    $(document).on("dialog-ready", function() {
		var $resourceType = $("coral-dialog-content").find("[name='./sling:resourceType']").val();
        if($resourceType == "girlscouts/components/image" || $resourceType == "gsusa/components/image"){
            var $rootSelector = $("form.foundation-form");
            var $imageAlignSelector = $rootSelector.find("coral-select[name='./imageAlignment']");
		    var $imageAlign = $imageAlignSelector.val();
            var $additionalCssSelector = $rootSelector.find("input.coral-Form-field[name='./additionalCss']");
    		var $additionalCss = $additionalCssSelector.val();
            //GSWP-2140-Case2-For old/existing image component, where “Additional CSS” is empty, “Left” dropdown should be selected.
            if ($additionalCss == "" && $imageAlign == "select") {
				$imageAlignSelector.each(function(index, itemsElement) {
                    itemsElement.items.getAll().forEach(function(item, idx){
                        if(item.value === "left"){
                           item.selected = true;
                        }
                	});
                });
            }
            //GSWP-2140-Case4-For old/existing image component, where “Additional CSS” has “left”, when you choose “Left” from dropdown, it detects “left” word from “Additional CSS” field and deletes “left” word from the field.
            if (($additionalCss !== "" && $additionalCss !== null && $additionalCss !== "undefined" ) && ($additionalCss.indexOf("left") >=0 || $additionalCss.indexOf("center") >=0 || $additionalCss.indexOf("right") >=0)) {

				//On change of the image dropdown value, additional css field having "left/center/right" keywords should get removed
                $imageAlignSelector.on("change", function() {
                    if (this.value != "select") {
                        //additionalCss having ex:"highlight center hide right left show right" will be "highlight hide show"
                        var $commaSeperated = $additionalCss.replace(/[ ]/g," ").split(" ");
                        var $modifiedAdditionalCss = [];
                        for(var i =0; i < $commaSeperated.length ; i++){
                            if(($commaSeperated[i]!="left" && $commaSeperated[i]!="right" && $commaSeperated[i]!="center") && $modifiedAdditionalCss.indexOf($commaSeperated[i]) == -1)
                            $modifiedAdditionalCss.push($commaSeperated[i]);
                        }
                        $modifiedAdditionalCss=$modifiedAdditionalCss.join(" ");
                        $additionalCssSelector.attr("value", $modifiedAdditionalCss);
                    }
                    else {
						 $additionalCssSelector.attr("value", $additionalCss);
                    }
                });
            }

        }
    });
})(document, Granite.$, Granite.author);