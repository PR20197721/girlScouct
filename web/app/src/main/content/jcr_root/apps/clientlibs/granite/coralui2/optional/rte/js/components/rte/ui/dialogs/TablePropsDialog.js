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

    CUI.rte.ui.cui.TablePropsDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractDialog,

        toString: "TablePropsDialog",

        propertyItems: null,

        $columns: null,

        $rows: null,

        $width: null,

        $height:  null,

        $cellPadding: null,

        $cellSpacing: null,

        $border: null,

        $caption: null,

        selectCUI: null,

        headerSelectorString: "span.coral-RichText-dialog-column--headerField",

        apply: function() {
            if (this.validate()) {
                this.toModel();
                this.editorKernel.getDialogManager().hide();
                if (this.applyFn) {
                    this.applyFn(this.config.cmd, this.createExecutionConfig(), this.editorKernel.getEditContext());
                }
            }
        },

        /**
         * @private
         */
        createExecutionConfig: function() {
            var execConfig = { };
            $.each(this.propertyItems, function(index, item) {
                if (item.length <= 0) {
                    return;
                }
                var id = item.data().type;
                var value = item.val();
                if (value) {
                    execConfig[id] = value;
                }
                return true;
            });
            return execConfig;
        },

        initialize: function(config) {
            this.$columns = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"columns\"]");
            this.$rows = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"rows\"]");
            this.$width = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"width\"]");
            this.$height = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"height\"]");
            this.$cellPadding = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"cellpadding\"]");
            this.$cellSpacing = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"cellspacing\"]");
            this.$border = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"border\"]");
            this.$caption = this.$container.find(".coral-RichText-dialog--tableprops input[data-type=\"caption\"]");
            //Set property items. For header dropdown, CUI.Select is being used, so we need to add a custom
            //object for that which would be updated with new value of select whenever user selects something from
            //that dropdown. This object would be helpful in retrieving selected header value in createTableConfig
            //function.
            this.propertyItems = [this.$columns, this.$rows, this.$width, this.$height, this.$cellPadding, this.$cellSpacing,
                                      this.$border, this.$caption,
                                      {
                                          value : "none",
                                          data: function(){return {type : "header"};},
                                          val: function(){return this.value;},
                                          setVal: function(val) {this.value = val;}
                                      }
                                 ];
            //initialize the header select dropdown
            if (!this.selectCUI) {
                this.selectCUI = new CUI.Select({ element: this.$dialog.find(this.headerSelectorString)[0]});
                //On header selection, update propertyItems with new value of header
                var self = this;
                $(this.headerSelectorString)
                       .on('selected', function(event) {
                    $.each(self.propertyItems, function(index, item) {
                        if (item.length <= 0) {
                            return;
                        }
                        var id = item.data().type;
                        if (id == "header") {
                            item.setVal(event.selected);
                        }
                    });
                });
            }
            this.config = config;
            this.applyFn = config.execFn;
        },

        getDataType: function() {
            return "tableprops";
        },

        /**
         * @private
         */
        setValueForSelect: function(selectorString, propertyId, value) {
            var select = $(selectorString).data("select");
            value = value ? value : "none";
            select.setValue(value);
            $.each(this.propertyItems, function(index, item) {
                if (item.length <= 0) {
                    return;
                }
                var id = item.data().type;
                if (id == propertyId) {
                    item.setVal(value);
                }
            });
        },

        onShow: function() {
            //reset dialog input values to defaults
            this.$columns.val("3");
            this.$rows.val("2");
            this.$width.val("");
            this.$height.val("");
            this.$cellPadding.val("1");
            this.$cellSpacing.val("0");
            this.$border.val("1");
            this.setValueForSelect(this.headerSelectorString, "header", "none");
        }
    });


})(window.jQuery);