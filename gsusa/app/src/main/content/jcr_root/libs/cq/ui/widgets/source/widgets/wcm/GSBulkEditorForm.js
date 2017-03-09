/*
 * Copyright 1997-2009 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */

/**
 * @class CQ.wcm.BulkEditorForm
 * @extends CQ.Ext.form.FormPanel
 * The BulkEditorForm provides {@link CQ.wcm.BulkEditor} surrounded by a HTML form. This is the standalone
 * version of the {@link CQ.wcm.BulkEditor}, HTML form is required for import button.
 * @constructor
 * Creates a new BulkEditorForm
 * @param {Object} config The config of the bulk editor object
 */
CQ.wcm.GSBulkEditorForm = CQ.Ext.extend(CQ.Ext.form.FormPanel, {
    constructor: function(config) {
        config = (!config ? {} : config);

        //this.bulkEditor = new CQ.wcm.BulkEditor(config);
        config.xtype = "gsbulkeditor";
        var renderTo = config["renderTo"];
        delete config["renderTo"];

        var defaults = {
            "renderTo": renderTo,
            //"region": "center",
            "items": [ config ],
            "hideBorders": true,
            "border": false,
            "stateful": false,
            "layout": "fit"
        };

        //CQ.Util.applyDefaults(config, defaults);

        // init component by calling super constructor
        CQ.wcm.GSBulkEditorForm.superclass.constructor.call(this, defaults);
    }
});

CQ.Ext.reg("gsbulkeditorform", CQ.wcm.GSBulkEditorForm);