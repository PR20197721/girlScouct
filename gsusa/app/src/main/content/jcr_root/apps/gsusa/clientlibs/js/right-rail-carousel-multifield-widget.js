gsusa.components.RightRailCarouselMultifield = CQ.Ext.extend(CQ.form.MultiField, {
    constructor: function(config) {
        config = config || {};
        defaults = {};
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.RightRailCarouselMultifield.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        gsusa.components.RightRailCarouselMultifield.superclass.initComponent.call(this);

        var dialog = this.findParentByType("dialog");
        dialog.on('beforesubmit', function(){
            this.items.each(function(item/*,index, length*/) {
                if (item instanceof CQ.form.MultiField.Item) {
                	var field = item.field;
			    	
                    var hiddenValue = field.labelField.getValue() + "|||" +
			                	field.pathField.getValue() + "|||"  +
			                	field.checkBoxField.getValue() + "|||" +
			                	field.imagePathField.getValue();
                    field.hiddenField.setValue(hiddenValue);
                }
            }, this);
        }, this);
    }
});
CQ.Ext.reg('rightrailcarouselmultifield', gsusa.components.RightRailCarouselMultifield);