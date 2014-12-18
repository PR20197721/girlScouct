girlscouts.components.TimezoneSelection = CQ.Ext.extend(CQ.form.Selection, {
	listeners: {
		selectionchanged: {
			scope: this,
			fn: function(widget, value) {
				console.info('###selectionchanged. newvalue=' + value);
			}
		}
	}
});

CQ.Ext.reg("timezoneselect", girlscouts.components.TimezoneSelection);