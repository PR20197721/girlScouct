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

girlscouts.components.AccordionWidget = CQ.Ext.extend(CQ.form.CompositeField, {
	
    hiddenField: null,
    nameField: null,
	anchorField: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.AccordionWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.AccordionWidget.superclass.initComponent.call(this);
        
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({text: "Section Name"}));
        this.nameField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.nameField);
        
        this.add(new CQ.Ext.form.Label({text: "Anchor"}));
        this.anchorField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.anchorField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.nameField.setValue(parts[0]);
        this.anchorField.setValue(parts[1]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.nameField.getValue() + "|||" 
        	+ this.anchorField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('accordionfield', girlscouts.components.AccordionWidget);
