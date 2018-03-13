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
 * @class CUI.rte.commands.CutCopy
 * @extends CUI.rte.commands.Command
 * @private
 */
CUI.rte.commands.CutCopy = new Class({

    toString: "CutCopy",

    extend: CUI.rte.commands.Command,

    isCommand: function(cmdStr) {
        var cmdLC = cmdStr.toLowerCase();
        return (cmdLC == "cut") || (cmdLC == "copy");
    },

    execute: function(execDef) {

        var command = execDef.command;
        var doc = execDef.editContext.doc;

        var isSuccess = false;
        try {
            // additional IE eventing required to check if the cut/copy succeeded
            var isIESuccess = false;
            var successHandler = function() {
                isIESuccess = true;
            };
            var isIE = CUI.rte.Common.ua.isIE;
            if (isIE) {
                CUI.rte.Eventing.on(this.editContext, doc, command, successHandler);
            }
            isSuccess = doc.execCommand(command, false, null);
            if (isIE) {
                isSuccess = isIESuccess;
                CUI.rte.Eventing.un(this.editContext, doc, command, successHandler);
            }
        } catch (e) {
            // handled via isSuccess
        }
        if (!isSuccess) {
            throw new Error("Cannot " + command + ".");
        }
    }

});

// register command
CUI.rte.commands.CommandRegistry.register("_cutcopy", CUI.rte.commands.CutCopy);