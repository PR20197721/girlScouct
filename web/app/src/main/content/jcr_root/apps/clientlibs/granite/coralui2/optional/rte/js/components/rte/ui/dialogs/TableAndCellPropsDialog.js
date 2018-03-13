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

    CUI.rte.ui.cui.TableAndCellPropsDialog = new Class({

        extend: CUI.rte.ui.cui.TablePropsDialog,

        toString: "TableAndCellPropsDialog",

        /**
         * @private
         */
        tabPanelSelector: ".coral-TabPanel.coral-RichText-dialog-tabPanel--tableandcellprops",

        tabPanel: null,

        cellHorizontalAlignmentSelector: ".coral-RichText-dialog-select--cellHorizontalAlignment",

        cellVerticalAlignmentSelector: ".coral-RichText-dialog-select--cellVerticalAlignment",

        cellTypeSelector: ".coral-RichText-dialog-select--cellType",

        $cellHeadersContainer: null,

        $cellIdContainer: null,

        $cellScopeContainer: null,

        cellScopeSelector: ".coral-RichText-dialog-select--scopeAttribute",

        cellHorizontalAlignmentCUI: null,

        cellVerticalAlignmentCUI: null,

        cellTypeCUI: null,

        cellType: null,

        cellScope: null,

        $cellWidth: null,

        $cellHeight: null,

        $cellHeaders: null,

        $cellId: null,

        getDataType: function() {
            return "tableandcellprops";
        },

        // Override CUI.rte.ui.cui.TablePropsDialog#createExecutionConfig
        createExecutionConfig: function() {
            var execConfig = CUI.rte.Utils.scope(this.superClass.createExecutionConfig, this)();
            if (this.cellType == "th") {
                var value = this.cellScope;
                if (value) {
                    execConfig["cell-scope"] = value;
                }
                var id = this.$cellId.data().type;
                value = this.$cellId.val();
                if (value) {
                    execConfig[id] = value;
                }
                if (this.$hiddenHeaderSwitch.is(":checked")) {
                    var id = this.$hiddenHeaderSwitch.data().type;
                    execConfig[id] = this.$hiddenHeaderSwitch.val();
                }
            } else {
                var id = this.$cellHeaders.data().type;
                value = this.$cellHeaders.val();
                if (value) {
                    execConfig[id] = value;
                }
            }
            return execConfig;
        },

        initialize: function(config) {
            this.$width = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"width\"]");
            this.$height = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"height\"]");
            this.$cellPadding = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cellpadding\"]");
            this.$cellSpacing = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cellspacing\"]");
            this.$border = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"border\"]");
            this.$caption = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"caption\"]");
            this.$cellWidth = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cell-width\"]");
            this.$cellHeight = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cell-height\"]");
            this.$cellHeaders = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cell-headers\"]");
            this.$cellId = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cell-id\"]");
            this.$hiddenHeaderSwitch = this.$container.find(".coral-RichText-dialog--tableandcellprops input[data-type=\"cell-hiddenheader\"]");
            this.$cellHeadersContainer = $(".coral-RichText-dialog.coral-RichText-dialog--tableandcellprops .coral-RichText-dialog-columnContainer--headerAttribute");
            this.$cellIdContainer = $(".coral-RichText-dialog.coral-RichText-dialog--tableandcellprops .coral-RichText-dialog-columnContainer--idAttribute");
            this.$cellScopeContainer = $(".coral-RichText-dialog.coral-RichText-dialog--tableandcellprops .coral-RichText-dialog-columnContainer--scopeAttribute");
            this.propertyItems = [this.$width, this.$height, this.$cellPadding, this.$cellSpacing, this.$border, this.$caption];
            this.propertyItems.push(this.$cellWidth);
            this.propertyItems.push(this.$cellHeight);
            this.propertyItems.push({
                value : "none",
                data: function(){return {type : "cell-align"};},
                val: function(){return this.value == "none" ? CUI.rte.commands.Table.CONFIG_NONE : this.value;},
                setVal: function(val) {this.value = val;}
            });
            this.propertyItems.push({
                value : "none",
                data: function(){return {type : "cell-valign"};},
                val: function(){return this.value == "none" ? CUI.rte.commands.Table.CONFIG_NONE : this.value;},
                setVal: function(val) {this.value = val;}
            });
            this.propertyItems.push({
                value : "none",
                data: function(){return {type : "cell-cellType"};},
                val: function(){return this.value == "none" ? null : this.value;},
                setVal: function(val) {this.value = val;}
            });
            if(!this.tabPanel) {
                this.tabPanel = new CUI.Tabs({element: this.$dialog.find(this.tabPanelSelector)[0]});
            }
            if (!this.cellHorizontalAlignmentCUI) {
                this.cellHorizontalAlignmentCUI = new CUI.Select({ element: this.$dialog.find(this.cellHorizontalAlignmentSelector)[0]});
                //On selection change, update propertyItems with new value of selections
                var self = this;
                $(this.cellHorizontalAlignmentSelector)
                       .on('selected', function(event) {
                    $.each(self.propertyItems, function(index, item) {
                        if (item.length <= 0) {
                            return;
                        }
                        var id = item.data().type;
                        if (id == "cell-align") {
                            item.setVal(event.selected);
                        }
                    });
                });

            }
            if (!this.cellVerticalAlignmentCUI) {
                this.cellVerticalAlignmentCUI = new CUI.Select({ element: this.$dialog.find(this.cellVerticalAlignmentSelector)[0]});
                //On selection change, update propertyItems with new value of selections
                var self = this;
                $(this.cellVerticalAlignmentSelector)
                       .on('selected', function(event) {
                    $.each(self.propertyItems, function(index, item) {
                        if (item.length <= 0) {
                            return;
                        }
                        var id = item.data().type;
                        if (id == "cell-valign") {
                            item.setVal(event.selected);
                        }
                    });
                });
            }
            if (!this.cellTypeCUI) {
                this.cellTypeCUI = new CUI.Select({ element: this.$dialog.find(this.cellTypeSelector)[0]});
                //On selection change, update propertyItems with new value of selections
                var self = this;
                $(this.cellTypeSelector)
                       .on('selected', function(event) {
                    $.each(self.propertyItems, function(index, item) {
                        if (item.length <= 0) {
                            return;
                        }
                        var id = item.data().type;
                        if (id == "cell-cellType") {
                            item.setVal(event.selected);
                        }
                        if (self.cellType != event.selected) {
                            self.cellType = event.selected;
                            self.$cellHeadersContainer.toggle();
                            self.$cellScopeContainer.toggle();
                            self.$cellIdContainer.toggle();
                        }
                    });
                });
            }
            if (!this.headerCellScopeCUI) {
                this.headerCellScopeCUI = new CUI.Select({ element: this.$dialog.find(this.cellScopeSelector)[0]});
                //On selection change, update cellScope with new value of selection
                var self = this;
                $(this.cellScopeSelector)
                       .on('selected', function(event) {
                    self.cellScope = (event.selected == "none" ? CUI.rte.commands.Table.CONFIG_NONE : event.selected);
                });
            }
            this.config = config;
            this.applyFn = config.execFn;
        },

        onShow: function() {
            //set dialog input values to those obtained from table/cell being edited
            var table = this.editorKernel.queryState("table");
            var cell = this.editorKernel.queryState("modifycell");
            var com = CUI.rte.Common;
            this.cellType = com.isTag(cell, "th") ? "th" : "td";
            var cellStyle = com.getAttribute(cell, "style");
            var captionField = com.getChildNodesByType(table, "caption");
            var caption = captionField.length > 0 ? captionField[0].innerHTML : null;
            this.$width.val(com.getAttribute(table, "width"));
            this.$height.val(com.getAttribute(table, "height"));
            this.$cellPadding.val(com.getAttribute(table, "cellpadding"));
            this.$cellSpacing.val(com.getAttribute(table, "cellspacing"));
            this.$border.val(com.getAttribute(table, "border"));
            this.$caption.val(caption);
            this.$cellWidth.val(com.getAttribute(cell, "width"));
            this.$cellHeight.val(com.getAttribute(cell, "height"));
            this.$hiddenHeaderSwitch.prop("checked", com.getAttribute(cell, "hiddenheader"));
            var align = null;
            if (cellStyle) {
                var styles = cellStyle.split(";");
                for (var i = 0; i < styles.length; i++) {
                    if (styles[i].trim().indexOf("text-align") == 0) {
                        align = styles[i].split(":")[1].trim();
                    }
                }
            }
            this.setValueForSelect(this.cellHorizontalAlignmentSelector, "cell-align", align);
            this.setValueForSelect(this.cellVerticalAlignmentSelector, "cell-valign", com.getAttribute(cell, "valign"));
            this.setValueForSelect(this.cellTypeSelector, "cell-cellType", com.isTag(cell, "th") ? "th" : "td");
            if (this.cellType == "th") {
                var select = $(this.cellScopeSelector).data("select");
                var value = com.getAttribute(cell, "scope");
                value = value ? value : "none";
                select.setValue(value);
                this.cellScope = (value == "none" ? CUI.rte.commands.Table.CONFIG_NONE : value);
                this.$cellId.val(com.getAttribute(cell, "id"));
                this.$cellHeadersContainer.hide();
                this.$cellIdContainer.show();
                this.$cellScopeContainer.show();
            } else {
                this.$cellHeaders.val(com.getAttribute(cell, "headers"));
                this.$cellScopeContainer.hide();
                this.$cellIdContainer.hide();
                this.$cellHeadersContainer.show();
            }
        }
    });


})(window.jQuery);