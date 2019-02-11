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
    subtitleField: null,
    //nameFieldDelete: null,
	durationField: null,
	//durationFieldDelete: null,
	descriptionField: null,
	//descriptionFieldDelete: null,
	materialField: null,
//	materialFieldDelete: null,
	materialFieldLabel: null, //important for isNasa checkbox
	nodeName: null,
	whateverButton: null,
	moreDescription: null,
	skillSet: null,
	isMainAgenda: false,
	additionalAgendaField: null,
	isMultipleAgenda: null,
	isMulitpleAgendaLabel: null,
	isNasaChecked: true,
    outdoorField:false,
    globalField:false,
    durationConst:[5,10,15,20,25,30,35,40,45,50,55,60],

	//outdoorCheckboxField: null,
	//outdoorDescriptionField: null,
    
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "autoScroll":true,
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

        /*this.hiddenSlingBooleanField = new CQ.Ext.form.Hidden({value: "Boolean"});
        this.add(this.hiddenSlingBooleanField);
        
        this.hiddenSlingDeleteField = new CQ.Ext.form.Hidden({});
        this.add(this.hiddenSlingDeleteField);*/

        this.numberField = new CQ.Ext.form.Hidden({});
        this.add(this.numberField);
        
        this.additionalAgendaField = new CQ.Ext.form.Hidden({});
        this.add(this.additionalAgendaField);
        
