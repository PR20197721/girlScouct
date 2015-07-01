gsusa.components.DynamicPathField = CQ.Ext.extend(CQ.form.PathField, {
    constructor: function(config) {
        var PARENT_LEVEL = 3;	
        var currentPath = CQ.shared.HTTP.getPath();
        var slashIndex = 0;
        for (var i = 0; i < PARENT_LEVEL; i++) {
			slashIndex = currentPath.indexOf("/", slashIndex + 1);
        }
        var rootPath = slashIndex == -1 ? currentPath : currentPath.substring(0, slashIndex);
        if (typeof(config.relativePath) != 'undefined') {
			rootPath += config.relativePath;
        }

        config = config || { };
        var defaults = {
            "rootPath": rootPath,
            "rootTitle": "Website"
        };
        config = CQ.Util.applyDefaults(config, defaults);
        gsusa.components.DynamicPathField.superclass.constructor.call(this, config);
    }
});

// register xtype
CQ.Ext.reg('dynamicPathField', gsusa.components.DynamicPathField);
