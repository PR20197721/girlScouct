gsusa.components.MeetTheCookiesMultifield = CQ.Ext.extend(CQ.form.MultiField, {
    constructor: function(config) {
        config = config || {};
        defaults = {};
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.MeetTheCookiesMultifield.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.MeetTheCookiesMultifield.superclass.initComponent.call(this);

        var dialog = this.findParentByType("dialog");
        dialog.on('beforesubmit', function(){
            this.items.each(function(item/*,index, length*/) {
                if (item instanceof CQ.form.MultiField.Item) {
                	var field = item.field;
			    	
                    var hiddenValue = field.title.getValue() + "|||" +
                                field.image.getValue() + "|||" +
                                field.description.getValue();
                    field.hiddenField.setValue(hiddenValue);
                }
            }, this);
        }, this);
    }
});
CQ.Ext.reg('meetthecookiesmultifield', gsusa.components.MeetTheCookiesMultifield);