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
 * @class CUI.rte.CUIDomCleanup
 * @extends CUI.rte.DomCleanup
 * @private
 * This class extends CUI.rte.DomCleanup to add CUI specific functionality.
 */
CUI.rte.CUIDomCleanup = new Class({

    toString: "CUIDomCleanup",

    extend: CUI.rte.DomCleanup,

    handleCellSpacingAttribute: function(dom) {
        var com = CUI.rte.Common;
        var dcu = CUI.rte.DomCleanup;
        var fullScreenAdapter = this.editorKernel.getEditContext().getState("fullscreenadapter");
        if (fullScreenAdapter && fullScreenAdapter.isFullScreen() && com.isTag(dom, "table")) {
            if (this.processingMode == dcu.PRE) {
                var cellSpacing = com.getAttribute(dom, "cellspacing");
                if (cellSpacing) {
                    com.addInlineStyles(dom, {"border-collapse": "separate", "border-spacing": cellSpacing + "px"});
                } else {
                    com.addInlineStyles(dom, {"border-collapse": "initial", "border-spacing": "initial"});
                }
            } else {
                com.removeInlineStyles(dom, ["border-collapse", "border-spacing"]);
            }
        }
    }

});