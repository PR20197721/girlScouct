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
 * @class CUI.rte.commands.FullScreen
 * @extends CUI.rte.commands.Command
 * @private
 */
CUI.rte.commands.FullScreen = new Class({

    toString: "FullScreen",

    extend: CUI.rte.commands.Command,

    adapter: undefined,

    isCommand: function(cmdStr) {
        var cmdLC = cmdStr.toLowerCase();
        return (cmdLC === "fullscreen-toggle") || (cmdLC === "fullscreen-start")
                || (cmdLC === "fullscreen-finish") || (cmdLC === "setfullscreenadapter");
    },

    isUndoable: function(cmdStr) {
        return false;
    },

    getProcessingOptions: function() {
        var cmd = CUI.rte.commands.Command;
        return cmd.PO_SELECTION;
    },

    execute: function(execDef) {
        var updateToolbar = false;
        var oppositeKernel = undefined;
        switch (execDef.command.toLowerCase()) {
            case "setfullscreenadapter":
                this.adapter = execDef.value;
                execDef.editContext.setState("fullscreenadapter", execDef.value);
                break;
            case "fullscreen-toggle":
                if (this.adapter) {
                    var fullScreenMode = this.adapter.isFullScreen();
                    if (fullScreenMode) {
                        oppositeKernel = this.adapter.finish();
                    } else {
                        oppositeKernel = this.adapter.start(execDef.value);
                    }
                    updateToolbar = true;
                }
                break;
            case "fullscreen-start":
                if (this.adapter) {
                    oppositeKernel = this.adapter.start(execDef.value);
                    updateToolbar = true;
                }
                break;
            case "fullscreen-finish":
                if (this.adapter) {
                    oppositeKernel = this.adapter.finish();
                    updateToolbar = true;
                }
                break;
        }
        if (updateToolbar && oppositeKernel) {
            CUI.rte.Utils.defer(oppositeKernel.updateToolbar, 1, oppositeKernel);
        }
        return {
            preventBookmarkRestore: true
        };
    },

    queryState: function(selectionDef, cmd) {
        if (this.adapter) {
            return this.adapter.isFullScreen();
        }
        return false;
    }

});


// register command
CUI.rte.commands.CommandRegistry.register("fullscreen", CUI.rte.commands.FullScreen);