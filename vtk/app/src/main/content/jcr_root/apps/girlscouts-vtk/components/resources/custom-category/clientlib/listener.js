(function (document, $, ns) {
    "use strict";
    var myObserver;


    function mutationHandler(mutationRecords) {
        mutationRecords.forEach(function (mutation) {
            if ("childList" == mutation.type) {
                var $addedNodes = $(mutation.addedNodes[0]);
                if ($addedNodes.is("coral-multifield-item")) {
                    $addedNodes.find("input[type='hidden'][name='id']").each(function (index, item) {
                        setId(item);
                    });
                }
            }
        });
    }

    function setId(el) {
        var $el = $(el);
        var id = $el.val();
        if (id == null || id == "") {
            var id = "vtkrtid" + (new Date()).getTime() + Math.floor(Math.random() * Math.floor(999));
            $(el).val(id);
        }
    }

})(document, Granite.$, Granite.author);
$.validator.register({
    selector: ".coral-Form-field[name='uri']",
    validate: function (el) {
        var field,
            value;

        var multiFieldItem = el.closest("coral-multifield-item-content");
        var type = multiFieldItem.find("coral-select[name='type']")[0].value;
        field = el.find("input");
        value = el.val();

        if ((type == 'pdf' || type == 'link' || type == 'video' || type == 'download') && (value == null || value.trim() == "")) {
            return Granite.I18n.get('Please fill out this field for ' + type + ' type.');
        }
    },
    show: function (el, message) {
        var fieldErrorEl,
            field,
            error,
            arrow;

        fieldErrorEl = $("<span class='coral-Form-fielderror coral-Icon coral-Icon--alert coral-Icon--sizeS' data-init='quicktip' data-quicktip-type='error' />");
        field = el.closest(".coral-Form-field");

        field.attr("aria-invalid", "true")
            .toggleClass("is-invalid", true);

        field.nextAll(".coral-Form-fieldinfo")
            .addClass("u-coral-screenReaderOnly");

        error = field.nextAll(".coral-Form-fielderror");

        if (error.length === 0) {
            arrow = field.closest("form").hasClass("coral-Form--vertical") ? "right" : "top";

            fieldErrorEl
                .attr("data-quicktip-arrow", arrow)
                .attr("data-quicktip-content", message)
                .insertAfter(field);
        } else {
            error.data("quicktipContent", message);
        }
    },
    clear: function (el) {
        var field = el.closest(".coral-Form-field");

        field.removeAttr("aria-invalid").removeClass("is-invalid");

        field.nextAll(".coral-Form-fielderror").tooltip("hide").remove();
        field.nextAll(".coral-Form-fieldinfo").removeClass("u-coral-screenReaderOnly");
    }
});