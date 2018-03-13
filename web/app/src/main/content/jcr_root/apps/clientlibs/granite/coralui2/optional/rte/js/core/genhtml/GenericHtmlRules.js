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

/**
 * @class CUI.rte.genhtml.GenericHtmlRules
 * <p>This class defines the rules for managing "generic HTML".</p>
 * <p>Generic HTML is HTML that can't be edited directly by the RTE. Instead, a placholder
 * (typically an image) that is carrying the generic HTML code as a payload is used during
 * editing.</p>
 * <p>The serialize/deserialize methods of this class leverage a configurable hierarchy of
 * converters to create placeholders from uneditable HTML (deserialization) and convert
 * them back to the HTML (serialization) before the edited HTML code (the entire HTML, not
 * only the uneditable fragment) is persisted.</p>
 * @since 6.3
 * @constructor
 * Creates a GenericHtmlRules object.
 * @param {Object} config The configuration
 */
CUI.rte.genhtml.GenericHtmlRules = new Class({

    toString: "GenericHtmlRules",

    usePlaceholderStyle: false,

    placeholderStyle: null,
    
    placeholderClass: null,

    converterChain: null,


    construct: function(config) {
        config = config || { };
        var defaults = {
            "usePlaceholderStyle": (CUI.rte.Theme.PLACEHOLDER_STYLE !== null),
            "placeholderStyle": CUI.rte.Theme.PLACEHOLDER_STYLE,
            "placeholderClass": CUI.rte.Theme.PLACEHOLDER_CLASS
        };
        CUI.rte.Utils.applyDefaults(config, defaults);
        this.converterChain = new CUI.rte.genhtml.ConverterChain();
        this.converterChain.initialize(config, this);
        CUI.rte.Utils.apply(this, config);
    },

    /**
     * @private
     */
    _execute: function(context, dom, fnName, isRoot, blocksToRemove) {
        var com = CUI.rte.Common;
        var ignoreRecursion = false;
        if (!isRoot) {
            var converted = this.converterChain[fnName](context, dom);
            if (converted != null) {
                ignoreRecursion = true;
                if (!converted.keepEmptyContainers && blocksToRemove) {
                    var replacement = converted.replacement;
                    var parent = converted.parent;
                    if (com.isTag(parent, com.EDITBLOCK_TAGS)
                            && parent.childNodes.length == replacement.length) {
                        blocksToRemove.push(parent);
                    }
                }
            }
        }

        if (!ignoreRecursion) {
            var children = dom.childNodes;
            for (var c = 0; c < children.length; c++) {
                this._execute(context, children[c], fnName, false, blocksToRemove);
            }
        }
    },

    /**
     * @private
     */
    _remove: function(toRemove) {
        for (var r = 0; r < toRemove.length; r++) {
            if (toRemove[r].parentNode) {
                CUI.rte.DomProcessor.removeWithoutChildren(toRemove[r]);
            }
        }
    },

    /**
     * Serializes the generic HTML parts of the DOM tree starting at the specified DOM node.
     * @param {CUI.rte.EditContext} context The editor context
     * @param {Node} dom The DOM node
     */
    serialize: function(context, dom) {
        var blocksToRemove = [ ];
        this._execute(context, dom, "serialize", true, blocksToRemove);
        this._remove(blocksToRemove);
    },

    /**
     * Deserializes the generic HTML parts of the DOM tree starting at the specified DOM
     * node.
     * @param {CUI.rte.EditContext} context The editor context
     * @param {Node} dom The DOM node
     */
    deserialize: function(context, dom) {
        this._execute(context, dom, "deserialize", true);
    }

});

/**
 * Check if the specified DOM node is used as a placeholder object which carries generic
 * HTML and therefore has to be treated specially in some situations.
 * @param {Node} dom The node to check
 * @returns {Boolean} True if the specified DOM node is a placeholder object, carrying
 *          generic HTML
 * @static
 */
CUI.rte.genhtml.GenericHtmlRules.isPlaceholder = function(dom) {
    var com = CUI.rte.Common;
    var phAttrib = CUI.rte.genhtml.GenericHtmlRules.PLACEHOLDER_ATTRIB;
    return com.isAttribDefined(dom, phAttrib);
};

CUI.rte.genhtml.GenericHtmlRules.PLACEHOLDER_ATTRIB = "_rte_placeholder";