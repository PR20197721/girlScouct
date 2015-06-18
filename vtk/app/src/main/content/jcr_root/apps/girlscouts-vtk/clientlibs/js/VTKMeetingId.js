/*
 * Copyright 1997-2010 Day Management AG
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
 * @class Ejst.CustomWidget
 * @extends CQ.form.CompositeField
 * This is a custom widget based on {@link CQ.form.CompositeField}.
 * @constructor
 * Creates a new CustomWidget.
 * @param {Object} config The config object
 */

girlscouts.components.VTKMeetingId = CQ.Ext.extend(CQ.form.Selection, {
    constructor: function(config) {
        config = config || {};
        defaults = {
    		type: 'select',
    		editable: true,
    		allowBlank: false,
    		optionsConfig: {
    			forceSelection: true
    		}
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKMeetingId.superclass.constructor.call(this, config);
    },
    
    initComponent: function() {
        girlscouts.components.VTKMeetingId.superclass.initComponent.call(this);

		this.idField = new CQ.Ext.form.Hidden({});
		this.add(this.idField);

		this.ocmField = new CQ.Ext.form.Hidden({});
		this.add(this.ocmField);
    }
});

// register xtype
CQ.Ext.reg('vtk-meeting-id', girlscouts.components.VTKMeetingId);