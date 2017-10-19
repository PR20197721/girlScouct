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

gsusa.components.SocialMediaWidgetWithName = CQ.Ext.extend(CQ.form.CompositeField, {

    hiddenField: null,
    nameField: null,
    linkField: null,
	iconField: null,
    
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.SocialMediaWidgetWithName.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.SocialMediaWidgetWithName.superclass.initComponent.call(this);

        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({text: "Name"}));
        this.nameField = new CQ.Ext.form.TextField({
        	style:"margin-right:10px; margin-left:5px;",
        	listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.nameField);
        
        this.add(new CQ.Ext.form.Label({text: "URL"}));
        this.linkField = new CQ.Ext.form.TextField({
        	style:"margin-right:10px; margin-left:5px;",
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.linkField);
        
        this.add(new CQ.Ext.form.Label({text: "Icon Path"}));
        this.iconField = new CQ.form.PathField({
        	style:"margin-right:0px; margin-left:5px;",
        	rootPath: "/content/dam/girlscouts-gsusa",
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
        this.add(this.iconField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.nameField.setValue(parts[0]);
        this.linkField.setValue(parts[1]);
        this.iconField.setValue(parts[2]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.nameField.getValue() + "|||" 
        	+ this.linkField.getValue() + "|||"
        	+ this.iconField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('socialmediawidgetwithname', gsusa.components.SocialMediaWidgetWithName);
