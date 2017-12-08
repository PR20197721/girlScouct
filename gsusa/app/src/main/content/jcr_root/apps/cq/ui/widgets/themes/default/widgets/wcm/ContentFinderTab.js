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
 * @class CQ.themes.wcm.ContentFinderTab
 * The theme-specific constants for {@link CQ.wcm.ContentFinderTab}.
 * @static
 * @singleton
 */
CQ.themes.wcm.ContentFinderTab = function() {

    return {

        /**
         * The default height of Query Boxes (defaults to 75).
         * @static
         * @final
         * @type Number
         */
        QUERYBOX_HEIGHT: 75,

        /**
         * The default {@link CQ.Ext.Panel#bodyStyle bodyStyle} value of Query Boxes
         * (defaults to "padding:11px 17px 6px 8px;").
         * @static
         * @final
         * @type String
         */
        QUERYBOX_BODYSTYLE: "padding:11px 17px 6px 8px;",

        /**
         * The default anchor width for form fields of Query Boxes (defaults to "100%").
         * @static
         * @final
         * @type String
         */
        QUERYBOX_ANCHOR: "100%",

        /**
         * The default {@link CQ.Ext.Component#style style} value for form fields
         * of Query Boxes (defaults to "margin-top:5px;").
         * @static
         * @final
         * @type String
         */
        QUERYBOX_FIELDS_STYLE: "margin-top:5px;",

        /**
         * The offset of thumbnails in the {@link CQ.Ext.dd.DDProxy Drag and Drop Proxy}
         * when multiple items are dragged (defaults to 6).
         * @static
         * @final
         * @type Number
         */
        DDPROXY_MULTI_OFFSET: 6,

        /**
         * The top padding of the {@link CQ.Ext.dd.DDProxy Drag and Drop Proxy}
         * (defaults to 3).
         * @static
         * @final
         * @type Number
         */
        DDPROXY_PADDING_TOP: 3,

        /**
         * The left padding of the {@link CQ.Ext.dd.DDProxy Drag and Drop Proxy} (defaults to 20).
         * @static
         * @final
         * @type Number
         */
        DDPROXY_PADDING_LEFT: 20,

        /**
         * The width of thumbnails inside the {@link CQ.Ext.dd.DDProxy Drag and Drop Proxy}
         * (defaults to 140).
         * @static
         * @final
         * @type Number
         */
        DDPROXY_WIDTH: 140,

        /**
         * The height of thumbnails inside the {@link CQ.Ext.dd.DDProxy Drag and Drop Proxy}
         * (defaults to 100).
         * @static
         * @final
         * @type Number
         */
        DDPROXY_HEIGHT: 100
    };

}();