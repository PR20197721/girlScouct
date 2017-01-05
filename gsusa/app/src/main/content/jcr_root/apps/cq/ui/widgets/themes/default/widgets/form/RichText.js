/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2012 Adobe Systems Incorporated
*  All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
* if any.  The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
**************************************************************************/

/**
 * @class CUI.rte.Theme
 * The theme-specific constants for {@link CQ.form.RichText}.
 * @static
 * @singleton
 */
CUI.rte.Theme = function() {

    return {

        /**
         * The default height of a rich text editor component, including the toolbar(s)
         * (defaults to 210)
         * @static
         * @final
         * @type Number
         */
        DEFAULT_HEIGHT: 210,

        /**
         * The default path where the required stylesheets are located (defaults to
         * "/libs/cq/widgets/themes/default/widgets/form/RichText" [5.2] resp.
         * "/libs/cq/ui/widgets/themes/default/widgets/form/RichText" [as of 5.3])
         * @static
         * @final
         * @type String
         */
        DEFAULT_REQCSS_PATH: "/libs/cq/ui/widgets/themes/default/widgets/form/RichText",

        /**
         * CSS class to be used for styling an anchor (defauls to "cq-rte-anchor")
         * @static
         * @final
         * @type String
         */
        ANCHOR_CLASS: "cq-rte-anchor",

        /**
         * CSS class to be used for styling a table with no actual border (defaults to
         * "cq-rte-forcedborder")
         * @static
         * @final
         * @type String
         */
        TABLE_NOBORDER_CLASS: "cq-rte-forcedborder",

        /**
         * CSS class to be used for custom selections within a table (defaults to
         * "cq-rte-tableselection")
         * @static
         * @final
         * @type String
         * @since 5.3
         */
        TABLESELECTION_CLASS: "cq-rte-tableselection"

    };

}();

// map to CQ.themes.RichText for compatibility issues
CQ.themes.RichText = CUI.rte.Theme;