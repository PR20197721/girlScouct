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

    function requiresFocus(dom) {
        var $dom = $(dom);
        return $dom.is("input:text") || $dom.is(".coral-SelectList-item") || $dom.is(".coral-Select-select--native");
    }

    function requiresEvent(dom) {
        var $dom = $(dom);
        return $dom.is("input") || $dom.is("button")
                || $dom.is("span.coral-Checkbox-checkmark");
    }

    CUI.rte.ui.cui.AbstractBaseDialog = new Class({

        $container: null,

        $dialog: null,

        mask: null,

        editorKernel: null,

        objToEdit: null,

        range: null,

        isSuspended: false,

        restoreSelectionOnHide: false,

        _command: undefined,

        /**
         * @private
         */
        getTrigger: function() {
          if (!this.editorKernel || !this._command) {
            return;
          }
          return this.editorKernel.getToolbar().getToolbar().parent().find(
             'button[data-action=\'' + this._command + '\']');
        },

        /**
         * @private
         */
        getPopoverManager: function() {
          if (!this.editorKernel) {
            return;
          }
          return this.editorKernel.toolbar.popover;
        },

        attach: function(config, $container, editorKernel, enforceCreation) {
            this.$container = $container;
            this.editorKernel = editorKernel;
            this.setRestoreSelectionOnHide(true);
            this._command = config.parameters.command;
            var mode = config.mode;
            var dataType = this.getDataType(config);
            this.$dialog = CUI.rte.UIUtils.getDialog(dataType, mode, this.$container);
            if (enforceCreation && this.$dialog.length) {
                this.$dialog.remove();
                this.$dialog = CUI.rte.UIUtils.getDialog(dataType, mode, this.$container);
            }
            if (!this.$dialog.length) {
                // if dialog is not available (for example, provided in the markup), create
                // it by using an appropriate Handlebars template
                this.$dialog = $(CUI.rte.Templates["dlg-" + dataType](config));
                var space = mode || "global";
                var $dlgSpace = CUI.rte.UIUtils.getSpace(space, $container);
                $dlgSpace.append(this.$dialog);
            }
            var self = this;
            this.$dialog.finger("click.rte-dialog", function(e) {
                var focusRequired = requiresFocus(e.target);
                var eventRequired = requiresEvent(e.target);
                if (!focusRequired && !self.isSuspended) {
                    self.editorKernel.focus();
                    if (self.range) {
                        CUI.rte.Selection.selectRangeBookmark(
                            self.editorKernel.getEditContext(), self.range);
                    }
                } else if (!focusRequired && !eventRequired) {
                    CUI.rte.UIUtils.killEvent(e);
                    if (!focusRequired) {
                        self.editorKernel.focus();
                    }
                }
            });
            this.$dialog.on("click.rte-dialog", "button[data-type=\"apply\"]",
                    function(e) {
                        self.apply();
                        e.preventDefault();
                        e.stopPropagation();
                    });
            this.$dialog.on("click.rte-dialog", "button[data-type=\"cancel\"]",
                    function(e) {
                        self.cancel();
                        e.preventDefault();
                        e.stopPropagation();
                    });
            this.mask = new CUI.rte.ui.cui.Mask();
            this.initialize(config);
        },

        detach: function() {
            this.$dialog.off("click.rte-dialog");
        },

        initialize: function(config) {
            // may be overridden for additional dialog initialization
        },

        show: function() {
            this.range = CUI.rte.Selection.createRangeBookmark(
                    this.editorKernel.getEditContext());
            var popoverManager = this.getPopoverManager();
            var isSelfTriggered = popoverManager.isShown(this.$dialog);
            var toolbar = this.editorKernel.toolbar;
            popoverManager.hide(true);
            if (this.$dialog && !isSelfTriggered) {
                var self = this;
                popoverManager.use(this.$dialog, this.getTrigger(), toolbar.getToolbar(),
                        function() {
                            self.cancel();
                        });
                this.editorKernel.lock();
                this.editorKernel.fireUIEvent("dialogshow");
                // TODO mask is currently on top of everything - dialog included
                // this.mask.show();
                // maually do the layout - is required here because the editor is already
                // locked, so automatic update will not work
                toolbar.triggerUIUpdate();
                this.onShow();
            } else if (isSelfTriggered) {
                this.editorKernel.focus();
                this.editorKernel.unlock();
                CUI.rte.Selection.selectRangeBookmark(this.editorKernel.getEditContext(),
                        this.range);
                this.editorKernel.fireUIEvent("dialoghide");
            }
        },

        hide: function() {
            this.getPopoverManager().hide();
            this.editorKernel.focus();
            this.editorKernel.unlock();
            if (this.restoreSelectionOnHide) {
                CUI.rte.Selection.selectRangeBookmark(this.editorKernel.getEditContext(),
                        this.range);
            }
            this.editorKernel.fireUIEvent("dialoghide");
            // TODO mask is currently on top of everything - dialog included
            // this.mask.hide();
            // hide the toolbar temporarily on touch devices, as the device will most
            // likely do some screen updates immediately after the command is executed and
            // the dialog is hidden - so this should result in a less disruptive UI behavior
            var tb = this.editorKernel.toolbar;
            if (CUI.rte.Common.ua.isTouch && !tb._isSticky) {
                tb.hideTemporarily();
            }
            this.onHide();
        },

        isShown: function() {
            return this.getPopoverManager().isShown(this.$dialog);
        },

        getDataType: function() {
            throw new Error("Dialog does not implement #getDataType");
        },

        apply: function() {
            this.hide();
        },

        cancel: function() {
            this.hide();
        },

        onShow: function() {
            // method is called after the dialog is shown and can be overridden if necessary
        },

        onHide: function() {
            // method is called after the dialog is hidden and can be overridden if necessary
        },

        transparent: function() {
            this.$dialog.addClass("transparent");
        },

        opaque: function() {
            this.$dialog.removeClass("transparent");
        },

        suspend: function(isSuspended) {
            this.isSuspended = isSuspended;
        },

        setRestoreSelectionOnHide: function(restoreOnHide) {
            this.restoreSelectionOnHide = restoreOnHide;
        },

        mustRecreate: function() {
            return false;
        }

    });

})(window.jQuery);