(function (document, $) {
    "use strict";
    //download action
    $(document).on('foundation-selections-change', '.foundation-collection', function () {
        var collection = $(this);
        var trashcanActivator = "button.cq-siteadmin-admin-actions-trashcan-activator";
        var trashcanRestoreActivator = "button.cq-siteadmin-admin-actions-trashcan-restore-activator";
        var damTrashcanActivator = "button.cq-damadmin-admin-actions-trashcan-activator";
        var damTrashcanRestoreActivator = "button.cq-damadmin-admin-actions-trashcan-restore-activator";
        if (Granite.HTTP.getPath().includes("/trashcan")) {
            $(trashcanActivator).hide();
            $(damTrashcanActivator).hide();
            if(Granite.HTTP.getPath().endsWith("/trashcan")) {
                $(trashcanRestoreActivator).hide();
                $(damTrashcanRestoreActivator).hide();
            }else{
                $(trashcanRestoreActivator).show();
                $(damTrashcanRestoreActivator).show();
            }
        } else {
            $(trashcanActivator).show();
            $(damTrashcanActivator).show();
            $(trashcanRestoreActivator).hide();
            $(damTrashcanRestoreActivator).hide();
        }
        var trashcanEventHandler = function () {
            var activator = $(this);
            var items = collection.find('.foundation-selections-item');
            function showErrorDialog(cause, action) {
                dialog.remove();
                var header = "";
                if (action == "trash") {
                    header = "Error moving to trashcan";
                }else{
                    header = "Error restoring from trashcan";
                }
                var errorDialog = new Coral.Dialog().set({
                    id: "errorDialog",
                    header: {
                        innerHTML: header
                    },
                    content: {
                        innerHTML: cause
                    },
                    footer: {
                        innerHTML: "<button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\" coral-close=\"\"><coral-button-label>Ok</coral-button-label></button>"
                    },
                    variant: "error"
                });
                errorDialog.on('coral-overlay:close', function (event) {
                    setTimeout(function(){ location.reload(true); }, 3000);
                })
                document.body.appendChild(errorDialog);
                errorDialog.show();
            }
            if (items.length) {
                var item = $(items[0]);
                var itemPath = item.data("foundation-collection-item-id");
                var url = Granite.HTTP.externalize(activator.data("href")) + itemPath;
                var message = "<p>Following item will be moved to trashcan:</p>" + itemPath;
                var header = "Moving to trashcan";
                if (itemPath.includes("/trashcan/")) {
                    message = "<p>Following item will be restored from trashcan:</p>" + itemPath;
                    header = "Restoring from trashcan";
                }

                var dialog = new Coral.Dialog().set({
                    id: "trashcanDialog",
                    header: {
                        innerHTML: header
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
                    }).success(function (data) {
                        if(data.success){
                            dialog.remove();
                            if(data.action == "trash"){
                                $(window).adaptTo("foundation-ui").notify("Moving to trash: ","Moving to <strong>"+data.destination_path+"</strong>","success");
                            }else{
                                $(window).adaptTo("foundation-ui").notify("Restoring from trash: ","Restoring to <strong>"+data.destination_path+"</strong>","success");
                            }
                            setTimeout(function(){ location.reload(true); }, 3000);
                        }else{
                            showErrorDialog(data.errorCause, data.action);
                        }
                    }).error(function() {
                        $(window).adaptTo("foundation-ui").notify("Error","Unexpected error occurred while moving item "+itemPath+" to trashcan.","error");
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
        