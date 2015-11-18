gsusa.components.MeetTheCookiesWidget = CQ.Ext.extend(CQ.form.CompositeField, {
    
    hiddenField: null,
    title: null,
    image: null,
    description: null,
    buttonTitle: null,
    buttonLink: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":3
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.MeetTheCookiesWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.MeetTheCookiesWidget.superclass.initComponent.call(this);
        
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({text: "Title"}));
        this.title = new CQ.Ext.form.TextField({
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
        this.add(this.title);
        
        this.add(new CQ.Ext.form.Label({text: "Image"}));
        this.image = new CQ.form.PathField({
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
        this.add(this.image);

        this.add(new CQ.Ext.form.Label({text: "Description"}));
        this.description = new CQ.form.RichText({
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
        this.description = new CQ.form.RichText({
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
        this.add(this.description);
        
        this.add(new CQ.Ext.form.Label({text: "Button Title"}));
        this.buttonTitle = new CQ.Ext.form.TextField({
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
        this.add(this.buttonTitle);
        
        this.add(new CQ.Ext.form.Label({text: "Button Link"}));
        this.buttonLink = new CQ.Ext.form.TextField({
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
        this.add(this.buttonLink);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.title.setValue(parts[0]);
        this.image.setValue(parts[1]);
        this.description.setValue(parts[2]);
        this.buttonTitle.setValue(parts[3]);
        this.buttonLink.setValue(parts[4]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.title.getValue() + "|||" 
            + this.image.getValue() + "|||"
            + this.description.getValue() + "|||"
            + this.buttonTitle.getValue() + "|||"
            + this.buttonLink.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    },
    
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
	}
});

// register xtype
CQ.Ext.reg('meetthecookies', gsusa.components.MeetTheCookiesWidget);