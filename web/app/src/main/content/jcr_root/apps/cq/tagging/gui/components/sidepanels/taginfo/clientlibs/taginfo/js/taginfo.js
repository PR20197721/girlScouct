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
     * Root object for taginfo
     * @type {*}
     */    
    ns.TagInfo = (function () {

        var self = {};

        /**
         * TagInfo namespace
         * @type {string}
         */
        self.NAMESPACE = "taginfo";

        /**
         * TagInfo namespace for items list
         * @type {string}
         */
        self.NAMESPACE_LIST = "list";

        /**
         * TagInfo namespace for items detail
         * @type {string}
         */
        self.NAMESPACE_DETAIL = "tagref-details";

        /**
         * Event when tag-info changes
         * @type {string}
         */
        self.EVENT_CHANGE = "taginfo-change";


        /**
         * Text to display when selection is empty
         * @type {string}
         */
        self.SELECTION_TEXT_EMPTY = Granite.I18n.get("Select a tag to display its info.");

        /**
         * Text to display when selection is multiple
         * @type {string}
         */
        self.SELECTION_TEXT_MULTIPLE = Granite.I18n.get("Tag info is not available for multiple selection.");


        /**
         * TagInfo root DOM element
         * @type {jQuery}
         */
        self.$root = $("." + self.NAMESPACE);


        /**
         * TagInfo wait indicator
         * @type {jQuery}
         */
        self.$spinner = self.$root.find(".refSpinner");


        /**
         * TagInfo message
         * @type {jQuery}
         */
        self.$message = self.$root.find(".message");

        /**
         * TagInfo list
         * @type {jQuery}
         */
        self.$list = self.$root.find("." + self.NAMESPACE_LIST);

        /**
         * TagInfo detail
         * @type {jQuery}
         */
        self.$detail = $("." + self.NAMESPACE_DETAIL);       

        /**
         * Triggers the general TagInfo change event
         * @param options Optional event extra parameters
         */
        self.triggerChange = function(options) {
            self.$root.trigger(self.EVENT_CHANGE + "." + self.NAMESPACE, options);
        };

        /**
         * Triggers the general TagInfo resize event
         * @param options Optional event extra parameters
         */
        self.triggerResize = function(options) {
            self.$root.trigger(self.EVENT_RESIZE, options);
        };

        /**
         * Hides wait indicator when loading tag-info is completed
         */
        self.hideSpinner = function() {
        	self.$spinner.hide();
    	}

        /**
         * Shows wait indicator while loading tag-info
         */
        self.showSpinner = function() {
            self.$message.hide();
        	self.$spinner.show();
    	}

        /**
         * Shows a panel
         * @param panel {String} Name of the panel to show
         */
        self.showPanel = function(panel) {            
            if (panel !== "detail") {
                self.$root.toggle(true);
                self.$detail.toggle(false);                
            } else {
                self.$root.toggle(false);
                self.$detail.toggle(true);
            }
        };

        /**
         * Displays the given HTML message
         * @param message Message to display
         */
        self.displayMessage = function(message) {
            // Show message
            self.hideSpinner();
            self.$message.html(message);
        };        


        /**
         * Gets the current tag paths
         * @returns {Array} Current tag paths
         */
        self.getTagPaths = function() {
            return self.$root.data("paths") ? self.$root.data("paths").split(",") : [];
        };

        /**
         * Gets the current tag path
         * @returns {string} Current tag path
         */
        self.getTagPath = function() {
            var paths = self.getTagPaths();
            return paths.length > 0 ? paths[0] : undefined;
        };


        /**
         * Listen to change events of the tag-info
         */
        self.$root.on(self.EVENT_CHANGE + "." + self.NAMESPACE, function(e, options) {
            var paths = [];
            options = options || {};

            if (options.paths) {
                paths = options.paths;
            }

            if (paths.length === 1) {
                self.$root.data("paths", paths[0]);
            } else if (paths.length === 0) {
                self.$root.data("paths", "");
            } else {
                var p = "";
                var comma = "";
                for (var i in paths) {
                    p += comma + paths[i];
                    comma = ",";
                }
                self.$root.data("paths", p);
            }

            if ((!self.$root.is(":visible") && !$(".tagref-details").is(":visible")) || options.avoidRefresh) {
                // refreshing chocked because rail view is not visible or by options
                return;
            }

            // full refresh of the references (hide first)
            self.$root
                .hide()
                .fadeIn("fast")
                .trigger(self.EVENT_CHANGE + "." + self.NAMESPACE + "-" + self.NAMESPACE_LIST)
            ;
        });

        /**
         * Listen to change events of the tag-info list
         */
        self.$root.on(self.EVENT_CHANGE + "." + self.NAMESPACE + "-" + self.NAMESPACE_LIST, function(event, forceDetail) {
            self.requestTagInfo(function() {
                if (forceDetail) {
                   self.showPanel("detail");
                } else {
                    self.showPanel("list");
                }
                self.triggerResize({list:true,scrollToTop:true});
            });
        });

        /**
         * Request the tag info
         * @param callback The method to call after the taginfo have been requested and appended to
         *                 the hidden textarea
         */
        self.requestTagInfo = function(callback) {

            self.showSpinner();

            var paths = self.getTagPaths();

            if (paths.length === 0 || (paths.length === 1 && paths[0] === "")) {
                // selection mode (card layout) without selection
                self.displayMessage(self.SELECTION_TEXT_EMPTY);
            } else if (paths.length > 1) {
                // multi selection
                self.displayMessage(self.SELECTION_TEXT_MULTIPLE);
            } else {
                var componentPath = self.$root.data("component-path") + ".values.json";

                $.ajax({
                    url: Granite.HTTP.externalize(componentPath),
                    method: "GET",
                    data: {
                        tagPath: paths[0]
                    },
                    cache: false
                }).done(function(result) {
                    var tagName = result["tagName"];
                    var tagDescription = result["tagDescription"];
                    var lastModified = result["lastModified"];
                    var lastModifiedBy = result["lastModifiedBy"];
                    var lastReplicated = result["lastReplicated"];
                    var lastReplicatedBy = result["lastReplicatedBy"];
                    var referenceCount = result["referenceCount"];
                    var references = result["taggedItems"];

                    $(".tag-info-name-value").text(tagName);
                    if (lastModified && lastModifiedBy) {
                        $(".tag-info-modification").show();
                        $(".tag-info-modified").text(lastModified);
                        $(".tag-info-modifiedby").text(lastModifiedBy);
                    } else {
                        $(".tag-info-modification").hide();
                    }
                    if (lastReplicated && lastReplicatedBy) {
                        $(".tag-info-replication").show();
                        $(".tag-info-replicated").text(lastReplicated);
                        $(".tag-info-replicatedby").text(lastReplicatedBy);
                    } else {
                        $(".tag-info-replication").hide();
                    }
                    if (tagDescription) {
                        $(".tag-info-description").show();
                        $(".tag-info-description-value").text(tagDescription);
                    } else {
                        $(".tag-info-description").hide();
                    }

                    var $refList = $(".tagref-details-list");
                    var $refSection = $($("section.tagref-item")[0]);
                    var $refTitle = $refSection.find(".tagref-title").html("");
                    var $refPath = $refSection.find(".tagref-path").html("");
                    //$refList.html($refSection);
                    //var emptyRefSectionHTML = $refList.html();
                    $refList.html("");

                    if (referenceCount > 0) {
                        $(".tag-info-empty-references").hide();
                        $(".tag-info-non-empty-references").show();
                        $(".tag-info-non-empty-references-badge").text(referenceCount);

                        for (var i = 0, len = references.length; i < len; i++) {
                            var reference = references[i];

                            $refTitle.html(reference["title"]);                            
                            $refPath.html(self.shortenPath(reference["path"]));
                            $refPath.attr("title", reference["path"]);
                            var refSectionHTML = $('<div>').append($refSection.clone()).html();
                            var $copyRefSection = $(refSectionHTML);
                            $copyRefSection.attr("data-path", reference["path"]);
                            $refList.append($copyRefSection);
                        }

                    } else {
                        $refList.html($refSection);
                        $(".tag-info-non-empty-references").hide();
                        $(".tag-info-empty-references").show();
                    }

                    self.hideSpinner();
                    if (typeof callback != "undefined") {
                        callback();
                    }
                })
                .fail(function() {
                    self.displayMessage("An error occurred while refreshing list of references.");
                });
            }
        };

        self.shortenPath = function(path) {
            if (path.length > 30) {
                return "..." + path.substring(path.length-30);
            }
        };

        return self;

    }());

}(jQuery, Granite));