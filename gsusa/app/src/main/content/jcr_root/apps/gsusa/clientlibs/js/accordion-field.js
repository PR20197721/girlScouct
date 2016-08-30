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

deleteAccordionFlag = false;
accIndex = 0;

girlscouts.functions.deleteaccordion=function(field, event){
	if(deleteAccordionFlag == false){
		deleteAccordionFlag = true;
		CQ.Ext.Msg.show({
			title: "Warning", 
			msg: "If you delete this field, all content contained within will be removed as well. You can RESTORE the content at a later point ON THIS PAGE ONLY by using the same identifier. " +
			"If you do not want to delete this field, click 'Cancel' below to undo all changes",
			buttons: CQ.Ext.Msg.OK
			});
	}
};

gsusa.components.AccordionWidget = CQ.Ext.extend(CQ.form.CompositeField, {
	
    hiddenField: null,
    nameField: null,
	anchorField: null,
	idField: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":3
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.AccordionWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.AccordionWidget.superclass.initComponent.call(this);
        
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
        
        this.add(new CQ.Ext.form.Label({text: "Identifier"}));
        
        this.idField = new CQ.Ext.form.TextField({
        	allowBlank: false,
            listeners: {
            	added: {
            		scope:this,
            		fn:this.createId
            	},
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });

        this.add(this.idField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.nameField.setValue(parts[0]);
        this.anchorField.setValue(parts[1]);
        this.idField.setValue(parts[2]);
        
	    if(this.idField.value == null || this.idField.value == ""){
	    	this.idField.setValue(accIndex);
	    	this.idField.defaultValue = accIndex;
	    }
	    
	    accIndex++;
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {   	
        return this.nameField.getValue() + "|||" 
        	+ this.anchorField.getValue() + "|||"
        	+ this.idField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    },
    
    createId: function() {
	    var text = "";
	    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	    for( var i=0; i < 8; i++ )
	        text += possible.charAt(Math.floor(Math.random() * possible.length));

	    this.idField.setValue(text);
	}

});

// register xtype
CQ.Ext.reg('accordionfield', gsusa.components.AccordionWidget);