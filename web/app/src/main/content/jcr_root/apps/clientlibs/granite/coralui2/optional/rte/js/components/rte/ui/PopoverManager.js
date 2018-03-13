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

    CUI.rte.ui.cui.PopoverManager = new Class({

        toString: "ToolbarImpl",

        $container: null,

        $popover: null,

        $popoverTrigger: null,

        triggerToElements: null,

        tbType: null,

        _popoverXOffset: 0,

        _popoverBorder: 0,

        _popoverStyleSheet: null,

        autoHideFn: undefined,

        _isHiding: false,

        useFixedInlineToolbar: false,

        construct: function($container, tbType, useFixedInlineToolbar) {
            this.$popover = null;
            this.$popoverTrigger = null;
            this.$container = $container;
            this.triggerToElements = [ ];
            this.tbType = tbType;
            this.useFixedInlineToolbar = useFixedInlineToolbar;
        },

        _calcArrowTop: function(pos, toolbarOffs, arrowSize) {
            toolbarOffs = toolbarOffs || this.$toolbar.offset();
            arrowSize = arrowSize || this.getArrowHeight();
            var arrowTopOffs = this._popoverBorder;
            var isBottom = false;
            if (pos) {
                isBottom = (pos == "down");
            } else {
                isBottom = (toolbarOffs.top > this.$popover.outerHeight());
            }
            if (isBottom) {
                arrowTopOffs += this.$popover.innerHeight();
            } else {
                arrowTopOffs = -(this._popoverBorder + arrowSize);
            }
            return arrowTopOffs;
        },

        /**
         * Calculates the height of the "arrow" of a popup.
         * @return {Number} The height of the "arrow"
         * @private
         */
        getArrowHeight: function() {
            var $p = this.$popover;
            if (!$p) {
                return 0;
            }
            var width = $p.outerWidth() - $p.width();
            width -= parseInt($p.css("padding-left"));
            width -= parseInt($p.css("padding-right"));
            return Math.round(width);
        },

        /**
         * Sets the position of the popover and its arrow.
         * @param {{top:Number}, {left:Number}, {arrow:String}} pos The popover position;
         *        valid values for arrow: bottom, top
         */
        setPosition: function(pos) {
            if (this.$popover) {
                this.$popover.offset(pos);
                var $arrow = this.$popover.find('.coral-Popover-arrow');
                $arrow.removeClass(
                        "coral-Popover-arrow--up coral-Popover-arrow--right "
                        + "coral-Popover-arrow--down coral-Popover-arrow--left")
                    .addClass('coral-Popover-arrow--' + pos["arrow"]);
                $arrow.css({
                    top: this._calcArrowTop(pos["arrow"])
                });
            }
        },

        /**
         * Calculates the height of the current popover.
         * @return {{height: Number, arrowHeight: Number}} The total height height and the
         *         height of the "arrow" of the popover; both values are 0 if no popover is
         *         currently shown
         */
        calc: function() {
            var $p = this.$popover;
            if (!$p) {
                return {
                    "width": 0,
                    "height": 0,
                    "arrowHeight": 0,
                    "padX": 0,
                    "padY": 0
                };
            }
            // arrow height calculation taken from CUI.Popover
            var arrowHeight = this.getArrowHeight();
            var padX = $p.outerWidth() - $p.width();
            var padY = $p.outerHeight() - $p.height();
            return {
                "width": $p.outerWidth(),
                "height": $p.outerHeight() + arrowHeight,
                "arrowHeight": arrowHeight,
                "xOffset": this._popoverXOffset,
                "padX": padX,
                "padY": padY
            };
        },

        addTriggerToElement: function($trigger, $element) {
            var trigger = $trigger[0];
            var element = $element[0];
            var triggerElements = this.getElementsForTrigger($trigger);
            if (!triggerElements) {
                triggerElements = {
                    "trigger": trigger,
                    "elements": [ ]
                };
                this.triggerToElements.push(triggerElements);
            }
            var elements = triggerElements.elements;
            if (!CUI.rte.Common.arrayContains(elements, element)) {
                elements.push(element);
            }
        },

        getElementsForTrigger: function($trigger) {
            var trigger = $trigger[0];
            var triggerCnt = this.triggerToElements.length;
            for (var t = 0; t < triggerCnt; t++) {
                if (this.triggerToElements[t].trigger === trigger) {
                    return this.triggerToElements[t];
                }
            }
            return undefined;
        },

        getTriggerForElement: function($element) {
            var element = $element[0];
            var triggerCnt = this.triggerToElements.length;
            for (var t = 0; t < triggerCnt; t++) {
                var triggerElements = this.triggerToElements[t];
                var elements = triggerElements.elements;
                var elementCnt = elements.length;
                for (var e = 0; e < elementCnt; e++) {
                    if (elements[e] === element) {
                        return $(triggerElements.trigger);
                    }
                }
            }
            return undefined;
        },

        isShown: function($popover) {
            if (!$popover) {
                return !!this.$popover;
            }
            return (this.$popover === $popover);
        },

        isTriggeredBy: function($trigger) {
            return this.$popoverTrigger && ($trigger[0] === this.$popoverTrigger[0]);
        },

        use: function(ref, $trigger, $toolbar, autoHideFn) {
            this.autoHideFn = autoHideFn;
            this.$toolbar = $toolbar;
            this.$popoverTrigger = $trigger;
            this.$popoverTrigger.addClass("is-triggered");
            if (ref.jquery) {
                this.$popover = ref;
            } else {
                this.$popover = CUI.rte.UIUtils.getPopover(ref, this.tbType,
                        this.$container);
            }
            if (this.$popover.length) {
                if (!this.useFixedInlineToolbar && !this.editorKernel.getToolbar().isSticky()) {
                    // calculate & set "arrow" position
                    var triggerOffs = $trigger.offset();
                    var triggerCenter = ($trigger.width() / 2);
                    var toolbarOffs = $toolbar.offset();
                    var triggerDX = triggerOffs.left - toolbarOffs.left;
                    var arrowSize = this.getArrowHeight();
                    var popoverWidth = this.$popover.outerWidth();
                    var pageWidth = $("body").outerWidth();
                    this._popoverBorder =
                        (this.$popover.outerHeight() - this.$popover.innerHeight()) / 2;
                    var popoverX =
                        Math.round(triggerDX + triggerCenter - (popoverWidth / 2))
                        + this._popoverBorder;
                    if (popoverX < 0) {
                        popoverX = 0;
                    }
                    if (popoverX + toolbarOffs.left + popoverWidth > pageWidth) {
                        popoverX = pageWidth - popoverWidth - toolbarOffs.left;
                    }
                    this._popoverXOffset = popoverX;
                    var arrowLeftOffs = Math.round(triggerCenter + triggerDX - arrowSize) + 2 -
                        popoverX - this._popoverBorder;
                    // when showing the popover, the element will get focused out of screen,
                    // so we save the scroll position to force it afterwards
                    var scrollParent = this.$popover.parents().filter(function () {
                        return $(this).css('overflow-y').match(/(auto|scroll)/);
                    }).eq(0);
                    var scrollPos = scrollParent.scrollTop();
                    // must be shown before calculating positions, as jQuery will miscalculate
                    // position:absolute otherwise
                }
                this.$popover.popover({
                    pointAt: (this.useFixedInlineToolbar || this.editorKernel.getToolbar().isSticky() ? $trigger : null),
                    preventAutoHide: true
                }).popover("show");
                if (this.useFixedInlineToolbar) {
                    if (this.$popover.css('left').charAt(0) === '-') {
                        this.$popover.css({left: 0});
                    }
                }
                this.$popover.find(".coral-Popover-arrow").css({
                    top: this._calcArrowTop(undefined, toolbarOffs, arrowSize),
                    left: arrowLeftOffs
                });
               // Re-apply scroll position to avoid jumping around
                if (!this.useFixedInlineToolbar && !this.editorKernel.getToolbar().isSticky()) {
                    scrollParent.scrollTop(scrollPos);
                }
            } else {
                this.$popover = null;
            }
        },

        hide: function(isAutoHide) {
            // break if we are already in the process of hiding the popover
            if (this._isHiding) {
                return false;
            }
            this._isHiding = true;
            if (this.$popoverTrigger) {
                this.$popoverTrigger.removeClass("is-triggered");
                this.$popoverTrigger = null;
            }
            var mustHide = !!this.$popover;
            if (mustHide) {
                if (this.autoHideFn && isAutoHide) {
                    this.autoHideFn();
                }
                this._popoverStyleSheet = null;
                this._popoverXOffset = 0;
                this.$popover.popover("hide");
                this.$popover = null;
            }
            this.autoHideFn = undefined;
            this._isHiding = false;
            return mustHide;
        },

        notifyEditStart: function(editorKernel) {
            this.editorKernel = editorKernel;
        }

    });

})(window.jQuery);
