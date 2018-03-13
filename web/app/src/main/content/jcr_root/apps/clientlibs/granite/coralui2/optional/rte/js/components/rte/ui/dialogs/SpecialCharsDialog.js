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

    var SPECIAL_CHARS_PER_ROW = 9;

    CUI.rte.ui.cui.SpecialCharsDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractBaseDialog,

        toString: "SpecialCharsDialog",

        getDataType: function() {
            return "specialchars";
        },

        initialize: function(config) {
            this.chars = config.chars;
            // TODO use theming, etc.
            config.tableCls = "coral-RichText-dialog-innerTable";
            config.rowCls = "coral-RichText-dialog-columnContainer";
            config.cellCls = "coral-RichText-dialog-column";
            var charSelector = "";
            var charCnt = 0;
            for (var cName in this.chars) {
                if (this.chars.hasOwnProperty(cName)) {
                    var cDef = this.chars[cName];
                    var col;
                    if (cDef.entity) {
                        var encodedEntity = CUI.rte.Utils.htmlEncode(cDef.entity).replace(/&amp;/g, "&");
                        col = charCnt % SPECIAL_CHARS_PER_ROW;
                        if (col == 0) {
                            charSelector += "<div";
                            if (config.rowCls) {
                                charSelector += " id=\"" + config.rowCls + "\"";
                            }
                            charSelector +=  ">";
                        }
                        charSelector += "<div data-entity=\"" + encodedEntity + "\"";
                        if (config.cellCls != null) {
                            charSelector += " class=\"" + config.cellCls + "\"";
                        }
                        charSelector += ">";
                        charSelector += encodedEntity;
                        charSelector += "</div>";
                        if (col == (SPECIAL_CHARS_PER_ROW - 1)) {
                            charSelector += "</div>";
                        }
                        charCnt++;
                    } else if ((cDef.rangeStart) && (cDef.rangeEnd)) {
                        for (var cCode = cDef.rangeStart; cCode <= cDef.rangeEnd; cCode++) {
                            var entity = "&#" + cCode + ";";
                            col = charCnt % SPECIAL_CHARS_PER_ROW;
                            if (col == 0) {
                                charSelector += "<div";
                                if (config.rowCls) {
                                    charSelector += " id=\"" + config.rowCls + "\"";
                                }
                                charSelector +=  ">";
                            }
                            charSelector += "<div data-entity=\"" + entity + "\"";
                            if (config.cellCls != null) {
                                charSelector += " class=\"" + config.cellCls + "\"";
                            }
                            charSelector += ">";
                            charSelector += entity;
                            charSelector += "</div>";
                            if (col == (SPECIAL_CHARS_PER_ROW - 1)) {
                                charSelector += "</div>";
                            }
                            charCnt++;
                        }
                    }
                }
            }
            this.$selector = this.$dialog.find(
                    ".coral-RichText-specialchars-selector ." + config.tableCls);
            this.$selector.html(charSelector);
            var self = this;
            this.$selector.on("click", "div." + config.cellCls, function(e) {
                var toInsert = $(e.target).data("entity");
                var dm = self.editorKernel.getDialogManager();
                dm.hide(self);
                // TODO use correct caret position
                self.editorKernel.focus();
                config.insertCharacter(toInsert);
                e.stopPropagation();
                e.preventDefault();
            });
            var context = self.editorKernel.getEditContext();
            var tsl = context.getState("CUI.touchScrollLimiter");
            this.$dialog.on("show", function() {
                if (tsl) {
                    tsl.suspend();
                }
            });
            this.$dialog.on("hide", function() {
                if (tsl) {
                    tsl.reactivate();
                }
            });
        }

    });

})(window.jQuery);