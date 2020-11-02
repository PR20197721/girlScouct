(function (document, $) {
    "use strict";
    // download action
    $(document).on('foundation-selections-change', '.foundation-collection', function () {
        var collection = $(this);
        var trashcanActivator = "button.cq-siteadmin-admin-actions-trashcan-activator";
        var trashcanRestoreActivator = "button.cq-siteadmin-admin-actions-trashcan-restore-activator";
        var damTrashcanActivator = "button.cq-damadmin-admin-actions-trashcan-activator";
        var searchTrashcanActivator = "button.cq-searchadmin-admin-actions-trashcan-activator";
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
            var restorePath = "";
            function showErrorDialog(cause, action) {
                var header = "";
                if (action == "trash") {
                    header = "Error moving to trashcan";
                }else{
                    header = "Error restoring from trashcan";
                }
                var errContent = cause;
                if(action == "restore"){
                    var pathBrowserRoot = "/content";
                    var pathBrowserCrumb = "/content";
                    if (items.length) {
                        var item = $(items[0]);
                        var itemPath = item.data("foundation-collection-item-id");
                        if(itemPath.indexOf("/content/dam/trashcan/") != -1){
                            itemPath = itemPath.replace(/\/content\/dam\/trashcan\//g, "");
                            pathBrowserCrumb = itemPath.substring(0,itemPath.indexOf("/"));
                            pathBrowserRoot = "/content/dam/"+pathBrowserCrumb;
                        }else{
                            itemPath = itemPath.replace(/\/content\/trashcan\//g, "");
                            pathBrowserCrumb = itemPath.substring(0,itemPath.indexOf("/"));
                            pathBrowserRoot = "/content/"+pathBrowserCrumb;
                        }
                    }
                    errContent = errContent +
                        "<p><div>Please select new restore location:</div><div id=\"restore-path-selector\" class=\"coral-Form-fieldwrapper\">" +
                        "<span class=\"coral-Form-field coral-PathBrowser\" data-init=\"pathbrowser\" data-root-path=\""+pathBrowserRoot+"\" data-option-loader=\"granite.ui.pathBrowser.pages.hierarchyNotFile\" data-picker-src=\"/libs/wcm/core/content/common/pathbrowser/column.html/content?predicate=hierarchyNotFile\" data-picker-title=\"Select Path\" data-crumb-root=\"content\" data-picker-multiselect=\"false\" data-root-path-valid-selection=\"true\">" +
                        "<span class=\"coral-InputGroup coral-InputGroup--block\">" +
                        "<input id=\"restore-path\"class=\"coral-InputGroup-input coral-Textfield js-coral-pathbrowser-input\" type=\"text\" autocomplete=\"off\" value=\"\" data-validation=\"\" aria-owns=\"coral-2\" id=\"coral-3\" aria-haspopup=\"true\" aria-expanded=\"false\">" +
                        "<span class=\"coral-InputGroup-button\">" +
                        "<button class=\"coral-Button coral-Button--square js-coral-pathbrowser-button\" type=\"button\" title=\"Browse\">" +
                        "<i class=\"coral-Icon coral-Icon--sizeS coral-Icon--folderSearch\"></i>" +
                        "</button>" +
                        "</span>" +
                        "</span>" +
                        "<ul id=\"coral-2\" class=\"coral-SelectList\" role=\"listbox\" aria-hidden=\"true\" tabindex=\"-1\"></ul></span></div><p>"
                }
                var footer = "<button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\" coral-close=\"\"><coral-button-label>Ok</coral-button-label></button>";
                if(action == "restore"){
                    footer = "<button id=\"cancelButton\" is=\"coral-button\" variant=\"default\" coral-close=\"\"><coral-button-label>Cancel</coral-button-label></button><button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\"><coral-button-label>Ok</coral-button-label></button>";
                }
                var errorDialog = new Coral.Dialog().set({
                    id: "errorDialog",
                    header: {
                        innerHTML: header
                    },
                    content: {
                        innerHTML: errContent
                    },
                    footer: {
                        innerHTML: footer
                    },
                    variant: "error"
                });

                errorDialog.on('click', '#acceptButton', function () {
                    if(action == "restore"){
                        restorePath = $('#restore-path').val();
                        handleTrashcanEvent();
                    }
                    errorDialog.remove();
                });
                document.body.appendChild(errorDialog);
                errorDialog.show();
                if ($('#restore-path-selector').length) {
                    $('#restore-path-selector').trigger("foundation-contentloaded");
                }
            }

            function handleTrashcanEvent() {
                if (items.length) {
                    var message = "";
                    var inTrashMessage = "";
                    var header = "";
                    var url = Granite.HTTP.externalize(activator.data("href"));
                    var payloadJSON = {};
                    var itemsJSON = [];
		    	var isSearch= $(".cq-searchadmin-admin-actions-trashcan-activator");
                    var isInTrash = false;
                    var isTrash = false;
                    if(isSearch && isSearch.length){
                        payloadJSON.action = "trash";
                        header = "Moving to trashcan";
                        inTrashMessage = "<p>Following items are already in trashcan:</p><ol>";
                        message = "<p>Following items will be moved to trashcan:</p><ol>";
                        for(var i=0; i<items.length; i++){
                            var item = $(items[i]);
                            var itemPath = item.data("foundation-collection-item-id");
                            if(itemPath.includes("/trashcan/")){
                                itemPath = encodeURIComponent(itemPath);
                            	inTrashMessage += "<li>"+decodeURIComponent(itemPath)+"</li>";
                                isInTrash = true;
                            }else {
                                itemPath = encodeURIComponent(itemPath);
                            	message += "<li>"+decodeURIComponent(itemPath)+"</li>";
                            	itemsJSON.push({source:itemPath, target:null});
                                isTrash = true;
                            }

                        }
                        inTrashMessage += "</ol>";
                        message += "</ol>";
                        if(isInTrash && isTrash){
                            message += inTrashMessage;
                        }else if (isInTrash && !isTrash){
                            header = "Nothing to move to trashcan";
                            message = inTrashMessage;
                        }


                    } else{
                        if($(items[0]).data("foundation-collection-item-id").includes("/trashcan/")){
                        	payloadJSON.action = "restore";
                        	var item = $(items[0]);
                        	var itemPath = item.data("foundation-collection-item-id");
                        	itemPath = encodeURIComponent(itemPath);
                        	header = "Restoring from trashcan"
                        	message = "<p>Following item will be restored from trashcan:</p><ol><li>"+item.data("foundation-collection-item-id")+"</li></ol>";
                        	itemsJSON.push({source:itemPath, target:restorePath})
                    	}else{
                        	payloadJSON.action = "trash";
                        	header = "Moving to trashcan";
                        	message = "<p>Following items will be moved to trashcan:</p><ol>";
                        	for(var i=0; i<items.length; i++){
                            	var item = $(items[i]);
                            	var itemPath = item.data("foundation-collection-item-id");
                            	itemPath = encodeURIComponent(itemPath);
                            	message += "<li>"+decodeURIComponent(itemPath)+"</li>";
                            	itemsJSON.push({source:itemPath, target:null})
                        	}
                        	message += "</ol>";
                    	}
                    }

                    payloadJSON.items = itemsJSON;
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
                            url: url,
                            type: 'POST',
                            data: JSON.stringify(payloadJSON),
                        }).success(function (data) {
                            if (data.success) {
                                dialog.remove();
                                if (data.action == "trash") {
                                    $(window).adaptTo("foundation-ui").notify("Moving to trash: ", "Moving to <strong>" + data.destination_path + "</strong>", "success");
                                } else {
                                    $(window).adaptTo("foundation-ui").notify("Restoring from trash: ", "Restoring to <strong>" + data.destination_path + "</strong>", "success");
                                }
                                dialog.remove();
                                setTimeout(function () {
                                    location.reload(true);
                                }, 3000);
                            } else {
                                dialog.remove();
                                showErrorDialog(data.errorCause, data.action);
                            }
                        }).error(function () {
                            $(window).adaptTo("foundation-ui").notify("Error", "Unexpected error occurred while moving item " + itemPath + " to trashcan.", "error");
                        });
                    });
                    document.body.appendChild(dialog);
                    dialog.show();
                }
                return {payloadJSON, dialog};
            }
            var {payloadJSON, dialog} = handleTrashcanEvent();
        }
        $(trashcanActivator).unbind("click");
        $(trashcanActivator).bind("click", trashcanEventHandler);
        $(trashcanRestoreActivator).unbind("click");
        $(trashcanRestoreActivator).bind("click", trashcanEventHandler);
        $(damTrashcanActivator).unbind("click");
        $(damTrashcanActivator).bind("click", trashcanEventHandler);
        $(searchTrashcanActivator).unbind("click");
        $(searchTrashcanActivator).bind("click", trashcanEventHandler);
        $(damTrashcanRestoreActivator).unbind("click");
        $(damTrashcanRestoreActivator).bind("click", trashcanEventHandler);
    });
})(document, Granite.$);
