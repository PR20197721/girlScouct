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

    CUI.rte.ui.cui.CuiDialogManager = new Class({

        toString: "CuiDialogManager",

        extend: CUI.rte.ui.DialogManager,

        editorKernel: null,

        dialogShown: null,

        construct: function(editorKernel) {
            this.editorKernel = editorKernel;
            this.editorKernel.addUIListener("dialoghide",this.onDialogHide,this);
        },

        create: function(dialogId, config) {
            var dialog = undefined;
            // add CUI-specific config data
            var context = this.editorKernel.getEditContext();
            var $container = CUI.rte.UIUtils.getUIContainer($(context.root));
            switch (dialogId) {
                case CUI.rte.ui.DialogManager.DLG_ANCHOR:
                    dialog = new CUI.rte.ui.cui.AnchorDialog();
                    dialog.attach(config, $container, this.editorKernel);
                    break;
                case CUI.rte.ui.DialogManager.DLG_FINDREPLACE:
                    dialog = new CUI.rte.ui.cui.FindReplaceDialog();
                    dialog.attach(config, $container, this.editorKernel);
                    break;
                case CUI.rte.ui.DialogManager.DLG_PASTE:
                    dialog = new CUI.rte.ui.cui.PasteDialog();
                    dialog.attach(config, $container, this.editorKernel, true);
                    break;
                case CUI.rte.ui.DialogManager.DLG_SPECCHARS:
                    dialog = new CUI.rte.ui.cui.SpecialCharsDialog();
                    dialog.attach(config, $container, this.editorKernel);
                    break;
                case CUI.rte.ui.DialogManager.DLG_SPELLCHECKER:
                    dialog = new CUI.rte.ui.cui.SpellcheckerDialog();
                    dialog.attach(config, $container, this.editorKernel, true);
                    break;
                case CUI.rte.ui.DialogManager.DLG_TABLEPROPS:
                    dialog = new CUI.rte.ui.cui.TablePropsDialog();
                    dialog.attach(config, $container, this.editorKernel);
                    break;
                case CUI.rte.ui.DialogManager.DLG_TABLEANDCELLPROPS:
                    dialog = new CUI.rte.ui.cui.TableAndCellPropsDialog();
                    dialog.attach(config, $container, this.editorKernel);
                    break;
                case CUI.rte.ui.DialogManager.DLG_IMAGEPROPS:
                    dialog = new CUI.rte.ui.cui.ImagePropsDialog();
                    dialog.attach(config, $container, this.editorKernel, true);
                    break;
            }
            return dialog;
        },

        mustRecreate: function(dialog) {
            return dialog.mustRecreate();
        },

        show: function(dialog) {
            dialog.show();
            this.dialogShown = dialog;
        },

        hide: function(dialog) {
            dialog = dialog || this.dialogShown;
            if (dialog) {
                dialog.hide();
            }
        },

        onDialogHide: function(dialog) {
            this.dialogShown = undefined;
        },

        alert: function(title, message, fn, options) {
            var self = this;
            options = options || { };
            var boxWidth = undefined;
            var globalDef = undefined;
            var $insertRef;
            var classes = undefined;
            if (options.dialog) {
                var $dialog = options.dialog.$dialog;
                $dialog.find(".coral-Alert").alert("hide");
                $insertRef = $dialog.find(".coral-Popover-content");
                boxWidth = $insertRef.innerWidth()
                        - parseInt($insertRef.css("padding-left"))
                        - parseInt($insertRef.css("padding-right"));
            } else {
                var $editable = $(this.editorKernel.getEditContext().root);
                var $uiContainer = CUI.rte.UIUtils.getUIContainer($editable);
                $insertRef = $uiContainer.find("div[data-type='global']");
                $insertRef = $insertRef.length ? $insertRef : $(document.body);
                classes = "coral-RichText-globalAlert";
                globalDef = {
                    vpWidth: $(window).width()
                };
            }
            var $alert = $("<div></div>");
            var $message = $("<div></div>").addClass("coral-Alert-message");
            $alert.append($message);
            $alert.alert({
                heading: title,
                content: message,
                closable: true,
                size: "small",
                type: options.type || "info"
            });
            if (classes) {
                $alert.addClass(classes);
            }
            $insertRef.append($alert);
            if (globalDef) {
                var width = $alert.outerWidth();
                $alert.offset({
                    "left": Math.round((globalDef.vpWidth - width) / 2),
                    "top": $alert.offset().top
                });
            }
            if (boxWidth) {
                var offset = parseInt($alert.css("border-left"))
                        + parseInt($alert.css("border-right"));
                $alert.innerWidth(boxWidth - offset);
            }
            $alert.on("hide", function(e) {
                $alert.remove();
                $alert = undefined;
            });

            // need to handle the close button on our own in this case - note that we will
            // always react on the click event (due to the infamous iframe touchevent bugs
            // of SafariMobile)

            function close(evt) {
                if ($alert) {
                    $alert.hide();
                }
                self.editorKernel.focus();
                evt.stopPropagation();
                evt.preventDefault();
            }

            if (!CUI.rte.Common.ua.isTouchInIframe) {
                $alert.fipo("tap", "click", close);
                $alert.fipo("tap", "click", "[data-dismiss=\"alert\"]", close);
            } else {
                $alert.on("click", close);
                $alert.on("click", "[data-dismiss=\"alert\"]", close);
            }
        },

        isShown: function(dialog) {
            return dialog && dialog.isShown();
        },

        createDialogHelper: function() {
            return new CUI.rte.ui.cui.CuiDialogHelper(this.editorKernel);
        },

        prepareShow: function(dialog) {
            var popovers = this.editorKernel.toolbar.popover;
            popovers.hide(true);
        },

        toggleVisibility: function(dialog) {
            return true;
        }

    });

})(window.jQuery);