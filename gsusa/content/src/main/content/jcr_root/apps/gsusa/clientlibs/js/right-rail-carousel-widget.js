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

gsusa.components.RightRailCarouselWidget = CQ.Ext.extend(CQ.form.CompositeField, {

    hiddenField: null,
    labelField: null,
	pathField: null,
	checkBoxField : null,
	imagePathField: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.RightRailCarouselWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.RightRailCarouselWidget.superclass.initComponent.call(this);

        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

		this.checkBoxField = new CQ.Ext.form.Checkbox({
			listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
		});
        this.add(new CQ.Ext.form.Label({text: "New window"}));
		this.add(this.checkBoxField);

        this.add(new CQ.Ext.form.Label({text: "Label"}));
        this.labelField = new CQ.form.RichText({
        	rtePlugins: this.RTE_PLUGIN_CONF(),
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
        this.add(this.labelField);

        this.add(new CQ.Ext.form.Label({text: "Link Path"}));
        this.pathField = new CQ.form.PathField({
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
        
        this.add(new CQ.Ext.form.Label({text: "Image Path"}));
        this.imagePathField = new CQ.form.PathField({
            rootPath: "/content/dam",
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
        this.add(this.imagePathField);
        
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.labelField.setValue(parts[0]);
        this.pathField.setValue(parts[1]);
        this.imagePathField.setValue(parts[3]);
        this.hiddenField.setValue(value);
        if (parts.length > 2) {
        	this.checkBoxField.setValue((parts[2] === "true"));
        } else {
        	this.checkBoxField.setValue(false);
        }
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.labelField.getValue() + "|||"
        	+ this.pathField.getValue() + "|||" 
        	+ this.checkBoxField.getValue() + "|||"
        	+ this.imagePathField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    },

	RTE_PLUGIN_CONF: function(){return {
	   "links": {
	      "linkDialogConfig": {
	    	  "height": "250",
	    	  "linkAttributes": [{
	    		  "attribute": "class",
	    		  "xtype": "selection",
	    		  "options": [
	    		      {
	    		          value: "",
	    		          text: "Normal Link"
	    		      },
	    		      {
	    		          value: "button white",
	    		          text: "Button",
	    		      }
	    		  ],
	    		  "fieldLabel": "Style"
	    	  }]
	      }
	   },
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
	};}

});

// register xtype
CQ.Ext.reg('rightrailcarouselwidget', gsusa.components.RightRailCarouselWidget);