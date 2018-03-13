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


CUI.rte.genhtml.RawConverter = new Class({

    toString: "RawConverter",

    extend: CUI.rte.genhtml.Converter.AbstractConverter,

    keepEmptyContainers: false,

    configure: function(config) {
        this.inherited(arguments);
        this.keepEmptyContainers = !!config.keepEmptyContainers;
    },

    serialize: function(context, placeholderNode) {
        var com = CUI.rte.Common;
        var pylAttrib = CUI.rte.genhtml.RawConverter.PAYLOAD_ATTRIB;

        var payload = com.getAttribute(placeholderNode, pylAttrib, true);
        payload = (payload != null ? payload : "");
        var payloadDiv = context.createElement("div");
        payloadDiv.innerHTML = payload;

        var replacement = [ ];
        for (var c = 0; c < payloadDiv.childNodes.length; c++) {
            replacement.push(payloadDiv.childNodes[c]);
        }
        return {
            "replacement": replacement,
            "keepEmptyContainers": this.keepEmptyContainers
        };
    },

    deserialize: function(context, domNode) {
        var com = CUI.rte.Common;
        var pylAttrib = CUI.rte.genhtml.RawConverter.PAYLOAD_ATTRIB;

        var payload = "";
        if (domNode) {
            switch (domNode.nodeType) {
                case 1:
                    payload = com.getOuterHTML(context, domNode);
                    break;
                case 8:
                    payload = "<!--" + domNode.nodeValue + "-->";
                    break;
                default:
                    payload = domNode.nodeValue;
                    break;
            }
        }

        var placeholder = this._createPlaceholderImage(context);
        com.setAttribute(placeholder, pylAttrib, payload);
        return {
            "replacement": placeholder
        };
    }

});

CUI.rte.genhtml.RawConverter.PAYLOAD_ATTRIB = "_rte_payload";

// register converter
CUI.rte.genhtml.ConverterRegistry.register("raw", CUI.rte.genhtml.RawConverter);