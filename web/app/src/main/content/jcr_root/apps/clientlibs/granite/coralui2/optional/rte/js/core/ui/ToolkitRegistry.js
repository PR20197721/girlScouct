/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2012 Adobe Systems Incorporated
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

CUI.rte.ui.ToolkitRegistry = function() {

    var toolkits = { };

    return {

        /**
         * Registers a "potential" toolkit implementation.
         * @param {String} toolkitName The toolkit name
         * @param {Function} cls The constructor ("class") of the toolkit
         */
        register: function(toolkitName, cls) {
            toolkits[toolkitName] = {
                "initialized": false,
                "obj": new cls()
            };
        },

        /**
         * Initializes the specified toolkit.
         * @param {String} toolkitName The toolkit
         * @param {Function} callback (optional) Callback that is executed when the toolkit
         *        has been fully initialized
         * @throws Error if no toolkit has been registered for the specified toolkit name
         */
        initialize: function(toolkitName, callback) {
            if (!toolkits.hasOwnProperty(toolkitName)) {
                throw new Error("No toolkit registered for type '" + toolkitName + "'");
            }
            CUI.rte._toolkit = toolkitName;
            var toolkitDef = toolkits[toolkitName];
            var toolkit = toolkitDef.obj;
            if (!toolkitDef.initialized && toolkit.requiresInit()) {
                toolkit.initialize(function() {
                    toolkitDef.initialized = true;
                    if (callback) {
                        callback(toolkit);
                    }
                });
            } else {
                if (callback) {
                    callback(toolkit);
                }
            }
        },

        /**
         * Gets a toolkt by its name or the currently used toolkit (if already initialized).
         * @param {String} toolkitName (optional) The name of the toolkit; undefined to
         *        determine the currently used toolkit (if already initialized)
         * @returns {CUI.rte.ui.Toolkit} The toolkit
         * @throws Error if the specified toolkit does not exist or is not initialized
         */
        get: function(toolkitName) {
            toolkitName = toolkitName || CUI.rte._toolkit;
            if (!toolkits.hasOwnProperty(toolkitName)) {
                throw new Error("No toolkit registered for type '" + toolkitName + "'");
            }
            var toolkitDef = toolkits[toolkitName];
            var toolkit = toolkitDef.obj;
            if (!toolkitDef.initialized) {
                if (toolkit.requiresInit()) {
                    throw new Error("Toolkit not yet initialized.");
                } else {
                    toolkitDef.initialized = true;
                }
            }
            return toolkitDef.obj;
        }

    };

}();