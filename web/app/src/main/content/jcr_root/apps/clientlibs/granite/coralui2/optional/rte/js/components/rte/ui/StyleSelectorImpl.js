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

    CUI.rte.ui.cui.StyleSelectorImpl = new Class({

        toString: "StyleSelectorImpl",

        extend: CUI.rte.ui.TbStyleSelector,

        // Helpers -----------------------------------------------------------------------------

        notifyGroupBorder: function() {
            // do nothing
        },

        _getStyleId: function($button) {
            var styleId = null;
            var targetId = $button.data("action");
            var hashPos = targetId.indexOf("#");
            if (hashPos >= 0) {
                styleId = targetId.substring(hashPos + 1);
            }
            return styleId;
        },

        _resetSelection: function() {
            var $selectorItems = this.$ui.find("i");
            $selectorItems.removeClass("coral-Icon--check");
        },

        _select: function(styleToSelect) {
            var self = this;
            this.$ui.each(function() {
                var $fmtItem = $(this);
                var styleId = self._getStyleId($fmtItem);
                if (styleId && (styleId === styleToSelect)) {
                    $fmtItem.find("i").addClass("coral-Icon--check");
                }
            });
        },


        // Interface implementation ------------------------------------------------------------

        addToToolbar: function(toolbar) {
            this.toolbar = toolbar;
        },

        notifyToolbar: function(toolbar, skipHandlers) {
            this.toolbar = toolbar;
            var self = this;
            var pluginId = this.plugin.pluginId;
            var $cont = toolbar.getToolbarContainer();
            var tbType = toolbar.tbType;
            if (!this.plugin.hasStylesConfigured()) {
                var styles = [ ];
                var $popover = CUI.rte.UIUtils.getPopover(pluginId, tbType, $cont);
                var $styleItems = $popover.find("li");
                $styleItems.each(function() {
                    var $button = $(this).find("button");
                    var href = $button.data("action");
                    var action = href.split("#");
                    if ((action.length === 2) && (action[0] === pluginId)) {
                        styles.push({
                            "cssName": action[1],
                            "text": $button.text()
                        });
                    }
                });
                this.plugin.setStyles(styles);
            }
            var $tbCont = CUI.rte.UIUtils.getToolbarContainer($cont, tbType);
            this.$trigger = $tbCont.find('button[data-action="#' + pluginId + '"]');
            this.$ui = $tbCont.find('button[data-action^="' + pluginId + '#"]');
            if (!skipHandlers) {
                this.$ui.on("click.rte-handler", function (e) {
                    if (!self.$ui.hasClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS)) {
                        var targetId = $(this).data("action");
                        var hashPos = targetId.indexOf("#");
                        var style = targetId.substring(hashPos + 1);
                        var editContext = self.plugin.editorKernel.getEditContext();
                        editContext.setState("CUI.SelectionLock", 1);
                        self.plugin.execute("applystyle", style);
                        self.plugin.editorKernel.enableFocusHandling();
                        self.plugin.editorKernel.focus(editContext);
                    }
                    // e.stopPropagation();
                });
            }
        },

        createToolbarDef: function() {
            return {
                "id": this.id,
                "element": this
            };
        },

        initializeSelector: function() {
            // TODO ...?
        },

        getSelectorDom: function() {
            return this.$ui;
        },

        getSelectedStyle: function() {
            return null;
        },

        selectStyles: function(styles, selDef) {
            this.setSelected(styles.length > 0);
            this._resetSelection();
            for (var s = 0; s < styles.length; s++) {
                this._select(styles[s].className);
            }
        },

        setDisabled: function(isDisabled) {
            var com = CUI.rte.Common;
            if (com.ua.isTouchInIframe) {
                // workaround for CUI-649; see ElementImpl#setDisabled for an explanation
                this.$trigger.css("display", "none");
            }
            if (isDisabled) {
                this.$trigger.addClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                this.$trigger.attr("disabled", "disabled");
            } else {
                this.$trigger.removeClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                this.$trigger.removeAttr("disabled");
            }
            if (com.ua.isTouchInIframe) {
                // part 2 of workaround for CUI-649
                var self = this;
                window.setTimeout(function() {
                    self.$trigger.css("display", "inline-block");
                }, 1);
            }
        },

        setSelected: function(isSelected, suppressEvent) {
            this._isSelected = isSelected;
            if (isSelected) {
                this.$trigger.addClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS);
            } else {
                this.$trigger.removeClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS);
            }
        },

        isSelected: function() {
            return this._isSelected;
        }

    });

})(window.jQuery);