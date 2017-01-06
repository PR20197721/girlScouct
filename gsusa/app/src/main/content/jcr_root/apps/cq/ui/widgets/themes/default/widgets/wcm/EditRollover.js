/*
 * Copyright 1997-2009 Day Management AG
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
 * @class CQ.themes.wcm.EditRollover
 * The theme-specific constants for {@link CQ.wcm.EditRollover}.
 * @static
 * @singleton
 */
CQ.themes.wcm.EditRollover = function() {

    return {

        /**
         * The number of pixels of an element's minimum height (defaults to 80).
         * If element's height is less than this number, an empty box is created to complete the difference.
         * @static
         * @final
         * @type int
         */
        ELEMENT_MIN_HEIGHT: 80,

        /**
         * The number of pixels of an element's minimum height when insert-only text is
         * not displayed (defaults to 20).
         * @static
         * @final
         * @type int
         */
        ELEMENT_NO_TEXT_MIN_HEIGHT: 20,

        /**
         * The number of pixels for the width of the highlight box (defaults to 10).
         * @static
         * @final
         * @type int
         */
        HIGHLIGHT_WIDTH: 10,

        /**
         * The number of pixels between the edit rollover component and the highlight box (defaults to 2).
         * @static
         * @final
         * @type int
         */
        HIGHLIGHT_PADDING: 2

    }
}();