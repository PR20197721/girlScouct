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
;(function ($, ns) {
    "use strict";

    /**
     * Listens to "foundation-selections-change" events (triggered for instance by the card or list layout)
     * and triggers the general references change event
     */
    $(document).on("foundation-selections-change", function(e) {
        var collection = $(e.target);
        if (collection.is(".foundation-layout-card") && !collection.is(".mode-selection")) {
            return;
        }

        var items = collection.find(".foundation-selections-item");

        if (items.length === 0) {
            if (collection.is(".foundation-layout-list")) {
                // list layout and no selection: load content path
                ns.triggerChange();
            } else {
                // card layout and no selection: empty references list
                ns.triggerChange({
                    paths: []
                });
            }

        } else {
            // list or card layout and selection: add selected paths
            var paths = [];
            items.each(function() {
                paths.push($(this).data("path"));
            });

            var formerPaths;
            if (ns.$root.data("paths")) {
                formerPaths = ns.getTagPaths();
            } else {
                formerPaths = [];
            }

            ns.triggerChange({
                paths: paths,
                // avoid refresh if mutli selection now and before
                avoidRefresh: paths.length > 1 && formerPaths.length > 1
            });
        }
    });

    $(document).on("click", ".js-endor-innerrail-toggle[data-target='#aem-tags-rail-viewproperties']", function(event, forceDetail) {
        if ($(this).hasClass("is-selected")) {
            ns.$root.trigger(ns.EVENT_CHANGE + "." + ns.NAMESPACE + "-" + ns.NAMESPACE_LIST, forceDetail);
        }
    });

}(jQuery, Granite.TagInfo));
