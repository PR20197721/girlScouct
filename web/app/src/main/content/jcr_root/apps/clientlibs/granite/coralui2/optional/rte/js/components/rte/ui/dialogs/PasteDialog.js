/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 *  Copyright 2014 Adobe Systems Incorporated
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

    CUI.rte.ui.cui.PasteDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractDialog,

        toString: "PasteDialog",

        $iframe: null,

        $pasteBox:null,

        $textArea: null,

        apply: function() {
            if (this.validate()) {
                this.toModel();
                this.hide();
                if (this.applyFn) {
                    var isHtml = (this.config.type == "iframe");
                    var value = this.$pasteBox.val();
                    var dom = null;
                    if (isHtml) {
                        // we are working in different document/window environments;
                        // hence we'll have to move the DOM tree over to the editor's
                        // document
                        var pastedDom = this.$iframe.prop("contentWindow").document.body;
                        dom = this.config.editContext.createElement("div");
                        dom.innerHTML = pastedDom.innerHTML;
                    }
                    this.applyFn(this.config.editContext, value, isHtml, dom);
                }
            }
        },

        initialize: function(config) {
            if(config.type == "iframe") {
                this.$iframe = this.$container.find("iframe");
                this.$pasteBox = this.$iframe;
            } else {
                this.$textArea = this.$container.find("textarea");
                this.$pasteBox = this.$textArea;
            }
            this.config = config;
            this.applyFn = config.pasteFn;
        },

        getDataType: function(config) {
            return (config.type == "iframe" ? "pastewordhtml" : "pasteplaintext");
        },

        mustRecreate: function() {
        return true;
        },

        onShow: function() {
            if(this.config.type == "iframe") {
                var com = CUI.rte.Common;
                this.$iframe.attr("name", "coral-richtext-paste-html-" + Date.now());
                this.$iframe.attr("frameBorder", "0");
                this.$iframe.attr("src", "javascript:;"); //TODO : Add a check for IE
                var win = this.$iframe.prop("contentWindow");
                win.document.open();
                win.document.write("<html><head><style type=\"text/css\">"
                    + "body{border:.0625rem solid #2d2d2d;margin:0;padding:3px;cursor:text;height:94%;"
                    + "font-family:sans-serif;font-size:13px;color:#dcdcdc}"
                    + "body:focus{border-color:#3287d2}"
                    + "</style></head>"
                    + "<body></body></html>");
                win.document.designMode = "on";
                win.document.close();
            } else {
                this.$pasteBox.click(function(e) {
                    e.stopPropagation();
                });
            }
            if (!CUI.rte.Common.ua.isTouch) {
                var self = this;
                window.setTimeout(function() {
                    self.$pasteBox.focus();
                });
            }
        },

        setValue: function(value) {}
    });


})(window.jQuery);