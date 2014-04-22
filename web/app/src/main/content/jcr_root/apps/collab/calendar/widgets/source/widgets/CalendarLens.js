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
 * The <code>CQ.collab.cal.CalendarLens</code> introduces the methods
 * required by a lens used in the calendar environment, in addition
 * to the standard {@link CQ.search.Lens} methods.
 * @class CQ.collab.cal.CalendarLens
 * @extends CQ.search.Lens
 */
CQ.collab.cal.CalendarLens = CQ.Ext.extend(CQ.search.Lens, {

    /**
     * Option for LensDeck: do not load last data on lens switch, because
     * calendar lenses define a new timeframe for the query and hence
     * need a new query anyway.
     */
    loadLastData: false,

    /**
     * Returns the start date of this lens when going back one unit,
     * eg. the previous month for a month lens.
     * 
     * @return {Date} previous date
     */
	prev: function() {
	},
	
    /**
     * Returns the start date of this lens when going forward one unit,
     * eg. the next month for a month lens.
     * 
     * @return {Date} next date
     */
	next: function() {
	},
	
	/**
	 * Sets this lens to the given date. For example, when the today
	 * button is clicked, this method will be called with "now".
	 * Depending on what the lens actually displays, this might be
	 * interpreted as for example "current month" if the lens is a month lens.
     * 
     * This method must return "true" if the date changed so that the
     * view should be rerendered.
	 * 
	 * Implementations shall not render itself inside, this will
	 * be done implicitly because a query will be submitted after
	 * calling this function and {@link CQ.search.Lens#loadData} will be called, which
	 * must handle the rendering of the newly loaded data.
	 * 
	 * @param {Date} date
	 * @return {Boolean} whether the internal date changed and the lens must be rendered again
	 */
	setDate: function(date) {
	},
	
	/**
	 * Return the date to display on the top of the calendar.
	 * 
	 * @return {String} date display text (or html)
	 */
	getDateDisplayText: function() {
	},
	
	/**
	 * Returns the start date to use as lower bound for the query for events.
	 * 
	 * @return {Date} lower bound date
	 */
	getStartDate: function() {
	},
	
    /**
     * Returns the end date to use as upper bound for the query for events.
     * 
     * @return {Date} upper bound date
     */
    getEndDate: function() {
    }
    
});
