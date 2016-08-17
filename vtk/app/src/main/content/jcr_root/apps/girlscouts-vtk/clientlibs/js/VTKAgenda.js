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

girlscouts.components.VTKAgenda = CQ.Ext.extend(CQ.form.CompositeField, {

	RTE_PLUGIN_CONF: {
	   "links": [
	      {
	         "height": 316,
	         "linkAttributes": [
	            {
	               "collapsed": true,
	               "collapsible": true,
	               "inputValue": "advanced",
	               "name": "./linkdialog/cq:adhocLinkTrackingTab",
	               "title": "Link tracking",
	               "xtype": "dialogfieldset",
	               "items": {
	                  "enable": {
	                     "attribute": "enabletracking",
	                     "fieldDescription": "override analytics framework settings",
	                     "fieldLabel": "Custom link tracking",
	                     "name": "./linkdialog/cq:adhocLinkTrackingEnableTracking",
	                     "xtype": "checkbox",
	                     "listeners": {
	                        "check": function(component){ var dlg=component.findParentByType('rtelinkdialog');dlg.enableSCFields(component.checked); }
	                     }
	                  },
	                  "events": {
	                     "attribute": "adhocevents",
	                     "fieldDescription": "e.g.: event2, event7",
	                     "fieldLabel": "Include SiteCatalyst events",
	                     "name": "./linkdialog/cq:adhocLinkTrackingEvents",
	                     "xtype": "textfield"
	                  },
	                  "evars": {
	                     "attribute": "adhocevars",
	                     "fieldDescription": "e.g.: eVar1: pagedata.url, prop4: 'const'",
	                     "fieldLabel": "Include SiteCatalyst variables",
	                     "name": "./linkdialog/cq:adhocLinkTrackingEvars",
	                     "xtype": "textfield"
	                  }
	               }
	            }
	         ]
	      }
	   ],
	   "misctools": {
	      "features": "*"
	   },
	   "edit": {
	      "features": "[paste-plaintext,paste-wordhtml]"
	   },
	   "findreplace": {
	      "features": "*"
	   },
	   "format": {
	      "features": "*"
	   },
	   "image": {
	      "features": "*"
	   },
	   "keys": {
	      "features": "*"
	   },
	   "justify": {
	      "features": "*"
	   },
	   "lists": {
	      "features": "*"
	   },
	   "paraformat": {
	      "features": "*",
	      "formats": [
	          {
	              "description": "Paragraph",
	              "tag": "p"
	          },
	          {
	              "description": "Header 1",
	              "tag": "h1"
	          },
	          {
	              "description": "Header 2",
	              "tag": "h2"
	          },
	          {
	              "description": "Header 3",
	              "tag": "h3"
	          },
	          {
	              "description": "Header 4",
	              "tag": "h4"
	          },
	          {
	              "description": "Header 5",
	              "tag": "h5"
	          },
	          {
	              "description": "Header 6",
	              "tag": "h6"
	          }
		  ]
	   },
	   "spellcheck": {
	      "features": "*"
	   },
	   "styles": {
	      "features": "*"
	   },
	   "subsuperscript": {
	      "features": "*"
	   },
	   "table": {
	      "features": "*"
	   },
	   "undo": {
	      "features": "*"
	   }
	},

    hiddenField: null,
    numberField: null,
    nameField: null,
	durationField: null,
	descriptionField: null,
	nodeName: null,
	outdoorCheckboxField: null,
	outdoorDescriptionField: null,
    
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":8
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKAgenda.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.VTKAgenda.superclass.initComponent.call(this);
        this.ocmField = new CQ.Ext.form.Hidden({});
        this.add(this.ocmField);

        this.hiddenField = new CQ.Ext.form.Hidden({});
        this.add(this.hiddenField);
        
        this.hiddenSlingBooleanField = new CQ.Ext.form.Hidden({value: "Boolean"});
        this.add(this.hiddenSlingBooleanField);
        
        this.hiddenSlingDeleteField = new CQ.Ext.form.Hidden({});
        this.add(this.hiddenSlingDeleteField);

        this.numberField = new CQ.Ext.form.Hidden({});
        this.add(this.numberField);

        this.add(new CQ.Ext.form.Label({text: "Name"}));
        this.nameField = new CQ.Ext.form.TextField({
        	width: 100,
        	allowBlank: false,
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.nameField);
        
        this.add(new CQ.Ext.form.Label({text: "Duration"}));
        this.durationField = new CQ.Ext.form.NumberField({
        	maxValue: 30,
        	width: 30,
        	maxText: 'Duration should not exceed 30 minutes',
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.durationField);
        
        this.add(new CQ.Ext.form.Label({text: "Description"}));
        this.descriptionField = new CQ.form.RichText({
        	rtePlugins: this.RTE_PLUGIN_CONF,
        	width: 300,
        	specialCharsConfig: {
        		chars: {
        			"em-dash": {
        				"entity": "&#8212;"
        			},
        			"copyright": {
        				"entity": "&#169;"
        			},
        			"registerd": {
        				"entity": "&#174;",
        			},
        			"trademark": {
        				"entity": "&#8482;",
        			},
        			"horizontal-rule": {
        				"entity": "<hr>"
        			}	
        		}
        	},

            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.descriptionField);
        
        this.add(new CQ.Ext.form.Label({text: "Outdoor?"}));
        this.outdoorCheckboxField = new CQ.Ext.form.Checkbox({  
        	inputValue: true,
        	listeners: {
        		check: {
                    scope: this,
                    fn: function(me, val) {
                    	console.info(val);
                    	var panel = me.findParentByType('panel');
                    	console.info(panel);
                    	var panelParent = panel.findParentByType('panel');
                    	if (val) {
                    		panelParent.find("gstag", "gs")[0].enable();
                    	} else {
                    		panelParent.find("gstag", "gs")[0].setValue('');
                    		panelParent.find("gstag", "gs")[0].disable();
                    	}
                    }
                },
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.outdoorCheckboxField);
        
        this.outdoorDescriptionField = new CQ.form.RichText({
        	width: 300,
        	rtePlugins: this.RTE_PLUGIN_CONF,
        	disabled: true,
        	gstag: "gs",
        	specialCharsConfig: {
        		chars: {
        			"em-dash": {
        				"entity": "&#8212;"
        			},
        			"copyright": {
        				"entity": "&#169;"
        			},
        			"registerd": {
        				"entity": "&#174;",
        			},
        			"trademark": {
        				"entity": "&#8482;",
        			},
        			"horizontal-rule": {
        				"entity": "<hr>"
        			}	
        		}
        	},

        	listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.outdoorDescriptionField);
        
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
    	this.nodeName = value.nodeName;
    	this.nameField.setValue(value.name);
    	this.durationField.setValue(value.duration);
    	this.descriptionField.setValue(value.description);
    	this.outdoorCheckboxField.setValue(value.isOutdoorAvailable);
    	this.outdoorDescriptionField.setValue(value.activityDescription_outdoor);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	var agenda = {
    		"nodeName": this.nodeName,
    		"name": this.nameField.getValue(),
    		"duration": this.durationField.getValue(),
    		"description": this.descriptionField.getValue(),
    		"isOutdoorAvailable": this.outdoorCheckboxField.getValue(),
    		"activityDescription_outdoor": this.outdoorDescriptionField.getValue()
    	};
    	return agenda;
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('vtk-agenda', girlscouts.components.VTKAgenda);
