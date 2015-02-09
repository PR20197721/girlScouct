girlscouts.components.VTKAgenda= CQ.Ext.extend(CQ.Ext.form.Field, {

    /**
     * @cfg {String/Object} defaultAutoCreate DomHelper element spec
     * Let superclass to create hidden field instead of textbox.
     * Hidden will be submitted to server
     */
    defaultAutoCreate:{tag:'input', type:'hidden'},

    /**
     * @cfg {Boolean} hideTime True to hide the time field
     */
    hideTime:false,

    /**
     * @cfg {Number} timeWidth Width of time field in pixels (defaults to 100)
     */
    numberWidth:100,

    /**
     * @cfg {Number} textWidth Width of date field in pixels (defaults to 200)
     */
    textWidth:200,

    /**
     * @cfg {String} dtSeparator Date - Time separator. Used to split date and time (defaults to ' ' (space))
     */
    dtSeparator:' ',

    /**
     * @cfg {String} defaultValue Init time or date field if not explicitly filled in (defaults to "").
  	 * Set to "now" to specify current date as default.
     */
    defaultValue: "",

    /**
     * @cfg {Boolean} disableTypeHint True to disable the field @TypeHint
     */
    disableTypeHint:false,

    /**
     * @cfg {String} typeHint The type hint for the server. (defaults to 'Date')
     */
    typeHint: null,

    /**
     * Taken from CQ.Ext.form.TriggerField. Needed for readOnly case, but before
     * the date or time widgets were created, so calling getTriggerWidth() is not
     * possible. However, the default will mostly be used anyway.
     * @private
     */
    defaultTriggerWidth: 17,

    /**
     * @private
     * creates DateField and TimeField and installs the necessary event handlers
     */
    initComponent:function() {
        // call parent initComponent
        girlscouts.components.VTKAgenda.superclass.initComponent.call(this);
        
        // create TextField for agenda content
        var textConfig = CQ.Ext.apply({}, {
             id:this.id + '-date',
            width: this.textWidth,
            allowBlank: false,
            listeners:{
                 blur:{scope:this, fn:this.onBlur},
                 focus:{scope:this, fn:this.onFocus}
            }
        }, this.textConfig);
        this.textField = new CQ.Ext.form.TextField(textConfig);

        // create NumberField for agenda duration
        var numberConfig = CQ.Ext.apply({}, {
            width: this.numberWidth,
            allowBlank: false,
            listeners:{
                 blur:{scope:this, fn:this.onBlur},
                 focus:{scope:this, fn:this.onFocus}
            }
        }, this.numberConfig);
        this.numberField = new CQ.Ext.form.NumberField(numberConfig);
        
        //TODO
        // relay events
        //this.relayEvents(this.textField, ['focus', 'specialkey', 'invalid', 'valid']);
        //this.relayEvents(this.numberField, ['focus', 'specialkey', 'invalid', 'valid']);
    },

    /**
     * @private
     * Renders underlying DateField and TimeField and provides a workaround for side error icon bug
     */
    onRender:function(ct, position) {
        // don't run more than once
        if(this.isRendered) {
            return;
        }

        // TODO: what is this?
        // render underlying hidden field
        CQ.form.DateTime.superclass.onRender.call(this, ct, position);

        // render DateField and TimeField
        // create bounding table
        var t;
        t = CQ.Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',children:[
            {tag:'tr',children:[
                {tag:'td', style:'padding-right:' + '0px', cls:'ux-vtk-agenda-text'},
                {tag:'td', cls:"ux-vtk-agenda-number"}
            ]}
        ]}, true);

        this.tableEl = t;

        this.wrap = t.wrap();
        this.wrap.on("mousedown", this.onMouseDown, this, {delay:10});

        // render DateField & TimeField
        this.textField.render(t.child('td.ux-vtk-agenda-text'));
        this.numberField.render(t.child('td.ux-vtk-agenda-number'));

        // workaround for IE trigger misalignment bug
        if(CQ.Ext.isIE && CQ.Ext.isStrict) {
            t.select('input').applyStyles({top:0});
        }

        // TODO: what is this?
        //this.on('specialkey', this.onSpecialKey, this);
        //this.textField.el.swallowEvent(['keydown', 'keypress']);
        //this.numberField.el.swallowEvent(['keydown', 'keypress']);

        // create icon for side invalid errorIcon
        if('side' === this.msgTarget) {
            var elp = this.el.findParent('.x-form-element', 10, true);
            this.errorIcon = elp.createChild({cls:'x-form-invalid-icon'});

            this.textField.errorIcon = this.errorIcon;
            this.numberField.errorIcon = this.errorIcon;
        }

        // setup name for submit
        this.el.dom.name = this.hiddenName || this.name || this.id;

        // prevent helper fields from being submitted
        this.textField.el.dom.removeAttribute("name");
        this.numberField.el.dom.removeAttribute("name");

        // TODO: we don't need this, right?
        // add hidden type hint
