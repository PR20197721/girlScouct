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

        // Do not submit the default value. Value will be submitted by the hidden field.
        this.name = '';
        // Cleanup before submission
        form.on('beforeaction', function(){
        	// The combined value.
        	// e.g. agenda = [1^As Girls Arrive^10][2^As Girls Arrive^10][3^As Girls Arrive^10]...
        	this.hiddenField.setValue(this.getValue());

        	// Setup each agenda item
            var index = 1;
            this.items.each(function(item/*,index, length*/) {
                if (item instanceof CQ.form.MultiField.Item) {
                	var field = item.field;
			    	// Generate name if empty
			    	if (!field.nodeName) {
			    		field.nodeName = 'A' + (Date.now() + Math.floor(Math.random()*9000) + 1000);
			    	}
			    	// Remove special Characters
			    	field.nodeName = field.nodeName.replace(/[^a-zA-Z0-9]/g, '');
			    	
			    	// Setup property keys
			    	var path = './activities/' + field.nodeName+ '/';
                    field.ocmField.el.dom.name = path + 'ocm_classname';
                    field.ocmField.setValue('org.girlscouts.vtk.models.Activity');
			    	field.nameField.el.dom.name = path + 'name';
			    	field.durationField.el.dom.name = path + 'duration';
			    	field.descriptionField.el.dom.name = path + 'activityDescription';
			    	field.numberField.el.dom.name = path + 'activityNumber';
			    	field.outdoorCheckboxField.el.dom.name = path + 'isOutdoorAvailable';
			    	field.hiddenSlingBooleanField.el.dom.name = path + 'isOutdoorAvailable@TypeHint';
			    	field.hiddenSlingDeleteField.el.dom.name = path + 'isOutdoorAvailable@Delete';
			    	
			    	field.outdoorDescriptionField.el.dom.name = path + 'activityDescription_outdoor';
                	field.numberField.setValue(index++);
                }
            }, this);
        }, this);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(newValue) {
        //this.fireEvent("change", this, value, this.getValue());
        var oldItems = this.items;
        oldItems.each(function(item/*, index, length*/) {
            if (item instanceof CQ.form.MultiField.Item) {
                this.remove(item, true);
                this.findParentByType("form").getForm().remove(item);
            }
        }, this);
        this.doLayout();
        
        var http = CQ.shared.HTTP;
        // If it is an update, read agenda items.
        var path = window.location.pathname;
        if (path.indexOf('/etc/scaffolding') != 0) { 
        	// /content/girlscouts-vtk/meetings/myyearplan/brownie/B14B04.scaffolding.html
        	// =>
        	// /content/girlscouts-vtk/meetings/myyearplan/brownie/B14B04
        	path = path.substring(0, path.indexOf('.', path.lastIndexOf('/')));
	        var response = http.get(http.externalize(path + '/activities.1.json'));
	        
	        var agendaItems = new Array();
	        var responseJson = JSON.parse(response.responseText);
	        for (var childKey in responseJson) {
	        	var child = responseJson[childKey];
	        	if (responseJson.hasOwnProperty(childKey) && typeof child === 'object') { // If object, then it is a child node.
	        		// Skip CQ built-in stuff
	        		if (childKey.indexOf('jcr:') == 0 || childKey.indexOf('cq:') == 0) {
	        			continue;
	        		}
	        		var activityNumber = child.activityNumber;
	        		agendaItems.push({
	        			"nodeName": childKey,
	        			"activityNumber": child.activityNumber,
	        			"name": child.name,
	        			"duration": child.duration,
	        			"description": child.activityDescription,
	        			"isOutdoorAvailable": child.isOutdoorAvailable,
	        			"activityDescription_outdoor": child.activityDescription_outdoor
	        		});
	        	}
	        }
	
	        agendaItems.sort(function(a, b){return a.activityNumber - b.activityNumber});

	        for (var i = 0; i < agendaItems.length; i++) {
	        	this.addItem(agendaItems[i]);
	        }
        }
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        var value = '';
        this.items.each(function(item, index/*, length*/) {
            if (item instanceof CQ.form.MultiField.Item) {
            	var agendaItem = item.getValue();
                value += '[' + index + '^' + agendaItem.name + '^' + agendaItem.duration + ']';
                index++;
            }
        }, this);
        return value;
    }
});

// register xtype
CQ.Ext.reg('vtk-agendalist', girlscouts.components.VTKAgendaList);
