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

    CUI.rte.ui.cui.LinkBaseDialog = new Class({

        extend: CUI.rte.ui.cui.AbstractDialog,

        toString: "LinkBaseDialog",

        selectCUI: null,

        targetSelectorString: "span.coral-RichText-dialog-column--targetField",

        getDataType: function() {
            return "link";
        },

        handleLinkSuccess: function(href){
            if(href){
                this.$hrefField.val(href);
            }
        },

        // config.linkOptionsLoader - to load options for path browser to complete internal links
        initialize: function(config) {
            this.config = config;
            var self = this;
            this.context = this.editorKernel.getEditContext();
            this.touchScrollLimiter = this.context.getState("CUI.touchScrollLimiter");

            // Initialize PathBrowser
            this.$dialog.find("span.coral-RichText-pathbrowser").pathBrowser({
                optionLoader: config.linkOptionsLoader,

                /* options for autocomplete and picker: */
                rootPath: config.rootPath,
                showTitles: false,

                /* autocomplete configuration: */
                optionLoaderRoot: null, // provide property path to array in return value of optionLoader, e.g. 'results.values',
                optionValueReader: function (object) {
                    return '' + object;
                },
                optionTitleReader: function (object) {
                    return '' + object;
                },

                /* picker configuration: */
                pickerSrc: config.pickerSrc,
                pickerTitle: CUI.rte.Utils.i18n("dialog.link.pickerTitle"),
                crumbRoot: {
                    title: config.crumbRoot,
                    icon: 'coral-Icon-home'
                }
            });

            this.$hrefField = config.pathbrowserPicker ?
                                this.$dialog.find(".coral-RichText-pathbrowser input[type='text']") :
                                this.$dialog.find("input.coral-RichText-pathbrowser[type='text']");

            this.$hrefField.on('change', function(event) {
                if (event.originalEvent === undefined) {
                    // captures the code change trigger:
                    // - picker selection
                    // - dropdown selection
                    // - autofill
                    // We do not want to encode the value if the user manually types or pastes the path in the input field
                    // (and triggers original native change event) as we expect it to be already encoded (coming from browser url etc.)
                    $(this).val(encodeURI($(this).val()));
                }
            });

            if (!this.selectCUI) {
                this.selectCUI = new CUI.Select({element: this.$dialog.find(this.targetSelectorString)[0]});
            }
            // Cancel all keydown events
            this.$dialog.on("keydown", this.handleKeyDown);
        },

        onShow: function() {
            if (!CUI.rte.Common.ua.isTouch) {
                var self = this;
                window.setTimeout(function() {
                    self.$hrefField.focus();
                }, 1);
            } /* else {
                this.$hrefField.focus();
                this.$hrefField[0].select();
            } */
            if(this.touchScrollLimiter) {
                this.touchScrollLimiter.suspend();
            }
        },

        onHide: function() {
            if(this.touchScrollLimiter) {
                this.touchScrollLimiter.reactivate()
            }
        },

        handleKeyDown: function(event){
            event.stopPropagation();
        },

        preprocessModel: function() {
            if (this.objToEdit && this.objToEdit.dom) {
                this.objToEdit.href = CUI.rte.HtmlRules.Links.getLinkHref(
                    this.objToEdit.dom);
                var com = CUI.rte.Common;
                var attribNames = com.getAttributeNames(this.objToEdit.dom, false,
                    function(dom, attribName, attribNameLC) {
                        // exclude href, rte_href & target from generic attribute handling,
                        // as they are handled explicitly and not generically
                        return attribNameLC == com.HREF_ATTRIB || attribNameLC == "href"
                            || attribNameLC == "target";
                    });
                for (var i = 0; i < attribNames.length; i++) {
                    var attribName = attribNames[i];
                    var value = com.getAttribute(this.objToEdit.dom, attribName);
                    if (typeof value !== 'undefined') {
                        this.objToEdit.attributes[attribName] = value;
                    }
                }
            }
        },

        dlgFromModel: function() {

            var value;
            if (this.$hrefField) {
                value = "";
                if (this.objToEdit) {
                    var href = this.objToEdit.href;
                    if (href) {
                        value = href;
                    }
                }
                this.$hrefField.val(value);
            }
            var targetSelect = $(this.targetSelectorString).data("select");
            var target = (this.objToEdit && this.objToEdit.target
                ? this.objToEdit.target.toLowerCase() : null);
            targetSelect.setValue(target || '');
            var titleField = this.getFieldByType("title");
            if(titleField){
                value = (this.objToEdit && this.objToEdit.attributes
                        && this.objToEdit.attributes["title"]
                    ? this.objToEdit.attributes["title"] : null);
                titleField.val( value );
            }
        },

        validate: function() {

            if (this.$hrefField) {
                var href = this.$hrefField.val();
                if (href && (href.length > 0)) {
                    return true;
                }
            }
            return false;
        },

        dlgToModel: function() {
            if (this.objToEdit) {

                if (this.$hrefField) {
                    var href = this.$hrefField.val();
                    if (href) {
                        this.objToEdit.href = href;
                    }
                }
                var targetSelect = $(this.targetSelectorString).data("select");
                var target = targetSelect.getValue();
                if (target) {
                    this.objToEdit.target = target;
                } else {
                    this.objToEdit.target = null;
                }
                var titleField = this.getFieldByType("title");
                if (titleField){
                    var titleVal = titleField.val();
                    if (titleVal != null) {
                        this.objToEdit.attributes["title"] = titleVal;
                    }
                }
            }
        },

        postprocessModel: function() {
            var linkRules = this.getParameter("linkRules");
            if (linkRules) {
                linkRules.applyToObject(this.objToEdit);
            }
        }

    });


})(window.jQuery);