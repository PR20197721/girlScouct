/*
 * Copyright 1997-2008 Day Management AG
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
 * @class CQ.wcm.AuditLogDialog
 * @extends CQ.Dialog
 * The AuditLogDialog is a dialog displaying the audit log of a page.
 * @constructor
 * Creates a new AuditLogDialog.
 * @param {Object} config The config object
 */
CQ.wcm.AuditLogDialog = CQ.Ext.extend(CQ.Dialog, {

   /**
    * Store of the grid
    * @private
    */
    store: null,

	constructor: function(config) {
    	this.debug = config.debug;

	    var cm = new CQ.Ext.grid.ColumnModel([
		    {
		        "header":CQ.I18n.getMessage("Modification"),
		        "dataIndex":"type"
		    },
		    {
		    	"header": CQ.I18n.getMessage("Date"),
     	        "dataIndex": "date"
		    },
		    {
		        "header":CQ.I18n.getMessage("User"),
		        "dataIndex":"user"
		    }
		]);
	                          		
  		var sm = new CQ.Ext.grid.RowSelectionModel({ 
    			"singleSelect":true
    		});
	                          		
		var storeConfig =  {
			"autoLoad":false,
            "proxy": new CQ.Ext.data.HttpProxy({ 
        	   "url":"/bin/audit/page.json",
        	   "method":"GET"
            }),
            "baseParams":{
			    "start":0,
			    "limit":25
		    },
            "reader": new CQ.Ext.data.JsonReader({
                "totalProperty": "results", "root": "entries",
                "fields": [ "type", "date", "user"]
            })
        };
	    this.store = new CQ.Ext.data.GroupingStore(storeConfig);

	    var grid =  {
            "xtype": "grid",
            "region":"center",
            "margins":"5 5 5 5",
            "loadMask":true,
            "stripeRows":true,
            "cm":cm,
            "sm":sm,
            "viewConfig": new CQ.Ext.grid.GroupingView({
                "forceFit":true
            }),
            "store":this.store
        };
        var panel = new CQ.Ext.Panel({
            layout:'border',
            width:500,
            minHeight:500,
            statefull:false,
            items:[grid]
        });

        dialog = this;
        
        config.items=[panel];
        config.buttons= [
          {
              "text": CQ.I18n.getMessage("OK"),
              "handler": function() { dialog.hide(); }
          }

        ];
		CQ.wcm.AuditLogDialog.superclass.constructor.call(this, config);
    },

    loadContent: function(path) {
    	this.setTitle(CQ.I18n.getMessage("AuditLog for") + " " + path)
        this.store.baseParams.path = path;
        this.store.reload();
    },

    initComponent : function() {
        CQ.wcm.AuditLogDialog.superclass.initComponent.call(this);
    }
});

CQ.Ext.reg('auditlogdialog', CQ.wcm.AuditLogDialog);