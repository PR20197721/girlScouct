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

    CUI.rte.ui.cui.AnchorDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractBaseDialog,

        toString: "AnchorDialog",

        $idField: null,

        $removeButton: null,

        $removeColumn: null,

        id: null,

        getDataType: function() {
            return "anchor";
        },

        initialize: function(config) {
            var self = this;
            this.applyFn = config.execute;
            this.$idField = this.$container.find("input[data-type=\"id\"]");
            this.$removeButton = this.$container.find("button[data-type=\"delete\"]");
            this.$removeColumn = this.$removeButton.parent(".coral-RichText-dialog-column");
            this.$removeButton.on("click", function(e) {
                self.hide();
                e.stopPropagation();
                config.execute(undefined);
            });
        },

        setAnchor: function(anchor) {
            if (anchor) {
                this.$removeColumn.removeClass("is-hidden");
            } else if (!this.$removeColumn.hasClass("is-hidden")) {
                this.$removeColumn.addClass("is-hidden");
            }
            this.$idField.val(anchor.id);
        },

        getId: function() {
            var id = null;
            var idValue = this.$idField.val();
            if (idValue.length > 0) {
                id = idValue;
            }
            return id;
        },

        resetValues: function() {
            if (!this.$removeColumn.hasClass("is-hidden")) {
                this.$removeColumn.addClass("is-hidden");
            }
            this.$idField.val("");
        },

        apply: function() {
            this.applyFn(this.getId());
            this.inherited(arguments);
        },

        onShow: function() {
            if (!CUI.rte.Common.ua.isTouch) {
                var self = this;
                window.setTimeout(function() {
                    self.$idField.focus();
                });
            }
        }

    });

})(window.jQuery);