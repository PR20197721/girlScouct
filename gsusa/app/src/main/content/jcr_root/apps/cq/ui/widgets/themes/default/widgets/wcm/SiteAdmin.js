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
 * @class CQ.themes.wcm.SiteAdmin
 * The theme-specific constants for {@link CQ.wcm.SiteAdmin}.
 * @static
 * @singleton
 */
CQ.themes.wcm.SiteAdmin = function() {

    return {

        /**
         * The width of the tree panel in pixels (defaults to 240).
         * @static
         * @final
         * @type Number 
         */
    	TREE_WIDTH: 240,

        /**
         * The default page size in the grid (defaults to 30).
         * @static
         * @final
         * @type Number 
         */
        GRID_PAGE_SIZE: 30,

        /**
         * The default page text in the grid (defaults to "Items {0} - {1} of {2}").
         * @static
         * @final
         * @type String
         */
        GRID_PAGE_TEXT: CQ.I18n.getMessage("Items {0} - {1} of {2}", null, "paging display, e.g. Items 1 - 10 of 100")

    };
}();

