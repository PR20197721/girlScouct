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

    CUI.rte.ui.cui.ElementImpl = new Class({

        toString: "ElementImpl",

        extend: CUI.rte.ui.TbElement,

        dom: null,

        $ui: null,


        notifyGroupBorder: function(isFirst) {
            // TODO ...?
        },


        // Interface implementation ------------------------------------------------------------

        addToToolbar: function(toolbar) {
            var commandRef = this.plugin.pluginId + "#" + this.id;
            toolbar.push({
                "ref": commandRef,
                "plugin": this.plugin.pluginId,
                "command": this.id,
                "tooltip": this.tooltip ? this.tooltip.title : ""
            });
        },

        notifyToolbar: function(toolbar, skipHandlers) {
            var pluginId = this.plugin.pluginId;
            var self = this;
            var $cont = toolbar.getToolbarContainer();
            var $tbCont = CUI.rte.UIUtils.getToolbarContainer($cont, toolbar.tbType);
            var $newUI = $tbCont.find('button[data-action="' + pluginId + '#' + this.id + '"]');
            if ($newUI.length <= 0) {
                // this element doesn't exist in the passed toolbar.
                return;
            }
            this.$ui = $newUI;
            this.toolbar = toolbar;
            if (!skipHandlers) {
                this.$ui.on("click.rte-handler", function (e) {
                    var editorKernel = self.plugin.editorKernel;
                    if (!self.$ui.hasClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS)) {
                        var editContext = editorKernel.getEditContext();
                        if (self.toggle && !self._isHighlighted) {
                            self.setSelected(!self.isSelected());
                        }
                        editContext.setState("CUI.SelectionLock", 1);
                        var cmd = (self.cmdDef ? self.cmdDef.cmd : self.id);
                        var cmdValue = (self.cmdDef ? self.cmdDef.cmdValue : undefined);
                        var env = {
                            "editContext": editContext
                        };
                        if (self.plugin.isHeadless(cmd, cmdValue)) {
                            var dm = editorKernel.getDialogManager();
                            dm.hide();
                        }
                        var opts = self.plugin.execute(cmd, cmdValue, env);
                        editorKernel.enableFocusHandling();
                        if (!opts || !opts.preventTouchFocus) {
                            editorKernel.focus(editContext);
                        }
                    }
                    e.stopPropagation();
                });
            }
        },

        createToolbarDef: function() {
            return {
                "id": this.id,
                "element": this
            };
        },

        setDisabled: function(isDisabled) {
            // Background toolbars like tableEditOptions toolbar might not be created during RTE initialization. So the
            // following check is required as $ui would be null for the elements meant for the background toolbar until
            // that toolbar is created.
            if (!this.$ui) {
                return;
            }
            var pm = this.toolbar.getPopoverManager();
            var $trigger = pm.getTriggerForElement(this.$ui);
            if (isDisabled) {
                this.$ui.addClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                this.$ui.attr("disabled", "disabled");
                if ($trigger && $trigger.length) {
                    $trigger.addClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                    $trigger.attr('disabled', 'disabled');
                }
            } else {
                this.$ui.removeClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                this.$ui.removeAttr("disabled");
                if ($trigger && $trigger.length) {
                    $trigger.removeClass(CUI.rte.Theme.TOOLBARITEM_DISABLED_CLASS);
                    $trigger.removeAttr('disabled');
                }
            }
        },

        setHighlighted: function(isHighlighted) {
            // see #setDisabled for explanation as to why this check is required
            if (!this.$ui) {
                return;
            }
            this._isHighlighted = isHighlighted;
            if (isHighlighted) {
                this.$ui.addClass("is-itemHighlighted");
            } else {
                this.$ui.removeClass("is-itemHighlighted");
            }
        },

        setSelected: function(isSelected, suppressEvent) {
            // see #setDisabled for explanation as to why this check is required
            if (!this.$ui) {
                return;
            }
            this._isSelected = isSelected;
            if (isSelected) {
                this.$ui.addClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS);
            } else {
                this.$ui.removeClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS);
            }
            var pm = this.toolbar.getPopoverManager();
            var $trigger = pm.getTriggerForElement(this.$ui);
            if ($trigger && $trigger.length) {
                var elements = pm.getElementsForTrigger($trigger);
                elements = (elements ? elements.elements : [ ]);
                var selected = [ ];
                var elementCnt = elements.length;
                for (var e = 0; e < elementCnt; e++) {
                    var $el = $(elements[e]);
                    if ($el.hasClass(CUI.rte.Theme.TOOLBARITEM_SELECTED_CLASS)) {
                        selected.push($el);
                    }
                }
                if (selected.length > 0) {
                    $trigger.addClass("is-itemsSelected");
                } else {
                    $trigger.removeClass("is-itemsSelected");
                }
                var baseIcon = $trigger.data("base-icon");
                if (baseIcon) {
                    var targetIcon = baseIcon;
                    if (selected.length === 1) {
                        var selIcon = CUI.rte.UIUtils.determineIconClass(selected[0]);
                        targetIcon = (selIcon ? selIcon : targetIcon);
                    }
                    var currentIcon = $trigger.data("current-icon");
                    if (currentIcon !== targetIcon) {
                        if (currentIcon) {
                            $trigger.removeClass(currentIcon);
                        }
                        $trigger.addClass(targetIcon);
                        $trigger.data("current-icon", targetIcon);
                    }
                }
            }
        },

        isSelected: function() {
            return this._isSelected;
        },

        destroy: function() {
            // see #setDisabled for explanation as to why this check is required
            if (!this.$ui) {
                return;
            }
            this.$ui.off("click.rte-handler");
        }

    });

})(window.jQuery);