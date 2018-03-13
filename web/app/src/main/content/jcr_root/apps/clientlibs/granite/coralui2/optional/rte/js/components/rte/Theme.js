/*************************************************************************
*
* ADOBE CONFIDENTIAL
* ___________________
*
*  Copyright 2013 Adobe Systems Incorporated
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

(function($) {

    /**
     * @class CUI.rte.Theme
     * The theme-specific constants for the RTE component.
     * @static
     * @singleton
     * @ignore
     */
    CUI.rte.Theme = function() {

        return {

            /**
             * The default height of a rich text editor component, including the toolbar(s)
             * (defaults to 210)
             * @static
             * @final
             * @type Number
             * @ignore
             */
            DEFAULT_HEIGHT: 210,

            /**
             * The default path where the required stylesheets are located (defaults to
             * "/libs/cq/widgets/themes/default/widgets/form/RichText" [5.2] resp.
             * "/libs/cq/ui/widgets/themes/default/widgets/form/RichText" [as of 5.3])
             * @static
             * @final
             * @type String
             * @ignore
             */
            DEFAULT_REQCSS_PATH: "/libs/cq/ui/rte/themes/default/internal",

            /**
             * CSS class to be used for styling an anchor (defauls to "richtext-anchor")
             * @static
             * @final
             * @type String
             * @ignore
             */
            ANCHOR_CLASS: "richtext-anchor",

            /**
             * CSS class to be used for styling a generic HTML placeholder (defaults to
             * "richtext-placeholder")
             * @static
             * @final
             * @type String
             * @ignore
             */
            PLACEHOLDER_CLASS: "richtext-placeholder",

            /**
             * CSS style to be used for styling a generic HTML placeholder if no class
             * should be used (defaults to undefined)
             * @static
             * @final
             * @type String
             * @ignore
             */
            PLACEHOLDER_STYLE: null,

            /**
             * CSS class to be used for styling a table with no actual border (defaults to
             * "richtext-forcedborder")
             * @static
             * @final
             * @type String
             * @ignore
             */
            TABLE_NOBORDER_CLASS: "richtext-forcedborder",

            /**
             * CSS class to be used for custom selections within a table (defaults to
             * "richtext-tableselection")
             * @static
             * @final
             * @type String
             * @since 5.3
             * @ignore
             */
            TABLESELECTION_CLASS: "richtext-tableselection",

            /**
             * CSS class that is added to active toolbar items
             */
            TOOLBAR_ACTIVE: "is-active",

            /**
             * CSS class that is added to disabled toolbar items
             * @ignore
             */
            TOOLBARITEM_DISABLED_CLASS: "is-disabled",

            /**
             * CSS class that is added to selected toolbar items
             * @ignore
             */
            TOOLBARITEM_SELECTED_CLASS: "is-selected",

            /**
             * Prefix for icon classes
             */
            TOOLBARITEM_ICON_PREFIX: "coral-Icon--",

            /**
             * Blank image path
             */
            BLANK_IMAGE: "../resources/images/blank.png"

        };

    }();

})(window.jQuery);