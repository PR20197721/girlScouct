girlscouts.components.TimezoneSelection = CQ.Ext.extend(CQ.form.Selection, {
	timezone: null,
	
    initComponent:function() {
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
    			this.timezone = response.body;
    		} else {
    			this.timezone = this.defaultTimezone;
    		}
    	} else {
    		this.timezone = this.defaultTimezone;
    	}
    	alert('this.timezone=' +this.timezone);
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