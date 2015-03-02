Ejst = {};
Ejst.CustomWidget = CQ.Ext.extend(CQ.form.CompositeField, {
 
    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
    hiddenField: null,
 
   /**
    * @private
    * @type CQ.Ext.form.TextField
    */
    linkText: null,
 
    /**
    * @private
    * @type CQ.Ext.form.TextField
    */
    linkHref: null,
 
 
    /**
     * @private
     * @type CQ.Ext.form.ComboBox
     */
    linkType: null,
 
    /**
     * @private
     * @type CQ.Ext.form.TextField
     */
 
    formPanel: null,
     
    constructor: function(config) {
        config = config || { };
        var defaults = {
            "border": true,
            "layout": "table",
            "columns":4
        };
        config = CQ.Util.applyDefaults(config, defaults);
        Ejst.CustomWidget.superclass.constructor.call(this, config);
    },
 
    // overriding CQ.Ext.Component#initComponent
    initComponent: function() {
        Ejst.CustomWidget.superclass.initComponent.call(this);
        //Hidden Field
        this.hiddenField = new CQ.Ext.form.Hidden({
            name: this.name
        });
 
        this.add(this.hiddenField);
 
        //DROP DOWN
 
       this.linkType = new CQ.form.Selection({
            type:"select",
            cls:"customwidget-1",
            listeners: {
                selectionchanged: {
                    scope:this,
                    fn: this.updateHidden
                }
            },
            optionsProvider: this.optionsProvider
        });
        this.add(new CQ.Ext.form.Label({
            cls:"customwidget-label",
            text: "Type"}));
        this.add(this.linkType);
 
        //Link Text
 
        this.linkText = new CQ.Ext.form.TextField({
            cls:"customwidget-2",
            id:"valueTxt",
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(new CQ.Ext.form.Label({
            cls:"customwidget-label",
            text: "Text"}));
        this.add(this.linkText);
 
        //Link HREF Starts
 
        this.linkHref = new CQ.Ext.form.TextField({
            cls:"customwidget-3",
            listeners: {
                change: {
                    scope:this,
                    fn:this.updateHidden
                }
            }
        });
        this.add(new CQ.Ext.form.Label({
            cls:"customwidget-label",
            text: "URL"}));
        this.add(this.linkHref);
 
        //Link HREF ends
 
    },
 
    // overriding CQ.form.CompositeField#processPath
    processPath: function(path) {
        console.log("CustomWidget#processPath", path);
        this.linkType.processPath(path);
        this.linkType.processPath(path);
    },
 
    // overriding CQ.form.CompositeField#processRecord
    processRecord: function(record, path) {
        console.log("CustomWidget#processRecord", path, record);
        this.linkType.processRecord(record, path);
        this.linkType.processRecord(record, path);
    },
 
    // overriding CQ.form.CompositeField#setValue
    setValue: function(value) {
        var parts = value.split("\\");
        //this.linkType.setValue(parts[0]);
        this.linkType.setValue(parts[0]);
        this.linkText.setValue(parts[1]);
        this.linkHref.setValue(parts[2]);
        this.hiddenField.setValue(value);
 
    },
 
    // overriding CQ.form.CompositeField#getValue
    getValue: function() {
        this.getRawValue();
        return this.getRawValue();
    },
 
    // overriding CQ.form.CompositeField#getRawValue
    getRawValue: function() {
       return this.linkType.getValue() + "\\" +
               this.linkText.getValue() + "\\" +
               this.linkHref.getValue();
    },
 
    // private
    updateHidden: function() {
        //alert('customwidget updatehidden');
 
    var val1 = this.linkType.getValue();
        this.linkText.setValue(val1);
 
         
    }
 
 
 
});
 
// register xtype
CQ.Ext.reg("ejstdate", Ejst.CustomWidget);
 
//------------------------------------------------------------------------------
 
Ejst.x3 = {};
 
Ejst.x3.provideOptions = function(path, record) {
    // do something with the path or record
    return [{
        text:"Button22",
        value:"button"
    },{
        text:"Link",
        value:"link"
    }];
};