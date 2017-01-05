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
 * @class CQ.themes.wcm.Annotation
 * The theme-specific constants for {@link CQ.wcm.Annotation}.
 * @static
 * @singleton
 */
CQ.themes.wcm.Annotation = function() {

    return {

        /**
         * The width of the annotation.
         * @static
         * @final
         * @type Number
         */
        WIDTH: 160,

        /**
         * The height of the annotation.
         * @static
         * @final
         * @type Number
         */
        HEIGHT: 160,

        /**
         * The min width of the annotation.
         * @static
         * @final
         * @type Number
         */
        MINWIDTH: 1,

        /**
         * The min height of the annotation.
         * @static
         * @final
         * @type Number
         */
        MINHEIGHT: 1,

        /**
         * The available color schemes
         * @static
         * @final
         * @type String[]
         */
        COLORS: ["yellow", "orange", "pink", "orchid", "blue", "lime"],

        /**
         * The default color.
         * @static
         * @final
         * @type String
         */
        COLOR: "yellow",

        /**
         * The stroke colors of the available color schemes.
         * @static
         * @final
         * @type Object
         */
        STROKE_STYLES: {
            "yellow": "#eacc02",
            "orange": "#ee7000",
            "pink": "#c80265",
            "orchid": "#9013a3",
            "blue": "#14a9da",
            "lime": "#0eb901"
        },

        /**
         * The line width in sketches.
         * @static
         * @final
         * @type Number
         */
        LINE_WIDTH: 2.5,

        /**
         * The delay in milliseconds for save calls after resize, changing the color, 
         * minimize/maximize and drag and drop.
         * @static
         * @final
         * @type Number
         */
        SAVE_DELAY: 1000,

        /**
         * The delay in milliseconds to exit the sketch tool after a sketch has
         * been finished.
         * @static
         * @final
         * @type Number
         */
        EXIT_SKETCH_DELAY: 1200,

        /**
         * True to enable the sketch tool.
         * @static
         * @final
         * @type Boolean
         */
        SKETCHABLE: true

    };
}();
