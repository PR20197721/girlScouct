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

girlscouts.components.VTKAgendaList= CQ.Ext.extend(CQ.form.MultiField, {
    constructor: function(config) {
        config = config || {};
        defaults = {};
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKAgendaList.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.VTKAgendaList.superclass.initComponent.call(this);
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        var form = this.findParentByType("form");
        form.on('beforeaction', function(){
        	this.hiddenField.setValue(this.getValue());
        }, this);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        this.fireEvent("change", this, value, this.getValue());
        
        var newValues = value.split(']');
        newValues.each(function(_value, i) {
        	// [1^open ceremory^10 => open ceremony^10 
        	_value[i] = /[^\^]+^(.*)/.exec(_value[i].trim())[0]; 
        });
        
        this.items = new Array();
        newValues.each(function(_value) {
        	this.addValue(_value);
        });
        this.doLayout();
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        var value = '';
        this.items.each(function(item, index/*, length*/) {
            if (item instanceof CQ.form.MultiField.Item) {
                //value[index] = item.getValue();
                value += '[' + index + '^' + item.getValue() + ']';
                index++;
            }
        }, this);
        return value;
    },
    
//    addItem: function() {
//        girlscouts.components.VTKAgendaList.superclass.addItem.call(this, arguments);
//        alert('getValue = ' + this.getValue());
//        this.hiddenField.setValue(this.getValue());
//    },
    
    addItem: function(value) {
        var item = this.insert(this.items.getCount() - 1, {});
        var form = this.findParentByType("form");
        if (form)
            form.getForm().add(item.field);
        this.doLayout();

        if (item.field.processPath) item.field.processPath(this.path);
        if (value) {
            item.setValue(value);
        }

        if (this.fieldWidth < 0) {
            // fieldWidth is < 0 when e.g. the MultiField is on a hidden tab page;
            // do not set width but wait for resize event triggered when the tab page is shown
            return;
        }
        if (!this.fieldWidth) {
            this.calculateFieldWidth(item);
        }
        try {
            item.field.setWidth(this.fieldWidth);
        }
        catch (e) {
            CQ.Log.debug("CQ.form.MultiField#addItem: " + e.message);
        }
    }
});

// register xtype
CQ.Ext.reg('vtk-agendalist', girlscouts.components.VTKAgendaList);
