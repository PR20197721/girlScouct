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
(function(window, document, $, URITemplate, Granite, Coral) {
    "use strict";

    var REFRESH_INTERVAL = 60000;
    var KEY_DATA = "granite.shell.badge.data";
    var KEY_TIMESTAMP = "granite.shell.badge.timestamp";
    var KEY_USER = "granite.shell.badge.user";

    var currentToggleable;
    var dirty = true;
    var lastXhr;

    function getData(user, src, resolveWhenNewData) {
        var cachedUser = window.sessionStorage.getItem(KEY_USER);
        var timestamp = parseInt(window.sessionStorage.getItem(KEY_TIMESTAMP), 10);
        var stale = cachedUser !== user ||  isNaN(timestamp) || timestamp + REFRESH_INTERVAL <= Date.now();

        if (!stale || (cachedUser === user && lastXhr && lastXhr.state() === "pending")) {
            if (resolveWhenNewData) {
                return $.Deferred().reject().promise();
            } else {
                var data = window.sessionStorage.getItem(KEY_DATA);
                if (data) {
                    return $.Deferred().resolve(JSON.parse(data)).promise();
                }
            }
        }

        lastXhr = $.ajax({
            url: src,
            cache: false,
            timeout: REFRESH_INTERVAL
        });

        return lastXhr.then(function(data) {
            try {
                dirty = true;

                window.sessionStorage.setItem(KEY_USER, user);
                window.sessionStorage.setItem(KEY_TIMESTAMP, Date.now());
                window.sessionStorage.setItem(KEY_DATA, JSON.stringify(data));
            } catch (e) {
            }

            return data;
        });
    }

    function createToggleable(state) {
        var popover = $(new Coral.Popover().set({
            placement: "bottom"
        })).addClass("foundation-toggleable foundation-layout-util-maxheight80vh foundation-layout-util-width300 foundation-layout-util-breakword");
        var contentEl = popover[0].content;

        if (state.data) {
            state.data.forEach(function(v) {
                var item = $(document.createElement("div"))
                    .addClass("granite-shell-badge-item")
                    .appendTo(contentEl);

                item.context.style.backgroundImage = "url(" + v.backgroundImageUrl + ")";
                item.context.style.backgroundSize = "36px 36px";
                item.context.style.backgroundRepeat = "no-repeat";
                item.context.style.backgroundPosition = "0px 4px";

                if (v.title) {
                    var titleSpan = $(document.createElement("span"))
                        .addClass("granite-shell-badge-item-title")
                        .html(v.title); // v.title is already XSS checked in the server

                    titleSpan.appendTo(item);
                }

                if (v.description) {
                    var descriptionSpan = $(document.createElement("span"))
                        .addClass("granite-shell-badge-item-description")
                        .html(v.description);

                    descriptionSpan.appendTo(item);
                }

                var a = $(document.createElement("a"))
                    .addClass("granite-shell-badge-item-link");

                a.attr("href", v.detailsUrl);
                a.appendTo(item);
            });
        }

        $(document.createElement("div"))
            .addClass("granite-shell-badge-item-inbox")
            .text(Granite.I18n.get("View All ({0} New)", state.total, "link to notification inbox"))
            .appendTo(contentEl)
            .append($(document.createElement("a"))
                .addClass("granite-shell-badge-console granite-shell-badge-item-link"));

        return popover;
    }

    function updateBadge(el, src, resolveWhenNewData) {
        var user = el[0].dataset.graniteShellBadgeUser;

        getData(user, src, resolveWhenNewData).then(function(data) {
            el.attr("badge", data.total);
        });
    }

    // Assume the badge element is only injected to the DOM during page load only.
    $(function() {
        var el = $(".granite-shell-badge").first();
        var src = el.data("graniteShellBadgeSrc");

        if (!src) {
            return;
        }

        updateBadge(el, src);

        setInterval(function() {
            updateBadge(el, src, true);
        }, 3600000);
    });

    $(document).on("click", ".granite-shell-badge", function(e) {
        e.preventDefault();

        var el = this;
        var control = $(this);
        var src = el.dataset.graniteShellBadgeSrc;
        var user = el.dataset.graniteShellBadgeUser;

        if (!src || !user) {
            return;
        }

        var consoleURL = URITemplate.expand(el.dataset.graniteShellBadgeConsole, {
            ref: window.location.href
        });

        getData(user, src).then(function(data) {
            if (data.total === 0) {
                window.location = consoleURL;
                return;
            }

            if (!currentToggleable) {
                currentToggleable = createToggleable(data);
                currentToggleable.find(".granite-shell-badge-console").attr("href", consoleURL);
                currentToggleable.appendTo(document.body);

                dirty = false;

                requestAnimationFrame(function() {
                    currentToggleable.adaptTo("foundation-toggleable").show(el);
                });
                return;
            }

            var currentApi = currentToggleable.adaptTo("foundation-toggleable");

            if (currentApi.isOpen()) {
                currentApi.hide();

                requestAnimationFrame(function() {
                    currentToggleable.detach();
                });
                return;
            }

            var toggleable;
            if (!dirty) {
                toggleable = currentToggleable;
            } else {
                currentToggleable = toggleable = createToggleable(data);
                dirty = false;
            }

            toggleable.find(".granite-shell-badge-console").attr("href", consoleURL);
            toggleable.appendTo(document.body);

            requestAnimationFrame(function() {
                toggleable.adaptTo("foundation-toggleable").show(el);
            });
        }, function() {
            window.location = consoleURL;
        });
    });
})(window, document, Granite.$, Granite.URITemplate, Granite, Coral);
