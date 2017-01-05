/*
 * Copyright 1997-2008 Day Management AG
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
 * @class CQ.themes.Highlight
 * The theme-specific constants for {@link CQ.Highlight}.
 * @static
 * @singleton
 */
CQ.themes.Highlight = function() {

    return {

        /**
         * The width of the highlight frame in pixels (defaults to 10).
         * @static
         * @final
         * @type Number
         */
        FRAME_WIDTH: 10,

        /**
         * The padding between the element and the highlight frame in pixels
         * (defaults to 2).
         * @static
         * @final
         * @type Number
         */
        FRAME_PADDING: 2,

        /**
         * The hexadecimal color value for the flashing effect
         * (defaults to "#ab6fc7").
         * @static
         * @final
         * @type String
         */
        FLASH_COLOR: "#ab6fc7",

        /**
         * The number of times to repeat the flashing effect (defaults to 3).
         * @static
         * @final
         * @type Number
         */
        FLASH_NUM: 3,

        /**
         * The duration of the flashing effect in seconds (defaults to 0.3).
         * @static
         * @final
         * @type Number
         */
        FLASH_DURATION: 0.3

    };

}();

