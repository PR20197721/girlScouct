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

gsusa.components.TitlePathWidget = CQ.Ext.extend(CQ.form.CompositeField, {
	
    hiddenField: null,
    titleField: null,
    pathField: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":3
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.TitlePathWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.TitlePathWidget.superclass.initComponent.call(this);
        
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);
        
        this.add(new CQ.Ext.form.Label({text: "Video Title"}));
        this.titleField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.titleField);

        this.add(new CQ.Ext.form.Label({text: "Link to Video/Image"}));
        this.pathField = new CQ.form.PathField({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                },
                dialogselect: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.pathField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.titleField.setValue(parts[0]);
        this.pathField.setValue(parts[1]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.titleField.getValue() + "|||" 
        	+ this.pathField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('titlepathfield', gsusa.components.TitlePathWidget);