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
 * @class CQ.form.rte.plugins.SpecialCharsDialog
 * @extends CQ.Dialog
 * @private
 * The RichText.SpecialCharsDialog is a dialog for
 * inserting special characters.
 */
CQ.form.rte.plugins.SpecialCharsDialog = CQ.Ext.extend(CQ.Dialog, {

    /**
     * @cfg {CUI.rte.EditConfig} editContext
     * The edit config
     */
    editContext: null,

    /**
     * @private
     */
    charsInTable: null,

    /**
     * @private
     */
    backRef: null,

    constructor: function(config) {

        var cpr = CQ.form.rte.plugins.SpecialCharsDialog.SPECIAL_CHARS_PER_ROW;
        var charMagnifier = "<table";
        if (config.tableCls) {
            charMagnifier += " class=\"" + config.tableCls + "\"";
        }
        charMagnifier += "><tr><td";
        if (config.magnifyCls) {
            charMagnifier += " class=\"" + config.magnifyCls + "\"";
        }
        charMagnifier += ">&nbsp;</td></tr></table>";

        var charSelector = "<table";
        if (config.tableCls) {
            charSelector += " class=\"" + config.tableCls + "\"";
        }
        charSelector += ">";
        this.chars = config.chars;
        this.charsInTable = [ ];
        var charCnt = 0;
        for (var cName in this.chars) {
            if (this.chars.hasOwnProperty(cName)) {
                var cDef = this.chars[cName];
                var col;
                if (cDef.entity) {
                    col = charCnt % cpr;
                    if (col == 0) {
                        charSelector += "<tr>";
                    }
                    charSelector += "<td";
                    if (config.cellCls != null) {
                        charSelector += " class=\"" + config.cellCls + "\"";
                    }
                    charSelector += ">";
                    charSelector += cDef.displayEntity ? cDef.displayEntity: cDef.entity;
                    charSelector += "</td>";
                    this.charsInTable.push(cDef.entity);
                    if (col == (cpr - 1)) {
                        charSelector += "</tr>";
                    }
                    charCnt++;
                } else if ((cDef.rangeStart) && (cDef.rangeEnd)) {
                    for (var cCode = cDef.rangeStart; cCode <= cDef.rangeEnd; cCode++) {
                        var entity = "&#" + cCode + ";";
                        col = charCnt % cpr;
                        if (col == 0) {
                            charSelector += "<tr>";
                        }
                        charSelector += "<td";
                        if (config.cellCls != null) {
                            charSelector += " class=\"" + config.cellCls + "\"";
                        }
                        charSelector += ">";
                        charSelector += entity;
                        charSelector += "</td>";
                        this.charsInTable.push(entity);
                        if (col == (cpr - 1)) {
                            charSelector += "</tr>";
                        }
                        charCnt++;
                    }
                }
            }
        }
        col = charCnt % cpr;
        if (col > 0) {
            for (; col < cpr; col++) {
                charSelector += "<td>&nbsp;</td>";
            }
            charSelector += "</tr>";
        }
        charSelector += "</table>";

        delete config.chars;
        delete config.tableCls;
        delete config.cellCls;
        delete config.magnifyCls;

        var dialogRef = this;
        CQ.Util.applyDefaults(config, {
            "title": CQ.I18n.getMessage("Insert special character"),
            "modal": true,
            "width": 450,
            "height": 300,
            "buttons": CQ.Dialog.CANCEL,
            "items": [ {
                "xtype": "panel",
                "layout": "border",
                "items": [ {
                    "xtype": "panel",
                    "region": "center",
                    "cls": "cq-rte-specialcharsdialog",
                    "border": false,
                    "html": charSelector,
                    "autoScroll": true,
                    "afterRender": function() {
                        var com = CUI.rte.Common;

                        CQ.Ext.Panel.prototype.afterRender.call(this);
                        var table = this.body.dom.firstChild;
                        var cells;
                        if (com.ua.isIE) {
                            cells = table.cells;
                        } else {
                            cells = [ ];
                            var rows = table.rows;
                            for (var r = 0; r < rows.length; r++) {
                                var row = rows[r];
                                for (var c = 0; c < row.childNodes.length; c++) {
                                    if (row.childNodes[c].nodeType == 1) {
                                        cells.push(row.childNodes[c]);
                                    }
                                }
                            }
                        }
                        var cellCnt = dialogRef.charsInTable.length;
                        for (var i = 0; i < cellCnt; i++) {
                            var cell = cells[i];
                            var el = CQ.Ext.get(cell);
                            cell.charToInsert = dialogRef.charsInTable[i];
                            CQ.Ext.EventManager.on(el, "click", function() {
                                dialogRef.hide();
                                dialogRef.insertCharacter(this.charToInsert);
                            });
                            CQ.Ext.EventManager.on(el, "mouseover", function() {
                                if (dialogRef.overCls) {
                                    var el = CQ.Ext.get(this);
                                    el.addClass(dialogRef.overCls);
                                }
                                if (dialogRef.magnifierCell) {
                                    dialogRef.magnifierCell.innerHTML = this.charToInsert;
                                }
                            });
                            CQ.Ext.EventManager.on(el, "mouseout", function() {
                                if (dialogRef.overCls) {
                                    var el = CQ.Ext.get(this);
                                    el.removeClass(dialogRef.overCls);
                                }
                                if (dialogRef.magnifierCell) {
                                    dialogRef.magnifierCell.innerHTML = "&nbsp;";
                                }
                            });
                        }
                    }
                }, {
                    "xtype": "panel",
                    "region": "east",
                    "cls": "cq-rte-specialcharsdialog",
                    "width": 70,
                    "border": false,
                    "html": charMagnifier,
                    "afterRender": function() {
                        CQ.Ext.Panel.prototype.afterRender.call(this);
                        var table = this.body.dom.firstChild;
                        var row = table.rows[0];
                        dialogRef.magnifierCell = row.childNodes[0];
                    }
                } ]
            } ],
            "listeners": {
                "show": function() {
                    this.editorKernel.fireUIEvent("dialogshow");
                },
                "hide": function() {
                    this.editorKernel.fireUIEvent("dialoghide");
                }
            }
        });
        CQ.form.rte.plugins.SpecialCharsDialog.superclass.constructor.call(this, config);
    }

});

/**
 * Number of special chars to be displayed per row (defaults to 16)
 * @static
 * @final
 * @type Number
 * @private
 */
CQ.form.rte.plugins.SpecialCharsDialog.SPECIAL_CHARS_PER_ROW = 16;

// register SpecialCharDialog component as xtype
CQ.Ext.reg("rtespecialcharsdialog", CQ.form.rte.plugins.SpecialCharsDialog);