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
 * @class CUI.rte.plugins.StylesPlugin
 * @extends CUI.rte.plugins.Plugin
 * <p>This class implements styling text fragments with a CSS class (using "span" tags) as a
 * plugin.</p>
 * <p>The plugin ID is "<b>styles</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>styles</b> - adds a style selector (styles will be applied on selection scope)
 *     </li>
 * </ul>
 */
CUI.rte.plugins.StylesPlugin = new Class({

    toString: "StylePlugin",

    extend: CUI.rte.plugins.Plugin,

    /**
     * @cfg {Object/Object[]} styles
     * <p>Defines CSS classes that are available to the user for formatting text fragments
     * (defaults to { }). There are two ways of specifying the CSS classes:</p>
     * <ol>
     *   <li>Providing styles as an Object: Use the CSS class name as property name.
     *   Specify the text that should appear in the style selector as property value
     *   (String).</li>
     *   <li>Providing styles as an Object[]: Each element has to provide "cssName" (the
     *   CSS class name) and "text" (the text that appears in the style selector)
     *   properties.</li>
     * </ol>
     * <p>Styling is applied by adding "span" elements with corresponding "class"
     * attributes appropriately.</p>
     * @since 1.11
     */

    /**
     * @private
     */
    cachedStyles: null,

    /**
     * @private
     */
    stylesUI: null,


    getFeatures: function() {
        return [ "styles" ];
    },

    reportStyles: function() {
        return [ {
                "type": "text",
                "styles": this.getStyles()
            }
        ];
    },

    getStyles: function() {
        var com = CUI.rte.Common;
        if (!this.cachedStyles) {
            this.cachedStyles = this.config.styles;
            if (this.cachedStyles) {
                // take styles from config
                com.removeJcrData(this.cachedStyles);
                this.cachedStyles = com.toArray(this.cachedStyles, "cssName", "text");
            } else {
                this.cachedStyles = [ ];
            }
        }
        return this.cachedStyles;
    },

    setStyles: function(styles) {
        this.cachedStyles = styles;
    },

    hasStylesConfigured: function() {
        return !!this.config.styles;
    },

    initializeUI: function(tbGenerator, options) {
        var plg = CUI.rte.plugins;
        if (this.isFeatureEnabled("styles")) {
            this.stylesUI = new tbGenerator.createStyleSelector("styles", this, null,
                    this.getStyles());
            tbGenerator.addElement("styles", plg.Plugin.SORT_STYLES, this.stylesUI, 10);
        }
    },

    notifyPluginConfig: function(pluginConfig) {
        pluginConfig = pluginConfig || { };
        CUI.rte.Utils.applyDefaults(pluginConfig, { });
        this.config = pluginConfig;
    },

    execute: function(cmdId, styleDef) {
        if (!this.stylesUI) {
            return;
        }
        var cmd = null;
        var tagName = undefined;
        var className = undefined;
        switch (cmdId.toLowerCase()) {
            case "applystyle":
                cmd = "style";
                tagName = "span";
                className = (styleDef != null ? styleDef
                        : this.stylesUI.getSelectedStyle());
                break;
        }
        if (cmd && tagName && className) {
            this.editorKernel.relayCmd(cmd, {
                "tag": tagName,
                "attributes": {
                    "class": className
                }
            });
        }
    },

    updateState: function(selDef) {
        if (!this.stylesUI) {
            return;
        }
        var com = CUI.rte.Common;
        var styles = selDef.startStyles;
        var actualStyles = [ ];
        var s;
        var styleableObject = selDef.selectedDom;
        if (styleableObject) {
            if (!CUI.rte.Common.isTag(selDef.selectedDom,
                    CUI.rte.plugins.StylesPlugin.STYLEABLE_OBJECTS)) {
                styleableObject = null;
            }
        }
        var stylesDef = this.getStyles();
        var styleCnt = stylesDef.length;
        if (styleableObject) {
            for (s = 0; s < styleCnt; s++) {
                var styleName = stylesDef[s].cssName;
                if (com.hasCSS(styleableObject, styleName)) {
                    actualStyles.push({
                        "className": styleName
                    });
                }
            }
        } else {
            var checkCnt = styles.length;
            for (var c = 0; c < checkCnt; c++) {
                var styleToProcess = styles[c];
                for (s = 0; s < styleCnt; s++) {
                    if (stylesDef[s].cssName == styleToProcess.className) {
                        actualStyles.push(styleToProcess);
                        break;
                    }
                }
            }
        }
        this.stylesUI.selectStyles(actualStyles, selDef);
    }

});

/**
 * Array with tag names that define objects (like images) that are styleable when selected
 * @private
 * @static
 * @final
 * @type String[]
 */
CUI.rte.plugins.StylesPlugin.STYLEABLE_OBJECTS = [
    "img"
];


// register plugin
CUI.rte.plugins.PluginRegistry.register("styles", CUI.rte.plugins.StylesPlugin);