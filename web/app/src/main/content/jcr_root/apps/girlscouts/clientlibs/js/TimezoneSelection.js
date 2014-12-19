girlscouts.components.TimezoneSelection = CQ.Ext.extend(CQ.form.Selection, {
	timezones: null,
	
    initComponent:function() {
        // call parent initComponent
        girlscouts.components.TimezoneSelection.superclass.initComponent.call(this);
        
    },
    
    optionsCallback: function() {
        var timezones = girlscouts.functions.getTimezones();
        var options = new Array();
        
        for (var i = 0; i < timezones.length; i++) {
        	var timezone = timezones[i];
        	options.push({
        		value: timezone.label,
        		text: timezone.timezone,
        		qtip: timezone.label
        	});
        }
        this.setOptions(options);
    },
    
	listeners: {
		selectionchanged: {
			scope: this,
			fn: function(that, value) {
				var datetimes = that.findParentByType('panel').findByType('timezonedatetime');
				for (var i = 0; i < datetimes.length; i++) {
					var datetime = datetimes[i];
					datetime.setTimezone(value);
				}
			}
		}
	}
});

CQ.Ext.reg("timezoneselect", girlscouts.components.TimezoneSelection);