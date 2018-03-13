/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2013 Adobe Systems Incorporated
*  All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
**************************************************************************/

(function($) {

    var isFullScreen = false;

    var FULLSCREEN_ID = "RTEFullScreenMode";

    var $fullScreenDiv = undefined;

    /**
     * Array containing link elements referencing external style sheets which were added while starting FSM.
     */
    var $externalStyleSheetLinks = [];

    var com = CUI.rte.Common;

    function ensureFullScreenDiv() {
        var $fsDiv = $("#" + FULLSCREEN_ID);
        if ($fsDiv.length === 0) {
            var $content = $("body");
            $fsDiv = $("<div/>").attr("id", FULLSCREEN_ID);
            $content.append($fsDiv);
        }
        return $fsDiv.height($(document).height());
    }

    function removeFullScreenDiv() {
        var $toRemove = $fullScreenDiv || $("#" + FULLSCREEN_ID);
        if ($toRemove.length > 0) {
            $toRemove.remove();
        }
        $fullScreenDiv = undefined;
    }


    CUI.rte.ui.cui.FullScreenHelper = function() {

        return {

            /**
             * Starts full screen mode.
             * @param $fsContent (optional) The content to be shown in fullscreen mode
             * @param options (optional) The options for some fullscreen mode behavior
             * @return {jQuery} The full screen container
             */
            start: function($fsContent, options) {
                if (!isFullScreen) {
                    $fullScreenDiv = ensureFullScreenDiv();
                    if (options) {
                        if (options.hasOwnProperty("css")) {
                            $fullScreenDiv.addClass(options["css"]);
                        }
                        if (options.hasOwnProperty("externalStyleSheets")) {
                            if (CUI.rte.Utils.isArray(options["externalStyleSheets"])) {
                                for (var index = 0; index < options["externalStyleSheets"].length; index++) {
                                    var $link = $("<link rel=\"stylesheet\" href=\"" + options["externalStyleSheets"][index] + "\" type=\"text/css\">");
                                    $externalStyleSheetLinks.push($link);
                                    $(document.head).append($link);
                                }
                            } else {
                                var $link = $("<link rel=\"stylesheet\" href=\"" + options["externalStyleSheets"] + "\" type=\"text/css\">");
                                $externalStyleSheetLinks.push($link);
                                $(document.head).append($link);
                            }
                        }
                    }
                    if ($fsContent) {
                        $fullScreenDiv.append($fsContent);
                    }
                    this.addEventListeners();
                    isFullScreen = true;
                }
                return $fullScreenDiv;
            },

            addEventListeners : function() {
                if (!com.ua.isTouch) {
                    $(window).on("resize.rteFSResize", function(e) {
                        if ($fullScreenDiv) {
                            ensureFullScreenDiv();
                        }
                    });
                }
            },

            /**
             * Finished full screen mode.
             */
            exit: function() {
                if (isFullScreen) {
                    removeFullScreenDiv();
                    while ($externalStyleSheetLinks.length > 0) {
                        $externalStyleSheetLinks.pop().remove();
                    }
                    isFullScreen = false;
                }
            },

            /**
             * Checks if full screen mode is currently active.
             * @returns {boolean} True if full screen mode is currently active
             */
            isActive: function() {
                return isFullScreen;
            },

            getContainer: function() {
                return $fullScreenDiv;
            }

        };

    }();

})(window.jQuery);