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

CUI.rte.genhtml.ConverterRegistry = (function() {

    var _converters = { };

    return {

        toString: "ConverterRegistry",

        register: function(typeStr, converterClass) {
            _converters[typeStr] = converterClass;
        },

        create: function(typeStr) {
            if (_converters.hasOwnProperty(typeStr)) {
                return new _converters[typeStr];
            }
            return undefined;
        }

    };

})();