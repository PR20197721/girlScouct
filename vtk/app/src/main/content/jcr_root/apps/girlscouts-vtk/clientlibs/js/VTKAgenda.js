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
	      "formats": {
	         "p": {
	            "description": "Paragraph",
	            "tag": "p"
	         },
	         "h1": {
	            "description": "Header 1",
	            "tag": "h1"
	         },
	         "h2": {
	            "description": "Header 2",
	            "tag": "h2"
	         },
	         "h3": {
	            "description": "Header 3",
	            "tag": "h3"
	         },
	         "h4": {
	            "description": "Header 4",
	            "tag": "h4"
	         },
	         "h5": {
	            "description": "Header 5",
	            "tag": "h5"
	         },
	         "h6": {
	            "description": "Header 6",
	            "tag": "h6"
	         }
	      }
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
    nameField: null,
	durationField: null,
	descriptionField: null,
    
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":3
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKAgenda.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.VTKAgenda.superclass.initComponent.call(this);

        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({text: "Name"}));
        this.nameField = new CQ.Ext.form.TextField({
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
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
    	this.nameField.setValue(value.name);
    	this.durationField.setValue(value.duration);
    	this.descriptionField.setValue(value.description);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	var agenda = {
    		"name": this.nameField.getValue(),
    		"duration": this.durationField.getValue(),
    		"description": this.descriptionField.getValue()
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
