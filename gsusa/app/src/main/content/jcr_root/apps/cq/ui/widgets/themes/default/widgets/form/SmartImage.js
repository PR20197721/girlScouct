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
 * @class CQ.themes.SmartImage
 * The theme-specific constants for {@link CQ.form.SmartImage}.
 * @static
 * @singleton
 */
CQ.themes.SmartImage = function() {

    return {

        /**
         * Background color of the canvas. Note that you'll have to use the #rrggbb
         * notation here. It is strongly recommended to use an appropriate a properly
         * setup canvas css instead of changing this value. Defaults to null.
         * @static
         * @final
         * @type String
         */
        BACKGROUND_COLOR: null,

        /**
         * CSS style used for styling the canvas (defaults to "cq-image-canvas")
         * @static
         * @final
         * @type String
         */
        CANVAS_CLASS: "cq-image-canvas",

        /**
         * Default drawing mode (defaults to null). For valid values see
         * <a href="http://developer.mozilla.org/en/Canvas_tutorial/Compositing" target="_blank">http://developer.mozilla.org/en/Canvas_tutorial/Compositing</a>.
         * Note that this setting is ignored completely on Internet Explorer.
         * @static
         * @final
         * @type String
         */
        DEFAULT_DRAWING_MODE: null // "xor"

    };

}();
