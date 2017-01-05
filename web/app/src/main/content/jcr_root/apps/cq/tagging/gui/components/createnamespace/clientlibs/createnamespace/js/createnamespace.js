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
(function (document, $) {
    "use strict";
    var activator = ".cq-tagging-touch-actions-createnamespace-activator";
    var form = "#createnamespaceform";
    var createnamespaceerror = "#createnamespace-error";
    var namespacename = "#namespacename";
    var namespacetitle = "#namespacetitle";
    var submit = "#createnamespace-submit";
    var orderable = "#orderable";
    var submitPath = "/bin/tagcommand";
    $(document).on("click", activator, function (e) {
        var $form = $(form);
        reset($form);
    });


    $(document).on("click", submit, function (e) {
        e.preventDefault();
        var $submit = $(e.target);
        var $form = $submit.closest("form");
        var $namespacetitle = $form.find("#namespacetitle");
        var $namespacename = $form.find("#namespacename");

        //hide the modal
        var $modal = $submit.closest(".coral-Modal");
        $modal.modal("hide");
        //create the folder
        $namespacetitle.val($namespacetitle.val().trim());
        $namespacename.val($namespacename.val().trim());
        
        var contentPath = getContentPath();
        var promise = $.ajax({
            type: $form.prop("method"),
            url: Granite.HTTP.externalize(contentPath),
            contentType: $form.prop("enctype"),
            data: $form.serialize(),
            cache: false
        }).done(function (data, textStatus, jqXHR) {            
            //refresh foundation-content 
            var contentApi = $(".foundation-content").adaptTo("foundation-content");
            contentApi.refresh();
        }).fail(function (jqXHR, textStatus, errorThrown) {
            //show the error
            $("#createnamespace-error").modal("show");
        });

    });

    $(document).on("foundation-contentloaded", function (e) {
        //remove the previously active #createnamespaceform and #createnamespace-error modals if any  as these are fetched again as part of refresh
        var $createnamespaceform = $("body>form[id=\"createnamespaceform\"]");
        var $createnamespaceerror = $("body>div[id=\"createnamespace-error\"]");
        if ($createnamespaceform.length) {
            $createnamespaceform.remove();
        }
        if ($createnamespaceerror.length) {
            $createnamespaceerror.remove();
        }
    });

    $(document).on("keypress", namespacename, function (e) {
        if (isRestricted(e.charCode)) {
            e.preventDefault();
        }
    });

    //for IE9 case special handling of del key and backspace key as IE9 doesn't trigger input event on presseing these keys.
    $(document).on("keyup", namespacename + "," + namespacetitle, function (e) {
        var speicalkeys = [8, 46]; //8 - backspace & 46 - delete
        if (e.keyCode) {
            for (var i = 0; i < speicalkeys.length; i++) {
                if (speicalkeys[i] === e.keyCode) {
                    $(this).trigger("input");
                }
            }
        }
    });

    $(document).on("input", namespacetitle, function (e) {
        //process foldertitle and set as foldername
        var $namespacetitle = $(namespacetitle);
        var $namespacename = $(namespacename);
        var $submit = $(submit);
        var title = $namespacetitle.val().trim();
        //set the namespacename value
        if (handleRestrictedCodes(title)) {
            $namespacename.val(replaceRestrictedCodes(title.toLowerCase()).replace(/ /g, '-'));
        } else {
            $namespacename.val(title.toLowerCase().replace(/ /g, '-'));
        }
        if (title === "") {
            //if title is empty, then add the error classe to the foldername
            $namespacename.addClass("error");
            //disable the submit button
            $submit.attr('disabled', 'disabled');
        } else {
            $namespacename.removeClass("error");
            $submit.removeAttr('disabled');
        }
    });

    $(document).on("input", namespacename, function (e) {
        var $namespacename = $(namespacename);
        var $submit = $(submit);
        var name = $namespacename.val().trim();
        if (name === "") {
            $namespacename.addClass("error");
            $submit.attr('disabled', 'disabled');
            //remove the tooltip if any
            removeTooltip();
        } else {
            if (handleRestrictedCodes(name)) {
                $namespacename.val(replaceRestrictedCodes(name));
            }
            $namespacename.removeClass("error");
            $submit.removeAttr('disabled');
        }
    });

    function getContentPath() {
        return submitPath;
    }

    function replaceRestrictedCodes(name) {
        return name.replace(/[\/:\[\]*|'#]/g, "-");
    }

    /* 
     * If the 'value' contains restricted chars, then adds a tooltip to the foldername and returns true, otherwise, remove the tooltip & returns false
     * */

    function handleRestrictedCodes(value) {
        //replace the restricted codes if any and add the error tooltip
        var restrictedChars = ['/', ':', '[', ']', '*', '|', '\'', '#'];
        var $namespacename = $(namespacename);
        var rcExists = false;
        //remove the tooltip if any
        removeTooltip();

        for (var i = 0; i < restrictedChars.length; i++) {
            if (value.indexOf(restrictedChars[i]) != -1) {
                rcExists = true;
                break;
            }
        }
        if (rcExists) {
            //add the error tooltip
            var rcNotAllowedMessage = Granite.I18n.get("The name must not contain {0}, so replaced by {1}", 
                    [restrictedChars.toString().replace(/[,]/g, " "), "-"]);
            var tooltip = createTooltip(rcNotAllowedMessage);
            addTooltip(tooltip, $namespacename);
            return true;
        }
        return false;
    }

    function addTooltip(tooltip, element) {
        element.after(tooltip);
    }

    function removeTooltip() {
        $(".coral-Tooltip--error").remove();
    }

    function createTooltip(message) {
        return $("<span class=\"coral-Tooltip coral-Tooltip--error hidden coral-Tooltip--positionBelow\" data-init=\"tooltip\" data-interactive=\"false\">" + message + "</span>");
    }

    function reset($form) {
        //reset the form
        $form[0].reset();
        //remove the error classes set, if any on foldername & foldertitle
        var $namespacename = $(namespacename, $form);
        var $namespacetitle = $(namespacetitle, $form);
        $namespacename.removeClass("error");
        //disable the form submit button
        var $submit = $(submit);
        $submit.attr('disabled', 'disabled');
        //remove the tooltips
        $(".coral-Tooltip--error", $form).remove();
    }

    function isRestricted(code) {
        var restrictedCharCodes = [42, 47, 58, 91, 93, 124, 39, 35];
        if ($.inArray(code, restrictedCharCodes) > -1) {
            return true;
        } else {
            return false;
        }
    }
})(document, Granite.$);