//if the NASA checked box is checked, it will return an array of length 1 with true in it. If unchecked, it will return an array with length 0..
        if (this.findParentByType('panel').findParentByType('panel').getComponent('isNasa') != null) {
	        if (true){//this.findParentByType('panel').findParentByType('panel').getComponent('isNasa').getValue().length > 0) {
	        	this.isNasaChecked = true;
	        } else {
	        	//this.isNasaChecked = false;
	        }
        }
        
        if (this.isMainAgenda) {
        	this.isMulitpleAgendaLabel = new CQ.Ext.form.Label({text: "Multiple Agenda?", disabled:((this.isMainAgenda && !this.isNasaChecked) ? true : false)});
	        this.add(this.isMulitpleAgendaLabel);
	        
	        this.isMultipleAgenda = new CQ.Ext.form.Checkbox({  
	        	inputValue: true,
	        	disabled: false,
                checked:true,
	        	listeners: {


render: {
	                    scope: this,
    fn: function(me, val) {

setTimeout(function(){  

        
        					    var panel = me.findParentByType('panel');
	                    	    var panelParent = panel.findParentByType('panel');


	                    		//panelParent.findByType("label")[0].enable(); //"Multiple Agenda?" label
	                    		me.enable();
	                    		panelParent.findByType("button")[0].enable();
	                    		panelParent.findByType("button")[0].show();
	                    		
	                    		panelParent.findByType("textfield")[1].enable(); //duration
	                            panelParent.findByType("textfield")[1].show();

                                panelParent.findByType("label")[2].enable(); //duration
	                            panelParent.findByType("label")[2].show();

	                    		panelParent.findByType("richtext")[0].disable();  //description
	                    		panelParent.findByType("richtext")[1].disable();  //material list
	                    		
	                    		//panelParent.findByType("textfield")[1].hide();
	                    		
	                    		panelParent.findByType("richtext")[0].hide();
	                    		panelParent.findByType("richtext")[1].hide();
                                panelParent.findByType("label")[0].hide();
                                panelParent.findByType("checkbox")[0].hide();
    
	                    		//panelParent.findByType("label")[2].hide();
	                    		panelParent.findByType("label")[3].hide();
                                panelParent.findByType("label")[4].hide();
                                panelParent.findByType("label")[6].hide(); //outdoor
                                panelParent.findByType("label")[7].hide(); //global
                                panelParent.findByType("checkbox")[1].hide();// outdoor
                                panelParent.findByType("checkbox")[2].hide();// global



           }, 100);
    }
},

	        		check: {
	                    scope: this,
	                    fn: function(me, val) {
	                    	var panel = me.findParentByType('panel');
	                    	var panelParent = panel.findParentByType('panel');

	                		
	                    		//TODO: clean this up
	                    		panelParent.findByType("label")[0].enable(); //"Multiple Agenda?" label
	                    		me.enable();
	                    		panelParent.findByType("button")[0].enable();
	                    		panelParent.findByType("button")[0].show();
	                    		//panelParent.findByType("textfield")[0].disable(); //name
	                    		//-panelParent.findByType("textfield")[1].disable(); //duration
	                    		//panelParent.findByType("textfield")[2].disable(); //subtitle
	                    		panelParent.findByType("richtext")[0].disable();  //description
	                    		panelParent.findByType("richtext")[1].disable();  //material list
	                    		//panelParent.findByType("textfield")[0].hide();
	                    		//panelParent.findByType("textfield")[1].hide();
	                    		//panelParent.findByType("textfield")[2].hide();//subtitle
	                    		panelParent.findByType("richtext")[0].hide();
	                    		panelParent.findByType("richtext")[1].hide();
	                    		//panelParent.findByType("label")[1].hide();
	                    		//panelParent.findByType("label")[2].hide();
	                    		panelParent.findByType("label")[3].hide();
	                    		panelParent.findByType("label")[4].hide();
	                    		//panelParent.findByType("label")[5].hide();
                                panelParent.findByType("checkbox")[1].hide();
                                panelParent.findByType("label")[0].hide();
                                panelParent.findByType("checkbox")[0].hide();
	                    	
	                    }
	                }
	            }
	        });
	        this.add(this.isMultipleAgenda);
        }
        
        this.add(new CQ.Ext.form.Label({text: "Name"}));
        this.nameField = new CQ.Ext.form.TextField({
        	width: 120,

            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.nameField);
        
        
       
        	
//        this.nameFieldDelete = new CQ.Ext.form.Hidden();
//        this.add(this.nameFieldDelete);
        
        this.add(new CQ.Ext.form.Label({text: "Duration", hidden:true}));
        this.durationField = new CQ.Ext.form.NumberField({
        	maxValue: 60,
        	width: 30,
            allowNegative: false,
        	allowDecimals:false,
        	min:5,
        	max:45,
            hidden:true,
        	maxText: 'Duration should not exceed 30 minutes',
            listeners: {
                change: {
                    scope:this,
                      fn: function(me, val) {
                          if( this.durationConst.indexOf(val) ==-1 ){ alert("Invalid duration: must be one of the following: "+ this.durationConst ); me.setValue(5);}

                      }
                }
            }
        });
    	this.add(this.durationField);
    	
//    	this.durationFieldDelete = new CQ.Ext.form.Hidden();
//        this.add(this.durationFieldDelete);
        
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
        
//        this.descriptionFieldDelete = new CQ.Ext.form.Hidden();
//        this.add(this.descriptionFieldDelete);
        
       
        this.materialFieldLabel = new CQ.Ext.form.Label({text: "Material List", disabled: ((this.isMainAgenda && !this.isNasaChecked) ? true : false)});

        this.add(this.materialFieldLabel);
    	this.materialField = new CQ.form.RichText({
        	rtePlugins: this.RTE_PLUGIN_CONF,
        	disabled: ((this.isMainAgenda && !this.isNasaChecked) ? true : false),
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
        this.add(this.materialField);
        
//        this.materialFieldDelete = new CQ.Ext.form.Hidden();
//        this.add(this.materialFieldDelete);

        this.add(new CQ.Ext.form.Label({text: "Subtitle"}));
        this.subtitleField = new CQ.Ext.form.TextField({
        	width: 120,
        	allowBlank: true,
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.subtitleField);
        
        
        
        //outdoor
        this.add(new CQ.Ext.form.Label({text: "Outdoor"}));
        this.outdoorField = new CQ.Ext.form.Checkbox({  
        	inputValue: true,
        	disabled: false,
        	listeners: {
        		check: {
                    scope: this,
                    	fn: function(me, val) {
                    		
                    	}
        		}
        	}
        });
        this.add(this.outdoorField);

        this.add(new CQ.Ext.form.Label({text: "  Global"}));
        this.globalField = new CQ.Ext.form.Checkbox({  
        	inputValue: true,
        	disabled: false,
        	listeners: {
        		check: {
                    scope: this,
                    	fn: function(me, val) {
                    		
                    	}
        		}
        	}
        });
        this.add(this.globalField);
    
        
        
        if (true){//this.isMainAgenda) {       
	        this.whateverButton =  new CQ.Ext.Button({
	        	text: "Add/Edit Additional Agenda(s)<br>Current Count: 0",
	        	height: 50,
	        	disabled: true,
	        	hidden: true,
	        	handler: function(me) {
	        		var modalWindow = new CQ.Ext.Window({
	        			boxMaxHeight: 500,
	        			width: 1150,
	    	        	modal: true,
	        			autoScroll: true,
	        			title: "Add/Edit Additional Agenda(s)",
	        			gsAdditionAbutton: me
	        		});
	        		var form = new CQ.Ext.form.FormPanel({
	        			height: 'auto'
	        		});
	        		
	        		var interMultiField = new CQ.form.MultiField({
	        			fieldLabel: "Additional Agenda(s)",  
	        			orderable: false,
	        			fieldDescription: "Click '+' to add additional agenda(s)",                 
	        			fieldConfig: {                     
	        				"xtype": "vtk-agenda", 
	        				isMainAgenda: false
	        			}
	        		});
	        		form.add(interMultiField);
	        		
//if there are additional agendas already added to this agenda before, it will load the modal window with all these agendas
        			var subAgendaItems = [];

	        		me.findParentByType("vtk-agenda").items.each(function(item) {
	    				if (item instanceof CQ.Ext.form.Hidden) {
	    					for (var ii = 0; ii < 99; ii ++) { //TODO: fix 99...
		    					if (item.gsField === 'addName'+ii) {
         
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].name = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'name':item.getValue()};
		    						}
		    					} else if (item.gsField === 'addSubtitle'+ii) {
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].subtitle = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'subtitle':item.getValue()};
		    						}
		    					} else if (item.gsField === 'addOutdoor'+ii) {
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].outdoor = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'outdoor':item.getValue()};
		    						}
		    					} else if (item.gsField === 'addGlobal'+ii) {
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].global = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'global':item.getValue()};
		    						}
		    					} else if (item.gsField === 'addDescript'+ii) {
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].description = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'description':item.getValue()};
		    						}
                                } else if (item.gsField === 'addDuration'+ii) {
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].duration = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'duration':item.getValue()};
		    						}
		    					} else if (item.gsField === 'addMaterial'+ii) {
		    						if (subAgendaItems[ii] != null) {
		    							subAgendaItems[ii].materials = item.getValue();
		    						} else {
		    							subAgendaItems[ii] = {'materials':item.getValue()};
		    						}
		    					}
	    					}
	    				}
	    			})
    				for (var i = 0; i < subAgendaItems.length; i++) {
    					interMultiField.addItem(subAgendaItems[i]);
    		        }
	        		//finish loading now. 
	        		
	        		
	        		form.add(new CQ.Ext.Button({
	        			text: "Save",
	        			cls: "float: right",
	        			handler: function (me) {
	        				var form = me.findParentByType("form"),
	        					additionalAgendaItemsList = form.findByType("vtk-agenda"), //array of vtkagenda in the modal window
	        					//-duration = form.findByType("numberfield"), //there is only one number fields for each agenda, so all the duration field will be listed in this array
	        					addAdditionalAgendaButton = me.findParentByType("window").gsAdditionAbutton,
	        					agendaCompositeField = addAdditionalAgendaButton.findParentByType("vtk-agenda"),
	        					agendaList = addAdditionalAgendaButton.findParentByType("vtk-agendalist");
	        				
	        				//First, remove all the existing additional field in the agendaCompositeField
	        				agendaCompositeField.removeAllHiddenField();
/*

							//the following are added for ocm mapping
	    		        	var hiddenDummyActivityDescriptionFieldT = new CQ.Ext.form.Hidden({
	        					'gsField': 'dummyDescription',
	        					name: './activities/' + agendaCompositeField.nodeName+ '/activityDescription@TypeHint',
	        					value: 'String'
	    					});
	    		        	agendaCompositeField.add(hiddenDummyActivityDescriptionFieldT);

	        				//the following are added for ocm mapping
	    		        	var hiddenDummyActivityDescriptionField = new CQ.Ext.form.Hidden({
	        					'gsField': 'dummyDescription',
	        					name: './activities/' + agendaCompositeField.nodeName+ '/activityDescription',
	        					value: ''
	    					});
	    		        	agendaCompositeField.add(hiddenDummyActivityDescriptionField);
	        				agendaCompositeField["hiddenDummyActivityDescriptionField"] = hiddenDummyActivityDescriptionField;
*/
	        				/*
	        				//the following are added for ocm mapping
	    		        	var hiddenDummyActivityDurationField = new CQ.Ext.form.Hidden({
	        					'gsField': 'dummyDuration',
	        					name: './activities/' + agendaCompositeField.nodeName+ '/duration',
	        					value: '1'
	    					});
	    		        	agendaCompositeField.add(hiddenDummyActivityDurationField);
	        				agendaCompositeField["hiddenDummyActivityDurationField"] = hiddenDummyActivityDurationField;
*/
	        				//Second, we add the new one
	        				for (var i =0;i < additionalAgendaItemsList.length; i++ ) {
		        				//agendaCompositeField.additionalAgendaField.setValue(name[i].getValue() + "&&&" + description[i].getValue());

	        					//set nodeName if it doesn't exist
	        					if (agendaCompositeField.nodeName == null) {
	        						agendaCompositeField.nodeName = 'A' + (Date.now() + Math.floor(Math.random()*9000) + 1000);
	        						agendaCompositeField.nodeName = agendaCompositeField.nodeName.replace(/[^a-zA-Z0-9]/g, '');
	        					}
	        					
	        					
	        					var hiddenNameFieldX = new CQ.Ext.form.Hidden({
	        						'gsField': 'addName'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/isSelected',
		        					value: i==0 ? "false" : "false"
	        					});
	        					
	        					var hiddenNameField = new CQ.Ext.form.Hidden({
	        						'gsField': 'addName'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/name',
		        					value: additionalAgendaItemsList[i].nameField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenNameField);
		        				agendaCompositeField["hiddenNameField" + i] = hiddenNameField; //give it a reference for the new field we just added.

		        				var hiddenSubtitleField = new CQ.Ext.form.Hidden({
	        						'gsField': 'addSubtitle'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/subtitle',
		        					value: additionalAgendaItemsList[i].subtitleField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenSubtitleField);
		        				agendaCompositeField["hiddenSubtitleField" + i] = hiddenSubtitleField; //give it a reference for the new field we just added.

		        				var hiddenOutdoorField = new CQ.Ext.form.Hidden({
	        						'gsField': 'addOutdoor'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/outdoor',
		        					value: additionalAgendaItemsList[i].outdoorField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenOutdoorField);
                                agendaCompositeField["hiddenOutdoorField" + i] = hiddenSubtitleField;
                                
                                var hiddenGlobalField = new CQ.Ext.form.Hidden({
	        						'gsField': 'addGlobal'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/global',
		        					value: additionalAgendaItemsList[i].globalField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenGlobalField);
		        				agendaCompositeField["hiddenGlobalField" + i] = hiddenSubtitleField;

		        				/*
		        				var hiddenDurationField = new CQ.Ext.form.Hidden({
		        					'gsField': 'addDuration'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/duration',
		        					value: additionalAgendaItemsList[i].durationField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenDurationField);
		        				agendaCompositeField["hiddenDurationField" + i] = hiddenDurationField;
*/


                                var hiddenDescriptionFieldT = new CQ.Ext.form.Hidden({
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/activityDescription@TypeHint',
		        					value: "String"
	        					});
		        				agendaCompositeField.add(hiddenDescriptionFieldT);

		        				var hiddenDescriptionField = new CQ.Ext.form.Hidden({
		        					'gsField': 'addDescript'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/activityDescription',
		        					value: additionalAgendaItemsList[i].descriptionField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenDescriptionField);
		        				agendaCompositeField["hiddenDescriptionField" + i] = hiddenDescriptionField;
		        				
		        				var hiddenMaterialField = new CQ.Ext.form.Hidden({
		        					'gsField': 'addMaterial'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/materials',
		        					value: additionalAgendaItemsList[i].materialField.getValue()
	        					});
		        				agendaCompositeField.add(hiddenMaterialField);
		        				agendaCompositeField["hiddenMaterialField" + i] = hiddenMaterialField;

		        				var hiddenOCMField = new CQ.Ext.form.Hidden({
		        					'gsField': 'addOCM'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/ocm_classname',
		        					value: "org.girlscouts.vtk.models.Activity"
	        					});
		        				agendaCompositeField.add(hiddenOCMField);
		        				agendaCompositeField["hiddenOCMField" + i] = hiddenOCMField;
		        				
		        				//hidden isSelect field, which is always set to true for the first one
		        				var hiddenIsSelected = new CQ.Ext.form.Hidden({
		        					'gsField': 'addIsSelected'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/isSelected',
		        					value: false
	        					});
		        				agendaCompositeField.add(hiddenIsSelected);
		        				agendaCompositeField["hiddenIsSelected" + i] = hiddenIsSelected;
		        				
		        				var activityNumberField = new CQ.Ext.form.Hidden({
		        					'gsField': 'addActivityNumber'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/activityNumber',
		        					value: i+1
	        					});
		        				agendaCompositeField.add(activityNumberField);
		        				agendaCompositeField["activityNumberField" + i] = activityNumberField;
		        				
		        				var hiddenIsSelectedTypeHint = new CQ.Ext.form.Hidden({
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '/isSelected@TypeHint',
		        					value: "Boolean"
	        					});
		        				agendaCompositeField.add(hiddenIsSelectedTypeHint);
		        				agendaCompositeField["hiddenIsSelectedTypeHint" + i] = hiddenIsSelectedTypeHint;
		        				
		        				var hiddenActivityDeleteField = new CQ.Ext.form.Hidden({
		        					'gsField': 'addslingPostDelete'+i,
		        					name: './activities/' + agendaCompositeField.nodeName+ '/multiactivities/agenda' + i + '@Delete'
	        					});
		        				agendaCompositeField.add(hiddenActivityDeleteField);
		        				agendaCompositeField["hiddenActivityDeleteField" + i] = hiddenActivityDeleteField;

	        				}
	        				
	        				//at last, we update the button text
	        				addAdditionalAgendaButton.setText("Add/Edit Additional Agenda(s)<br>Current Count: " + i);
	        				agendaCompositeField.doLayout();

	        				agendaCompositeField.updateHidden();
	        				me.findParentByType("window").close();
	        			}
	        		}));
//	        		form.add(new CQ.Ext.Button({
//	        			text: "Cancel",
//	        			handler: function (me) {
//	        				me.findParentByType("window").close();
//	        			}
//	        		}));
	        		
	        		modalWindow.add(form);
	        		modalWindow.show();
	        	}
	        })
	        this.add(this.whateverButton);
        }
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {

    	if (value != null) {
	    	this.nodeName = value.nodeName;
	    	this.nameField.setValue(value.name);
	    	this.subtitleField.setValue(value.subtitle);
            this.outdoorField.setValue(value.outdoor);
            this.globalField.setValue(value.global);
	    	this.durationField.setValue(value.duration);
	    	this.descriptionField.setValue(value.description);
	    	this.materialField.setValue(value.materials);
	    	var me = this;
	    	

	    	var totalNumberOfFields = me.items.items.length;
	    	//remove all hidden fields for additional agenda
	    	for (var i = totalNumberOfFields-1; i >= 0; i--) {
				if (me.items.items[i].name != null) {
					if ((me.items.items[i].name.indexOf('multiactivities') != -1) && (me.items.items[i].name.indexOf('Delete') === -1)) {
						var item = me.items.items[i];
						me.remove(item.id);
						item.destroy();
					}
				}
			}
	    	
	    	//check if there are any additional agenda
            if (Object.keys(value).length > 8) { //there are additional agenda/activities in the value. if not there will only be at most 6 fields
	    		me.isMultipleAgenda.setValue(true);
/*

console.log('checkkkkk   ./activities/' + value.nodeName );

var hiddenDummyActivityDescriptionFieldTT = new CQ.Ext.form.Hidden({
					'gsField': 'dummyDescription',
					name: './activities/' + value.nodeName+ '/activityDescription@TypeHint',
					value: "String"
				});
				me.add(hiddenDummyActivityDescriptionFieldTT);

				//the following are added for ocm mapping
	        	var hiddenDummyActivityDescriptionField = new CQ.Ext.form.Hidden({
					'gsField': 'dummyDescription',
					name: './activities/' + value.nodeName+ '/activityDescription',
					value: 'NA123'
				});
				me.add(hiddenDummyActivityDescriptionField);
                */
                
				/*
				//the following are added for ocm mapping
	        	var hiddenDummyActivityDurationField
                = new CQ.Ext.form.Hidden({
					'gsField': 'dummyDuration',
					name: './activities/' + value.nodeName+ '/duration',
					value: '1'
				});
				me.add(hiddenDummyActivityDurationField);
                */

			// Calculate multiple agenda number
			var agendaIds = {};
			var fieldNames = Object.keys(value);
			var multiAgendaRegex = /[^/]+\/multiactivities\/agenda([0-9]+).*/;
			for (var keyNum = 0; keyNum < fieldNames.length; keyNum++) {
				var key = fieldNames[keyNum];
				var match = multiAgendaRegex.exec(key);
				if (match !== null) {
					agendaIds[match[1]] = true;
				}
			}
			var agendaTotal = Object.keys(agendaIds).length;
			
    			//for (var j =0; j < (Object.keys(value).length-6)/4; j ++) { //we know if there is additional agenda in the value, so we need to add additional fields (each additional item has 4 fields)
			for (var j =0; j < agendaTotal; j ++) {
    				//console.info(value.nodeName + '/multiactivities/agenda' + j + '/name' + j);
    				var hiddenNameField = new CQ.Ext.form.Hidden({
						'gsField': 'addName' + j,
						name: './activities/' + value.nodeName + '/multiactivities/agenda' + j + '/name',
						value: value[value.nodeName + '/multiactivities/agenda' + j + '/name']
    				});
		        	me.add(hiddenNameField);
		        	
		        	var hiddenSubtitleField = new CQ.Ext.form.Hidden({
						'gsField': 'addSubtitle' + j,
						name: './activities/' + value.nodeName + '/multiactivities/agenda' + j + '/subtitle',
						value: value[value.nodeName + '/multiactivities/agenda' + j + '/subtitle']
    				});
		        	me.add(hiddenSubtitleField);
		        	
		        	var hiddenOutdoorField = new CQ.Ext.form.Hidden({
						'gsField': 'addOutdoor' + j,
						name: './activities/' + value.nodeName + '/multiactivities/agenda' + j + '/outdoor',
						value: value[value.nodeName + '/multiactivities/agenda' + j + '/outdoor']
    				});
                    me.add(hiddenOutdoorField);
                    
                    var hiddenGlobalField = new CQ.Ext.form.Hidden({
						'gsField': 'addGlobal' + j,
						name: './activities/' + value.nodeName + '/multiactivities/agenda' + j + '/global',
						value: value[value.nodeName + '/multiactivities/agenda' + j + '/global']
    				});
		        	me.add(hiddenGlobalField);
/*
		        	var hiddenDurationField = new CQ.Ext.form.Hidden({
						'gsField': 'addDuration' + j,
						name: './activities/' + value.nodeName + '/multiactivities/agenda' + j + '/duration',
						value: value[value.nodeName + '/multiactivities/agenda' + j + '/duration'],
                        type:boolean
    				});
		        	me.add(hiddenDurationField);
		        	*/

                     var hiddenDescriptionFieldT = new CQ.Ext.form.Hidden({
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/activityDescription@TypeHint',
    					value: "String"
					});
    				me.add(hiddenDescriptionFieldT);

		        	var hiddenDescriptionField = new CQ.Ext.form.Hidden({
    					'gsField': 'addDescript'+j,
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/activityDescription',
    					value: value[value.nodeName + '/multiactivities/agenda' + j + '/description']
		        	});
		        	me.add(hiddenDescriptionField);
		        	
		        	var hiddenMaterialField = new CQ.Ext.form.Hidden({
    					'gsField': 'addMaterial'+j,
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/materials',
    					value: value[value.nodeName + '/multiactivities/agenda' + j + '/materials']

					});
    				me.add(hiddenMaterialField);
		        	
		        	var hiddenOCMField = new CQ.Ext.form.Hidden({
    					'gsField': 'addOCM'+j,
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/ocm_classname',
    					value: 'org.girlscouts.vtk.models.Activity'
					});
		        	me.add(hiddenOCMField);
		        	
		        	//hidden isSelect field, which is always set to true for the first one
    				var hiddenIsSelected = new CQ.Ext.form.Hidden({
    					'gsField': 'addIsSelected'+j,
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/isSelected',
    					value: (j == 0 ) ? false : false
					});
    				me.add(hiddenIsSelected);
    				
    				var hiddenIsSelectedTypeHint = new CQ.Ext.form.Hidden({
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/isSelected@TypeHint',
    					value: "Boolean"
					});
    				me.add(hiddenIsSelectedTypeHint);
    				
    				var activityNumberField = new CQ.Ext.form.Hidden({
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '/activityNumber',
    					value: j+1,
					});
    				me.add(activityNumberField);
		        	
		        	var hiddenActivityDeleteField = new CQ.Ext.form.Hidden({
    					'gsField': 'addslingPostDelete'+j,
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '@Delete'
					});
    				me.add(hiddenActivityDeleteField);
    				
		        	var hiddenActivityDeleteField = new CQ.Ext.form.Hidden({
    					'gsField': 'addslingPostDelete'+j,
    					name: './activities/' + value.nodeName+ '/multiactivities/agenda' + j + '@Delete'
					});
    				me.add(hiddenActivityDeleteField);
    				
    				me.doLayout();
    			}
    			me.whateverButton.setText("Add/Edit Additional Agenda(s)<br>Current Count: " + j);
	    	}
    	}
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	var agenda;
 
    	agenda = {
    		"nodeName": this.nodeName,
    		"name": this.nameField.getValue(),
    		"subtitle": this.subtitleField.getValue(),
            "outdoor": this.outdoorField.getValue(),
            "global": this.globalField.getValue(),
    		"duration": this.durationField.getValue(),
    		"materials": this.materialField.getValue(),
    		"description": this.descriptionField.getValue()
    		//,
    		//"isOutdoorAvailable": this.outdoorCheckboxField.getValue(),
    		//"activityDescription_outdoor": this.outdoorDescriptionField.getValue()
    	};
    	var j = 0;
		for (var i =0; i < this.items.items.length; i ++) {
			if (this.items.items[i] instanceof CQ.Ext.form.Hidden) {
				if (this.items.items[i].gsField != null) {
					if (this.items.items[i].gsField.indexOf('addName') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j +'/name'] = this.items.items[i].getValue();
					}else if (this.items.items[i].gsField.indexOf('addSubtitle') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j +'/subtitle'] = this.items.items[i].getValue();
					}else if (this.items.items[i].gsField.indexOf('addOutdoor') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j +'/outdoor'] = this.items.items[i].getValue();
					}else if (this.items.items[i].gsField.indexOf('addGlobal') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j +'/global'] = this.items.items[i].getValue();
					} else if (this.items.items[i].gsField.indexOf('addDescript') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j + '/description'] = this.items.items[i].getValue();
					} else if (this.items.items[i].gsField.indexOf('addDuration') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j + '/duration'] = this.items.items[i].getValue();
					} else if (this.items.items[i].gsField.indexOf('addMaterial') != -1) {
						agenda[this.nodeName + '/multiactivities/agenda' + j + '/materials'] = this.items.items[i].getValue();
						j++; //TODO: fix this variable j properly
					}
				}
			}
		}
    	//console.info(agenda);
    	return agenda;
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    },

    removeAllHiddenField: function () {
    	var me = this;
    	var totalNumberOfFields = me.items.items.length;
		for (var i = totalNumberOfFields-1; i >= 0; i--) {
			if (me.items.items[i].name != null) {
				//if the hidden field contains "multiactivities", OR, if they are the dummy fields
				if ( 
						((me.items.items[i].name.indexOf('multiactivities') != -1) && (me.items.items[i].name.indexOf('Delete') === -1)) 
						|| ((me.items.items[i].gsField != null) && (me.items.items[i].gsField.indexOf('dummy')) != -1)
					) {
					var item = me.items.items[i];
					me.remove(item.id);
					item.destroy();
				}
			}
		}
		me.doLayout();
    }

});

// register xtype
CQ.Ext.reg('vtk-agenda', girlscouts.components.VTKAgenda);
