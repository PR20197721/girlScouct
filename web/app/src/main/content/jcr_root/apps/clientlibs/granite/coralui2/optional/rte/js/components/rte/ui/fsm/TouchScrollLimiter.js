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

    CUI.rte.ui.cui.TouchScrollLimiter = new Class({

        $window: undefined,

        $document: undefined,

        $body: undefined,

        $container: undefined,

        isActive: false,

        attach: function($container, $editor, $content) {
            var self = this;
            this.isActive = true;
            this.$window = $(window);
            this.$container = $container;
            this.$document = $(document);
            this.$body = $(document.body);
            this.$container = $("<div></div>");
            this.$container.css("overflow", "hidden");
            var isBlocked, isBlockDecided, startY;
            this.$document.on("touchstart.rteFSEdit", function(e) {
                if (!self.isActive) {
                    return;
                }
                startY = e.originalEvent.pageY;
                isBlocked = false;
                isBlockDecided = false;
            });
            this.$document.on("touchmove.rteFSEdit", function(e) {
                if (!self.isActive) {
                    return;
                }
                // prevent outer container scrolling if the editor div is on top and the
                // user swipes upwards or the editor div is on bottom and the user swipes
                // downwards - Safari will scroll the outer container instead of the editor
                // div
                if (!isBlockDecided) {
                    var deltaY = e.originalEvent.pageY - startY;
                    if (deltaY != 0) {
                        var maxScrollTop =
                                $editor[0].scrollHeight - $editor[0].clientHeight;
                        var scrollTop = $editor[0].scrollTop;
                        if (deltaY > 0) {
                            isBlocked = (scrollTop <= 0);
                        } else {
                            isBlocked = (scrollTop >= maxScrollTop);
                        }
                    }
                    isBlockDecided = true;
                }
                if (isBlocked) {
                    e.stopPropagation();
                    e.preventDefault();
                    return;
                }
                // only block if an element other than the editor is targetted
                var isEditor = false;
                var toCheck = e.target;
                while (toCheck && (toCheck.tagName.toLowerCase() !== "body")) {
                    if (toCheck === $editor[0]) {
                        isEditor = true;
                        break;
                    }
                    toCheck = toCheck.parentNode;
                }
                if (!isEditor) {
                    e.stopPropagation();
                    e.preventDefault();
                }
            });
            // ensure that the main window is always scrolled to the top, ensuring the
            // toolbar is actually visible
            this.$window.on("scroll.rteFSEdit", function(e) {
                if (self.$window.scrollTop() !== 0) {
                    self.$window.scrollTop(0);
                }
            });
            // Add a padding to the bottom that allows to edit the entire text if the screen
            // keyboard is used
            if ($content) {
                var keybHeight = CUI.rte.Common.getScreenKeyboardHeight();
                $content.css("padding-bottom", keybHeight + "px");
            }
        },

        detach: function() {
            this.$document.off("touchstart.rteFSEdit");
            this.$document.off("touchmove.rteFSEdit");
            this.$window.off("scroll.rteFSEdit");
        },

        suspend: function() {
            this.isActive = false;
        },

        reactivate: function() {
            this.isActive = true;
        }

    });

})(window.jQuery);