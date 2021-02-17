(function (document, $, Coral) {
	var $doc = $(document);
	$doc.on('foundation-contentloaded', function(e) {
        $(".cq-dialog-dropdown-showhide").on("change",function (event, $formId) {
            switch ($(".cq-dialog-dropdown-showhide coral-select-item:selected").text()) {
              case "Send Mail":
                $(".form-identifier").val("gsmail");
                $(".form-identifier").prop('formid', 'gsmail');
                break;
              case "Store Content":
                $(".form-identifier").val("gsstore");
                break;
              case "Web To Lead":
                $(".form-identifier").val("web-to-lead");
                break;
              case "Web To Case":
                $(".form-identifier").val("web-to-case");
                break;
    
            }
        })
	});
})(document, Granite.$, Coral);