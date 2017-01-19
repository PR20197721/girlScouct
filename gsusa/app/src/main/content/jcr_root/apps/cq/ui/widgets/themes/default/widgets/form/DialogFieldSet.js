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
 * @class CQ.themes.DialogFieldSet
 * The theme-specific constants for {@link CQ.form.DialogFieldSet}.
 * @private
 * @static
 * @singleton
 */
CQ.themes.DialogFieldSet = function() {

    return {

        /**
         * The width of the field labels in pixels (defaults to 118).
         * @static
         * @final
         * @type Number
         */
        LABEL_WIDTH: 118,

        /**
         * The width of the input fields (defaults to "99%").
         * See {@link #TAB_BODY_STYLE}.
         * @static
         * @final
         * @type String
         */
        ANCHOR: "99%"

    };

}();

