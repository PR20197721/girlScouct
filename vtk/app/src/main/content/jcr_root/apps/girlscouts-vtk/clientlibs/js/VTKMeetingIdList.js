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

(function(){
	var helper= {};

	helper.options = null;
	helper.provideOptions = function() {
		if (!helper.options) {
			helper.updateOptions();
		}
		return helper.options;
	},
	helper.updateOptions = function() {
	    var http = CQ.shared.HTTP;
	    var base = '/content/girlscouts-vtk/meetings/myyearplan2017/';
		var options = new Array();
	    var levels = ['brownie', 'junior', 'daisy', 'multi-level'];
	
	    for (var i = 0; i < levels.length; i++) {
	    	var level = levels[i];
			var path = base + level.toLowerCase() + '.1.json';
			var response = http.get(http.externalize(path));
			var responseJson = JSON.parse(response.responseText);
			
		    for (var childKey in responseJson) {
		    	var child = responseJson[childKey];
		    	if (responseJson.hasOwnProperty(childKey) && typeof child === 'object') { // If object, then it is a child node.
		    		// Skip CQ built-in stuff
		    		if (childKey.indexOf('jcr:') == 0 || childKey.indexOf('cq:') == 0) {
		    			continue;
		        	}
		    	
			    	options.push({
			    		"value": base + level + '/' + childKey,
			    		"text": childKey + ' - ' + child.name,
			    		"qtip": child.name
			    	});
		    	}
		    }
	    }
	    
	    helper.options = options;
	};
	girlscouts.components.VTKMeetingIdHelper = helper;
})();

girlscouts.components.VTKMeetingIdList= CQ.Ext.extend(CQ.form.MultiField, {
	options: null,
	
    constructor: function(config) {
        config = config || {};
        defaults = {};
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKMeetingIdList.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.VTKMeetingIdList.superclass.initComponent.call(this);

        // Cleanup before submission
        var form = this.findParentByType("form");
        form.on('beforeaction', function(){
        	// Setup each meeting id
            var index = 1;
            this.items.each(function(item/*,index, length*/) {
                if (item instanceof CQ.form.MultiField.Item) {
                	var field = item.field;
			    	// Setup property keys
			    	var path = './meetings/meeting' + index + '/';
			    	field.idField.el.dom.name = path + 'id';
			    	field.idField.setValue(index);
			    	index++;

			    	field.ocmField.el.dom.name = path + 'ocm_classname';
			    	field.ocmField.setValue('org.girlscouts.vtk.models.MeetingE');

			    	field.hiddenField.el.dom.name = path + 'refId';
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
        	// /content/girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan1.scaffolding.html
        	// =>
        	// /content/girlscouts-vtk/yearPlanTemplates/yearplan2014/brownie/yearPlan1
        	path = path.substring(0, path.indexOf('.', path.lastIndexOf('/')));
	        var response = http.get(http.externalize(path + '/meetings.1.json'));
	        
	        var meetings = new Array();
	        var responseJson = JSON.parse(response.responseText);
	        for (var childKey in responseJson) {
	        	var child = responseJson[childKey];
	        	if (responseJson.hasOwnProperty(childKey) && typeof child === 'object') { // If object, then it is a child node.
	        		// Skip CQ built-in stuff
	        		if (childKey.indexOf('jcr:') == 0 || childKey.indexOf('cq:') == 0) {
	        			continue;
	        		}
	        		meetings.push({
	        			"id": child.id,
	        			"refId": child.refId
	        		});
	        	}
	        }
	
	        meetings.sort(function(a, b){return a.id - b.id});
	
	        for (var i = 0; i < meetings.length; i++) {
	        	this.addItem(meetings[i].refId);
	        }
        }
    }
});

// register xtype
CQ.Ext.reg('vtk-meeting-id-list', girlscouts.components.VTKMeetingIdList);
