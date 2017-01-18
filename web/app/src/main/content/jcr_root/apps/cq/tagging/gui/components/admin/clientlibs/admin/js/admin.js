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
(function(window, document, Granite, $) {
    "use strict";
    $(document).on("click",
        ".cq-tagging-touch-actions-edittag-activator, .cq-tagging-touch-actions-movetag-activator, .cq-tagging-touch-actions-mergetag-activator",
        function(e) {
    	e.preventDefault();
    	var $this = $(this);
    	var href = $this.data('href');
    	if (href) {    	   
            location.href = Granite.HTTP.externalize(href + $(".foundation-selections-item").data("path"));
    	}
    });    
})(window, document, Granite, Granite.$);