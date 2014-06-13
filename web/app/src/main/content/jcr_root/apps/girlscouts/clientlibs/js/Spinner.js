/**
 * Copyright (c) 2008, Steven Chim
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * The names of its contributors may not be used to endorse or promote
 *       products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @class CQ.form.Spinner
 * @extends CQ.Ext.form.TriggerField
 * <p>The Spinner is a trigger field for numeric, date or time values. The value
 * can be increased and decreased by using the provided up and down triggers, the scroll
 * wheel or keys.</p>
 * <p>The behavior of the spinning is defined in a {@link CQ.form.Spinner.Strategy}.
 * Each Strategy allows two different speeds of spinning. The normal increment is
 * definied as {@link CQ.form.Spinner.Strategy#incrementValue incrementValue}
 * of the Strategy and the alternate increment as
 * {@link CQ.form.Spinner.Strategy#alternateIncrementValue alternateIncrementValue}.
 * </p>
 * <p>The Spinner supports multiple possibilities to spin:
 * <ul>
 * <li>Use the trigger buttons</li>
 * <li>Hover the field and use the scroll wheel</li>
 * <li>When using triggers or scroll wheel additionally hold the {@link #alternateKey alternate key}
 * for alternate spinning</li>
 * <li>Focus the field and use the keys "up" and "down"</li>
 * <li>Use the keys "page up" and "page down" for alternate spinning</li>
 * <li>Drag the splitter between the up and down trigger</li>
 * </ul>
 * </p>
 * @constructor
 * Creates a new Spinner.
 * @param {Object} config The config object
 * @author  Steven Chim
 * @version Spinner.js 2008-05-08 v0.3
 */
CQ.form.Spinner = function(config){
    CQ.form.Spinner.superclass.constructor.call(this, config);
    this.addEvents({
        'spinup': true,
        'spindown': true
    });
};

