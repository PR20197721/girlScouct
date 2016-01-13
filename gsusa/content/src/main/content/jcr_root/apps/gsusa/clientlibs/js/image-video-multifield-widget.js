 /*
	EY - Kai Sum Li
	This multifield allows users to input images and video with hyperlinks
	It has path, alt and title.
	It has checkboxes for temporary hidden entities and open-in-new-window.
	
 */

gsusa.components.ImageVideoMultifieldWidget = CQ.Ext.extend(CQ.form.CompositeField, {

    hiddenField: null,
    titleField: null,
    pathField: null,
    altField: null,
    linkField: null,
    openInNewWindowField: null,
    hiddingContentField: null,
    
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.ImageVideoMultifieldWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.ImageVideoMultifieldWidget.superclass.initComponent.call(this);

        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({text: "Title"}));
        this.titleField = new CQ.Ext.form.TextField({
        	style:"margin-right:10px; margin-left:5px;",
        	listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.titleField);
        
        this.add(new CQ.Ext.form.Label({text: "Image Path"}));
        this.pathField =  new CQ.form.PathField({
            rootPath: "/content/dam/girlscouts-gsusa",
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
        
        this.add(new CQ.Ext.form.Label({text: "Link"}));
        this.linkField =  new CQ.form.PathField({
            rootPath: "/content/gsusa",
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
        this.add(this.linkField);
        
        this.add(new CQ.Ext.form.Label({text: "Alt"}));
        this.altField = new CQ.Ext.form.TextField({
        	style:"margin-right:0px; margin-left:5px;",
        	listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.altField);
        
        this.add(new CQ.Ext.form.Label({text: "New Window"}));
        this.openInNewWindowField = new CQ.Ext.form.Checkbox({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.openInNewWindowField);
        
        this.add(new CQ.Ext.form.Label({text: "Temporarily Hidden"}));
        this.hiddingContentField = new CQ.Ext.form.Checkbox({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(this.hiddingContentField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.titleField.setValue(parts[0]);
        this.altField.setValue(parts[1]);
        this.linkField.setValue(parts[2]);
        this.pathField.setValue(parts[3]);
        this.openInNewWindowField.setValue(parts[4]);
        this.hiddingContentField.setValue(parts[5]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.titleField.getValue() + "|||" 
        	+ this.altField.getValue() + "|||"
        	+ this.linkField.getValue() + "|||"
        	+ this.pathField.getValue() + "|||"
        	+ this.openInNewWindowField.getValue() + "|||"
        	+ this.hiddingContentField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('imagevideomultifieldwidget', gsusa.components.ImageVideoMultifieldWidget);
