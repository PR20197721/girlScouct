/*
 * Copyright 1997-2009 Day Management AG
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
 * The <code>CQ.collab.cal.DayLens</code> renders the day view
 * of the calendar
 * @class CQ.collab.cal.DayLens
 * @extends CQ.collab.cal.CalendarLens
 */
CQ.collab.cal.DayLens = CQ.Ext.extend(CQ.collab.cal.CalendarLens, {

    /**
     * date of the day
     * @private
     */
    date: null,
	
    /**
     * internal day panel
     * @private
     */
    dayPanel: null,

    constructor: function(config) {
        this.store = new CQ.Ext.data.JsonStore({
			"fields": CQ.collab.cal.Calendar.getFields()
		});
		
        config = CQ.Util.applyDefaults(config, {
            "autoScroll": true,
            "renderTo": CQ.Util.ROOT_ID,
            "border": false
        });
        
        CQ.collab.cal.DayLens.superclass.constructor.call(this, config);
    },
	
	// public calendar lens api
	
	prev: function() {
        // Note: rendering is delayed up to renderDay(), called upon querybuilder response
        return this.date.add(Date.DAY, -1);
	},
	
	next: function() {
        // Note: rendering is delayed up to renderDay(), called upon querybuilder response
        return this.date.add(Date.DAY, 1);
	},
	
	setDate: function(date) {
	    // calculate day
	    var newDate = date.clearTime(true);
	    
	    var oldDate = this.date;
	    this.date = newDate;

	    return (!oldDate || (oldDate.getTime() != this.date.getTime()));
	},
	
    getDateDisplayText: function() {
        // eg. "December 2009"
        return this.date.format(CQ.collab.cal.Calendar.getDatePattern("longDay"));
    },
    
    getStartDate: function() {
        return this.date;
    },
    
    getEndDate: function() {
        return this.date.add(Date.DAY, 1);
    },
    
    // public lens api
    
    loadData: function(data) {
        this.store.loadData(data.hits);
        this.renderDay();
    },

    getSelection: function() {
        // no selection at the moment for calendar events
        return [];
    },
    
	// internal stuff
	
	renderDay: function() {
		if (this.dayPanel) {
			this.remove(this.dayPanel);
		}
		
        this.dayPanel = new CQ.collab.cal.WeekView({
            "startDate": this.date,
            "endDate": this.date.add(Date.DAY, 1),
            "store": this.store
        });
        this.add(this.dayPanel);
        
        // TODO: hack, need better way to get our container parent
        this.findParentByType("lensdeck").doLayout();
        //this.doLayout();
	}
});

CQ.Ext.reg("daylens", CQ.collab.cal.DayLens);
