/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2013 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/

(function($) {

    CUI.rte.ui.cui.ImagePropsDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractDialog,

        toString: "ImagePropsDialog",

        selectCUI: null,

        selectedAlignment: "none",

        altString: ".coral-RichText-dialog--imageprops .coral-RichText-dialog-column--alt",

        getDataType: function() {
            return "image";
        },

        initialize: function(config) {
            var selectorString = "span.coral-RichText-dialog-column--imageAlignment";
            if (!this.selectCUI) {
                this.selectCUI = new CUI.Select({element:this.$dialog.find(selectorString)[0]});
                var self = this;
                $(selectorString).on('selected', function(event){
                    selectedAlignment = event.selected;
                });
            }
            this.config = config;
        },

        /**
         * Fetch the alignment of the selected Image
         * @param selectedImageNode
         * @returns {string}
         */
        getCurrentAlignment: function(selectedImageNode){
            var com = CUI.rte.Common;
            var style = com.getAttribute(selectedImageNode, "style");
            if(style){
                if(style.indexOf("float: left")!=-1){
                    return "left";
                }
                else if(style.indexOf("float: right")!=-1){
                    return "right";
                }
                else if(style.indexOf("float: inherit")!=-1){
                    return "inherit";
                }else{
                    return "none";
                }
            }
            else{
                return "none";
            }
        },

        onShow: function()
        {
            this.selectCUI.setValue(this.getCurrentAlignment(this.objToEdit));
            var altText = $(this.altString).find("input[data-type=\"alt\"]");
            altText.val(this.objToEdit.getAttribute("alt"));
        },


        apply: function()
        {
            if (this.validate()) {
                this.toModel();
                this.hide();

                var altText = $(this.altString).find("input[data-type=\"alt\"]");
                var text = altText.val();
                var props = {
                    "alt": text
                }
                this.applyFn("image", props);
            }
            var alignment = {
                "style.float": "none"
            }
            switch(selectedAlignment){
                case "none":
                    this.editorKernel.relayCmd("image", alignment);
                    break;
                case "left":
                    alignment["style.float"] = "left";
                    this.editorKernel.relayCmd("image", alignment);
                    break;
                case "inherit":
                    alignment["style.float"] = "inherit";
                    this.editorKernel.relayCmd("image", alignment);
                    break;
                case "right":
                    alignment["style.float"] = "right";
                    this.editorKernel.relayCmd("image", alignment);
                    break;
            }
        }
    });


})(window.jQuery);