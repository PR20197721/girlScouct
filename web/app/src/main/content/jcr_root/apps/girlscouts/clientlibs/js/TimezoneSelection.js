girlscouts.components.TimezoneSelection = CQ.Ext.extend(CQ.form.Selection, {
	timezoneDic: null,

    initComponent:function() {
        // call parent initComponent
        girlscouts.components.TimezoneSelection.superclass.initComponent.call(this);
        
    },
    
    optionsCallback: function() {
        var timezones = girlscouts.functions.getTimezones();
        // Hide this field if there is only one time zone.
        if (timezones.length <= 1) {
            this.hide();
            return;
        }

        var options = new Array();
        
        this.timezoneDic = {};
        for (var i = 0; i < timezones.length; i++) {
        	var timezone = timezones[i];
        	options.push({
        		value: timezone.label,
        		text: timezone.timezone,
        		qtip: timezone.label
        	});
        	this.timezoneDic[timezone.label] = timezone.timezone;
        }
        this.setOptions(options);
        this.setValue(options[0].value);
    },
    
	listeners: {
		selectionchanged: {
			scope: this,
			fn: function(that, value) {
				var datetimes = that.findParentByType('panel').findByType('timezonedatetime');
				for (var i = 0; i < datetimes.length; i++) {
					var datetime = datetimes[i];
					datetime.setTimezone(that.timezoneDic[value]);
				}
			}
		}
	}
});

CQ.Ext.reg("timezoneselect", girlscouts.components.TimezoneSelection);