CQ.Ext.extend(CQ.form.Spinner, CQ.Ext.form.TriggerField, {

    triggerClass: 'x-form-spinner-trigger',

    /**
     * @cfg {String} splitterClass
     * The CSS class to style the splitter between the up and down trigger
     * (defaults to "x-form-spinner-splitter").
     */
    splitterClass: 'x-form-spinner-splitter',
    
    /**
     * @cfg {Object} alternateKey
     * The key to hold to spin with {@link CQ.form.Spinner.Strategy#alternateIncrementValue alternate increment}.
     * (defaults to {@link CQ.Ext.EventObject#shiftKey shift key}).
     */
    alternateKey: CQ.Ext.EventObject.shiftKey,

    /**
     * @cfg {CQ.form.Spinner.Strategy/Object} strategy
     * The strategy to use. This can be either an instance of an implementation
     * of a {@link CQ.form.Spinner.Strategy Strategy} or a config object for
     * a {@link CQ.form.Spinner.Strategy Strategy}. A config object requires
     * an according xtype:
     * <ul>
     * <li>"number" in order to create a {@link CQ.form.Spinner.NumberStrategy}</li>
     * <li>"date" in order to create a {@link CQ.form.Spinner.DateStrategy}</li>
     * <li>"time" in order to create a {@link CQ.form.Spinner.TimeStrategy}</li>
     * </ul>
     * <pre><code>
     *
var spinner = new CQ.form.Spinner({
    "strategy": {
        "xtype": "number",
        "incrementValue": 2,
        "maxValue": 20
    }
});
</code></pre>
     *
     */
    strategy: undefined,
    
    //private
    onRender: function(ct, position){
        CQ.form.Spinner.superclass.onRender.call(this, ct, position);
        
        this.splitter = this.wrap.createChild({
            tag: 'div',
            cls: this.splitterClass,
            style: 'width:13px; height:2px;'
        });
        this.splitter.show().setRight((CQ.Ext.isIE) ? 1 : 2);
        this.splitter.show().setTop(10);
        
        this.proxy = this.trigger.createProxy('', this.splitter, true);
        this.proxy.addClass("x-form-spinner-proxy");
        this.proxy.setStyle('left', '0px');
        this.proxy.setSize(14, 1);
        this.proxy.hide();
        this.dd = new CQ.Ext.dd.DDProxy(this.splitter.dom.id, "SpinnerDrag", {
            dragElId: this.proxy.id
        });
        
        this.initSpinner();
    },
    
    //private
    initSpinner: function(){
        this.keyNav = new CQ.Ext.KeyNav(this.el, {
            "up": function(e){
                e.preventDefault();
                this.onSpinUp();
            },
            
            "down": function(e){
                e.preventDefault();
                this.onSpinDown();
            },
            
            "pageUp": function(e){
                e.preventDefault();
                this.onSpinUpAlternate();
            },
            
            "pageDown": function(e){
                e.preventDefault();
                this.onSpinDownAlternate();
            },
            
            scope: this
        });
        
        this.trigger.un("click", this.onTriggerClick);
        this.repeater = new CQ.Ext.util.ClickRepeater(this.trigger);
        this.repeater.on("click", this.onTriggerClick, this, {
            preventDefault: true
        });
        this.trigger.on("mouseover", this.onMouseOver, this, {
            preventDefault: true
        });
        this.trigger.on("mouseout", this.onMouseOut, this, {
            preventDefault: true
        });
        this.trigger.on("mousemove", this.onMouseMove, this, {
            preventDefault: true
        });
        this.trigger.on("mousedown", this.onMouseDown, this, {
            preventDefault: true
        });
        this.trigger.on("mouseup", this.onMouseUp, this, {
            preventDefault: true
        });
        this.wrap.on("mousewheel", this.handleMouseWheel, this);
        
        this.dd.setXConstraint(0, 0, 10);
        this.dd.setYConstraint(1500, 1500, 10);
        this.dd.endDrag = this.endDrag.createDelegate(this);
        this.dd.startDrag = this.startDrag.createDelegate(this);
        this.dd.onDrag = this.onDrag.createDelegate(this);
        
        if ('object' == typeof this.strategy && this.strategy.xtype) {
            switch (this.strategy.xtype) {
                case 'number':
                    this.strategy = new CQ.form.Spinner.NumberStrategy(this.strategy);
                    break;
                    
                case 'date':
                    this.strategy = new CQ.form.Spinner.DateStrategy(this.strategy);
                    break;
                    
                case 'time':
                    this.strategy = new CQ.form.Spinner.TimeStrategy(this.strategy);
                    break;
                    
                default:
                    delete (this.strategy);
                    break;
            }
            delete (this.strategy.xtype);
        }
        
        if (this.strategy == undefined) {
            this.strategy = new CQ.form.Spinner.NumberStrategy();
        }
    },
    
    //private
    onMouseOver: function(){
        if (this.disabled) {
            return;
        }
        var middle = this.getMiddle();
        this.__tmphcls = (CQ.Ext.EventObject.getPageY() < middle) ? 'x-form-spinner-overup' : 'x-form-spinner-overdown';
        this.trigger.addClass(this.__tmphcls);
    },
    
    //private
    onMouseOut: function(){
        this.trigger.removeClass(this.__tmphcls);
    },
    
    //private
    onMouseMove: function(){
        if (this.disabled) {
            return;
        }
        var middle = this.getMiddle();
        if (((CQ.Ext.EventObject.getPageY() > middle) && this.__tmphcls == "x-form-spinner-overup") ||
        ((CQ.Ext.EventObject.getPageY() < middle) && this.__tmphcls == "x-form-spinner-overdown")) {
        }
    },
    
    //private
    onMouseDown: function(){
        if (this.disabled) {
            return;
        }
        var middle = this.getMiddle();
        this.__tmpccls = (CQ.Ext.EventObject.getPageY() < middle) ? 'x-form-spinner-clickup' : 'x-form-spinner-clickdown';
        this.trigger.addClass(this.__tmpccls);
    },
    
    //private
    onMouseUp: function(){
        this.trigger.removeClass(this.__tmpccls);
    },
    
    //private
    onTriggerClick: function(){
        if (this.disabled || this.getEl().dom.readOnly) {
            return;
        }
        var middle = this.getMiddle();
        var ud = (CQ.Ext.EventObject.getPageY() < middle) ? 'Up' : 'Down';
        this['onSpin' + ud]();
    },
    
    //private
    getMiddle: function(){
        var t = this.trigger.getTop();
        var h = this.trigger.getHeight();
        var middle = t + (h / 2);
        return middle;
    },
    
    //private
    handleMouseWheel: function(e){
        if (this.disabled || this.getEl().dom.readOnly) {
            CQ.Ext.EventObject.preventDefault(); //prevent scrolling when disabled/readonly
            return;
        }
        var delta = e.getWheelDelta();
        if (delta > 0) {
            this.onSpinUp();
            e.stopEvent();
        }
        else 
            if (delta < 0) {
                this.onSpinDown();
                e.stopEvent();
            }
    },
    
    //private
    startDrag: function(){
        this.proxy.show();
        this._previousY = CQ.Ext.fly(this.dd.getDragEl()).getTop();
    },
    
    //private
    endDrag: function(){
        this.proxy.hide();
    },
    
    //private
    onDrag: function(){
        if (this.disabled) {
            return;
        }
        var y = CQ.Ext.fly(this.dd.getDragEl()).getTop();
        var ud = '';
        
        if (this._previousY > y) {
            ud = 'Up';
        } //up
        if (this._previousY < y) {
            ud = 'Down';
        } //down
        if (ud != '') {
            this['onSpin' + ud]();
        }
        
        this._previousY = y;
    },
    
    //private
    onSpinUp: function(){
        if (CQ.Ext.EventObject.shiftKey == true) {
            this.onSpinUpAlternate();
            return;
        }
        else {
            this.strategy.onSpinUp(this);
        }
        this.fireEvent("spinup", this);
    },
    
    //private
    onSpinDown: function(){
        if (CQ.Ext.EventObject.shiftKey == true) {
            this.onSpinDownAlternate();
            return;
        }
        else {
            this.strategy.onSpinDown(this);
        }
        this.fireEvent("spindown", this);
    },
    
    //private
    onSpinUpAlternate: function(){
        this.strategy.onSpinUpAlternate(this);
        this.fireEvent("spinup", this);
    },
    
    //private
    onSpinDownAlternate: function(){
        this.strategy.onSpinDownAlternate(this);
        this.fireEvent("spindown", this);
    }
    
});

CQ.Ext.reg('spinner', CQ.form.Spinner);
