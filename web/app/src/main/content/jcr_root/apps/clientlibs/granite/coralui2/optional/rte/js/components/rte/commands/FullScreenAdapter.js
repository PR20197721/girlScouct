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
 * @class CUI.rte.commands.FullScreenAdapter
 * Adapter interface for activating/deactivating full screen mode.
 */
CUI.rte.commands.FullScreenAdapter = new Class({

    start: function() {
        // must be implemented
    },

    finish: function() {
        // must be implemented
    },

    isFullScreen: function() {
        // must be implemented
    },

    toggleSourceEdit: function(sourceEditMode) {
        // must be implemented
    }

});