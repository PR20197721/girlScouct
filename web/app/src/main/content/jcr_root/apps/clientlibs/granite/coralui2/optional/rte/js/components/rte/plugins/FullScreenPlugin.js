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

/**
 * @class CUI.rte.plugins.FullScreenPlugin
 * @extends CUI.rte.plugins.Plugin
 * <p>This class implements full screen functionality as a plugin.</p>
 * <p>The plugin ID is "<b>fullscreen</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>toggle</b> - adds a button that toggles fullscreen mode</li>
 * </ul>
 */
CUI.rte.plugins.FullScreenPlugin = new Class({

    toString: "FullScreenPlugin",

    extend: CUI.rte.plugins.Plugin,

    toggleUI: null,

    startUI: null,

    finishUI: null,


    // Plugin implementation ---------------------------------------------------------------

    getFeatures: function() {
        return [ "toggle", "start", "finish" ];
    },

    _init: function(editorKernel) {
        this.inherited(arguments);
    },

    initializeUI: function(tbGenerator) {
        var plg = CUI.rte.plugins;
        var ui = CUI.rte.ui;
        if (this.isFeatureEnabled("toggle")) {
            this.toggleUI = tbGenerator.createElement("toggle", this, false,
                    this.getTooltip("toggle"));
            tbGenerator.addElement("control", plg.Plugin.SORT_MAX - 1, this.toggleUI,
                    12);
        }
        if (this.isFeatureEnabled("start")) {
            this.startUI = tbGenerator.createElement("start", this, false,
                this.getTooltip("start"));
            tbGenerator.addElement("control", plg.Plugin.SORT_MAX - 1, this.startUI,
                    10);
        }
        if (this.isFeatureEnabled("finish")) {
            this.finishUI = tbGenerator.createElement("finish", this, false,
                this.getTooltip("finish"));
            tbGenerator.addElement("control", plg.Plugin.SORT_MAX - 1, this.finishUI,
                    11);
        }
    },

    notifyPluginConfig: function(pluginConfig) {
        pluginConfig = pluginConfig || { };
        CUI.rte.Utils.applyDefaults(pluginConfig, {
            "features": "*",
            "tooltips": {
                "toggle": {
                    "title": CUI.rte.Utils.i18n("plugins.fullscreen.toggleTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.fullscreen.toggleText")
                },
                "start": {
                    "title": CUI.rte.Utils.i18n("plugins.fullscreen.startTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.fullscreen.startText")
                },
                "finish": {
                    "title": CUI.rte.Utils.i18n("plugins.fullscreen.finishTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.fullscreen.finishText")
                }
            }
        });
        this.config = pluginConfig;
    },

    execute: function(cmd) {
        var externalStyleSheetObj = this.config.externalStyleSheets ?
             {"externalStyleSheets" : this.config.externalStyleSheets} : undefined;
        this.editorKernel.execCmd("fullscreen-" + cmd, externalStyleSheetObj);
        // prevent transferring the focus to the original editor automatically - the
        // full screen editor should have it already
        return {
            "preventTouchFocus": true
        };
    },

    updateState: function(selDef) {
        // nothing to do
    }

});


// register plugin
CUI.rte.plugins.PluginRegistry.register("fullscreen", CUI.rte.plugins.FullScreenPlugin);