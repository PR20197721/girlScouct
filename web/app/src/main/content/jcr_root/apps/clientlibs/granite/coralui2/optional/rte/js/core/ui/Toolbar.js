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

CUI.rte.ui.Toolbar = new Class({

    toString: "Toolbar",

    /**
     * The toolbar type
     */
    tbType: null,

    /**
     * The toolkit-specific representation of the toolbar
     */
    toolkitRep: null,

    construct: function(toolkitRep) {
        this.toolkitRep = toolkitRep;
    },

    getToolkitRep: function() {
        return this.toolkitRep;
    },

    getItem: function(itemId) {
        throw new Error(CUI.rte.Common.ERROR_MESSAGES.TOOLBAR.GET_ITEM_NOT_IMPLEMENTED);
    },

    getHeight: function() {
        throw new Error(CUI.rte.Common.ERROR_MESSAGES.TOOLBAR.GET_HEIGHT_NOT_IMPLEMENTED);
    },

    /**
     * Returns true if provided dom is a part of toolbar or it itself is the toolbar, false otherwise.
     * This method must be overridden by implementing toolbar.
     * @param dom HTMLElement - dom to check
     * @return - true if provided dom is a part of toolbar or it itself is the toolbar, false otherwise.
     */
    containsElement: function(dom) {
        throw new Error(CUI.rte.Common.ERROR_MESSAGES.TOOLBAR.CONTAINS_ELEMENT_NOT_IMPLEMENTED);
    },

    adjustToWidth: function(width) {
        // may be overridden by implementing toolbar if adjustments to the available width
        // for the toolbar are required
    },

    startEditing: function(editorKernel) {
        // may be overridden by implementing toolbar if special handling is required,
        // for example the toolbar is only shown during editing
    },

    finishEditing: function() {
        // may be overridden by implementing toolbar if special handling is required,
        // for example the toolbar is only shown during editing
    },

    enable: function() {
        throw new Error(CUI.rte.Common.ERROR_MESSAGES.TOOLBAR.ENABLE_NOT_IMPLEMENTED);
    },

    disable: function(excludeItems) {
        throw new Error(CUI.rte.Common.ERROR_MESSAGES.TOOLBAR.DISABLE_NOT_IMPLEMENTED);
    },

    destroy: function() {
        throw new Error(CUI.rte.Common.ERROR_MESSAGES.TOOLBAR.DESTROY_NOT_IMPLEMENTED);
    }

});