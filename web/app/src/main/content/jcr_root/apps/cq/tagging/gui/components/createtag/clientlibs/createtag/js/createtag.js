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
    var activator = ".cq-tagging-touch-actions-createtag-activator";
    var form = "#createtagform";
    var createtagerror = "#createtag-error";
    var tagname = "#tagname";
    var tagtitle = "#tagtitle";
    var submit = "#createtag-submit";
    var orderable = "#orderable";
    var submitPath = "/bin/tagcommand";
    var tagsBasePath = "/etc/tags";
    $(document).on("click", activator, function (e) {
        var $form = $(form);
        reset($form);
    });

    $(document).on("click", submit, function (e) {
        e.preventDefault();
        var $submit = $(e.target);
        var $form = $submit.closest("form");
        var $tagtitle = $form.find("#tagtitle");
        var $tagname = $form.find("#tagname");
        var $parenttagid = $form.find("[name=parentTagID]");

        //hide the modal
        var $modal = $submit.closest(".coral-Modal");
        $modal.modal("hide");
        //create the folder
        $tagtitle.val($tagtitle.val().trim());
        $tagname.val($tagname.val().trim());
        $parenttagid.val(getTagId($(".foundation-selections-item").data("path")));

        var contentPath = getContentPath();
        var promise = $.ajax({
            type: $form.prop("method"),
            url: Granite.HTTP.externalize(contentPath),
            contentType: $form.prop("enctype"),
            data: $form.serialize(),
            cache: false
        }).done(function (data, textStatus, jqXHR) {                        
            //redirect window to newly created tag
            redirect(data);
        }).fail(function (jqXHR, textStatus, errorThrown) {
            //show the error
            $("#createtag-error").modal("show");
        });
        var redirect = function (data) {
            var $data = $(data);
            var redirect = $data.find("#Location");
            window.location = window.location + "/" + $tagname.val();
        };
    });

    $(document).on("foundation-contentloaded", function (e) {
        //remove the previously active #createtagform and #createtag-error modals if any  as these are fetched again as part of refresh
        var $createtagform = $("body>form[id=\"createfolderform\"]");
        var $createtagerror = $("body>div[id=\"createfolder-error\"]");
        if ($createtagform.length) {
            $createtagform.remove();
        }
        if ($createtagerror.length) {
            $createtagerror.remove();
        }
    });

    $(document).on("keypress", tagname, function (e) {
        if (isRestricted(e.charCode)) {
            e.preventDefault();
        }
    });

    //for IE9 case special handling of del key and backspace key as IE9 doesn't trigger input event on presseing these keys.
    $(document).on("keyup", tagname + "," + tagtitle, function (e) {
        var speicalkeys = [8, 46]; //8 - backspace & 46 - delete
        if (e.keyCode) {
            for (var i = 0; i < speicalkeys.length; i++) {
                if (speicalkeys[i] === e.keyCode) {
                    $(this).trigger("input");
                }
            }
        }
    });

    $(document).on("input", tagtitle, function (e) {
        //process foldertitle and set as foldername
        var $tagtitle = $(tagtitle);
        var $tagname = $(tagname);
        var $submit = $(submit);
        var title = $tagtitle.val().trim();
        //set the namespacename value
        if (handleRestrictedCodes(title)) {
            $tagname.val(replaceRestrictedCodes(title.toLowerCase()).replace(/ /g, '-'));
        } else {
            $tagname.val(title.toLowerCase().replace(/ /g, '-'));
        }
        if (title === "") {
            //if title is empty, then add the error classe to the foldername
            $tagname.addClass("error");
            //disable the submit button
            $submit.attr('disabled', 'disabled');
        } else {
            $tagname.removeClass("error");
            $submit.removeAttr('disabled');
        }
    });

    $(document).on("input", tagname, function (e) {
        var $tagname = $(tagname);
        var $submit = $(submit);
        var name = $tagname.val().trim();
        if (name === "") {
            $tagname.addClass("error");
            $submit.attr('disabled', 'disabled');
            //remove the tooltip if any
            removeTooltip();
        } else {
            if (handleRestrictedCodes(name)) {
                $tagname.val(replaceRestrictedCodes(name));
            }
            $tagname.removeClass("error");
            $submit.removeAttr('disabled');
        }
    });

    function getContentPath() {
        return submitPath;
    }

    function replaceRestrictedCodes(name) {
        return name.replace(/[\/:\[\]*|'#]/g, "-");
    }

    function getTagId(tagPath) {
        var tagId = tagPath.substring(tagsBasePath.length + 1);

        // ensure the first path element (namespace) ends with ":"
        if (tagId.indexOf("/") > 0) {
            // replace (first) slash after namespace and end it with a slash
            tagId = tagId.replace("/", ":") + "/";
        } else {
            // add colon after namespace
            tagId = tagId + ":";
        }
        return tagId;
    }

    /* 
     * If the 'value' contains restricted chars, then adds a tooltip to the foldername and returns true, otherwise, remove the tooltip & returns false
     * */

    function handleRestrictedCodes(value) {
        //replace the restricted codes if any and add the error tooltip
        var restrictedChars = ['/', ':', '[', ']', '*', '|', '\'', '#'];
        var $tagname = $(tagname);
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
            addTooltip(tooltip, $tagname);
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
        var $tagname = $(tagname, $form);
        var $tagtitle = $(tagtitle, $form);
        $tagname.removeClass("error");
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
