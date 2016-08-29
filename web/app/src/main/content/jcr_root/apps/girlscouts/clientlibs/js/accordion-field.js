/*
 * Copyright 1997-2010 Day Management AG
 * Barfuesserplatz 6, 4001 Basel, Switzerland
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Day Management AG, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Day.
 */
 
/**
 * @class Ejst.CustomWidget
 * @extends CQ.form.CompositeField
 * This is a custom widget based on {@link CQ.form.CompositeField}.
 * @constructor
 * Creates a new CustomWidget.
 * @param {Object} config The config object
 */

deleteAccordionFlag = false;

girlscouts.functions.deleteaccordion=function(field, event){
	if(deleteAccordionFlag == false){
		deleteAccordionFlag = true;
		CQ.Ext.Msg.show({
			title: "Warning", 
			msg: "If you delete this field, all content contained within will be lost as well. You can restore it at a later point ON THIS PAGE ONLY by using the same identifier. " +
			" If you do not want to delete this field, click 'Cancel' below to undo all changes",
			buttons: CQ.Ext.Msg.OK
			});
	}
};

//fix for loadcontent listener
girlscouts.components.AccordionPanel = CQ.Ext.extend(CQ.Ext.Panel,{
    panelValue: '',

    constructor: function(config){
        config = config || {};
        girlscouts.components.AccordionPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
    	girlscouts.components.AccordionPanel.superclass.initComponent.call(this);

        this.panelValue = new CQ.Ext.form.Hidden({
            name: this.name
        });

        this.add(this.panelValue);

        var multifield = this.findParentByType('multifield'),
            dialog = this.findParentByType('dialog');

        dialog.on('beforesubmit', function(){
            var value = this.getValue();

            if(value){
                this.panelValue.setValue(value);
            }
        },this);

        dialog.on('loadcontent', function(){
            if(_.isEmpty(multifield.dropTargets)){
                multifield.dropTargets = [];
            }

            multifield.dropTargets = multifield.dropTargets.concat(this.getDropTargets());

            _.each(multifield.dropTargets, function(target){
                if (!target.highlight) {
                    return;
                }

                var dialogZIndex = parseInt(dialog.el.getStyle("z-index"), 10);

                if (!isNaN(dialogZIndex)) {
                    target.highlight.zIndex = dialogZIndex + 1;
                }
            })
        }, this);

        if(dialog.gsInit){
            return;
        }

        var tabPanel = multifield.findParentByType("tabpanel");

        if(tabPanel){
            tabPanel.on("tabchange", function(panel){
                panel.doLayout();
            });
        }

        dialog.on('hide', function(){
            var editable = CQ.utils.WCM.getEditables()[this.path];

            //dialog caching is a real pain when there are multifield items; donot cache
            delete editable.dialogs[CQ.wcm.EditBase.EDIT];
            delete CQ.WCM.getDialogs()["editdialog-" + this.path];
        }, dialog);

        dialog.gsInit = true;
    },

    afterRender : function(){
        girlscouts.components.AccordionPanel.superclass.afterRender.call(this);

        this.items.each(function(){
            if(!this.contentBasedOptionsURL
                || this.contentBasedOptionsURL.indexOf(CQ.form.Selection.PATH_PLACEHOLDER) < 0){
                return;
            }

            this.processPath(this.findParentByType('dialog').path);
        })
    },

    getValue: function () {
        var pData = "";

        this.items.each(function(i){
            if(i.xtype == "label" || i.xtype == "hidden" || !i.hasOwnProperty("dName")){
                return;
            }

            if(pData == ""){
            	pData = i.getValue();
            }else{
            	pData = pData + "," + i.getValue();
            }
        });

        return pData;
    },

    setValue: function (value) {
        this.panelValue.setValue(value);

        var pData = value.split(',');

        var index = 0;
        this.items.each(function(i){
            if(i.xtype == "label" || i.xtype == "hidden" || !i.hasOwnProperty("dName")){
                return;
            }

            i.setValue(pData[index]);
            index++;
            
            i.fireEvent('loadcontent', this);
        });
    },

    getDropTargets : function() {
        var targets = [], t;

        this.items.each(function(){
            if(!this.getDropTargets){
                return;
            }

            t = this.getDropTargets();

            if(_.isEmpty(t)){
                return;
            }

            targets = targets.concat(t);
        });

        return targets;
    },

    validate: function(){
        var valid = true;

        this.items.each(function(i){
            if(!i.hasOwnProperty("dName")){
                return;
            }

            if(!i.isVisible()){
                i.allowBlank = true;
                return;
            }

            if(!i.validate()){
                valid = false;
            }
        });

        return valid;
    },

    getName: function(){
        return this.name;
    }
});

CQ.Ext.reg("accordionpanel", girlscouts.components.AccordionPanel);

girlscouts.components.AccordionWidget = CQ.Ext.extend(CQ.form.CompositeField, {
	
    hiddenField: null,
    nameField: null,
	anchorField: null,
	idField: null,

    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": false,
            "layout": "table",
            "columns":3
        };
        config = CQ.Util.applyDefaults(config, defaults);
        girlscouts.components.AccordionWidget.superclass.constructor.call(this, config);
    },

    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        girlscouts.components.AccordionWidget.superclass.initComponent.call(this);
        
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
        this.add(this.hiddenField);

        this.add(new CQ.Ext.form.Label({text: "Section Name"}));
        this.nameField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.nameField);
        
        this.add(new CQ.Ext.form.Label({text: "Anchor"}));
        this.anchorField = new CQ.Ext.form.TextField({
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });
        this.add(this.anchorField);
        
        this.add(new CQ.Ext.form.Label({text: "Identifier"}));
        
        this.idField = new CQ.Ext.form.TextField({
        	allowBlank: "{Boolean}false",
            listeners: {
            	loadcontent: {
            		scope:this,
            		fn:this.createId
            	},
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            } 
        });

        this.add(this.idField);
        
    },

    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("|||");
        this.nameField.setValue(parts[0]);
        this.anchorField.setValue(parts[1]);
        this.idField.setValue(parts[2]);
        this.hiddenField.setValue(value);
    },

    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        return this.getRawValue();
    },

    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
        return this.nameField.getValue() + "|||" 
        	+ this.anchorField.getValue() + "|||"
        	+ this.idField.getValue();
    },

    // private
    updateHidden: function() {
        this.hiddenField.setValue(this.getValue());
    },
    
    //private
    createID: function() {
    	    if(this.idField.value == null){
    	    	this.idField.setValue(this.idField.id);
    	    }
    }

});

// register xtype
CQ.Ext.reg('accordionfield', girlscouts.components.AccordionWidget);

girlscouts.createID = function() {
    if(this.idField.value == null){
    	this.idField.setValue(this.idField.id);
    }
}