//        if (!this.disableTypeHint) {
//            var th = new CQ.Ext.form.Hidden({
//                name: this.name + "@TypeHint",
//                value: this.typeHint ? this.typeHint : "Date",
//                ignoreDate: true,
//                renderTo: ct
//            });
//        }

        // we're rendered flag
        this.isRendered = true;

        // update hidden field
        this.updateHidden();
    },

    /**
     * @private
     */
    adjustSize: CQ.Ext.BoxComponent.prototype.adjustSize,

    /**
     * @private
     */
    alignErrorIcon:function() {
        this.errorIcon.alignTo(this.tableEl, 'tl-tr', [2, 0]);
    },

    /**
     * Calls clearInvalid on the DateField and TimeField
     */
    clearInvalid:function(){
        this.textField.clearInvalid();
        this.numberField.clearInvalid();
    },

    /**
     * Disable this component.
     * @return {Ext.Component} this
     */
    disable:function() {
        if(this.isRendered) {
            this.onDisable();
            this.textField.disabled = this.disabled;
            this.textField.onDisable();
            this.numberField.onDisable();
        }
        this.disabled = true;
        this.textField.disabled = true;
        this.numberField.disabled = true;
        this.fireEvent("disable", this);
        return this;
    },
    /**
     * Enable this component.
     * @return {Ext.Component} this
     */
    enable:function() {
        if(this.rendered){
            this.onEnable();
            this.textField.onEnable();
            this.numberField.onEnable();
        }
        this.disabled = false;
        this.textField.disabled = false;
        this.numberField.disabled = false;
        this.fireEvent("enable", this);
        return this;
    },
    /**
     * @private Focus date filed
     */
    focus:function() {
        this.textField.focus();
    },
    /**
     * @private
     */
    getPositionEl:function() {
        return this.wrap;
    },
    /**
     * @private
     */
    getResizeEl:function() {
        return this.wrap;
    },
    /**
     * Returns value of this field, depending on the config 'valueAsString'.
     * @return {Date/String} Returns value of this field
     */
    getValue:function() {
        // create new instance of date
        if (this.valueAsString) {
            return (this.dateValue && (typeof this.dateValue.format === "function")) ? this.dateValue.format(this.hiddenFormat) : '';
        } else {
            return this.dateValue ? this.dateValue.clone() : '';
        }
    },

    /**
     * @return {Boolean} True = valid, false = invalid, else false
     * @private Calls isValid methods of underlying DateField and TimeField and returns the result
     */
    isValid:function() {
        return this.textField.isValid() && this.numberField.isValid();
    },
    /**
     * Returns true if this component is visible
     * @return {Boolean}
     */
    isVisible : function(){
        return this.textField.rendered && this.textField.getActionEl().isVisible();
    },
    /**
     * @private Handles blur event
     */
    onBlur:function(f) {
        // called by both DateField and TimeField blur events

        // revert focus to previous field if clicked in between
        if(this.wrapClick) {
            f.focus();
            this.wrapClick = false;
        }

        // update underlying value
        if(f === this.textField) {
            this.updateDate();
        } else {
            this.updateTime();
        }
        this.updateHidden();

        // fire events later
        (function() {
            if(!this.textField.hasFocus && !this.numberField.hasFocus) {
                this.checkIfChanged(true);
                this.hasFocus = false;
                this.fireEvent('blur', this);
            }
        }).defer(100, this);

    },

    /**
     * Checks and returns if value has changed and fires
     * "change" event in the case of a modification. If
     * 'skipUpdateValue' is true, it will only check the
     * internal aggregated date+time value against
     * the old value, but not re-get the values from the
     * underlying date and time fields.
     */
    checkIfChanged: function(skipUpdateValue) {
        if (!skipUpdateValue) {
            this.updateValue();
        }
        var v = this.getValue();
        // startValue is undefined if field wasn't focused yet
        if(this.startValue !== undefined && String(v) !== String(this.startValue)) {
            this.fireEvent("change", this, v, this.startValue);
            this.startValue = v;
            return true;
        }
        return false;
    },

    /**
     * @private Handles focus event
     */
    onFocus:function() {
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            this.fireEvent("focus", this);
        }
    },
    /**
     * @private Just to prevent blur event when clicked in the middle of fields
     */
    onMouseDown:function(e) {
        if(!this.disabled) {
            this.wrapClick = 'td' === e.target.nodeName.toLowerCase();
        }
    },
    /**
     * @private
     * Handles Tab and Shift-Tab events
     */
    onSpecialKey:function(t, e) {
        var key = e.getKey();
        if(key === e.TAB) {
            if(t === this.textField && !e.shiftKey) {
                e.stopEvent();
                this.numberField.focus();
            }
            if(t === this.numberField && e.shiftKey) {
                e.stopEvent();
                this.textField.focus();
            }
        }
        // otherwise it misbehaves in editor grid
        if(key === e.ENTER) {
            this.updateValue();
        }

    },
    /**
     * @private
     * Sets correct sizes of underlying DateField and TimeField
     * With workarounds for IE bugs
     */
    setSize:function(w, h) {
        if(!w) {
            return;
        }
        if (typeof w == "object") {
            h = w.height;
            w = w.width;
        }
        this.textField.setSize(w - this.timeWidth - this.labelWidth, h);
        this.numberField.setSize(this.timeWidth, h);

        if(CQ.Ext.isIE) {
            this.textField.el.up('td').setWidth(w - this.timeWidth - this.labelWidth);
            this.numberField.el.up('td').setWidth(this.timeWidth);
        }
        this.fireEvent('resize', this, w, h, w, h);
    },
    /**
     * @param {Mixed} val Value to set
     * Sets the value of this field
     */
    setValue:function(val) {
    	// format #^agenda^duration
    	// e.g. 1^As Girls Arrive^10
    	var match = val.trim().split('^');
    	// Skip # field
    	for (var i = 1; i < match.length; i++) {
    		match[i] = match[i].trim();
    	}
    	var agenda = match[1];
    	var duration = match[2];
    	this.textField.setValue(agenda);
    	this.numberField.setValue(duration);
        //this.updateValue();
    },
    /**
     * Hide or show this component by boolean
     * @return {Ext.Component} this
     */
    setVisible: function(visible){
        if(visible) {
            this.textField.show();
            this.numberField.show();
        } else{
            this.textField.hide();
            this.numberField.hide();
        }
        return this;
    },
    show:function() {
        return this.setVisible(true);
    },
    hide:function() {
        return this.setVisible(false);
    },
    updateHidden:function() {
        if(this.isRendered) {
            //this.el.dom.value = (this.dateValue && (typeof this.dateValue.format === "function"))
            //        ? this.dateValue.format(this.hiddenFormat)
            //        : '';

        	//////// Customized code
        	// Use Sencha to format the date and then use moment.js to format to the current timezone.
        	// Sencha "submit" action will pick up this value later.
        	this.el.dom.value = this.dateValue ?
        			moment.tz(this.dateValue.format('Y-m-d H:i'), this.timezone).format("YYYY-MM-DDTHH:mm:00.000Z") 
        			: '';
        }
    },
    /**
     * @return {Boolean} True = valid, false = invalid, else false
     * calls validate methods of DateField and TimeField
     */
    validate:function() {
        var valid = this.textField.validate() && this.numberField.validate();

        if(this.validator){
            var result = this.validator.call(this,this.getValue(),this);
            if(result!==true){
                this.textField.markInvalid(result);
                this.numberField.markInvalid(result);
            }
            valid &= result===true;
        }
        
        return valid;
    },
    /**
     * Returns renderer suitable to render this field
     * @param {Object} Column model config
     */
    renderer: function(field) {
        var format = field.editor.dateFormat || CQ.Ext.form.DateField.prototype.format;
        format += ' ' + (field.editor.timeFormat || CQ.Ext.form.TimeField.prototype.format);
        return function(val) {
            return CQ.Ext.util.Format.date(val, format);
        };
    }
});

// register xtype
CQ.Ext.reg("vtk-agenda", girlscouts.components.VTKAgenda);
