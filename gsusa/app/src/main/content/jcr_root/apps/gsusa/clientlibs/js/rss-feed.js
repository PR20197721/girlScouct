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

/**
 * Customize Link widget that does not have medium and small label options
 */
gsusa.components.RssFeed = CQ.Ext.extend(CQ.form.CompositeField, {

    hiddenField: null,
    pathField: null,
    pageCheckbox: null,
    dirCheckbox: null,
    rootPath: null,
    
    constructor: function(config) {
    	var PARENT_LEVEL = 2; // content/councilName	
        var currentPath = CQ.shared.HTTP.getPath();
        var slashIndex = 0;
        for (var i = 0; i < PARENT_LEVEL; i++) {
			slashIndex = currentPath.indexOf("/", slashIndex + 1);
        }
        rootPath = slashIndex == -1 ? currentPath : currentPath.substring(0, slashIndex);
        if (typeof(config.relativePath) != 'undefined') {
			rootPath += config.relativePath;
        }
        
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.RssFeed.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.RssFeed.superclass.initComponent.call(this);

        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({
        	width: 35,
        	text: "Path"
        }));
        this.pathField = new CQ.form.PathField({
        	rootPath: rootPath,
        	width: 270,
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

        this.add(new CQ.Ext.form.Label({
        	width: 40,
        	text: "Page"
        }));
        this.pageCheckbox = new CQ.Ext.form.Checkbox({
        	width: 40,
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.pageCheckbox);
        
        this.add(new CQ.Ext.form.Label({
        	width: 50,
        	text: "SubDir"
        }));
        this.dirCheckbox = new CQ.Ext.form.Checkbox({
        	width: 40,
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.dirCheckbox);

    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.pathField.setValue(parts[0]);
		this.pageCheckbox.setValue(parts[1]);
		this.dirCheckbox.setValue(parts[2]);
		this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.pathField.getValue() + "|||"
            + (this.pageCheckbox.getValue() ? 'true' : 'false') + "|||"
            + (this.dirCheckbox.getValue() ? 'true' : 'false');
            
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('rssfeed', gsusa.components.RssFeed);
