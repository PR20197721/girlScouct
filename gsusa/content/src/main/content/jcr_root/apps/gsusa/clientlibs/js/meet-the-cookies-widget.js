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
        this.description = new CQ.Ext.form.TextField({
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
    } 

});

// register xtype
CQ.Ext.reg('meetthecookies', gsusa.components.MeetTheCookiesWidget);