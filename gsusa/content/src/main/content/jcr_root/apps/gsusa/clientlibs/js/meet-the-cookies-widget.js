gsusa.components.MeetTheCookiesWidget = CQ.Ext.extend(CQ.form.CompositeField, {
	
    hiddenField: null,
    pathField: null,
	linkField: null,
	newWindowField: null,

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

        this.add(new CQ.Ext.form.Label({text: "Path to image"}));
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
        
        this.add(new CQ.Ext.form.Label({text: "Link"}));
        this.linkField = new CQ.form.PathField({
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
        
        this.add(new CQ.Ext.form.Label({text: "NewWindow"}));
        this.newWindowField = new CQ.Ext.form.Checkbox({
        	listeners: {
        		change: {
        			scope:this,
        			fn:this.updateHidden
        		}
        	}
        });
        this.add(this.newWindowField);
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.pathField.setValue(parts[0]);
        this.linkField.setValue(parts[1]);
        this.newWindowField.setValue(parts[2]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.pathField.getValue() + "|||" 
        	+ this.linkField.getValue() + "|||"
        	+ this.newWindowField.getValue().toString();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    } 

});

// register xtype
CQ.Ext.reg('meetthecookies', gsusa.components.MeetTheCookiesWidget);