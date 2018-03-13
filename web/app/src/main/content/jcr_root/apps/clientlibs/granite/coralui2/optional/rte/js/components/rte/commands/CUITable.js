/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2014 Adobe Systems Incorporated
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
 * @class CUI.rte.commands.CUITable
 * @extends CUI.rte.commands.Table
 * @private
 * <p>This class provided the CUI specific functionality for creating and editing a table.</p>
 */
CUI.rte.commands.CUITable = new Class({

    toString: "CUITable",

    extend: CUI.rte.commands.Table,

    /**
     * Handle cellspacing in fullscreen mode. Coral UI currently uses global style definition for table element, and
     * adds some css for border-collapse and border-spacing which results in cellspacing attribute being ignored.
     * Hence, we need to border-collapse and border-spacing as inline styles to table element to achieve the desired
     * effect of cellspacing. In non-FSM, we expect the border-* styles to take precedence over the cellspacing/-padding
     * attributes, as they will as well after leaving edit mode.
     * @private
     */
    handleCellspacing: function(dom, execDef) {
        var com = CUI.rte.Common;
        var config = execDef.value;
        var fullscreenadapter = execDef.editContext.getState("fullscreenadapter");
        if (!fullscreenadapter || !fullscreenadapter.isFullScreen()) {
            return;
        }
        if (config.cellspacing) {
            com.addInlineStyles(dom, {"border-collapse": "separate", "border-spacing": config.cellspacing + "px"});
        } else {
            com.addInlineStyles(dom, {"border-collapse": "initial", "border-spacing": "initial"});
        }
    },

    getExecDefValue: function(execDef) {
        var isTableMode = execDef.value.isTableMode;
        var com = CUI.rte.Common;
        var nodeList = execDef.nodeList;
        var selection = execDef.selection;
        var singleCell = CUI.rte.commands.Table.getCellFromNodeList(execDef.editContext, nodeList);
        var isSingleCell = (singleCell != null);
        var tableDom = com.getTagInPath(execDef.editContext, nodeList.commonAncestor, "table");
        var isTable = isSingleCell || (tableDom != null);
        var cellSel = null;
        var tableMatrix;
        var canRemoveCol = true;
        var canRemoveRow = true;
        if (isTable) {
            tableMatrix = new CUI.rte.TableMatrix();
            tableMatrix.createTableMatrix(tableDom);
            if (isTableMode) {
                var size = tableMatrix.getTableSize();
                canRemoveCol = (size.cols > 1);
                canRemoveRow = (size.rows > 1);
            }
        }
        if (isTable && !isSingleCell) {
            if (selection.cellSelection) {
                cellSel = tableMatrix.createSelection(selection.cellSelection.cells);
            }
        }
        if (!cellSel && isSingleCell) {
            var cellInfo = tableMatrix.getCellInfo(singleCell);
            if (!cellInfo.isLastCol && execDef.value.direction == "right") {
                cellSel = tableMatrix.createSelection(singleCell);
                cellSel.expand(1, 0);
            }
            if (!cellInfo.isLastRow && execDef.value.direction == "down") {
                cellSel = tableMatrix.createSelection(singleCell);
                cellSel.expand(0, 1);
            }
        }
        return cellSel;
    }

});

// register command
CUI.rte.commands.CommandRegistry.register("_table", CUI.rte.commands.CUITable);