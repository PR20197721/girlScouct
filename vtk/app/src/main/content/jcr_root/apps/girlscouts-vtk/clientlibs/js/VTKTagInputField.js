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

    updateHiddenFields: function() {
        for (var i=0; i < this.hiddenFields.length; i++) {
            this.remove(this.hiddenFields[i]);
        }
        
        this.hiddenFields = [];
        
		// Girl Scouts: value is now always a single string.
		// Do not need these special logic any more.

//        // ensure multivalue property (when only one value is set)
//        var typeHintHiddenField = new CQ.Ext.form.Hidden({
//            name: this.getName() + CQ.Sling.TYPEHINT_SUFFIX,
//            value: "String"
//        });
//        this.add(typeHintHiddenField);
//        this.hiddenFields.push(typeHintHiddenField);
//        
//        // run patch operation on multi value property
//        var patchHiddenField = new CQ.Ext.form.Hidden({
//            name: this.getName() + "@Patch",
//            value: "true"
//        });
//        this.add(patchHiddenField);
//        this.hiddenFields.push(patchHiddenField);
//        
//        var op;
//        function addHiddenField(tagObj) {
//            if (tagObj.type == "denied" || tagObj.type == "partial") {
//                return;
//            }
//            var tagID = tagObj.tag.tagID || tagObj.tag;
//            var hiddenField = new CQ.Ext.form.Hidden({
//                name: this.getName(), // all hidden fields have the name of this field
//                value: op + tagID
//            });
//            this.add(hiddenField);
//            this.hiddenFields.push(hiddenField);
//        }
//        
//        // use @Patch operations
//        op = "+";
//        CQ.Ext.each(this.addedTags, addHiddenField, this);
//        op = "-";
//        CQ.Ext.each(this.removedTags, addHiddenField, this);

        var values = this.getValue();
		for (var i = 0; i < values.length; i++) {
			// girlscouts-vtk:tag/wog-badges => wog-badges
			values[i] = values[i].substring(values[i].lastIndexOf('/') + 1);
		}
		var finalValue = values.join(';');

        var hiddenField = new CQ.Ext.form.Hidden({
        	name: this.getName(),
        	value: finalValue
        });
        this.hiddenFields.push(hiddenField);
        this.add(hiddenField);
        
        this.doLayout();
    },

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
	}
});

// register xtype
CQ.Ext.reg("vtk-tags", girlscouts.components.TagInputField);