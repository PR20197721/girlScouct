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

CUI.rte.genhtml.NodeTypeDetector = new Class({

    toString: "NodeTypeDetector",

    extend: CUI.rte.genhtml.Detector,

    value: null,

    configure: function(config) {
        if (config.hasOwnProperty("value")) {
            this.value = parseInt(config.value);
        }
    },

    accepts: function(domNode) {
        if ((this.value == null) || isNaN(this.value)) {
            return false;
        }
        return (domNode.nodeType == this.value);
    }

});

// register detector
CUI.rte.genhtml.DetectorRegistry.register("nodeType", CUI.rte.genhtml.NodeTypeDetector);