/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2016 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
"use strict";

var global = this;
use([], function() {
    var PROP_PREFIX = "sites.listView.info.render";

    var CONST = {
        PROP_PROVIDER_NAME: PROP_PREFIX + ".provider",
        PROP_PROVIDER_PROPERTY: PROP_PREFIX + ".providerProperty",
        PROP_VALUE: PROP_PREFIX + ".value"
    };

    var _retrieveAttribute = function(attrName, defaultValue) {
        var value = defaultValue;

        if (global.request && global.request.getAttribute && global.request.getAttribute(attrName)) {
            value = global.request.getAttribute(attrName);
        }

        return value;
    };

    var providerName = _retrieveAttribute(CONST.PROP_PROVIDER_NAME, "default-provider");
    var providerProperty = _retrieveAttribute(CONST.PROP_PROVIDER_PROPERTY, "default-property");
    var valueAttribute = _retrieveAttribute(CONST.PROP_VALUE, -2);
    var obj = JSON.parse(valueAttribute);
    var text = obj.time;
    var value = obj.value;

    return {
        text: text,
        value: value
    };
});
