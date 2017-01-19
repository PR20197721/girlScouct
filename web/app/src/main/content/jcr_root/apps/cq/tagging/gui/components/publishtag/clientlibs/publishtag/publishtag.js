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
(function(document, $) {
    "use strict";

    var rel = ".cq-tagging-touch-actions-publish";
    var publishActivator = ".cq-tagging-touch-actions-publishtag-activator";
    function publishTags(paths, force) {
        var items = $(".foundation-collection").find(".foundation-selections-item");
        hideModal();
        if (paths == null) {
            var paths = [];
            if (items.length) {
                for (var i = 0; i < items.length; i++) {
                    var dataPath = $(items[i]).data("path");
                    paths.push(dataPath);                    
                }
            }
        }
        $.ajax({
            url: "/bin/tagcommand",
            type: "post",
            data: {
                cmd: "activateTag",
                path: paths,
                "_charset_": "utf-8"
            },
            success: function() {
                var contentApi = $(".foundation-content").adaptTo("foundation-content");
                contentApi.refresh();
                // display notification
                new Granite.UI.NotificationSlider($(".endor-Page-content.endor-Panel").eq(0), $(".endor-BlackBar")).notify({
                    content: Granite.I18n.get("Selected tag(s) queued up for publishing"),
                    type: "info",
                    className: "tag-notification-alert--absolute"
                });

            },
            error: function(data) {
                $(rel).modal("hide");
                $(".tags-publish-error").modal("show");
            }
        });
    }

    function hideModal() {

        $(rel).hide();
        $(rel).modal("hide");
        $(rel + "-error").modal("hide");

    }


    $(document).on("click." + rel, rel + " button.coral-Button--primary", function(e) {
        publishTags();
    });


    $(document).on("beforeshow" + rel, ".coral-Modal" + rel, function () {
        var selectedItems = $(".foundation-selections-item");
        var singleContentMessage, multipleContentMessage;
        singleContentMessage = Granite.I18n.get("You are going to publish the following tag:");
        multipleContentMessage = Granite.I18n.get("You are going to publish the following {0} tags:", selectedItems.length);
        populateModalWithSelectedItems($(this), selectedItems, singleContentMessage, multipleContentMessage);
    });

    function populateModalWithSelectedItems(modal, selectedItems, singleContentMessage, multipleContentMessage) {
        var MAX_ENTRIES = 10,
            MAX_ENTRY_SIZE = 40,
            itemlist,
            body = modal.find(".coral-Modal-body"),
            itemCnt = Math.min(selectedItems.length, MAX_ENTRIES),
            content = selectedItems.length === 1 ? _wrapContent("p", singleContentMessage) : _wrapContent("p", multipleContentMessage),
            list = $.map(selectedItems, function (val) {
                var title = _g.XSS.getXSSValue($($(val).find('h4')[0]).text()) || $(val).find(".foundation-collection-item-title").text();
                return title.length <= MAX_ENTRY_SIZE ? title : title.substring(0, MAX_ENTRY_SIZE) + "...";
            });

        if (list.length > itemCnt) {
            list = list.splice(0, itemCnt);
            list.push("...");
        }
        list = list.join("<br>");
        itemlist = $("<p class=\"item-list\"></p>").append(list);
        body.empty().append(content).append(itemlist);
    }

    function _wrapContent(tag, mesg) {
        return "<" + tag + ">" + mesg + "</" + tag + ">";
    }

})(document, Granite.$);