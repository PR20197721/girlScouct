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

CUI.rte.genhtml.RawPayloadEditor = new Class({

    toString: "RawPayloadEditor",
    
    extend: CUI.rte.genhtml.Editor,

    editorKernel: null,

    editorDialog: null,
    
    processor: null,

    config: function(config, toolkitInfo) {
        var dh = CUI.rte.ui.DialogHelper;
        var pla = CUI.rte.genhtml.RawConverter.PAYLOAD_ATTRIB;
        var self = this;

        var title = "";
        var processorId = null;
        if (config) {
            processorId = config.processor;
            title = CUI.rte.Utils.i18n(config.title);
        }
        if (!processorId) {
            throw new Error("Missing processor for the raw HTML.");
        }
        this.processor = CUI.rte.genhtml.RawPayloadEditor.ProcessorRegistry
                .create(processorId);
        if (!this.processor) {
            throw new Error("Invalid processor for the raw HTML: " + processorId);
        }

        this.editorKernel = toolkitInfo.editorKernel;
        var dm = this.editorKernel.getDialogManager();
        var dialogHelper = dm.createDialogHelper();
        var dialogConfig = {
            "configVersion": 1,
            "defaultDialog": {
                "dialogClass": {
                    "type": "rtedefaultdialog"
                }
            },
            "parameters": {
                "editorKernel": this.editorKernel,
                "command": "generichtml#generichtml",
                "selfDestroy": true
            },
            "dialogProperties": {
                "title": title
            },
            "dialogItems": [
                {
                    "item": dialogHelper.createItem(dh.TYPE_TEXTAREA, "payload", ""),
                    "fromModel": function(obj, field) {
                        var value = CUI.rte.Common.getAttribute(obj, pla, true);
                        value = self.processor.preprocess(value);
                        field.value = value;
                    },
                    "toModel": function(obj, field) {
                        var value = self.processor.postprocess(field.value);
                        CUI.rte.Common.setAttribute(obj, pla, value);
                    },
                    "validate": function(field) {
                        var value = field.value;
                        if (value.trim().length == 0) {
                            // empty content is always allowed - we're deleting then
                            return true;
                        }
                        return self.processor.validate(value);
                    }
                }
            ]
        };
        dialogHelper.configure(dialogConfig);
        this.editorDialog = dialogHelper.create();
        dialogHelper.calculateInitialPosition();
    },
    
    start: function(dom, callback) {
        this._callback = callback;
        var dm = this.editorKernel.getDialogManager();
        this.editorDialog.initializeEdit(this.editorKernel, dom,
                CUI.rte.Utils.scope(this.applyEdit, this));
        dm.show(this.editorDialog);
    },
    
    applyEdit: function(context, dom) {
        if (this._callback) {
            var pla = CUI.rte.genhtml.RawConverter.PAYLOAD_ATTRIB;
            var requiresDelete = !CUI.rte.Common.isAttribDefined(dom, pla);
            this._callback(dom, requiresDelete);
        }
    }
    
});

CUI.rte.genhtml.RawPayloadEditor.Processor = new Class({
   
    toString: "RawPayloadEditor.Validator",
    
    preprocess: function(content) {
        throw new Error("Processor.preprocess not implemented.");
    },
    
    postprocess: function(content) {
        throw new Error("Processor.postprocess not implemented.");
    },
    
    validate: function(content) {
        throw new Error("Processor.validate not implemented.");
    }
    
});

CUI.rte.genhtml.RawPayloadEditor.ProcessorRegistry = (function() {

    var _processors = { };

    return {

        toString: "RawPayloadEditor.ProcessorRegistry",

        register: function(typeStr, editorClass) {
            _processors[typeStr] = editorClass;
        },

        create: function(typeStr) {
            if (_processors.hasOwnProperty(typeStr)) {
                return new _processors[typeStr];
            }
            return undefined;
        }

    };

})();

CUI.rte.genhtml.RawPayloadEditor.CommentProcessor = new Class({

    toString: "RawPayloadEditor.CommentProcessor",

    extend: CUI.rte.genhtml.RawPayloadEditor.Processor,

    _hasLeadingSpaces: false,

    preprocess: function(content) {
        var com = CUI.rte.Common;
        if (!com.strStartsWith(content, "<!--") || !com.strEndsWith(content, "-->")) {
            return "";
        }
        var comment = content.substring("<!--".length, content.length - "-->".length);
        if (com.strStartsWith(comment, " ") && com.strEndsWith(comment, " ")) {
            this._hasLeadingSpaces = true;
            comment = comment.substring(1, comment.length - 1);
        }
        return comment;
    },

    postprocess: function(content) {
        if (content.trim().length == 0) {
            return "";
        }
        var spc = (this._hasLeadingSpaces ? " " : "");
        return "<!--" + spc + content + spc + "-->";
    },

    validate: function(content) {
        return (content.indexOf("<!--") < 0) && (content.indexOf("-->") < 0);
    }

});

CUI.rte.genhtml.EditorRegistry.register("rawpayload", CUI.rte.genhtml.RawPayloadEditor);
CUI.rte.genhtml.RawPayloadEditor.ProcessorRegistry
        .register("comment", CUI.rte.genhtml.RawPayloadEditor.CommentProcessor);