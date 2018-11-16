girlscouts.components.VTKResourcesUploadButton = CQ.Ext.extend(CQ.Ext.Button, {
    constructor: function(config) {
        var PARENT_LEVEL, regex;
        var currentPath = CQ.shared.HTTP.getPath();
        if (currentPath.indexOf("/content/vtkcontent") == 0) {
        		PARENT_LEVEL = 2;
        		regex = /^\/content\//;
        } else {
        		PARENT_LEVEL = 3;
        		regex = /^\/content\/vtk-resources2\//;
        }
        var slashIndex = 0;
        for (var i = 0; i < PARENT_LEVEL; i++) {
			slashIndex = currentPath.indexOf("/", slashIndex + 1);
        }
        var rootPath = slashIndex == -1 ? "/damadmin-resources2" : currentPath.substring(0, slashIndex).replace(regex, "/damadmin-resources2#/content/dam-resources2/girlscouts-");
        console.info('rootPath = ' + rootPath);
        if (typeof(config.relativePath) != 'undefined') {
			rootPath += config.relativePath;
        }

        config = config || { };
        var defaults = {
            "text": "Click here to upload resources",
            "handler": function(){window.open(rootPath);}
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.VTKResourcesUploadButton.superclass.constructor.call(this, config);
    }
});

// register xtype
CQ.Ext.reg('vtkresourcesuploadbutton', girlscouts.components.VTKResourcesUploadButton);
