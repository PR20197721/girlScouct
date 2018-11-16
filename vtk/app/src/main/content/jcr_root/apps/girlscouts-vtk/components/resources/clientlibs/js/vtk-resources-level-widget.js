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

girlscouts.components.VtkResourcesLevelWidget = CQ.Ext.extend(CQ.form.CompositeField, {

	hiddenField: null,
	titleField: null,
	typeField: null,
	pathField: null,
	idField: null,
	
	constructor: function(config) {
		config = config || { };
		var defaults = {
			"border": false,
			"layout": "table",
			"columns":2
		};
		config = CQ.Util.applyDefaults(config, defaults);
		girlscouts.components.VtkResourcesLevelWidget.superclass.constructor.call(this, config);
	},

	// overriding CQ.Ext.Component#initComponent
	initComponent: function() {
		girlscouts.components.VtkResourcesLevelWidget.superclass.initComponent.call(this);

		this.hiddenField = new CQ.Ext.form.Hidden({
			name: this.name
		});
		this.add(this.hiddenField);
		
		this.add(new CQ.Ext.form.Label({text: "Title"}));
		this.titleField = new CQ.Ext.form.TextField({
			allowBlank: false,
			width: 300,
			listeners: {
				change: {
					scope:this,
					fn:this.updateHidden
				}
			}
		});
		this.add(this.titleField);

		this.add(new CQ.Ext.form.Label({text: "Type"}));

		function isMaster() {
			return window.location.href.indexOf("/vtkcontent") != -1;
		}
		
		var typeFieldConf = {
			type: "select",
			allowBlank: false,
			options: [
				{
					text: "Link",
					value: "link"
				},
				{
					text: "PDF",
					value: "pdf"
				},
				{
					text: "Video",
					value: "video"
				},
				{
					text: "Download",
					value: "download"
				}
			],
			width: 120,
			listeners: {
				selectionchanged: {
					scope:this,
					fn:this.updateHidden
				}
			}
		};
		
		if (isMaster()) {
			typeFieldConf.options = typeFieldConf.options.concat([
				{
					text: "List",
					value: "list"
				},
				{
					text: "Meeting Overview",
					value: "meeting-overview"
				},
			]);
		}
		this.typeField = new CQ.form.Selection(typeFieldConf);
		this.add(this.typeField);
		
		this.add(new CQ.Ext.form.Label({text: "URI"}));
		var pathFieldConf = {
			rootPath: "/content/dam-resources2",
			width: 330,
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
		};

		if (isMaster()) {
			pathFieldConf.emptyText = 'Not required for "List" and "Meeting Overview" types.';
		}
		this.pathField = new CQ.form.PathField(pathFieldConf);
		this.add(this.pathField);
		
		this.idField = new CQ.Ext.form.Hidden();
		this.add(this.idField);
	},

	// overriding CQ.form.CompositeField#setValue
	setValue: function(value) {
		var parts = value.split("|||");
		this.titleField.setValue(parts[0]);
		this.typeField.setValue(parts[1]);
		this.pathField.setValue(parts[2]);
		// For ID field
		if (parts.length > 3) {
			this.idField.setValue(parts[3]);
		}
		this.hiddenField.setValue(value);
	},

	// overriding CQ.form.CompositeField#getValue
	getValue: function() {
		return this.getRawValue();
	},

	// overriding CQ.form.CompositeField#getRawValue
	getRawValue: function() {
		var id = this.idField.getValue();
		if (typeof id == "undefined" || "" == id) {
			// VTK Resource Type ID
			id = "vtkrtid" +
					(new Date()).getTime() +
					Math.floor(Math.random() * Math.floor(999));
		}
		return this.titleField.getValue() + "|||" 
			+ this.typeField.getValue() + "|||"
			+ this.pathField.getValue() + "|||"
			+ id;
	},

	// private
	updateHidden: function() {
		this.hiddenField.setValue(this.getValue());
	} 
});

// register xtype
CQ.Ext.reg('vtkresourceslevel', girlscouts.components.VtkResourcesLevelWidget);
