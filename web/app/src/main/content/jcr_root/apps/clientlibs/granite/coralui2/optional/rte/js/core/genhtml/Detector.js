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
 * @class CUI.rte.genhtml.Detector
 * <p>This class works as an interface for a detector module.</p>
 * <p>A detector is responsible for deciding if a provided DOM node is matching the
 * detector's purpose and configuration (for example, it matches a specific tag and/or
 * set of attributes).</p>
 * <p>Detectors are used by applicable converters to determine if they are responsible for
 * handling a specific DOM node.</p>
 * @since 6.3
 */
CUI.rte.genhtml.Detector = new Class({

    toString: "Detector",

    /**
     * <p>Configures the detector.</p>
     * <p>Basically, the configuration is dependent on the detector implementation. But
     * there are some mandatory/shared properties:</p>
     * <ul>
     *   <li><b>type</b> - the type of the detector (as registered at
     *     {@link CUI.rte.genhtml.DetectorRegistry))</li>
     * </ul>
     * @param {Object} config The configuration
     */
    configure: function(config) {
        throw new Error("Detector#configure is not implemented.");
    },

    /**
     * <p>Checks if the specified DOM node fulfills the criteria represented by the
     * detector (type + configuration).</p>
     * <p>For example, a tag/attribute-based detector would check here if the DOM node has
     * a suitable tagName property and matches a configured set of attributes.</p>
     * @param {Node} domNode The DOM node
     * @return {Boolean} True if the specified DOM node matches
     */
    accepts: function(domNode) {
        throw new Error("Detector#accepts is not implemented.");
    }

});