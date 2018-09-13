(function (document, $, ns) {
    "use strict";
    $(document).on("dialog-success", function() {		
        	window.location.reload(false);
	});
})(document, Granite.$);