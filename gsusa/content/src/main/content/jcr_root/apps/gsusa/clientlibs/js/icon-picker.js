gsusa.components.IconPicker= CQ.Ext.extend(CQ.form.CompositeField, {

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":2
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.IconPicker.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.IconPicker.superclass.initComponent.call(this);

        this.comboField = new CQ.Ext.form.ComboBox({
            mode: 'local',
            store: new CQ.Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'myId',  // numeric value is the key
                    'displayText'
                ],
                data: [[1, 'item1'], [2, 'item2']]  // data is local
            }),
            valueField: 'myId',
            displayField: 'displayText',
            triggerAction: 'all',
            listeners:{
                scope: this,
                'select': function() {
                	alert('hello' + this.comboField.getValue());
                }
            }
        });
        this.add(this.comboField);

        this.add(new CQ.Ext.form.Label({text: "ICON"}));
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        this.comboField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
    	return this.comboField.getValue();
    },
});

// register xtype
CQ.Ext.reg('iconpicker', gsusa.components.IconPicker);