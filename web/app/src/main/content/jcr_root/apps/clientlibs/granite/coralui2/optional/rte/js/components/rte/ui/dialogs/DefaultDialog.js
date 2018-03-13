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

    CUI.rte.ui.cui.DefaultDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractDialog,

        getDataType: function() {
            return "default";
        },

        initialize: function(config) {
            this.config = config;
            this.context = this.editorKernel.getEditContext();
            this.touchScrollLimiter = this.context.getState("CUI.touchScrollLimiter");
        },

        onShow: function() {
            if (!CUI.rte.Common.ua.isTouch) {
                var self = this;
                window.setTimeout(function() {
                    var items = self.config.dialogItems;
                    if (items && (items.length > 0)) {
                        var id = items[0].item.id;
                        if (id) {
                            $("#" + id).focus();
                        }
                    }
                }, 1);
            }
            if (this.touchScrollLimiter) {
                this.touchScrollLimiter.suspend();
            }
        },

        onHide: function() {
            if (this.touchScrollLimiter) {
                this.touchScrollLimiter.reactivate();
            }
            if (this.config.parameters && this.config.parameters.selfDestroy) {
                if (this.$dialog) {
                    this.$dialog.remove();
                    this.$dialog = null;
                }
            }
        },

        preprocessModel: function() {
            // nothing to do
        },

        _executeOnItems: function(fn) {
            var items = this.config.dialogItems;
            if (items && items.length) {
                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    if (item.item.id) {
                        var $field = $("#" + item.item.id);
                        fn(item, $field.get(0));
                    }
                }
            }
        },
        
        dlgFromModel: function() {
            var self = this;
            this._executeOnItems(function(item, field) {
                if (item.fromModel) {
                    item.fromModel(self.objToEdit, field);
                }
            });
        },

        validate: function() {
            var isValid = true;
            this._executeOnItems(function(item, field) {
                if (item.validate) {
                    if (!item.validate(field)) {
                        isValid = false;
                    }
                }
            });
            return isValid;
        },

        dlgToModel: function() {
            var self = this;
            this._executeOnItems(function(item, field) {
                if (item.toModel) {
                    item.toModel(self.objToEdit, field);
                }
            });
        },

        postprocessModel: function() {
            // nothing to do
        }

    });

}(window.jQuery));