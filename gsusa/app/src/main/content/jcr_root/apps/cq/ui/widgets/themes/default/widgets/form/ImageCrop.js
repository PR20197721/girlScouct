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
 * @class CQ.themes.ImageCrop
 * The theme-specific constants for {@link CQ.form.ImageCrop}.
 * @private
 * @static
 * @singleton
 */
CQ.themes.ImageCrop = function() {

    return {

        /**
         * The width of the aspect ratio selector (defaults to 150)
         * @static
         * @final
         * @type Number
         */
        ASPECT_RATIO_WIDTH: 150,

        /**
         * The color of the cropping rectangle (canvas color format; defaults to
         * "rgba(0, 0, 0, 1.0)")
         * @static
         * @final
         * @type String
         */
        CROP_RECT_COLOR: "rgba(0, 0, 0, 1.0)",

        /**
         * Background color (canvas color format) for invalid image parts; may be null
         * (defaults to "rgba(255, 255, 255, 0.8)")
         * @static
         * @final
         * @type String
         */
        BACKGROUND_INVALIDPARTS: "rgba(255, 255, 255, 0.8)",

        /**
         * Distance of the handle to the cropping rectangle (defaults to 3)
         * @static
         * @final
         * @type Number
         */
        HANDLE_DISTANCE: 3,

        /**
         * Length of the handle in pixels (defaults to 15)
         * @static
         * @final
         * @type Number
         */
        HANDLE_LENGTH: 15,

        /**
         * "Thickness" of the handle (defaults to 2)
         * @static
         * @final
         * @type Number
         */
        HANDLE_THICKNESS: 2,

        /**
         * Rollover color for handles (canvas color format; defaults to
         * "rgba(0, 0, 255, 1.0)")
         * @static
         * @final
         * @type String
         */
        HANDLE_ROLLOVER: "rgba(0, 0, 255, 1.0)"

    };

}();

