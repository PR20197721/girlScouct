girlscouts.components.TimezoneSelection = CQ.Ext.extend(CQ.form.Selection, {
	timezones: null,
	
    initComponent:function() {
    	var timezoneStr;
        // call parent initComponent
        girlscouts.components.TimezoneSelection.superclass.initComponent.call(this);
        
        // Setup timezone 
    	var url = window.location.pathname;
    	var path = CQ.shared.HTTP.getPath(url);
    	
    	// New event from scaffolding page
    	var regexScaffolding = /\/etc\/scaffolding\/[^/]+\/event/; //e.g. /etc/scaffolding/gsnetx/event
    	if (regexScaffolding.test(path)) {
    		var targetPathProperty = regexScaffolding.exec(path) + '/jcr:content/cq:targetPath';
    		var response = CQ.shared.HTTP.get(targetPathProperty);
    		if (response.status == 200) {
    			path = response.body;
    		}
    	}
    	
    	var regex = /^\/content\/[^/]+\/[^/]+/; // e.g. /content/girlscouts-prototype/en
    	if (regex.test(path)) {
    		var timezoneProperty = regex.exec(path) + '/jcr:content/timezone';
    		var response = CQ.shared.HTTP.get(timezoneProperty);
    		if (response.status == 200) {
    			timezoneStr = response.body;
    		} else {
    			timezoneStr = this.defaultTimezone;
    		}
    	} else {
    		timezoneStr = this.defaultTimezone;
    	}
    	
    	/*
    	var timezone = timezoneStr.split(',');
    	if (timezones.length <= 1) {
    		this.hide();
    	} else {
    		this.timezones = new Array();
    		for (var i = 0; i < timezones.length; i++) {
    			var timezoneSetting = timezones[i];
    			var result = timezoneSetting.split(':'); // US/Central:CST
    			this.timezone.push({
    				text: result[0],
    				value: result[1]
    			});
    		}
    	}
    	*/
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

CQ.Ext.reg("timezoneStrelect", girlscouts.components.TimezoneSelection);