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
    idField: null,
    ocmField: null,
	refIdField: null,
    
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

        this.idField = new CQ.Ext.form.Hidden({});
        this.add(this.idField);

        this.ocmField = new CQ.Ext.form.Hidden({
        	value: 'org.girlscouts.vtk.models.MeetingE'
        });
        this.add(this.ocmField);

        this.refIdField = new CQ.form.Selection({
            type: 'combobox',
            options: [
                {
                	"value": "B14B01",
                	"text": "B14B01"
                },
                {
                	"value": "B14B12",
                	"text": "B14B12"
                }
            ],
            listeners: {
                selectionchanged: {
                    scope:this,
                    fn:this.updateLink
                }
            }
        });
        this.add(this.refIdField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
    	this.idField.setValue(value.id);
    	this.refIdField.setValue(value.refId);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	var meeting = {
    		"id": this.idField.getValue(),
    		"refId": this.refIdField.getValue(),
    	};
    	return meeting;
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('vtk-meeting-id', girlscouts.components.VTKMeetingId);
