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
 * @class CUI.rte.plugins.ControlPlugin
 * @extends CUI.rte.plugins.Plugin
 * <p>This class implements some control functionality as a plugin.</p>
 * <p>The plugin ID is "<b>control</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>close</b> - adds a button that dispatches a "close" request</li>
 * </ul>
 */
CUI.rte.plugins.ControlPlugin = new Class({

    toString: "ControlPlugin",

    extend: CUI.rte.plugins.Plugin,


    getFeatures: function() {
        return [ "close", "save" ];
    },

    initializeUI: function(tbGenerator) {
        var plg = CUI.rte.plugins;
        var ui = CUI.rte.ui;
        if (this.isFeatureEnabled("close")) {
            this.closeUI = tbGenerator.createElement("close", this, false,
                    this.getTooltip("close"));
            tbGenerator.addElement("control", plg.Plugin.SORT_MAX - 1, this.closeUI, 10000);
        }
        if (this.isFeatureEnabled("save")) {
            this.saveUI = tbGenerator.createElement("save", this, false,
                    this.getTooltip("save"));
            tbGenerator.addElement("control", plg.Plugin.SORT_MAX - 1, this.saveUI, 20000);
        }
    },

    notifyPluginConfig: function(pluginConfig) {
        pluginConfig = pluginConfig || { };
        CUI.rte.Utils.applyDefaults(pluginConfig, {
            "features": "*",
            "tooltips": {
                "close": {
                    "title": CUI.rte.Utils.i18n("plugins.control.closeTitle"),
                    "text": CUI.rte.Utils.i18n("plugins.control.closeText")
                },
                "save": {
                    "title": CUI.rte.Utils.i18n("plugins.control.saveTitle")
                }
            }
        });
        this.config = pluginConfig;
    },

    execute: function(cmd, value, env) {
        if (cmd == "close") {
            this.editorKernel.fireUIEvent("requestClose");
        }
        if (cmd == "save") {
            this.editorKernel.fireUIEvent("requestSave");
        }
    },

    updateState: function(selDef) {
        // nothing to do
    }

});


// register plugin
CUI.rte.plugins.PluginRegistry.register("control", CUI.rte.plugins.ControlPlugin);