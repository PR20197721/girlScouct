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
 * @class CQ.themes.Slideshow
 * @private
 * The theme-specific constants for {@link CQ.form.Slideshow}.
 * @static
 * @singleton
 */
CQ.themes.Slideshow = function() {

    return {

        /**
         * Width of label column for the image display subcomponent (defaults to 80)
         * @static
         * @final
         * @type Number
         * @private
         * @deprecated Not used at all
         */
        LABEL_WIDTH: 80,

        /**
         * Fixed height of the {@link CQ.form.SmartImage} component used for displaying the
         * slides (defaults to 200)
         * @static
         * @final
         * @type Number
         * @private
         * @deprecated Not used at all
         */
        SMARTIMAGE_HEIGHT: 200,

        /**
         * CSS class to use for each image editor of the slideshow (defaults to
         * "cq-slideshow-imgpnl")
         * @static
         * @final
         * @type String
         * @private
         * @deprecated Not used at all
         */
        IMAGEPANEL_CLASS: "cq-slideshow-imgpnl"

    };

}();