/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2016 Adobe Systems Incorporated
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

CUI.rte.genhtml.GenericHtmlPlugin = new Class({

    toString: "Detector",

    extend: CUI.rte.plugins.Plugin,

    tools: null,
    
    toolUI: null,

    placeholderNode: null,
    
    defaultConverter: null,

    excludeForToolkit: [ "ext" ],


    _createTools: function() {
        this.tools = [ ];
        if (this.config.hasOwnProperty("tools")) {
            var toolDefs = this.config.tools;
            toolDefs = CUI.rte.Common.toArray(toolDefs);
            if (CUI.rte.Utils.isArray(toolDefs)) {
                for (var t = 0; t < toolDefs.length; t++) {
                    var toolDef = toolDefs[t];
                    if (toolDef.hasOwnProperty("converter")
                            && toolDef.hasOwnProperty("editor")) {
                        this.tools.push({
                            "ui": toolDef.ui,
                            "converter": toolDef.converter,
                            "editor": toolDef.editor,
                            "editorConfig": toolDef.editorConfig
                        });
                    }
                }
                delete this.config.tools;
            }
        }
    },

    getFeatures: function() {
        return [ "generichtml" ];
    },

    initializeUI: function(tbGenerator, options) {
        var plg = CUI.rte.plugins;
        this._createTools();
        if (this.isFeatureEnabled("generichtml")) {
            this.toolUI = tbGenerator.createElement("generichtml", this, false,
                    this.getTooltip("generichtml"));
            tbGenerator.addElement("generichtml", plg.Plugin.SORT_GENERICHTML,
                    this.toolUI, 10);
        }
    },

    execute: function(pluginCommand, value, envOptions) {
        var com = CUI.rte.Common;
        var ghr = CUI.rte.genhtml.GenericHtmlRules;
        var self = this;

        var context = envOptions.editContext;
        if (this.placeholderNode == null) {
            var converterId = this.defaultConverter;
            if (!converterId) {
                if (this.tools.length > 0) {
                    converterId = this.tools[0].converter;                
                }
            }
            if (!converterId) {
                return;
            }
            var converterChain = this.editorKernel.htmlRules.genericHtml.converterChain;
            var createConverter = converterChain.getByName(converterId);
            this.placeholderNode = createConverter.deserialize(context).replacement;
        }
        
        var converter = com.getAttribute(this.placeholderNode, ghr.PLACEHOLDER_ATTRIB,
                true);
        var toolIndex = com.arrayIndex(this.tools, converter,
                function(element, cmp) {
                    return element.converter === cmp;
                });
        if (toolIndex >= 0) {
            var tool = this.tools[toolIndex];
            var editorId = tool.editor;
            var toolkitId = this.editorKernel.uiToolkit;

            var editor = CUI.rte.genhtml.EditorRegistry.create(editorId);
            editor.config(tool.editorConfig || { }, {
                "id": toolkitId,
                "toolkit": CUI.rte.ui.ToolkitRegistry.get(toolkitId),
                "editorKernel": this.editorKernel
            });
            editor.start(this.placeholderNode, function(dom, requiresDelete) {
                var parentNode = dom.parentNode;
                if (requiresDelete) {
                    var sel = CUI.rte.Selection;
                    var bookmark = sel.createSelectionBookmark(context);
                    if (parentNode) {
                        parentNode.removeChild(dom);
                    }
                    sel.selectBookmark(context, bookmark);
                } else {
                    if (!parentNode) {
                        self.editorKernel.execCmd("inserthtml",
                                com.getOuterHTML(context, dom));
                    }
                }
            });
        }
    },

    updateState: function(selDef) {
        var com = CUI.rte.Common;
        var ghr = CUI.rte.genhtml.GenericHtmlRules;
        var selectedNode = selDef.selectedDom;
        var isPlaceholder = false;
        if (selectedNode) {
            if (ghr.isPlaceholder(selectedNode)) {
                var converter = com.getAttribute(selectedNode, ghr.PLACEHOLDER_ATTRIB,
                        true);
                isPlaceholder = com.arrayContains(this.tools, converter,
                        function(element, cmp) {
                            return element.converter === cmp;
                        });
            }
        }
        this.placeholderNode = undefined;
        if (this.toolUI) {
            // this.toolUI.setDisabled(!isPlaceholder);
            if (isPlaceholder) {
                this.placeholderNode = selectedNode;
            }
        }
    },

    isHeadless: function(command, value) {
        return (this.tools.length == 0);
    }

});

// register plugin
CUI.rte.plugins.PluginRegistry.register("generichtml", CUI.rte.genhtml.GenericHtmlPlugin);