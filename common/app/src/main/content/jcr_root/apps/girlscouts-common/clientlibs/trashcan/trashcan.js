

var dialog = '';
let forceDeleteRefCheckBxesDiv, forceDeleteRefCheckBxesDivLenth = '';
$(document).on('change', '.ref-checkbox', function(event){
            forceDeleteRefCheckBxesDiv =$('.ref-checkbox').toArray();
           forceDeleteRefCheckBxesDivLenth = $('.ref-checkbox').toArray().length;

  if($(forceDeleteRefCheckBxesDiv[forceDeleteRefCheckBxesDivLenth-1]).is(":checked") || $(forceDeleteRefCheckBxesDiv[forceDeleteRefCheckBxesDivLenth-2]).is(":checked")){
    $('.procced-button').removeAttr("disabled")
  }
    else{
        $('.procced-button').attr('disabled', true);
    }
    
});



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
    function showErrorDialog(data) {
        var header = "";
        if (data.action == "trash") {
            header = "Error moving to trashcan";
        }else{
            header = "Error restoring from trashcan";
        }
        var errContent = data.errorCause;
        if(data.action == "restore"){
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
        if(data.action == "restore"){
            footer = "<button id=\"cancelButton\" is=\"coral-button\" variant=\"default\" coral-close=\"\"><coral-button-label>Cancel</coral-button-label></button><button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\"><coral-button-label>Ok</coral-button-label></button>";
        }

        else if(data.hasReferenceErrorType){ // only of there are any references. related errors.
            const checkbox = document.getElementById('forceDeleteRef')

            footer = "<button  id=\"cancelButton\" is=\"coral-button\" variant=\"default\" coral-close=\"\"><coral-button-label>Cancel</coral-button-label></button><button disabled  id=\"acceptButton\" class=\"procced-button\"  is=\"coral-button\" variant=\"primary\"><coral-button-label>Proceed</coral-button-label></button>";
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
            if(data.action == "restore"){
                restorePath = $('#restore-path').val();

                handleTrashcanEvent();
            }else if(data.action == "trash" && data.hasReferenceErrorType){ // for GSAWDO-61-[ALL] Move to Trashcan - Force delete references
                var forceDeleteRef = $(forceDeleteRefCheckBxesDiv[forceDeleteRefCheckBxesDivLenth-2]).is(":checked")
                var forceRepublishUpdatedPages = $(forceDeleteRefCheckBxesDiv[forceDeleteRefCheckBxesDivLenth-1]).is(":checked");
                if(!forceDeleteRef && !forceRepublishUpdatedPages){
                    errorDialog.remove();
                    return;
                }
                payloadJSON.refErrorAssertLocation=data.hasReferenceAssertLocation;
                payloadJSON.forceDeleteRef=forceDeleteRef;
                payloadJSON.forceRepublishUpdatedPages=forceRepublishUpdatedPages;
                //Doing an Ajax call to trashcan servlet with some additional parameters..
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
                        }
                        dialog.remove();
                        setTimeout(function () {
                            location.reload(true);
                        }, 3000);
                    } else {
                        dialog.remove();
                        showErrorDialog(data);
                    }
                }).error(function () {
                    $(window).adaptTo("foundation-ui").notify("Error", "Unexpected error occurred while moving item " + itemPath + " to trashcan.", "error");
                });
            }else if(data.referenceErrorTypeProcessed){// This means the references are removed successfully, lets refresh the page if someone clicks on OK, so that they can see what all got removed.
                location.reload(true);
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
        dialog ? dialog.remove() : null
        if (items.length) {
            var message = "";
            var inTrashMessage = "";
            var header = "";
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
             dialog = new Coral.Dialog().set({
                id: "trashcanDialog",
                header: {
                    innerHTML: header
                },
                content: {
                    innerHTML: message
                },
                footer: {
                    innerHTML: "<button id=\"cancelButton\" is=\"coral-button\" variant=\"default\" coral-close=\"\"><coral-button-label>Cancel</coral-button-label></button><button id=\"acceptButton\" is=\"coral-button\" variant=\"primary\"><coral-button-label>OK</coral-button-label></button>"
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
                        showErrorDialog(data);
                    }
                }).error(function () {
                    $(window).adaptTo("foundation-ui").notify("Error", "Unexpected error occurred while moving item " + itemPath + " to trashcan.", "error");
                });
            });
             
            document.body.appendChild(dialog);
           
            // console.log(getPopups)
           // $('.coral3-Dialog-wrapper').removeClass('last-pop-up')
           // $('.coral3-Dialog-wrapper').last().addClass('last-pop-up');
            dialog.show();
            const getPopups = $('.coral3-Dialog-wrapper');
            if(getPopups && getPopups.length >0){
                for(let i=0; i<=getPopups.length; i++){[1,2]
                    $(getPopups[getPopups.length-1]).addClass('last-pop-up');
                    
                }
            }
        }
        return {payloadJSON, dialog};
    }
    var payloadJSON = {};
    var url = Granite.HTTP.externalize(activator.data("href"));
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
