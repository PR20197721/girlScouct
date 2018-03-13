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

/**
 * This class defines the toolkit abstraction layer for the underlying UI toolkit.
 * @class CUI.rte.ui.Toolkit
 */
CUI.rte.ui.Toolkit = new Class({

    toString: "Toolkit",

    toolkitData: { },

    initialize: function() {
        // can be overridden for additional initialization the first time the toolkit
        // is acquired
    },

    requiresInit: function() {
        throw new Error("Toolkit#requiresInit is not implemented by the specific toolkit.");
    },

    createToolbarBuilder: function(hint) {
        throw new Error("Toolkit#createToolbarBuilder is not implemented by the specific "
                + "toolkit.");
    },

    createContextMenuBuilder: function(editorKernel) {
        throw new Error("Toolkit#createContextMenuBuilder is not implemented by the "
                + "specific toolkit.");
    },

    createDialogManager: function(editorKernel) {
        throw new Error("Toolkit#createDialogManager is not implemented by the specific "
                + "toolkit.");
    },

    /**
     * Adds toolkit specific data.
     * <p>
     * Overwrites existing data under the same name.
     * @param {String} name The name/key of the data
     * @param {*} value The data
     */
    addToolkitData: function(name, value) {
        this.toolkitData[name] = value;
    },

    /**
     * Removes toolkit specific data.
     * <p>
     * Call is ignored if no data is available under the specified name.
     * @param {String} name The name/key of the data
     */
    removeToolkitData: function(name) {
        if (this.toolkitData.hasOwnProperty(name)) {
            delete this.toolkitData[name];
        }
    },

    /**
     * Checks if toolkit specific data is available under the specified name.
     * @param {String} name the name/key of the data
     * @returns {Boolean} true if respective data is avaialble
     */
    hasToolkitData: function(name) {
        return this.toolkitData.hasOwnProperty(name);
    },

    /**
     * Retrieves toolkit specific data for a given name.
     * @param {String} name The name/key of the data
     * @returns {*} The data; undefined, if no data is available for the name
     */
    getToolkitData: function(name) {
        if (this.toolkitData.hasOwnProperty(name)) {
            return this.toolkitData[name];
        }
        return undefined;
    }

});

CUI.rte.ui.Toolkit.TBHINT_LOCAL = "local";

CUI.rte.ui.Toolkit.TBHINT_GLOBAL = "global";