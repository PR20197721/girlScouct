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

        this.idField = new CQ.Ext.form.Hidden({});
        this.add(this.idField);

        this.ocmField = new CQ.Ext.form.Hidden({
        	value: 'org.girlscouts.vtk.models.MeetingE'
        });
        this.add(this.ocmField);

        var form = this.findParentByType("form");

        this.refIdField = new CQ.form.Selection({
            type: 'combobox',
            optionsProvider: girlscouts.components.VTKMeetingId.Helper.getOptions,
            listeners: {
                selectionchanged: {
                    scope:this,
                    fn:this.updateLink
                }
            }
        });
        this.add(this.refIdField);
        
        this.linkField = new CQ.Ext.form.Label({
        	html: '<span class="cq-tbtn cq-tbtn-medium x-btn-noicon">' + 
        		'<span class="x-btn-text" href="/etc/scaffolding/girlscouts-vtk/meeting.html">' +
        		'Create New Meeting</span></span>',
        	name: ''
        });
        this.add(this.linkField);
    },
    
    updateLink: function(path) {
    	this.linkField.setText('<span class="cq-tbtn cq-tbtn-medium x-btn-noicon">' + 
    			'<span class="x-btn-text" href="http://www.google.com/">' + 
    			'Edit Meeting</span></span>',
    			false);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
    	this.idField.setValue(value.id);
    	this.refIdField.setValue(value.refId);
    	this.updateLink();
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

girlscouts.components.VTKMeetingId.Helper = {
	options: null,
	getOptions: function() {
		alert('getOptions');
		if (!this.options) {
			this.updateOptions();
		}
	},
	updateOptions: function() {
		alert('updateOptions');
	    var http = CQ.shared.HTTP;
	    var base = '/content/girlscouts-vtk/meetings/myyearplan/';
		var options = new Array();
	    var levels = ['brownie', 'junior', 'daisy'];

	    for (var i = 0; i < levels.length; i++) {
	    	var level = levels[i];
			var path = base + level.toLowerCase() + '.1.json';
			var response = http.get(http.externalize(path));
			var responseJson = JSON.parse(response.responseText);
			
		    for (var childKey in responseJson) {
		    	if (responseJson.hasOwnProperty(childKey) && typeof child === 'object') { // If object, then it is a child node.
		    		// Skip CQ built-in stuff
		    		if (childKey.indexOf('jcr:') == 0 || childKey.indexOf('cq:') == 0) {
		    			continue;
		        	}
		    	
			    	options.push({
			    		"value": base + childKey,
			    		"text": childKey
			    	});
		    	}
		    }
	    }
	    
	    this.options = options;
	}
};