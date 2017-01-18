/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
(function(document, $) {
    "use strict";

    $(".tag-info-non-empty-references").on("click", function (e) {
        // TODO - remove this ugly hack
        $(".js-endor-innerrail-toggle[data-target='#cq-rail-references']")
            .trigger("click", ["forceDetail"]);

    });

})(document, Granite.$);