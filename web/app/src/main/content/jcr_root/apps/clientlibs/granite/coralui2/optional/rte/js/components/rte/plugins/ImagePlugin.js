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
 * @class CUI.rte.plugins.ImagePlugin
 * @extends CUI.rte.plugins.AbstractImagePlugin
 * <p>This class implements the editing of image properties as a plugin.</p>
 * <p>The plugin ID is "<b>image</b>".</p>
 * <p><b>Features</b></p>
 * <ul>
 *   <li><b>image</b> - adds context menu entries for setting some image properties
 *     (currently only alignment is supported)</li>
 * </ul>
 * @since 5.3
 */
CUI.rte.plugins.ImagePlugin = new Class({

    toString: "ImagePlugin",

    /**
     * private
     **/
    imageDialog: null,

    selectedImageNode: null,

    isButtonDisabled: true,

    extend: CUI.rte.plugins.AbstractImagePlugin,

    /**
     * Creates the Image Button visible in the toolbar.
     * @param tbGenerator
     */
    initializeUI: function(tbGenerator) {
        var plg = CUI.rte.plugins;
        var ui = CUI.rte.ui;
        if(this.isAnyFeatureEnabled()){
            this.imageUI = tbGenerator.createElement("imageProps", this, false, this.getTooltip("image"));
            tbGenerator.addElement("image", plg.Plugin.SORT_IMAGE, this.imageUI, 10);
        }
    },

    // overrides CUI.rte.plugins.AbstractImagePlugin#notifyPluginConfig
    notifyPluginConfig: function(pluginConfig) {
        CUI.rte.Utils.scope(this.superClass.notifyPluginConfig, this)(pluginConfig);
        CUI.rte.Utils.applyDefaults(pluginConfig, {
            "tooltips": {
                "image": {
                    "title": CUI.rte.Utils.i18n("plugins.image.imageTitle")
                }
            }
        });
    },

    execute: function(pluginCommand, value, envOptions) {
        // todo implement
        if(pluginCommand == "imageProps"){

            var context = envOptions.editContext;
            var dpr = CUI.rte.DomProcessor;
            //Necessarily calling a private function
            var selection = this.editorKernel.createQualifiedSelection(context);
            var nodelist = dpr.createNodeList(context, selection);
            var selectionDef = {
                "selection": selection,
                "nodelist": nodelist
            };
            var image = CUI.rte.commands.Image.getSelectedImage(selectionDef);
            //Required for bookmark purposes.
            this.savedRange = this.editorKernel.createQualifiedRangeBookmark(context);

            var dm = this.editorKernel.getDialogManager();
            if (dm.isShown(this.imageDialog) && dm.toggleVisibility(this.imageDialog)) {
                dm.hide(this.imageDialog);
                return;
            }
            //Creates the image dialog for the first time
            if (!this.imageDialog || dm.mustRecreate(this.imageDialog)) {
                var propConfig = {
                    "cmd": "image",
                    "image": image,
                    "editContext": context,
                    "execFn": CUI.rte.Utils.scope(this.execModifyImage, this),
                    "parameters": {
                        "command": this.pluginId+"#imageProps"
                    }
                };
                this.imageDialog = dm.create(CUI.rte.ui.DialogManager.DLG_IMAGEPROPS, propConfig);
            }
            this.imageDialog.initializeEdit(this.editorKernel, this.selectedImageNode, CUI.rte.Utils.scope(this.execModifyImage, this));
            dm.prepareShow(this.imageDialog)
            dm.show(this.imageDialog);
        }
        //Executes the required command on the selected image object
        else if (pluginCommand == "image") {
            this.editorKernel.relayCmd("image", value);
        }
    },

    updateState: function(selDef) {
        var selectedNode = selDef.selectedDom;
        var isImage = false;
        if (selectedNode) {
            // only non-placeholder images are editable
            isImage = CUI.rte.Common.isTag(selectedNode, "img")
                    && !CUI.rte.genhtml.GenericHtmlRules.isPlaceholder(selectedNode);
            if (isImage) {
                if (this.imageUI) {
                    this.imageUI.setDisabled(false);
                }
                this.selectedImageNode = selectedNode;
            } else {
                if (this.imageUI) {
                    this.imageUI.setDisabled(true);
                }
            }
        } else {
            if (this.imageUI) {
                this.imageUI.setDisabled(true);
            }
        }
    },

    isHeadless: function(command, value){
        return false;
    }

});

// register plugin
CUI.rte.plugins.PluginRegistry.register("image", CUI.rte.plugins.ImagePlugin);