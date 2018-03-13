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

    function disableButton($btn, isDisabled) {
        if ($btn && $btn.length) {
            $btn.prop("disabled", isDisabled);
            if (isDisabled) {
                $btn.addClass("is-disabled");
            } else {
                $btn.removeClass("is-disabled");
            }
        }
    }

    function isDisabled($btn) {
        return $btn.prop("disabled");
    }

    CUI.rte.ui.cui.FindReplaceDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractBaseDialog,

        toString: "FindReplaceDialog",

        isReplace: false,

        canReplace: false,

        currentFindTerm: undefined,


        _adjustButtonState: function() {
            var text = this.$findField.val();
            var isEnabled = (text.length > 0);
            disableButton(this.$findButton, !isEnabled);
            disableButton(this.$replaceButton,
                    !(isEnabled && this.canReplace && (this.currentFindTerm === text)));
            disableButton(this.$replaceAllButton, !isEnabled);
        },

        getDataType: function(config) {
            return (config.isReplace ? "replace" : "find");
        },

        initialize: function(config) {
            this.isReplace = config.isReplace;
            this.findFn = config.findFn;
            this.replaceFn = config.replaceFn;
            var self = this;
            this.$findField = this.$dialog.find("input[data-type=\"find\"]");
            this.$findField.on("keyup", function(e) {
                self._adjustButtonState();
            });
            this.$dialog.on("focus", "input[type=\"text\"]", function(e) {
                self.opaque();
                self.setRestoreSelectionOnHide(true);
            });
            this.$replaceField = this.$dialog.find("input[data-type=\"replace\"]");
            this.$matchCaseBox = this.$dialog.find("input[data-type=\"matchCase\"]");
            this.$findButton = this.$dialog.find("button[data-type=\"execFind\"]");
            this.$findButton.on("click", function(e) {
                if (!isDisabled(self.$findButton)) {
                    // on find, we remember the actual find term (as we should deactivate
                    // "Replace" if the term changes to avoid inconsistencies) and hide
                    // any "end of text"-alerts that are currently present (because
                    // "Find" starts a new search then)
                    self.currentFindTerm = self.$findField.val();
                    self.$dialog.find(".coral-Alert").alert("hide");
                    self.setRestoreSelectionOnHide(!self.applyDialog(self.findFn, null));
                    e.preventDefault();
                    self.transparent();
                }
                e.stopPropagation();
            });
            disableButton(this.$findButton, true);
            if (this.isReplace) {
                this.$replaceButton = this.$dialog.find(
                        "button[data-type=\"execReplace\"]");
                this.$replaceButton.on("click", function(e) {
                    if (!isDisabled(self.$replaceButton)) {
                        self.applyDialog(self.replaceFn, {
                            "replaceAll": false
                        });
                        e.preventDefault();
                        self.transparent();
                    }
                    e.stopPropagation();
                });
                disableButton(this.$replaceButton, true);
                this.$replaceAllButton = this.$dialog.find(
                        "button[data-type=\"execReplaceAll\"]");
                this.$replaceAllButton.on("click", function(e) {
                    if (!isDisabled(self.$replaceAllButton)) {
                        self.applyDialog(self.replaceFn, {
                            "replaceAll": true
                        });
                        e.preventDefault();
                    }
                    e.stopPropagation();
                });
                disableButton(this.$replaceAllButton, true)
            }
        },

        setMode: function(isInitialFind, canReplace) {
            this.canReplace = canReplace;
            if (this.isReplace && this.$replaceButton) {
                disableButton(this.$replaceButton, !canReplace);
            }
            if (this.isReplace && this.$replaceAllButton) {
                disableButton(this.$replaceAllButton, !canReplace);
            }
        },

        focusFindField: function() {
            this.$findField.focus();
        },

        applyDialog: function(fn, options) {
            options = options || { };
            if (fn) {
                options.findText = this.$findField.val();
                options.matchCase = this.$matchCaseBox.is(":checked");
                if (this.isReplace) {
                    options.replaceText = this.$replaceField.val();
                }
                this.editorKernel.focus();
                return fn(this.editorKernel.getEditContext(), options, this);
            }
            return false;
        },

        onShow: function() {
            this._adjustButtonState();
            if (!CUI.rte.Common.ua.isTouch) {
                var self = this;
                window.setTimeout(function() {
                    self.$findField.focus();
                }, 1);
            }
        }

    });

})(window.jQuery);