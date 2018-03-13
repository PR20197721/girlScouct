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
 * @class CUI.rte.plugins.AbstractImagePlugin
 * @extends CUI.rte.plugins.Plugin
 * <p>This class implements the editing of image properties as a plugin.</p>
 * <p>The plugin ID is "<b>image</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>image</b> - provides functionality for setting some image properties
 *     (currently only alignment is supported)</li>
 * </ul>
 * @since 5.3
 */
CUI.rte.plugins.AbstractImagePlugin = new Class({

    toString: "AbstractImagePlugin",

    extend: CUI.rte.plugins.Plugin,


    getFeatures: function() {
        return [ "image" ];
    },

    notifyPluginConfig: function(pluginConfig) {
        this.config = pluginConfig;
    },

    execute: function(pluginCommand, value, envOptions) {
        if(pluginCommand == "imageProps"){

            var context = envOptions.editContext;
            var selectionDef = envOptions.selectionContext;
            var image = CUI.rte.commands.Image.getSelectedImage(selectionDef);
            this.savedRange = envOptions.savedRange;

            var propConfig = {
                "cmd": "image",
                "editContext": context,
                "execFn": CUI.rte.Utils.scope(this.execModifyImage, this),
                "image": image
            };
            var dm = this.editorKernel.getDialogManager();
            var dialog = dm.create(CUI.rte.ui.DialogManager.DLG_IMAGEPROPS, propConfig);
            dm.prepareShow(dialog);
            dm.show(dialog);
        }
        else if (pluginCommand == "image") {
            this.editorKernel.relayCmd("image", value);
        }
    },

    /**
     * @private
     */
    execModifyImage: function(cmd, imageConfig) {
        if (cmd && imageConfig) {
            var context = this.editorKernel.getEditContext();
            this.editorKernel.selectQualifiedRangeBookmark(context, this.savedRange);
            this.editorKernel.relayCmd(cmd, imageConfig);
        }
    }
});