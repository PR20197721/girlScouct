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
 * @class CQ.themes.wcm.Sidekick
 * The theme-specific constants for {@link CQ.wcm.Sidekick}.
 * @static
 * @singleton
 */
CQ.themes.wcm.Sidekick = function() {

    return {

        /**
         * The width of the Sidekick in pixels (defaults to 214).
         * @static
         * @final
         * @type Number
         */
        WIDTH: 214,

        /**
         * The height of the Sidekick in pixels (defaults to 406).
         * @static
         * @final
         * @type Number
         */
        HEIGHT: 406,

        /**
         * The minimum width of the Sidekick in pixels (defaults to 214).
         * @static
         * @final
         * @type Number
         */
        MIN_WIDTH: 214,

        /**
         * The minimum height of the Sidekick in pixels (defaults to 406).
         * @static
         * @final
         * @type Number
         */
        MIN_HEIGHT: 406,

        /**
         * The value for {@link CQ.wcm.Sidekick#bodyStyle bodyStyle}
         * (defaults to "padding:0;border:solid 1px #d0d0d0").
         * @static
         * @final
         * @type String
         */
        BODY_STYLE: "padding:0;border:solid 1px #d0d0d0",

        /**
         * The value for {@link CQ.wcm.Sidekick#resizable resizable}
         * (defaults to true).
         * @static
         * @final
         * @type Boolean
         */
        RESIZABLE: true,

        /**
         * The value for {@link CQ.wcm.Sidekick#resizeHandles resizeHandles}
         * (defaults to "all"). Only applies if {@link #RESIZABLE} is true.
         * @static
         * @final
         * @type String
         */
        RESIZE_HANDLES: "all",

        /**
         * The default icon for components in the Sidekick (defaults to
         * "/libs/cq/ui/widgets/themes/default/icons/16x16/components.png").
         * @static
         * @final
         * @type String
         */
        ICON_COMPONENT: "/libs/cq/ui/widgets/themes/default/icons/16x16/components.png"

    };

}();

