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
 * @class CUI.rte.commands.InsertHtml
 * @extends CUI.rte.commands.Command
 * @private
 */
CUI.rte.commands.InsertHtml = new Class({

    toString: "InsertHtml",

    extend: CUI.rte.commands.Command,

    isCommand: function(cmdStr) {
        return (cmdStr.toLowerCase() == "inserthtml");
    },

    getProcessingOptions: function() {
        var cmd = CUI.rte.commands.Command;
        return cmd.PO_SELECTION;
    },

    execute: function(execDef) {
        var com = CUI.rte.Common;
        var htmlToInsert = execDef.value;
        var context = execDef.editContext;
        if (htmlToInsert && (htmlToInsert.length > 0)) {
            if (com.ua.isIE || com.ua.isGecko) {
                // even IE with W3C compliant selection model don't support the
                // "inserthtml" command, so use the old selection model + pasteHTML
                // to insert the HTML
                // Also, for Firefox (<=45) "inserthtml" does not work fine for '&nbsp;'. See CRTE-67
                try {
                    var range = com.ua.isIE ? CUI.rte.Selection.saveNativeSelection(context) : CUI.rte.Selection.getLeadRange(context);
                    if (range.pasteHTML) {
                        range.pasteHTML(htmlToInsert);
                    } else {
                        var tempDiv = context.doc.createElement("div");
                        tempDiv.innerHTML = htmlToInsert;
                        var textFrag = context.doc.createDocumentFragment();
                        var firstNode, lastNode;
                        while ((firstNode = tempDiv.firstChild)) {
                            lastNode = textFrag.appendChild(firstNode);
                        }
                        range.deleteContents();
                        range.insertNode(textFrag);
                        range.setStartAfter(lastNode);
                    }
                } catch (e) {
                    throw new Error("Could not insert html due to IE limitations.");
                }
            } else {
                context.doc.execCommand("inserthtml", false, htmlToInsert);
            }
        }
    },

    queryState: function(selectionDef, cmd) {
        return false;
    }

});


// register command
CUI.rte.commands.CommandRegistry.register("inserthtml",
        CUI.rte.commands.InsertHtml);