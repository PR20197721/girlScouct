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
 * The <code>CQ.collab.cal.WeekLens</code> renders the week view
 * of the calendar
 * NOT USED class CQ.collab.cal.WeekLens
 * @extends CQ.collab.cal.CalendarLens
 */
CQ.collab.cal.WeekLens = CQ.Ext.extend(CQ.collab.cal.CalendarLens, {

    /**
     * start date of the week
     * @private
     */
    startDate: null,
    
    /**
     * end date of the week
     * @private
     */
    endDate: null,
	
    /**
     * internal week panel
     * @private
     */
    weekPanel: null,

    constructor: function(config) {
        this.store = new CQ.Ext.data.JsonStore({
			"fields": CQ.collab.cal.Calendar.getFields()
		});
		
        config = CQ.Util.applyDefaults(config, {
            "autoScroll": true,
            "renderTo": CQ.Util.ROOT_ID,
            "border": false
        });
        
        CQ.collab.cal.WeekLens.superclass.constructor.call(this, config);
    },
	
	// public calendar lens api
	
	prev: function() {
        // Note: rendering is delayed up to renderWeek(), called upon querybuilder response
        return this.startDate.add(Date.DAY, -7);
	},
	
	next: function() {
        // Note: rendering is delayed up to renderWeek(), called upon querybuilder response
        return this.startDate.add(Date.DAY, 7);
	},
	
	setDate: function(date) {
        var oldStartDate = this.startDate;
        
	    // day: reset to configured week start
	    this.startDate = date.clearTime(true).add(Date.DAY, CQ.collab.cal.Calendar.getStartOfWeek() - date.getDay());
	    this.endDate = this.startDate.add(Date.DAY, 7);
	    
	    return (!oldStartDate || (oldStartDate.getTime() != this.startDate.getTime()));
	},
	
    getDateDisplayText: function() {
        var cal = CQ.collab.cal.Calendar;
        
        // week span displayed as start + end date, which can be in different months or even years
        if (this.startDate.getFullYear() != this.endDate.getFullYear()) {
            return CQ.collab.cal.multiDateFormat(
                cal.getDatePattern("twoDatesInDifferentYears"),
                [this.startDate, this.endDate]
            );
        } else if (this.startDate.getMonth() != this.endDate.getMonth()) {
            return CQ.collab.cal.multiDateFormat(
                cal.getDatePattern("twoDatesInDifferentMonths"),
                [this.startDate, this.endDate]
            );
        } else {
            return CQ.collab.cal.multiDateFormat(
                cal.getDatePattern("twoDatesInSameMonth"),
                [this.startDate, this.endDate]
            );
        }
    },
    
    getStartDate: function() {
        return this.startDate;
    },
    
    getEndDate: function() {
        return this.endDate;
    },
    
    // public lens api
    
    loadData: function(data) {
        this.store.loadData(data.hits);
        this.renderWeek();
    },

    getSelection: function() {
        // no selection at the moment for calendar events
        return [];
    },
    
	// internal stuff

	renderWeek: function() {
		if (this.weekPanel) {
			this.remove(this.weekPanel);
		}
		
        this.weekPanel = new CQ.collab.cal.WeekView({
            "startDate": this.startDate,
            "endDate": this.endDate,
            "store": this.store
        });
        this.add(this.weekPanel);
        
        // TODO: hack, need better way to get our container parent
        this.findParentByType("lensdeck").doLayout();
        //this.doLayout();
	}
});

CQ.Ext.reg("weeklens", CQ.collab.cal.WeekLens);
