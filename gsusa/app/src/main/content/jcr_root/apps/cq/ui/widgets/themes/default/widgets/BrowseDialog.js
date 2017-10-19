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
 * @class CQ.themes.BrowseDialog
 * The theme-specific constants for {@link CQ.BrowseDialog}.
 * @static
 * @singleton
 */
CQ.themes.BrowseDialog = function() {

    return {

        /**
         * The width of browse dialogs in pixels (defaults to 400).
         * @static
         * @final
         * @type Number
         */
        WIDTH: 400,

        /**
         * The height of browse dialogs in pixels (defaults to 400).
         * @static
         * @final
         * @type Number
         */
        HEIGHT: 400,

        /**
         * The minimum width of browse dialogs in pixels (defaults to 200).
         * @static
         * @final
         * @type Number
         */
        MIN_WIDTH: 200,

        /**
         * The minimum height of browse dialogs in pixels (defaults to 240).
         * @static
         * @final
         * @type Number
         */
        MIN_HEIGHT: 240,

        /**
         * The {@link CQ.Ext.Window#resizable resizable} value for
         * browse dialogs (defaults to true).
         * @static
         * @final
         * @type Boolean
         */
        RESIZABLE: true,

        /**
         * The {@link CQ.Ext.Window#resizeHandles resizeHandles} value for
         * browse dialogs (defaults to "all"). Only applies if
         * {@link #RESIZABLE} is true.
         * @static
         * @final
         * @type String
         */
        RESIZE_HANDLES: "all",

        /**
         * The CSS style for the tree panel of the browse dialog
         * (defaults to "padding:5px 0 5px 5px").
         * @static
         * @final
         * @type String
         */
        TREE_STYLE: "padding:5px 0 5px 5px",

        /**
         * The {@link CQ.Ext.tree.TreePanel#lines lines} value for
         * the tree panel of the browse dialog (defaults to false).
         * @static
         * @final
         * @type Boolean
         */
        TREE_LINES: false,

        /**
         * The {@link CQ.Ext.Panel#border border} value for the tree panel
         * of the browse dialog (defaults to false). 
         * @static
         * @final
         * @type Boolean
         */
        TREE_BORDER: false
        
    };
}();

