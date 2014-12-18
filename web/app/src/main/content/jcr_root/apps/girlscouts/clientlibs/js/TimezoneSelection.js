girlscouts.components.TimezoneSelection = CQ.Ext.extend(CQ.form.Selection, {
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