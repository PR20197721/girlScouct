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
 * @class CQ.themes.ImageMap
 * The theme-specific constants for {@link CQ.form.ImageMap}.
 * @private
 * @static
 * @singleton
 */
CQ.themes.ImageMap = function() {

    return {

        // constants used when tools are internalized --------------------------------------

        /**
         * The width of the tool selector (defaults to 150)
         * @static
         * @final
         * @type Number
         */
        TOOL_SELECTOR_WIDTH: 150,

        /**
         * The height of the area edior (defaults to 110
         * @static
         * @final
         * @type Number
         */
        AREA_EDITOR_HEIGHT: 110,

        /**
         * "Fill" color (canvas color format; defaults to "rgba(255, 255, 255, 0.2)")
         * @static
         * @final
         * @type String
         */
        FILL_COLOR: "rgba(255, 255, 255, 0.2)",

        /**
         * "Shadow" color (canvas color format; defaults to "rgba(255, 255, 255, 0.5)")
         * @static
         * @final
         * @type String
         */
        SHADOW_COLOR: "rgba(255, 255, 255, 0.5)",

        /**
         * Basic color (canvas color format; defaults to "rgba(0, 0, 0, 1.0)")
         * @static
         * @final
         * @type String
         */
        BASIC_SHAPE_COLOR: "rgba(0, 0, 0, 1.0)",

        /**
         * Rollover color (canvas color format; defaults to "rgba(128, 128, 192, 1.0)")
         * @static
         * @final
         * @type String
         */
        ROLLOVER_COLOR: "rgba(128, 128, 192, 1.0)",

        /**
         * Selection color (canvas color format; defaults to "rgba(192, 0, 0, 1.0)")
         * @static
         * @final
         * @type String
         */
        SELECTED_COLOR: "rgba(192, 0, 0, 1.0)",

        /**
         * "Handle" color (canvas color format; defaults to "rgba(128, 64, 128, 1.0)")
         * @static
         * @final
         * @type String
         */
        HANDLE_COLOR: "rgba(128, 64, 128, 1.0)",

        /**
         * "Handle" color when rolled over (canvas color format; defaults to
         * "rgba(0, 0, 255, 1.0)")
         * @static
         * @final
         * @type String
         */
        HANDLE_ROLLOVER_COLOR: "rgba(0, 0, 255, 1.0)",

        /**
         * "Handle" color when selected (canvas color format; defaults to
         * "rgba(192, 0, 0, 1.0)")
         * @static
         * @final
         * @type String
         */
        HANDLE_SELECTED_COLOR: "rgba(192, 0, 0, 1.0)",

        /**
         * Size of a "Handle" (defaults to 5)
         * @static
         * @final
         * @type Number
         */
        HANDLE_SIZE: 5

    };

}();

