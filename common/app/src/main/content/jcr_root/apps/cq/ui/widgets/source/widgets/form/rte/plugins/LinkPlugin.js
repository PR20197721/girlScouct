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

// this class mirrors the CUI implementation for overlaying and makes it compatible
// with Ext's class system
CQ.form.rte.plugins.LinkPlugin = CQ.Ext.extend(CUI.rte.plugins.LinkPlugin, {

    _rtePluginType: "compat",

    constructor: function(/* varargs */) {
        if (this.construct) {
            this.construct.apply(this, arguments);
        }
    },

    getDialogClass: function() {
     return CQ.form.rte.plugins.LinkDialog;
    }

});

CQ.form.rte.plugins.LinkPlugin.LINKABLE_OBJECTS = CUI.rte.plugins.LinkPlugin.LINKABLE_OBJECTS;