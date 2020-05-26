/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

/**
 * Handle version creation in the toolbar.
 */

(function(document, $) {
    var count = 0;
    var selectionLength;
    var succeeded;
    var failed;
    var actionSelector = ".cq-common-admin-timeline-toolbar-actions-version";
    var eventName = "cq-common-admin-timeline-change";
    var ns = "cq-common-admin-timeline-toolbar-actions-version";
    var eventsSelector = ".cq-common-admin-timeline-events";
    var eventsNs = "cq-common-admin-timeline-events";
    var timelineSelector = ".cq-common-admin-timeline";

    // versions layer: listener of the create button
    $(document).on("click." + ns, actionSelector + "-ok", function (e) {
        submitTimelineForm();
    });

    // versions layer: listener of form submit event
    $(document).on("submit", actionSelector + "-form", function(e) {
        e.preventDefault();
        submitTimelineForm();
    });

    function submitTimelineForm() {
        var paths = [];
        if ($(timelineSelector).data("paths")) {
            paths = $(timelineSelector).data("paths");
        }
        var $form = $(actionSelector + "-form");
        createVersions(paths, $form);
    }

    function createVersions(paths, form) {
        var action = form.attr("action");
        var target = action;
        var $path = $("[name=\"path\"]", form);

        count = selectionLength = paths.length;
        succeeded = 0;
        failed = 0;
        if (paths.length == 1 && paths[0] === "") {
            $(actionSelector).attr("hidden", true);
            return;
            // note: this case of creating a verion with an empty path is no longer possible
        }
        for (var i = 0; i < paths.length; i++) {
            $path.val(paths[i]);
            if (action.indexOf("$resourcepath") !== -1) {
                if (action.charAt(action.indexOf("$resourcepath") - 1) == "/") {
                    target = action.replace(/\/\$resourcepath/g, paths[i]);
                } else {
                    target = action.replace(/\$resourcepath/g, paths[i]);
                }
            }
            createVersion(form, target);
        }
        form.find(".coral-Textfield").val("");
    }

    function createVersion(form, target) {
        if (!form) return;
        $.ajax({
        	url: encodeURIComponent(target).replace(/%2F/g, "/"),
            type: form.attr("method") || "post",
            data: form.serializeArray(),
            success: function() {
                succeeded++;
                createVersionCallback();
            },
            error: function() {
                failed++;
                var msg = form.data("errormessage");
                $(window).adaptTo("foundation-ui").notify("", msg ? Granite.I18n.getVar(msg) : Granite.I18n.get("Failed to create version"), "error");
                createVersionCallback();
            }
        });
    }

    function createVersionCallback() {
        count--;
        $(".version-label").val("");
        $(".version-comment").val("");
        //todo: <= because of missing mode
        if (count <= 0) {
            // todo: handle errors
            if (selectionLength > 1) {
                if (failed == 0) {
                    $(window).adaptTo("foundation-ui").notify("", Granite.I18n.get("Versions created for multiple items"), "success");
                }
                hideActionButton();
            } else {
                // single selection: refresh events
                if (failed == 0) {
                    $(window).adaptTo("foundation-ui").notify("", Granite.I18n.get("Version successfully created"), "success");
                }
                $(eventsSelector).trigger(eventName + "." + eventsNs);
            }
        }
    }


    //click handler of submit button in create version dialog (action bar) (since 6.2)
    $(document).on("click." + ns, ".cq-common-createversiondialog-submit", function (e) {
        var paths = [];
        $(".cq-siteadmin-admin-childpages.foundation-collection .foundation-collection-item.is-selected").each(function() {
            paths.push($(this).data("foundation-collection-item-id"));
        });
        var form = $(".cq-common-createversiondialog-form");
        submitDialog(".cq-common-createversiondialog", paths, form);
    });

    submitDialog = function(dialog, paths, form) {
        var $dialog = $(dialog);
        if ($dialog.length !== 0) {
            $dialog[0].hide();
        }
        createVersions(paths, form);

        //show timeline after version creation
        var timelinePanelTrigger = $('.granite-toggleable-control [data-granite-toggleable-control-target*="timeline"]');
        if (timelinePanelTrigger.length > 0) {
            timelinePanelTrigger[0].selected = true;
        }
    }


})(document, Granite.$);