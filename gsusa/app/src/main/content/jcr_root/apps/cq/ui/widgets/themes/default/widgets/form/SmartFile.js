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
 * @class CQ.themes.SmartFile
 * The theme-specific constants for {@link CQ.form.SmartFile}.
 * @static
 * @singleton
 */
CQ.themes.SmartFile = function() {

    return {

        /**
         * Path to icon resources (defaults to
         * "/etc/designs/default/images/icons")
         * @static
         * @final
         * @type String
         */
        ICON_RESOURCES: "/etc/designs/default/images/icons",

        /**
         * Map that defines icons for file extensions. The property name defines the file
         * extension; use the name of the corresponding icon file as property value.
         * Defaults to:
<pre>
 {
    "doc": "doc.gif",
    "eps": "eps.gif",
    "gif": "gif.gif",
    "jpg": "jpg.gif",
    "pdf": "pdf.gif",
    "ppt": "ppt.gif",
    "tif": "tif.gif",
    "txt": "txt.gif",
    "xls": "xls.gif",
    "zip": "zip.gif"
}
</pre>
         * @static
         * @final
         * @type Object
         */
        EXTENSION_TO_ICON: {
            "doc": "doc.gif",
            "eps": "eps.gif",
            "gif": "gif.gif",
            "jpg": "jpg.gif",
            "pdf": "pdf.gif",
            "ppt": "ppt.gif",
            "tif": "tif.gif",
            "txt": "txt.gif",
            "xls": "xls.gif",
            "zip": "zip.gif"
        },

        /**
         * Default icon, if no entry for a download's extension is found in
         * {@link #EXTENSION_TO_ICON} (defaults to "default.gif")
         * @static
         * @final
         * @type String
         */
        DEFAULT_ICON: "default.gif",

        /**
         * Processing panel: Width of icon column (defaults to 32)
         * @static
         * @final
         * @type Number
         */
        ICON_COLUMN_WIDTH: 32,

        /**
         * Processing panel: Width of buttons column (defaults to 60)
         * @static
         * @final
         * @type Number
         */
        BUTTONS_COLUMN_WIDTH: 60

    };

}();
