(function (document, $) {
    "use strict";
    //download action
    $(document).on('foundation-selections-change', '.foundation-collection', function () {
        var collection = $(this);
        var trashcanActivator = "button.cq-siteadmin-admin-actions-trashcan-activator";
        var trashcanRestoreActivator = "button.cq-siteadmin-admin-actions-trashcan-restore-activator";
        var damTrashcanActivator = "button.cq-damadmin-admin-actions-trashcan-activator";
        var damTrashcanRestoreActivator = "button.cq-damadmin-admin-actions-trashcan-restore-activator";
        if (Granite.HTTP.getPath().includes("/trashcan/")) {
            $(trashcanActivator).hide();
            $(damTrashcanActivator).hide();
            $(trashcanRestoreActivator).show();
            $(damTrashcanRestoreActivator).show();
        } else {
            $(trashcanActivator).show();
            $(damTrashcanActivator).show();
            $(trashcanRestoreActivator).hide();
            $(damTrashcanRestoreActivator).hide();
        }
        var trashcanEventHandler = function () {
            var activator = $(this);
            var items = collection.find('.foundation-selections-item');
            function showErrorDialog(data, xhr) {
                dialog.remove();
                var errorMessage = xhr.getResponseHeader("reason") ? xhr.getResponseHeader("reason") : "Error code: " + xhr.status;
                if(data.errorCause != null){
                    errorMessage = data.errorCause;
                }
                var errorDialog = new Coral.Dialog().set({
                    id: "errorDialog",
                    header: {
                        innerHTML: "Error moving to trashcan"
                    },
                    content: {
                        innerHTML: errorMessage
                    },
                    footer: {
                        innerHTML: "<button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\" coral-close=\"\"><coral-button-label>Ok</coral-button-label></button>"
                    },
                    variant: "error"
                });
                errorDialog.on('coral-overlay:close', function (event) {
                    location.reload(true);
                })
                document.body.appendChild(errorDialog);
                errorDialog.show();
            }
            if (items.length) {
                var item = $(items[0]);
                var itemPath = item.data("foundation-collection-item-id");
                var url = Granite.HTTP.externalize(activator.data("href")) + itemPath;
                var message = "<p>Following item will be moved to trashcan:</p>" + itemPath;
                if (itemPath.includes("/trashcan/")) {
                    message = "<p>Following item will be restored from trashcan:</p>" + itemPath;
                }
                var dialog = new Coral.Dialog().set({
                    id: "trashcanDialog",
                    header: {
                        innerHTML: "Moving to trashcan"
                    },
                    content: {
                        innerHTML: message
                    },
                    footer: {
                        innerHTML: "<button id=\"cancelButton\" is=\"coral-button\" variant=\"default\" coral-close=\"\"><coral-button-label>Cancel</coral-button-label></button><button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\"><coral-button-label>Ok</coral-button-label></button>"
                    },
                    variant: "warning"
                });
                dialog.on('click', '#acceptButton', function () {
                    $.ajax({
                        dataType: "json",
                        url: url
                    }).success(function (data, status, xhr) {
                        if(data.success){
                            dialog.remove();
                            location.reload(true);
                        }else{
                            showErrorDialog(data, xhr);
                        }
                    }).error(function (data, status, xhr) {
                        showErrorDialog(data, xhr);
                    });
                });
                document.body.appendChild(dialog);
                dialog.show();
            }
        }
        $(trashcanActivator).unbind("click");
        $(trashcanActivator).bind("click", trashcanEventHandler);
        $(trashcanRestoreActivator).unbind("click");
        $(trashcanRestoreActivator).bind("click", trashcanEventHandler);
        $(damTrashcanActivator).unbind("click");
        $(damTrashcanActivator).bind("click", trashcanEventHandler);
        $(damTrashcanRestoreActivator).unbind("click");
        $(damTrashcanRestoreActivator).bind("click", trashcanEventHandler);
    });
})(document, Granite.$);
        