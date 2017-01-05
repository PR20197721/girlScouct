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
 * @class CQ.themes.wcm.EditBase
 * The theme-specific constants for {@link CQ.wcm.EditBase}.
 * @static
 * @singleton
 */
CQ.themes.wcm.EditBase = function() {

    return {
        /**
         * The number of pixels between an edit component and the appearing target line
         * during drag&drop (defaults to 5).
         * @static
         * @final
         * @type int
         */
        TARGETLINE_DISTANCE: 5,

        /**
         * The number of pixels for the height of the appearing ghost
         * during drag&drop (defaults to 100).
         * @static
         * @final
         * @type int
         */
        TARGETGHOST_HEIGHT: 100,

        /**
         * The min number of pixels for the height of the appearing ghost
         * during drag&drop (defaults to 50).
         * @static
         * @final
         * @type int
         */
        TARGETGHOST_MINHEIGHT: 50,

        /**
         * The max number of pixels for the height of the appearing ghost
         * during drag&drop (defaults to 300).
         * @static
         * @final
         * @type int
         */
        TARGETGHOST_MAXHEIGHT: 300,

        /**
         * The number of pixels added before and after the appearing ghost
         * during drag&drop (defaults to 5).
         * @static
         * @final
         * @type int
         */
        TARGETGHOST_PADDING: 5,

        /**
         * The min height of the DD proxy (defaults to 50).
         * @static
         * @final
         * @type int
         */
        DDPROXY_MINHEIGHT: 50,

        /**
         * The max height of the DD proxy (defaults to 300).
         * @static
         * @final
         * @type int
         */
        DDPROXY_MAXHEIGHT: 300,

        /**
         * The min width of the DD proxy (defaults to 50).
         * @static
         * @final
         * @type int
         */
        DDPROXY_MINWIDTH: 50,

        /**
         * The max the width of the DD proxy (defaults to 300).
         * @static
         * @final
         * @type int
         */
        DDPROXY_MAXWIDTH: 600,

        /**
         * The number of pixels placed after the inline edit window (defaults to 10).
         *
         * @static
         * @final
         * @type int
         */
        INLINE_BOTTOM_PADDING: 10,

        /**
         * The default width of an edit dialog before it is opened in
         * inline mode instead of as a popup (defaults to 400).
         * It can be overridden through component config.
         * @static
         * @final
         * @type int
         */
        INLINE_MINIMUM_WIDTH: 400,

        /**
         * The background color of the highlighting effect used in drag & drop (defaults to "#FFCC00").
         * @static
         * @final
         * @type String
         */
        HIGHLIGHT_COLOR: "#FFCC00",

        /**
         * The options for the highlighting effect used in drag & drop. Defaults to: <pre><code>
{
    duration: 1.5
}
</code></pre>
         * @static
         * @final
         * @type Object
         */
        HIGHLIGHT_OPTIONS: {
            duration: 1.5
        },

        /**
         * The width of the insert dialog (defaults to 280).
         * @static
         * @final
         * @type int
         */
        INSERT_DIALOG_WIDTH: 280

    };

}();