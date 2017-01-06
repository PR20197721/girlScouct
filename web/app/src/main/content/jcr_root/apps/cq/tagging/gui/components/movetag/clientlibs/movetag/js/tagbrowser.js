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

(function(window, document, Granite, $) {
    "use strict";

    var rel = ".cq-TagsPickerField";

    $(document).ready(function () {
        var $pathBrowser = $(rel).find(".coral-PathBrowser");

        $pathBrowser.each(function () {
            //customise the tag browser
            var $tagBrowser = $(this).pathBrowser({
                optionLoader : function(searchFor, callback) {
                    jQuery.get( $(this).data("browserpath") + ".tags.json",
                        {
                            suggestByTitle: searchFor,
                            start: 0,
                            limit: 50
                        },
                        function(data) {
                            var tags = data.tags;
                            var result = [];
                            for(var i = 0; i < tags.length; i++) {
                                result.push(tags[i]);
                            }
                            if (callback) callback(result);
                        }, "json");
                    return false;
                }.bind(this),
                optionValueReader : function(value) {
                    return value.path;
                },
                optionTitleReader : function(value) {
                    return value.titlePath;
                },
                optionRenderer : function(iterator, index) {
                    var value = this.options.options[index];
                    var titleMarkup = '';
                    if (this.options.showTitles && this.options.optionDisplayStrings[index] && this.options.optionDisplayStrings[index].length > 0) {
                        titleMarkup = this.options.optionDisplayStrings[index];
                    }
                    return $('<li class="coral-SelectList-item coral-SelectList-item--option" data-value="'+ value +'">'+ titleMarkup +'</li>');
                },
                autocompleteCallback : function(searchFor) {
                    var self = $(this.element).data("pathBrowser");
                    var def = $.Deferred();
                    if (self.options.optionLoader) {
                        var loader = {
                            loadOptions: self.options.optionLoader
                        };
                        var loaderDef = $.Deferred();
                        loaderDef.promise(loader);
                        loader.done(
                            function(object) {
                                if ($.isFunction(object.promise)) {
                                    object.done(
                                        function(object) {
                                            self._rebuildOptions(def, searchFor, object);
                                        }
                                    );
                                } else {
                                    self._rebuildOptions(def, searchFor, object);
                                }
                            }
                        );
                        var results = loader.loadOptions(searchFor, function(data) {
                            loaderDef.resolve(data);
                        });
                        if (results) loaderDef.resolve(results);
                    }
                    return def.promise();
                }
            });
            var tagBrowser = $tagBrowser.data("pathBrowser");
            tagBrowser.picker.$columnView.columnView({multiselect:  $(this).data("pickermultiselect")});

        });
    });

})(window, document, Granite, Granite.$);
