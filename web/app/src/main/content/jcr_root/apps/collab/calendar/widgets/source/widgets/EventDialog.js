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
 * Handles the calendar event edit dialog.
 * @class CQ.collab.cal.EventDialog
 * @extends CQ.Ext.util.Observable
 */
CQ.collab.cal.EventDialog = CQ.Ext.extend(CQ.Ext.util.Observable, {
    
    iframeDialog: null,
    
    constructor: function(config) {
        CQ.collab.cal.EventDialog.superclass.constructor.call(this, config);
    },

    open: function(event, config, el, align) {
        var path = event.get("path");
        var isRecurrence = false;
        
        // is this a recurrence of a recurring event?
        if (event.get("recurrenceOf")) {
            // always point to original event path by default
            path = event.get("recurrenceOf");
            isRecurrence = true;
        } else {
            // check if event is original event of a recurrence (= has recurrence subnode)
            isRecurrence = typeof event.get("recurrence") === "object";
        }
        
        // used in delete handler, assigned later
        var originalEventStart, originalEventEnd;
        
        var iframeDialog = this.iframeDialog;
        
        function normalDeleteHandler() {
            CQ.Ext.Msg.confirm(
                    CQ.I18n.getMessage("Delete event"),
                    CQ.I18n.getMessage("Are you sure you want to delete this event?"),
                    function (button) {
                        if (button == "yes") {
                            iframeDialog.hide();
                            CQ.collab.cal.Calendar.deleteEvent(path);
                        }
                    }
                ).setIcon(CQ.Ext.Msg.QUESTION);
        };
        
        function recurrenceDeleteHandler() {
            CQ.Ext.Msg.show({
                title: CQ.I18n.getMessage("Delete Recurring Event"),
                msg: CQ.I18n.getMessage("Would you like to delete all events in the series or only this event?"),
                buttons: {
                    ok: CQ.I18n.getMessage("All events in the series"),
                    yes: CQ.I18n.getMessage("Only this instance"),
                    cancel: CQ.I18n.getMessage("Cancel")
                },
                fn: function(buttonId) {
                    if (buttonId === "ok") {
                        // delete all events (path points to original event)
                        iframeDialog.hide();
                        CQ.collab.cal.Calendar.deleteEvent(path);
                        
                    } else if (buttonId === "yes") {
                        // delete only this (point to recurring event path)
                        iframeDialog.hide();
                        CQ.collab.cal.Calendar.deleteEvent(event.get("path"), true);
                    } else {
                        // cancel, do nothing
                    }
                }
            });
        };
        
        var dlgConfig = {
            title: (event.isNew ? CQ.I18n.getMessage("Create event") : CQ.I18n.getMessage("Edit event")),
            
            okText: CQ.I18n.getMessage("Save"),
            buttons: [
                {
                    xtype: "button",
                    text: CQ.I18n.getMessage("Delete"),
                    handler: isRecurrence ? recurrenceDeleteHandler : normalDeleteHandler,
                    // only show delete button when we edit an event and if the user is allowed to delete
                    hidden: (typeof event.isNew !== "undefined") || !CQ.collab.cal.Calendar.canDeleteEvent(event)
                },
                CQ.Dialog.OK,
                CQ.Dialog.CANCEL
            ],
            success: function() {
                // run after successful ok() handler
                CQ.collab.cal.Calendar.update();
            },
            listeners: {
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
                                // specific functions are provided by the eventbasics form component
                                originalEventStart = eventbasics.getEventStart();
                                originalEventEnd = eventbasics.getEventEnd();
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
        };
        
        if (config.eventEditPattern) {
            var url = CQ.HTTP.externalize(CQ.collab.cal.Calendar.formatEventURL(event, config.eventEditPattern));
            dlgConfig.buildIframeUrl = function() {
                return url;
            };
        } else {
            dlgConfig.formPath = config.eventForm;
        }
        
        if (config.eventDialogWidth > 0) {
            dlgConfig.width = config.eventDialogWidth;
        }
        
        if (config.eventDialogHeight > 0) {
            dlgConfig.height = config.eventDialogHeight;
        }
        
        if (el) {
            dlgConfig.listeners.show = function(win) {
                win.alignTo(el, align || "l-r?");
            };
        }
        
        if (iframeDialog) {
            iframeDialog.destroy();
        }
        
        iframeDialog = new CQ.IframeDialog(dlgConfig);
        this.iframeDialog = iframeDialog;
        
        // for recurrences, change the save button behaviour
        if (isRecurrence) {
            iframeDialog.on("beforesubmit", function() {
                // find out what changed inside the form
                var win = iframeDialog.getContentWin();
                var eventbasics = win.eventbasics;
                var recurrence = win.recurrence;
                if (!eventbasics) {
                    return false;
                }
                
                // means that the date part of the start/end datetime changed, not the time
                var datesChanged = (eventbasics.dates_changed == true);
                var recurrenceRuleChanged = (recurrence && recurrence.rule_changed == true);
                // TODO: find out *any* change in the form (and also skip question / save then)
                var onlyRecurrenceRuleChanged = false;

                if (onlyRecurrenceRuleChanged) {
                    // always change full series when only the recurrence rule changed
                    // ie. just submit the dialog as-is
                    return true;
                } else {
                    // ask user what to do
                    CQ.Ext.Msg.show({
                        title: CQ.I18n.getMessage("Edit Recurring Event"),
                        msg: CQ.I18n.getMessage("Would you like to change all events in the series or only this event?"),
                        //msg: CQ.I18n.getMessage("Would you like to change all events in the series, only this event or this and all future events in the series?"),
                        buttons: {
                            ok: CQ.I18n.getMessage("All events in the series"),
                            yes: CQ.I18n.getMessage("Only this instance"),
                            //no: CQ.I18n.getMessage("All following"),
                            cancel: CQ.I18n.getMessage("Cancel")
                        },
                        fn: function(buttonId) {
                            var iform = iframeDialog.getIframeForm();
                            
                            if (buttonId === "ok") {
                                // all events
                                // request goes to original event

                                if (!datesChanged || recurrenceRuleChanged) {
                                    // reset date part of start/end fields to the value of the original event
                                    // (because if we are on a recurrence, the post goes to the original
                                    // while the dates were changed to display the ones from the recurrence)
                                    if (originalEventStart && eventbasics.setYearMonthDayForEventDates) {
                                        eventbasics.setYearMonthDayForEventDates(originalEventStart, originalEventEnd);
                                    }
                                }
                                
                                iframeDialog.submit(true);

                            } else if (buttonId === "yes") {
                                // only this
                                // request goes to recurring event

                                // disable recurrence rule fields
                                if (recurrence && recurrence.setRecurrenceDisabled) {
                                    recurrence.setRecurrenceDisabled(true);
                                }
                                
                                // insert hidden ":takeOutOfRecurrence" field
                                iform.addHiddenField(CQ.collab.cal.Calendar.TAKE_OUT_OF_RECURRENCE_PARAM, "");
                                
                                // change form action to actual recurring event path
                                iform.form.action = CQ.HTTP.externalize(event.get("path"));
                                // also change edit resource action hint
                                var fields = iform.formEl.query("input[name=':resource']");
                                if (fields && fields.length > 0) {
                                    fields[0].value = event.get("path");
                                }
                                
                                iframeDialog.submit(true);
                                
                            /*
                            } else if (buttonId === "no") {
                                // all following
                                // request goes to recurring event
                                
                                // disable recurrence rule fields
                                if (recurrence && recurrence.setRecurrenceDisabled) {
                                    recurrence.setRecurrenceDisabled(true);
                                }
                                // insert hidden ":splitRecurrence" field
                                iform.addHiddenField(":splitRecurrence", "");
                                
                                // change form action to actual recurring event path
                                iform.form.action = event.get("path");
                                // also change edit resource action hint
                                var fields = iform.formEl.query("input[name=':resource']");
                                if (fields && fields.length > 0) {
                                    fields[0].value = event.get("path");
                                }
                                
                                iframeDialog.submit(true);
                                
                            */
                            } else /* if (buttonId === "cancel") */ {
                                // cancel
                            }
                        }
                    });
                    return false;
                }
            }, this);
        }
        
        iframeDialog.show();
        iframeDialog.loadContent(path);
    }
});

CQ.Ext.reg("calendareventdialog", CQ.collab.cal.EventDialog);
