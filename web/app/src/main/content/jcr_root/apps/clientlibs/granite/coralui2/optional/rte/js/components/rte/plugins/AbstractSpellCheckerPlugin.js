/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2012 Adobe Systems Incorporated
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

/**
 * @class CUI.rte.plugins.AbstractSpellCheckerPlugin
 * @extends CUI.rte.plugins.Plugin
 * <p>This class implements the spellchecker functionality as a plugin.</p>
 * <p>The plugin ID is "<b>spellcheck</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>checktext</b> - adds a button to spellcheck the entire text</li>
 * </ul>
 * @since 5.3
 */
CUI.rte.plugins.AbstractSpellCheckerPlugin = new Class({

    toString: "AbstractSpellCheckerPlugin",

    extend: CUI.rte.plugins.Plugin,

    /**
     * @private
     */
    checkTextUI: null,

    isInspectionOn: false,

    spellcheckerDialog: null,

    /**
     * @private
     */
    misSpelledWords: null,

    doCheckText:  function(html, contentPath, successFn, failureFn) {
        //must be implemented by child class to do the spellchecking
    },

    /**
     * @cfg {String} invalidStyle
     * Style definition that is used to mark wrongly spelled words; defaults to
     * "background-color: #ffffaa;"
     */

    /**
     * @cfg {String} invalidClass
     * CSS class that is used to mark wrongly spelled words; defaults to null
     */

    /**
     * @private
     */
    checkText: function(context) {
        this.clearInvalidationMarks(context);
        this.doCheckText(this.editorKernel.getProcessedHtml(), this.editorKernel.getContentPath(),
                    CUI.rte.Utils.scope(this.checkSuccess, this), CUI.rte.Utils.scope(this.checkFailure, this));
    },

    checkSuccess: function(spellcheckResults) {
        var hasErrors = false;
        var words = spellcheckResults.words;
        var wordCnt = words.length;
        this.misSpelledWords = [];
        for (var w = 0; w < wordCnt; w++) {
            var word = words[w];
            var result = word.result;
            if (!result.isCorrect) {
                var startPos = word.start;
                var charCnt = word.chars;
                var suggestions = result.suggestions;
                this.misSpelledWords.push({
                    "word" : word,
                    "span" : this.markInvalidWord(this.editorKernel.getEditContext(), startPos, charCnt, suggestions)[0]
                });
                hasErrors = true;
            }
        }
        if (!hasErrors) {
            this.editorKernel.getDialogManager().alert(
                    CUI.rte.Utils.i18n("plugins.spellCheck.spellChecking"),
                    CUI.rte.Utils.i18n("plugins.spellCheck.noMistakeAlert"));
            return;
        }
        this.isInspectionOn = true;
        this.checkTextUI.setHighlighted(true);
    },

    checkFailure: function() {
        this.checkTextUI.setSelected(false);
        this.editorKernel.getDialogManager().alert(
                CUI.rte.Utils.i18n("plugins.spellCheck.spellChecking"),
                CUI.rte.Utils.i18n("plugins.spellCheck.failAlert"));
    },

    markInvalidWord: function(context, startPos, charCnt, suggestions) {
        var com = CUI.rte.Common;
        var dpr = CUI.rte.DomProcessor;
        var startDef = com.getNodeAtPosition(context, startPos, true);
        var endDef = com.getNodeAtPosition(context, startPos + charCnt, true);
        // handle EOP situations correctly
        var endNode = endDef.dom;
        var endOffset = endDef.offset;
        if ((endNode.nodeType == 1) && (endDef.offset == null)) {
            var baseEndNode = endNode;
            endNode = com.getLastTextChild(baseEndNode);
            if (!endNode) {
                endNode = com.getPreviousTextNode(context, baseEndNode);
            }
            endOffset = com.getNodeCharacterCnt(endNode);
        }
        var nodeList = dpr.createNodeList(context, {
            "startNode": startDef.dom,
            "startOffset": startDef.offset,
            "endNode": endNode,
            "endOffset": endOffset
        });
        var suggAttrib = null;
        if (suggestions != null) {
            var suggCnt = suggestions.length;
            for (var s = 0; s < suggCnt; s++) {
                var suggestion = suggestions[s];
                if (s == 0) {
                    suggAttrib = suggestion;
                } else {
                    suggAttrib += "|" + suggestion;
                }
            }
        }
        var attribs = {
            "_rtetemp": "spchk"
        };
        if (this.config.invalidStyle) {
            attribs["style"] = this.config.invalidStyle;
        }
        if (this.config.invalidClass) {
            attribs["className"] = this.config.invalidClass;
        }
        if (suggAttrib != null) {
            attribs["_rtespchksugg"] = suggAttrib;
        }
        return nodeList.surround(context, "span", attribs);
    },

    clearInvalidationMarks: function(context) {
        var dpr = CUI.rte.DomProcessor;
        dpr.removeTagsFromHierarchy(context.root, {
            "tagName": "span",
            "attribValues": { "_rtetemp": "spchk" }
        });
        this.isInspectionOn = false;
        this.checkTextUI.setHighlighted(false);
    },

    _init: function(editorKernel) {
        this.inherited(arguments);
        //this key up handler will be called only when the dialog is closed because editor kernel is locked when
        //dialog is open. So we will have to attach a handler later when the dialog is shown and remove it when
        //dialog is hidden.
        editorKernel.addPluginListener("keyup", this.handleOnKey, this, this, false);
        editorKernel.addPluginListener("sourceedit", this.handleSourceEdit, this, this,
                false);
    },

    handleOnKey: function(e) {
        if (!this.isAnyFeatureEnabled()) {
            return;
        }

        //if it is not a caret key or caret movement (page up, down, end, home),
        //clear the invalid checks and close dialog
        if (e.charCode < 33 || e.charCode > 40) {
            var dm = this.editorKernel.getDialogManager();
            var context = this.editorKernel.getEditContext();
            this.clearInvalidationMarks(context);
            this.checkTextUI.setSelected(false);
            // hide if dialog is already shown
            if (dm.isShown(this.spellcheckerDialog)) {
                dm.hide(this.spellcheckerDialog);
            }
        }
    },

    handleSourceEdit: function(e) {
        // deselect spellcheck button if sourceedit mode has been enabled, as results will
        // get invalid and highlights be removed anyway
        if (e.enabled && this.checkTextUI) {
            this.checkTextUI.setSelected(false, true);
        }
    },

    getFeatures: function() {
        return [ "checktext" ];
    },

    initializeUI: function(tbGenerator) {
        var plg = CUI.rte.plugins;
        var ui = CUI.rte.ui;
        if (this.isFeatureEnabled("checktext")) {
            this.checkTextUI = tbGenerator.createElement("checktext", this, true,
                    this.getTooltip("checktext"));
            tbGenerator.addElement("spellcheck", plg.Plugin.SORT_LISTS, this.checkTextUI,
                    10);
        }
    },

    getMisSpelledWord: function(selectionBookmark) {
        if (!selectionBookmark || !this.misSpelledWords) {
            return;
        }
        for (var w = 0; w < this.misSpelledWords.length; w++) {
            if (this.misSpelledWords[w] == null) {
                continue;
            }
            var word = this.misSpelledWords[w].word;
            if (word.start <= selectionBookmark.startPos
                    && word.start + word.chars >= selectionBookmark.startPos + selectionBookmark.charCnt) {
                return {"index" : w, "word" : this.misSpelledWords[w]};
            }
        }
    },

    handleSecondClick: function(context) {
        var dm = this.editorKernel.getDialogManager();
        // hide if dialog is already shown
        if (dm.isShown(this.spellcheckerDialog) && dm.toggleVisibility(this.spellcheckerDialog)) {
            dm.hide(this.spellcheckerDialog);
            return;
        }
        var sel = CUI.rte.Selection;
        var selectionBookmark = sel.createSelectionBookmark(context);
        var misSpelledWordObject = this.getMisSpelledWord(selectionBookmark);

        if (selectionBookmark && misSpelledWordObject) {
            var misSpelledIndex = misSpelledWordObject.index;
            var misSpelledWord = misSpelledWordObject.word;
            //limit the suggestions to 5
            var suggestions = [];
            suggestions = misSpelledWord.word.result.suggestions.slice(0,5);
            var suggestionCount = suggestions.length;
            //trim the length of a suggestion to 25. send the original word as well as trimmed word. The trimmed version
            //would be displayed in suggestion popover and original would be used to replace the content.
            for (var s = 0; s < suggestionCount; s++) {
                var suggestion = suggestions[s];
                var suggestionsObject = {"display" : suggestion.length > 25
                    ? suggestion.substring(0, 22) + "..." : suggestion, "original" : suggestion};
                suggestions.push(suggestionsObject);
            }
            if (!suggestionCount) {
                suggestions = [{"display" : CUI.rte.Utils.i18n("plugins.spellCheck.noSuggestions")}];
            }
            var dialogConfig= {"suggestions" : suggestions.slice(suggestionCount)};
            dialogConfig.parameters = {
                "command": this.pluginId + "#checktext"
            };
            this.spellcheckerDialog = dm.create(CUI.rte.ui.DialogManager.DLG_SPELLCHECKER, dialogConfig);
            var self = this;
            this.spellcheckerDialog.$dialog.on("click.rte-dialog", "button[data-type=\"replaceWord\"]",
                function(e) {
                    var replacement = $(e.target).data("value");
                    var span = misSpelledWord.span;
                    span.innerHTML = replacement;
                    CUI.rte.DomProcessor.removeWithoutChildren(span);
                    self.misSpelledWords[misSpelledIndex] = null;
                    dm.hide(self.spellcheckerDialog);
                    e.preventDefault();
                    e.stopPropagation();
                }
            );
            this.spellcheckerDialog.onShow = function() {
                $(self.editorKernel.getEditContext().root).on("keyup.rte-spellcheckerDialogOpen",
                    CUI.rte.Utils.scope(self.handleOnKey, self));
            }
            this.spellcheckerDialog.onHide = function() {
                $(self.editorKernel.getEditContext().root).off("keyup.rte-spellcheckerDialogOpen");
            }
            this.spellcheckerDialog.show();
        } else {
            this.clearInvalidationMarks(context);
        }
    },

    execute: function(id, value, env) {
        switch (id) {
            case "checktext":
                if (!this.isInspectionOn) {
                    this.checkText(env.editContext);
                } else {
                    this.handleSecondClick(env.editContext);
                }
                break;
        }
    },

    updateState: function(selDef) {
        // nothing to do here
    }

});