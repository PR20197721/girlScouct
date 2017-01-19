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
 * @class CQ.themes.Dialog
 * The theme-specific constants for {@link CQ.Dialog}.
 * @static
 * @singleton
 */
CQ.themes.Dialog = function() {

    return {

        /**
         * The width of dialogs in pixels (defaults to 520).
         * @static
         * @final
         * @type Number
         */
        WIDTH: 520,

        /**
         * The height of dialogs in pixels (defaults to 360).
         * @static
         * @final
         * @type Number
         */
        HEIGHT: 360,

        /**
         * The minimum width of dialogs in pixels (defaults to 360).
         * @static
         * @final
         * @type Number
         */
        MIN_WIDTH: 360,

        /**
         * The minimum height of dialogs in pixels (defaults to 200).
         * @static
         * @final
         * @type Number
         */
        MIN_HEIGHT: 200,

        /**
         * The {@link CQ.Ext.Window#bodyStyle bodyStyle} value for dialogs
         * (defaults to "padding:2px;").
         * @static
         * @final
         * @type String
         */
        BODY_STYLE: "padding:2px;",

        /**
         * The button alignment of dialogs (defaults to "right").
         * @static
         * @final
         * @type String
         */
        BUTTON_ALIGN: "right",

        /**
         * The minimum button width in pixels (defaults to 75).
         * @static
         * @final
         * @type Number
         */
        MIN_BUTTON_WIDTH: 75,

        /**
         * The {@link CQ.Ext.Window#plain plain} value for dialogs
         * (defaults to true).
         * @static
         * @final
         * @type Boolean
         */
        PLAIN: true,

        /**
         * The {@link CQ.Ext.TabPanel#plain plain} value for tab panels
         * inside dialogs (defaults to true).
         * @static
         * @final
         * @type Boolean
         */
        TABPANEL_PLAIN: true,

        /**
         * The {@link CQ.Ext.Panel#border border} value for tab panels
         * inside dialogs.
         * @static
         * @final
         * @type Boolean
         */
        TABPANEL_BORDER: true,

        /**
         * The {@link CQ.Ext.Panel#bodyStyle bodyStyle} value for the tabs
         * (defaults to "padding:15px;padding-right:0"). In order to avoid
         * horizontal scrollbars, right padding is set to 0, and the padding
         * is provided by the width of the input fields (see {@link #ANCHOR}).
         * @static
         * @final
         * @type String
         */
        TAB_BODY_STYLE: "padding:15px;padding-right:0",

        /**
         * The width of the field labels in pixels (defaults to 130).
         * @static
         * @final
         * @type Number
         */
        LABEL_WIDTH: 130,

        /**
         * The {@link CQ.Ext.form.Field#msgTarget msgTarget} value for
         * input fields (defaults to "qtip").
         * @static
         * @final
         * @type String
         */
        MSG_TARGET: "qtip",

        /**
         * The width of the input fields (defaults to "94%").
         * See {@link #TAB_BODY_STYLE}.
         * @static
         * @final
         * @type String
         */
        ANCHOR: "94%",

        /**
         * The width of checkbox fields (defaults to "90%").
         * Since overflow of checkbox selections is set to "hidden", the 
         * {@link CQ.Ext.form.Checkbox#boxLabel boxLabel}s can get truncated.
         * @static
         * @final
         * @type String
         */
        SELECT_CHECKBOX_ANCHOR: "90%",

        /**
         * The horizontal offset in pixels to use when positioning the dialog
         * in a corner (defaults to 50).
         * @static
         * @final
         * @type Number
         */
        CORNER_X: 50,

        /**
         * The vertical offset in pixels to use when positioning the dialog
         * in a corner (defaults to 30).
         * @static
         * @final
         * @type Number
         */
        CORNER_Y: 40,

        /**
         * The maximum number of characters for the header title
         * (defaults to 50). Applies to Internet Explorer 6.x only.
         * @static
         * @final
         * @type Number
         */
        IE6_TITLE_MAX_CHAR: 80,

        /**
         * The width of the edit lock button (defaults to 16).
         * @static
         * @final
         * @type Number
         */
        LOCK_WIDTH: 16

    };

}();

