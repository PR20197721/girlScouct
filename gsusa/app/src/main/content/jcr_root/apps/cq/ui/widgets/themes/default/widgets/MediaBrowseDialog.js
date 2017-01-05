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
 * @class CQ.themes.MediaBrowseDialog
 * The theme-specific constants for {@link CQ.MediaBrowseDialog}.
 * @static
 * @singleton
 * @deprecated
 */
CQ.themes.MediaBrowseDialog = function() {

    return {

        /**
         * The default format of the asset's creation and modification dates
         * (defaults to "m/d/Y g:i a").
         * @static
         * @final
         * @type String
         */
    	MEDIA_DATE: 'm/d/Y g:i a',

        /**
         * The default {@link CQ.MediaBrowseDialog#height height} (defaults to 500).
         * @static
         * @final
         * @type Number
         */
        HEIGHT: 500,

        /**
         * The default {@link CQ.MediaBrowseDialog#minHeight minHeight} (defaults to 500).
         * @static
         * @final
         * @type Number
         */
        MIN_HEIGHT: 500,

        /**
         * The default {@link CQ.MediaBrowseDialog#width width} (defaults to 540).
         * @static
         * @final
         * @type Number
         */
        WIDTH: 540,

        /**
         * The default {@link CQ.MediaBrowseDialog#minWidth minWidth} (defaults to 500).
         * @static
         * @final
         * @type Number
         */
        MIN_WIDTH: 500,

        /**
         * The default {@link CQ.MediaBrowseDialog#resiable resizable} (defaults to true).
         * @static
         * @final
         * @type Boolean
         */
        RESIZABLE: true,

        /**
         * The default {@link CQ.MediaBrowseDialog#resizeHandles resizeHandles} (defaults to "n,s,e,w,se,ne,sw,nw").
         * @static
         * @final
         * @type String
         */
        RESIZE_HANDLES: "n,s,e,w,se,ne,sw,nw"

    };

}();

