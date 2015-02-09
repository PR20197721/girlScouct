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
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        girlscouts.components.VTKAgendaList.superclass.setValue.call(this);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        var value;
        for (var i = 0; i < this.items.length; i++) {
        	value += '[' + (i+1) + '^' + this.items[i] + ']';
        }
        alert('value = ' + value);
        return value;
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('vtk-agendalist', girlscouts.components.VTKAgendaList);
