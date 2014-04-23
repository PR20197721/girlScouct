/*
 * Copyright 1997-2010 Day Management AG
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
 * Handles the calendar event view popup.
 * @class CQ.collab.cal.EventPopup
 * @extends CQ.Ext.util.Observable
 */
CQ.collab.cal.EventPopup = CQ.Ext.extend(CQ.Ext.util.Observable, {
    
    iframeDialog: null,

    constructor: function(config) {
        CQ.collab.cal.EventPopup.superclass.constructor.call(this, config);
    },

    open: function(event, config, el, align) {
        var url;
        if (config.eventDisplay === "formPopup") {
            url = event.get("path") + ".form.view.html" + config.eventViewForm + "?wcmmode=disabled";
        } else if (config.eventDisplay === "patternPopup") {
            url = CQ.collab.cal.Calendar.formatEventURL(event, config.eventViewPattern);
        }
        
        if (this.iframeDialog) {
            this.iframeDialog.destroy();
        }
        
        this.iframeDialog = new CQ.IframeDialog({
            cls: "cq-calendar-event-popup",
            baseCls: "cq-calendar-event-popup",
            draggable: false,
            resizable: false,
            closable: true,
            header: false,
            border: false,
            shadow: true,
            modal: true,
            y: 20,
            height: config.eventPopupHeight,
            width: config.eventPopupWidth,
            
            listeners: {
                scope: this,
                
                show: el ? function(win) {
                    win.alignTo(el, align || "l-r?");
                } : CQ.Ext.emptyFn,

                // similar to the loadContent handler in EventDialog.js, but we can do
                // less because we don't need to support the submit
                loadContent: function(dialog) {
                    var eventbasics = dialog.getContentWin().eventbasics;
                    if (eventbasics) {
                        // timezone must be set before we call eventbasics.getEventStart() below
                        if (eventbasics.setTimeZone) {
                            eventbasics.setTimeZone(config.timeZone);
                        }
                        
                        if (event.isNew || event.get("recurrenceOf")) {
                            // init the form with the date for the new event or
                            // adapt it to the recurring instance
                            if (eventbasics.setEventDate) {
                                eventbasics.setEventDate(event.get("start"), event.get("end"));
                            }
                            if (event.isDate && eventbasics.setIsDate) {
                                eventbasics.setIsDate(true);
                            }
                        }
                        
                        if (eventbasics.setDateFormats) {
                            eventbasics.setDateFormats(
                                CQ.collab.cal.Calendar.getDatePattern("normal"),
                                CQ.collab.cal.Calendar.getTimePattern("normal"),
                                CQ.collab.cal.Calendar.getStartOfWeek()
                            );
                        }
                    }
                    
                    var recurrence = dialog.getContentWin().recurrence;
                    if (recurrence && recurrence.setDateFormat) {
                        recurrence.setDateFormat(CQ.collab.cal.Calendar.getDatePattern("normal"));
                    }
                }
            }
        });
        this.iframeDialog.show();
        this.iframeDialog.loadContent(url);
    }
});

CQ.Ext.reg("calendareventpopup", CQ.collab.cal.EventPopup);
