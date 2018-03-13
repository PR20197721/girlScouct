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

CUI.rte.genhtml.ConverterChain = new Class({

    toString: "ConverterChain",

    _chain: undefined,

    initialize: function(config, rules) {
        var com = CUI.rte.Common;
        this._chain = [ ];
        if (config.hasOwnProperty("converters")) {
            var converters = config.converters;
            converters = com.toArray(converters);
            if (converters && CUI.rte.Utils.isArray(converters)) {
                for (var c = 0; c < converters.length; c++) {
                    var converterDef = converters[c];
                    if (converterDef.hasOwnProperty("type")) {
                        var type = converterDef.type;
                        var converter = CUI.rte.genhtml.ConverterRegistry.create(type);
                        converter.configure(converterDef, rules);
                        this._chain.push(converter);
                    }
                }
            }
            delete config.converters;
        }
    },

    _execute: function(context, domNode, fnName) {
        var converted = undefined;
        for (var c = 0; c < this._chain.length; c++) {
            var converter = this._chain[c];
            if (converter.accepts(domNode)) {
                converted = converter[fnName](context, domNode);
                break;
            }
        }

        if (converted && converted.hasOwnProperty("replacement")) {
            var replacement = converted.replacement;
            if (!CUI.rte.Utils.isArray(replacement)) {
                replacement = [ replacement ];
            }
            var parent = CUI.rte.Common.getParentNode(context, domNode);
            if (parent) {
                for (var r = 0; r < replacement.length; r++) {
                    parent.insertBefore(replacement[r], domNode);
                }
                parent.removeChild(domNode);
                converted.parent = parent;
            }
        }

        return converted;
    },

    serialize: function(context, domNode) {
        return this._execute(context, domNode, "serialize");
    },

    deserialize: function(context, domNode) {
        return this._execute(context, domNode, "deserialize");
    },
    
    getByName: function(name) {
        for (var c = 0; c < this._chain.length; c++) {
            var converter = this._chain[c];
            if (converter.getName() == name) {
                return converter;
            }
        }
        return undefined;
    }

});