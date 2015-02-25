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

girlscouts.components.VTKMeetingId = CQ.Ext.extend(CQ.form.CompositeField, {
    meetingField: null,
    linkField: null,
    
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":3
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKMeetingId.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.VTKMeetingId.superclass.initComponent.call(this);

        this.meetingField = new CQ.form.Selection({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateLink
                }
            }
        });
        this.add(this.meetingField);
        
        this.linkField = new CQ.Ext.form.Label({
        	html: '<a href="/etc/scaffolding/girlscouts-vtk/meeting.html">Create New Meeting</a>'
        });
        this.add(this.linkField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
    	this.meetingField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	return this.meetingField.getValue();
    }
});

// register xtype
CQ.Ext.reg('vtk-meeting-id', girlscouts.components.VTKMeetingId);
