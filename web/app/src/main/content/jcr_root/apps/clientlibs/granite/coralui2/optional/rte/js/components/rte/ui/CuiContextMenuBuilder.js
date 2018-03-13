/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2013 Adobe Systems Incorporated
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

(function($) {

    CUI.rte.ui.cui.CuiContextMenuBuilder = new Class({

        toString: "CuiContextMenuBuilder",

        extend: CUI.rte.ui.ContextMenuBuilder,


        construct: function(editorKernel) {
            // TODO ...?
        },

        build: function(selectionContext, context) {
            // returning undefined means that we don't use a context menu
            return undefined;
        },

        createItem: function(config) {
            return new CUI.rte.ui.cui.CmItemImpl(config);
        },

        createSeparator: function() {
            return new CUI.rte.ui.cui.CmSeparatorImpl();
        },

        showAt: function(x, y) {
            // TODO ...?
        },

        hideAll: function() {
            // TODO ...?
        },

        isVisible: function() {
            return false;
        }

    });

})(window.jQuery);