/*
 ADOBE CONFIDENTIAL

 Copyright 2015 Adobe Systems Incorporated
 All Rights Reserved.

 NOTICE:  All information contained herein is, and remains
 the property of Adobe Systems Incorporated and its suppliers,
 if any.  The intellectual and technical concepts contained
 herein are proprietary to Adobe Systems Incorporated and its
 suppliers and may be covered by U.S. and Foreign Patents,
 patents in process, and are protected by trade secret or copyright law.
 Dissemination of this information or reproduction of this material
 is strictly forbidden unless prior written permission is obtained
 from Adobe Systems Incorporated.
 */

/* This file contains the code used by Richtext Editor (RTE) when RTE is inside a form in Touch-UI.
   It contains the following configurations :
     * useFixedInlineToolbar : This Boolean Property defined on the RTE node (one with sling:resourceType="/libs/cq/gui/components/authoring/dialog/richtext")
                               should be set to true, when the user wants the RTE toolbar to be always visible (even on out of area clicks) and fixed instead of floating.
                               When this property is true, Richtext editing is ,by default, started on "foundation-contentloaded" event.
                               If you want to stop that, you can set the property 'customStart' to true and trigger the 'rte-start' event to start RTE editing.
                               When this property is 'true', the default behaviour, rte start on click, does not work.
     * customStart : This Boolean Property defined on the RTE node should be set to true, if the user wants to control when to start RTE
                     by triggering the event "rte-start" e.g. (Multifield Implementation)
     * rte-start : This event should be triggered on the contenteditable-div of RTE, when the user wants to start editing RTE. This works
                   only if 'customStart' has been set to true.
     * editorType : This specifies which editor to use - "text" or "table". Default is "text".
*/
(function(window, document, $) {
    "use strict";

    $(document).on("foundation-contentloaded", function(e) {

        var $container = $(e.target).hasClass(".richtext-container") ? $(e.target) : $(e.target).find(".richtext-container");

        // Copy hidden text field to RTE
        $container.each(function() {
            var $richTextDiv = $(this).find(".coral-RichText-editable");
            if (!$richTextDiv.data("rteinstance")) {
                var html = $(this).find("input[type=hidden].coral-Textfield").val();
                $richTextDiv.empty().append(unescapeHtml(html));
            }
            $container.find("[name='./textIsRich']").remove(); 
            $container.css("width", "300px").css("overflow","auto");
        });

        // Copy RTE text to hidden field
        $container.on("change", ".coral-RichText-editable", function() {
            var el = $(this).closest(".richtext-container");
            var rteInstance = el.find(".coral-RichText-editable").data("rteinstance");
            el.find("input[type=hidden].coral-Textfield").val(rteInstance.getContent());
        });

        var $richTextDiv = $(e.target).find(".richtext-container>.coral-RichText");
        $richTextDiv.each(function() {
            var $this = $(this);
            if ($this.data("customStart")) {
                $this.on("rte-start", function() {
                    var $this = $(this);
                    if ($this.data("useFixedInlineToolbar") && !$this.data("rteinstance")) {
                        var html = $(this).parent().find("input[type=hidden].coral-Textfield").val();
                        $this.empty().append(html);
                        startRTE($this);
                    }
                });
            } else {
                if ($this.data("useFixedInlineToolbar") && !$this.data("rteinstance")) {
                    startRTE($this);
                }
            }
            $this.on("editing-start", function() {
                var rte = $(this).data("rteinstance");
                rte.editorKernel.getToolbar().hide();
                $(this).closest("coral-dialog-content").on("mousedown", function(e) {
                    if (!$(e.target).closest(".richtext-container").length) {
                        if (rte.useFixedInlineToolbar) {
                            rte.editorKernel.toolbar.hide();
                        }
                    }
                });
            });
            $(this).on("click", function() {
                var self = this;
                $richTextDiv.each(function() {
                    var rte = $(this).data("rteinstance");
                    if (this !== self && rte) {
                        rte.editorKernel.getToolbar().hide();
                    }
                });
            });
        });
    });

    var startRTE = function($editable, options) {
        var editorType = $editable.data("editorType"), rtePluginsDefaults, configCallBack;
        var externalStyleSheets = $editable.data("externalStyleSheets"), index, $styleSheet, styleElements;
        if (editorType == "table") {
            rtePluginsDefaults = {
                "useColPercentage": false,
                "rtePlugins": {
                    "table": {
                        "features": "*",
                        "defaultValues": {
                            "width": "100%"
                        },
                        "editMode": CUI.rte.plugins.TablePlugin.EDITMODE_TABLE
                    }
                }
            };
            configCallBack = function(config) {
                return Granite.Util.applyDefaults({}, rtePluginsDefaults, config);
            };
        }
        var rte = new CUI.RichText({
            "element": $editable,
            "componentType": editorType,
            "preventCaretInitialize": true,
        });
        if (externalStyleSheets && externalStyleSheets.length > 0) {
            externalStyleSheets = externalStyleSheets.split(",");
            for (index = 0; index < externalStyleSheets.length; index++) {
                $styleSheet = $("head link[href='" + externalStyleSheets[index] +"']");
                if ($styleSheet.length <= 0) {
                    $styleSheet = $("<link rel=\"stylesheet\" href=\"" + externalStyleSheets[index] + "\" type=\"text/css\">");
                    $("head").append($styleSheet);
                    styleElements = $editable.data("externalStyleElements");
                    if (!styleElements) {
                        styleElements = [$styleSheet];
                    } else {
                        styleElements.push($styleSheet);
                    }
                    $editable.data("externalStyleElements", styleElements);
                }
            }
        }
        CUI.rte.ConfigUtils.loadConfigAndStartEditing(rte, $editable, configCallBack);
    };

    var rteFinish = function() {
        var $this = $(this), index;
        var rteInstance = $this.data("rteinstance"), styleElements = $this.data("externalStyleElements");
        if (rteInstance) {
            rteInstance.finish(false);
            if (styleElements) {
                for (index = 0; index < styleElements.length; index++) {
                    styleElements[index].remove();
                }
            }
            $(this).removeData("rteinstance");
        }
    };

    $(document).on("dialog-beforeclose", ".cq-Dialog", function(e) {
        if (!$(e.target).hasClass("cq-Dialog")) {
            // dialog currently throws dialog-beforeclose twice - once on form and once on coral-dialog element
            // we need to only listen to event on coral-dialog
            return;
        }
        $(this).find(".richtext-container>.coral-RichText").each(rteFinish);
    });

    $(document).on("dialog-layouttoggle-fullscreen", ".cq-Dialog", function(e) {
        var $richTextDiv = $(e.target).find(".richtext-container>.coral-RichText");
        $richTextDiv.each(function() {
            var rte = $(this).data("rteinstance");
            if (rte) {
                switchToolbar("dialogFullScreen", rte)
            }
        });
    });

    $(document).on("dialog-layouttoggle-floating", ".cq-Dialog", function(e) {
        var $richTextDiv = $(e.target).find(".richtext-container>.coral-RichText");
        $richTextDiv.each(function() {
            var rte = $(this).data("rteinstance");
            if (rte) {
                switchToolbar("inline", rte)
            }
        });
    });

    $(document).on("click", ".coral-Wizard-nextButton", function(e) {
        $(this).closest(".foundation-form").find(".richtext-container>.coral-RichText").each(rteFinish);
    });

    function switchToolbar(toolbarType, rte) {
        var ek = rte.editorKernel;
        if (!ek.hasBackgroundToolbar(toolbarType)) {
            ek.addBackgroundToolbar({
                "tbType": toolbarType,
                "isFullScreen": ek.getEditContext().getState("fullscreenadapter").isFullScreen(),
                "useFixedInlineToolbar": true
            });
        }
        ek.setActiveToolbar(toolbarType);
    };

    CUI.rte.Theme.BLANK_IMAGE = Granite.HTTP.externalize("/libs/clientlibs/granite/richtext/resources/images/blank.png");

    function unescapeHtml(safe) {
        return safe.replace(/&amp;/g, '&')
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>')
            .replace(/&quot;/g, '"')
            .replace(/&#039;/g, "'");
    }
})(window, document, Granite.$);