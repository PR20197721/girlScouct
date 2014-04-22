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
 * The <code>CQ.collab.cal.WeekView</code> renders one or multiple
 * days next to each other.
 * NOT USED CQ.collab.cal.WeekView
 * @extends CQ.Ext.Panel
 */
CQ.collab.cal.WeekView = CQ.Ext.extend(CQ.Ext.Panel, {

    constructor: function(config) {
        var cal = CQ.collab.cal.Calendar;
        
        var i;
        var startDate = config.startDate;
        // Note: end date is +1d after the last day displayed
        var days = this.getDaysBetween(startDate.utcDateOnly(), config.endDate.utcDateOnly());
        
        var date = startDate;
        var dateDisplayItems = [];
        var dayDateFormat = cal.getDatePattern("shortDay");
        for (i=0; i < days; i++) {
            dateDisplayItems.push({ "html": date.format(dayDateFormat) });
            date = date.add(Date.DAY, 1);
        }
        
        var topPanel = new CQ.Ext.Panel({
            "cls": "cq-calendar-week-top",
            "layout": "table",
            "layoutConfig": {
                "columns": 1
            },
            "bodyStle": "padding-left: 40px; padding-right: 16px; padding-bottom: 15px; border: 0px; background-color: #ddd",
            "items": [
                {
                    "cls": "cq-calendar-week-daybar",
                    "layout": "table",
                    "layoutConfig": {
                        "columns": days
                    },
                    "border": false,
                    "defaults": {
                        "border": true,
                        "style": "text-align: center"
                    },
                    "items": dateDisplayItems
                },
                {
                    "cls": "cq-calendar-week-alldaybar",
                    "layout": "table",
                    "layoutConfig": {
                        "columns": days
                    },
                    "border": false,
                    "style": "minHeight: 40px",
                    "defaults": {
                        "border": false
                    },
                    "items": this.buildAllDayEventPanels(startDate, days, config.store)
                }
           ]
        });
        
        // height of a single hour in pixels
        this.hourHeight = config.hourHeight || 40;
        
        var dayPanels = [];
        var timePanel = {
            "layout": "table",
            "layoutConfig": {
                "columns": 1
            },
            "cellStyle": "width:40px",
            "border": false,
            "width": 40,
            "items": []
        };
        var veryShortTimeFormat = cal.getTimePattern("short");
        var timeOfDay = startDate.clearTime(true);
        for (i = 0; i < 24; i++) {
            timePanel.items.push({
                "html": timeOfDay.add(Date.HOUR, i).format(veryShortTimeFormat),
                "style": "text-align: right; padding-right: 5px;",
                "height": this.hourHeight,
                "border": false
            });
        }
        dayPanels.push(timePanel);
        
        for (i = 0; i < days; i++) {
            date = startDate.add(Date.DAY, i);
            dayPanels.push(this.buildDayPanel(date, this.hourHeight, config.store));
        }
        
        var bodyPanel = new CQ.Ext.Panel({
            "cls": "cq-calendar-week-body",
            "border": false,
            "layout": "fit",
            "items": [
                {
                    "layout": "fit",
                    "bodyStyle": "overflow-y: scroll",
                    "autoWidth": true,
                    "defaults": {
                        "autoWidth": true
                    },
                    "items": {
                        "layout": "table",
                        "layoutConfig": {
                            "columns": 1 + days
                        },
                        "border": false,
                        "items": dayPanels
                    }
                }
            ]
        });
        
        config = CQ.Util.applyDefaults(config, {
            "cls": "cq-calendar-week",
            "height": CQ.collab.cal.Calendar.getLensHeight(),
            "layout": "table",
            "layoutConfig": {
                "columns": 1
            },
            "border": false,
            "items": [ topPanel, bodyPanel ]
        });
        
        CQ.collab.cal.WeekView.superclass.constructor.call(this, config);
    },
    
    buildAllDayEventPanels: function(startDate, days, store) {
        var allDayEventPanels = [];
        
        // events are already ordered
        var events = this.getAllDayEvents(store);
        if (events) {
            //events.each(function(event) {
            //    console.log("> event: " + event.get("title") + " " + event.get("start") + " - " + event.get("end"));    
            //});
            
            // start with first event
            var event = events.removeAt(0);
            var col = 0;
            var startCol, endCol, colsLeft;
            var startsEarlier = false, endsLater = false;
            
            while (event) {       
                // current event: calculate startcol + endcol
                startCol = this.getDaysBetween(startDate.utcDateOnly(), event.get("start"));
                // enforce boundaries for event that starts earlier
                if (startCol < 0) {
                    startCol = 0;
                    startsEarlier = true;
                } else {
                    startsEarlier = false;
                }
                endCol = this.getDaysBetween(startDate.utcDateOnly(), event.get("end"));
                // enforce boundaries for event that end later
                if (endCol >= days) {
                    endCol = days - 1;
                    endsLater = true;
                } else {
                    endsLater = false;
                }
                
                if (startCol > col) {
                    // place dummy between current col and startcol
                    allDayEventPanels.push({
                        "colspan": startCol - col
                    });
                }
                
                // place event
                allDayEventPanels.push({
                    //"title": (startsEarlier ? "< " : "") + event.get("title") + (endsLater ? " >" : ""),
                    "html": (startsEarlier ? "< " : "") + event.get("title") + (endsLater ? " >" : ""),
                    "colspan": endCol - startCol + 1,
                    "frame": true,
                    "border": false,
                    "calendarEvent": event,
                    "listeners": {
                        "render": function() {
                            this.body.unselectable();
                            var ev = this.calendarEvent;
                            this.body.on("dblclick", function() {
                                CQ.collab.cal.Calendar.openEvent(ev);
                            });
                        }
                    }
                });
                
                // calculate spec left
                col = endCol + 1;
                colsLeft = days - col;
                
                // we can stop immediately if no events are left
                if (events.getCount() == 0) {
                    break;
                }
                
                if (colsLeft >= 1) {
                    // find event that fits in the area left
                    for (var i=0; i < events.getCount(); i++) {
                        event = events.get(i);
                        startCol = this.getDaysBetween(startDate.utcDateOnly(), event.get("start"));
                        if (startCol >= col) {
                            // event found, remove from the list
                            events.removeAt(i);
                            // end this for loop
                            break;
                        }
                        // none found => close this row
                        event = null;
                    }
                    // if event that still fits into this row was found in the for loop,
                    // continue with another round of the while loop
                    if (event) {
                        continue;
                    }
                    
                    // => close row
                    // place dummy to close this row
                    allDayEventPanels.push({
                        "colspan": colsLeft
                    });
                }
                // start at beginning with next event
                col = 0;
                event = events.removeAt(0);
            }
            
            if (colsLeft > 0) {
                // place dummy to close this row
                allDayEventPanels.push({
                    "colspan": colsLeft
                });
            }
        }
        
        // return empty object if no panel created, as an empty array
        // gives an error for the panel's item property in Extjs
        return allDayEventPanels.length > 0 ? allDayEventPanels : {};
    },
    
    buildDayPanel: function(day, hourHeightInPx, store) {
        var timeFormat = CQ.collab.cal.Calendar.getTimePattern("normal");
        var dayPanel = {
            "cellStyle": "vertical-align: top; border-right: 1px solid #ccc;",
            "layout": "absolute",
            "border": false,
            "items": []
        };
        var tz = CQ.collab.cal.Calendar.getTimeZone();
        day = day.shift(tz).clearTime(true);
        var events = this.getEventsForDay(day, store);
        if (events) {
            
            var nextDay = day.add(Date.DAY, 1);
            
            var groups = [];
            groups.push([]); // create first group
            
            var groupEndY = 0;

            // 1. collect events into overlapping groups            
            events.each(function(event) {
                var start = event.get("start");
                var end = event.get("end");
                // limit to end of day
                if (end.getTime() > nextDay.getTime()) {
                    end = nextDay;
                }

                var startY = this.calculatePixels(day, start, hourHeightInPx);
                var endY = this.calculatePixels(day, end, hourHeightInPx);
                if (groupEndY == 0 || startY < groupEndY) {
                    // overlap, integrate into group
                    groups[groups.length-1].push(event);
                } else {
                    // new group
                    groups.push([event]);
                }
                // enlarge group if new event lasts longer than the existing group
                groupEndY = endY > groupEndY ? endY : groupEndY;
            }, this);
            
            // 2. render each group...
            for (var i=0; i < groups.length; i++) {
                // ...by distributing the events in each group horizontally
                var group = groups[i];
                if (group.length == 0) {
                    continue;
                }
                var groupStartY = this.calculatePixels(day, group[0].get("start"), hourHeightInPx);
                var groupPanel = {
                    "style": "width: 100%",
                    "y": groupStartY,
                    "border": false,
                    "layout": "table",
                    "layoutConfig": {
                        "columns": group.length
                    },
                    "items": []
                };
                for (var j=0; j < group.length; j++) {
                    var event = group[j];
                    var eventStartY = this.calculatePixels(day, event.get("start"), hourHeightInPx);
                    var eventEndY = this.calculatePixels(day, event.get("end"), hourHeightInPx);
                    var eventHeight = eventEndY - eventStartY;
                    // minimum height
                    if (eventHeight < 15) {
                        eventHeight = 15;
                    }
                    groupPanel.items.push({
                        "title": event.get("start").format(timeFormat) + " " + event.get("title"),
                        //"baseCls":"x-box",
                        "frame": true,
                        "cellStyle": "vertical-align: top; margin-top: " + (eventStartY - groupStartY) + "px;",
                        "style": "overflow: hidden;",
                        "height": eventHeight,
                        "calendarEvent": event,
                        "listeners": {
                            "render": function() {
                                this.body.unselectable();
                                var ev = this.calendarEvent;
                                this.body.on("dblclick", function() {
                                    CQ.collab.cal.Calendar.openEvent(ev);
                                });
                            }
                        }
                    });
                }
                dayPanel.items.push(groupPanel);
            }
        }
        if (dayPanel.items.length == 0) {
            dayPanel.items = null;
        }
        return dayPanel;
    },
    
    calculatePixels: function(start, end, hourHeightInPx) {
        var MILLIS_PER_HOUR = 60 * 60 * 1000.0;
        return ((end.getTime() - start.getTime()) / MILLIS_PER_HOUR) * hourHeightInPx;
    },

    getAllDayEvents: function(store) {
        if (!store) {
            return null;
        }
        return store.queryBy(function(event, id) {
            return event.get("isDate");
        }, this);
    },
    
    getEventsForDay: function(day, store) {
        if (!store) {
            return null;
        }
        day.clearTime();
        
        return store.queryBy(function(event, id) {
            if (event.get("isDate")) {
                return false;
            }
            return day.isSameDateAs(event.get("start"));
        }, this);
    },
    
    getDaysBetween: function(start, end) {
        var MILLIS_PER_DAY = 24 * 60 * 60 * 1000.0;
        return Math.floor((end.getTime() - start.getTime()) / MILLIS_PER_DAY);
    }
	
});

CQ.Ext.reg("weekview", CQ.collab.cal.WeekView);
