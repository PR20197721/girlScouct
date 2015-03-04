/*
 * Overriding TagInputField.
 * Girl Scouts
 */
/*
 * Copyright 1997-2010 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 *
 * All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */


girlscouts.components.TagInputField = CQ.Ext.extend(CQ.tagging.TagInputField, {
    
    // private
    loadTagNamespaces: function() {
        this.tagNamespaces = {};
        /*
        var tagJson = this.loadJson(this.tagsBasePath + CQ.tagging.TAG_LIST_JSON_SUFFIX + "?count=false");
        if (tagJson && tagJson.tags) {
            CQ.Ext.each(tagJson.tags, function(t) {
                this.tagNamespaces[t.name] = t;
            }, this);
        }
        */
        var tagJson = this.loadJson(this.tagsBasePath + '.1.json?count=false');
        if (tagJson) {
        	for (var tagKey in tagJson) {
        		if (tagJson[tagKey]['jcr:primaryType'] === "cq:Tag") {
        			var tagDef = this.loadJson(this.tagsBasePath + "/" + tagKey + CQ.tagging.TAG_JSON_SUFFIX);
        			this.tagNamespaces[tagKey] = tagDef;
        		}
        	}
        }
        
        this.setupPopupMenu();
        
        this.tagNamespacesLoaded = true;
    },

	setValue: function(rawValue, partialTags) {
		if (!rawValue) {
			return;
		}

		var valueArray = rawValue.split(';');
		for (var i = 0; i < valueArray.length; i++) {
			var value = valueArray[i];
			value = 'girlscouts-vtk:tag/' + value;
			valueArray[i] = value;
		}
		girlscouts.components.TagInputField.superclass.setValue.call(this, valueArray, partialTags);
	},
	
	getValue: function() {
		var values = girlscouts.components.TagInputField.superclass.getValue.call(this);
		return values.join(';');
	}
});

// register xtype
CQ.Ext.reg("vtk-tags", girlscouts.components.TagInputField);