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
 * @class CUI.rte.genhtml.Converter
 * <p>This class works as an interface for a converter module.</p>
 * <p>A converter is responsible for:</p>
 * <ul>
 *   <li>declaring itself responsible for a certain DOM node</li>
 *   <li>creating the placeholder for DOM nodes it is responsible for</li>
 *   <li>converting the placeholder back to suitable nodes</li>
 * </ul>
 * <p>Generally, each converter carries a specific internal name that needs to be
 * specified on the placeholder (_rte_placeholder attribute).</p>
 * <p>The detailed implementation is up to the converter module; it could be as simple
 * as storing HTML code on the placeholder DOM as a payload or as complex as using
 * data attributes for re-creating the HTML fragment "on the fly" (= on each serialization).
 * </p>
 * <p>Typical converters use detectors {@see CUI.rte.genhtml.Detector} to determine if they
 * are responsible or handling a specific DOM node. This allows for more flexibility, as a
 * converter can be used in different contexts. But it's not mandatory to use detectors if
 * a converter is tied to a very specific use case.</p>
 * @since 6.3
 */
CUI.rte.genhtml.Converter = new Class({

    toString: "Converter",

    /**
     * <p>Configures the converter module.</p>
     * <p>The configuration is dependent on the converter implementation, but it has the
     * following default properties:</p>
     * <ul>
     *   <li><b>type</b> - The type of the converter (as registered with
     *     {@link CUI.rte.genhtml.ConverterRegistry}).</li>
     *   <li><b>name</b> - The name of the converter instance (unique per editor).</li>
     *   <li><b>detectors</b> - (optional) The detectors (@see CUI.rte.genhtml.Detector} to
     *     be used for checking responsibility)
     * </ul>
     * @param {Object} config The configuration
     */
    configure: function(config) {
        throw new Error("Converter#configure not implemented.");
    },

    /**
     * Determines if the converter module is responsible for handling the specified DOM node
     * as generic HTML.
     * @param {Node} domNode The DOM node to check
     * @return True if the converter is responsible for handling the DOM node
     */
    accepts: function(domNode) {
        throw new Error("Converter#accepts not implemented.");
    },

    /**
     * <p>Serializes the provided placeholder DOM node.</p>
     * <p>This means that the target HTML has to be extracted from the specified placeholder
     * DOM and restored to a matching target DOM. This target DOM has to be returned. The
     * placeholder node is then replaced by the target DOM <b>by the callee</b>.</p>
     * <p>This method is only called if {@link #accepts} returned true for the specified
     * placeholder node.</p>
     * <p>The return value ("replacement definition") has the following properties:</p>
     * <ul>
     *   <li><b>replacement</b> - a DOM node or an array of DOM nodes the placeholder node
     *     will be replaced by</li>
     *   <li><b>keepEmptyContainers</b> - true to keep otherwise empty editing blocks after
     *     the serialization (defaults to false). If a placeholder node is placed outside
     *     an edit block, one will be automatically added to keep the placeholder fully
     *     editable (this happens on deserialization). On serialization, that automatically
     *     added block will be removed again unless this value is set to true. Note that
     *     the edit block will always be kept if regular HTML is added to that block during
     *     the editing process.</li>
     * </ul>
     * @param {CUI.rte.EditContext} context The editor context
     * @param {Node} placeholderNode The placeholder node
     * @return {Object} The replacement definition (see above)
     */
    serialize: function(context, placeholderNode) {
        throw new Error("Converter#serialize not implemented.");
    },

    /**
     * <p>Deserializes the provided DOM node.</p>
     * <p>This means that the generic HTML has to be extracted from the specified DOM (the
     * exact way to do this is left to the converter implementation), create a placeholder
     * DOM and store the generic HTML in an appropriate way on this placeholder node. The
     * DOM node is then replaced by the placeholder DOM <b>by the callee</b>.</p>
     * <p>This method is only called if {@link #accepts} returned true for the specified
     * DOM node.</p>
     * <p>The return value ("replacement definition") has the following properties:</p>
     * <ul>
     *   <li><b>replacement</b> - a DOM node or an array of DOM nodes the placeholder node
     *     will be replaced by</li>
     * </ul>
     * <p>The DOM node provided can be null/undefined; in this case a suitable placeholder
     * node that can be edited through an {@link CUI.rte.genhtml.Editor} should be created.
     * </p>
     * @param {CUI.rte.EditContext} context The editor context
     * @param {Node} domNode The DOM node
     * @return {Object} The replacement definition (see above)
     */
    deserialize: function(context, domNode) {
        throw new Error("Converter#deserialize not implemented.");
    },

    /**
     * <p>Returns the name of the converter.</p>
     * @return {String} The name
     */
    getName: function() {
        throw new Error("Converter#getName not implemented.");
    }

});


CUI.rte.genhtml.Converter.AbstractConverter = new Class({
    
    toString: "Converter.AbstractConverter",

    name: null,

    rules: null,

    detectors: undefined,

    extend: CUI.rte.genhtml.Converter,

    configure: function(config, rules) {
        this.rules = rules;
        this.detectors = [ ];
        this.name = (config.hasOwnProperty("name")
                ? config.name
                : "c_" + new Date().getTime() + "_" + Math.floor(Math.random() * 1000));
        if (config.hasOwnProperty("detectors")) {
            var detectors = config.detectors;
            detectors = CUI.rte.Common.toArray(detectors);
            if (detectors && CUI.rte.Utils.isArray(detectors)) {
                for (var d = 0; d < detectors.length; d++) {
                    var detectorDef = detectors[d];
                    if (detectorDef.hasOwnProperty("type")) {
                        var type = detectorDef.type;
                        var detector = CUI.rte.genhtml.DetectorRegistry.create(type);
                        detector.configure(detectorDef || { });
                        this.detectors.push(detector);
                    }
                }
            }
        }
    },
    
    accepts: function(domNode) {
        var com = CUI.rte.Common;
        var phAttrib = CUI.rte.genhtml.GenericHtmlRules.PLACEHOLDER_ATTRIB;
        if (com.isAttribDefined(domNode, phAttrib)) {
            // check acceptance for serialization - value of placeholder attribute must
            // match the name of the converter
            return (com.getAttribute(domNode, phAttrib, true) === this.name);
        }
        // for deserialization, we need to ask the detectors if this converter is
        // responsible for handling the DOM node
        for (var d = 0; d < this.detectors.length; d++) {
            if (this.detectors[d].accepts(domNode)) {
                return true;
            }
        }
        return false;
    },
    
    getName: function() {
        return this.name;
    },
    
    _createPlaceholderImage: function(context) {
        var com = CUI.rte.Common;
        var phAttrib = CUI.rte.genhtml.GenericHtmlRules.PLACEHOLDER_ATTRIB;
        
        var placeholder = context.createElement("img");
        com.setAttribute(placeholder, "src", CUI.rte.Theme.BLANK_IMAGE);
        if (this.rules.usePlaceholderStyle) {
            com.setAttribute(placeholder, "style", this.rules.placeholderStyle);
        } else {
            com.addClass(placeholder, this.rules.placeholderClass);
        }
        com.setAttribute(placeholder, phAttrib, this.name);
        return placeholder;
    }

});