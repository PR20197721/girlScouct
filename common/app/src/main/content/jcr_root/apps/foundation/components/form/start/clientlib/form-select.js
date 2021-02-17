(function (document, $, Coral) {
	var $doc = $(document);
	$doc.on('foundation-contentloaded', function(e) {
        Coral.commons.ready(function (component) {
        $(component).on("change",function (event, $formId) {
            switch ($(".cq-dialog-dropdown-showhide coral-select-item:selected").text()) {
              case "Send Mail":
                $(".form-identifier").attr('value', 'gsmail');
                $(".form-identifier").prop('formid', 'gsmail');
                break;
              case "Store Content":
                    $(".form-identifier").attr('value', 'gsstore');
                $(".form-identifier").prop('formid', 'gsstore');
                break;
              case "Web To Lead":
                    $(".form-identifier").attr('value','web-to-lead');
                $(".form-identifier").prop('formid', 'web-to-lead');
                break;
              case "Web To Case":
                    $(".form-identifier").attr('value','web-to-case');
                $(".form-identifier").prop('formid', 'web-to-case');
                break;
            }

        })
        })
	});
})(document, Granite.$, Coral);