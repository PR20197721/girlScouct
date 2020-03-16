(function(Granite, $) {
    "use strict";

    var ACTION_STATUS_KEY = "page.action.status";
    var _ILLEGAL_FILENAME_CHARS = "%/\\:*?\"[]|\n\t\r #{}^;+&";
    var wait;
    var ns = ".move-asset-wizard";
    var wizardInitialized = false;

    /**
     * @param  options                 Options for this wizard
     *         options.selectedItems   The path of the selected items to move
     * @param  $                       jQueryObject
     */
    var MoveAssetWizard = function(options, $) {
        this.selectedItems = options.selectedItems; // Paths of pages to be moved
        this.wizardElement = options.wizardElement;
        this.itemTemplate = options.itemTemplate;
        this.folderMove = options.folderMove;

        this.references = {}; // list of reference objects, grouped by item
        this.flatReferences = {}; // flat list of all reference objects
        this.referencesReady = false; // are all references loaded?
        this.moveAssets = {}; // objects of pages to be moved, for republish
        this.maxRefNo = null;
        this.totalVisibleRefs = 0;
        this.totalReferences = 0;
        this.hiddenRefs = 0;
        this.hasRenamed = false;
        this.refRendered = false;

        var contentPath = options.contentPath || "/content/dam";

        this.fromPath = contentPath; // Remember original contentPath ("from" path)
        this.destinationPath = contentPath; // Start destination selection at original path

        this.exceptPath = options.exceptPath;
        this.sourceParentPath = options.sourceParentPath;

        this.ui = $(window).adaptTo("foundation-ui");

        this.waitTicker = null;

        for (var i = 0; i < this.selectedItems.length; i++) {
            var item = this.selectedItems[i];
            // eslint-disable-next-line no-useless-escape
            var jQSelectionEscapedItem = item.replace(/(\')/g, "\\$1");
            $(".rename-item-name[data-path='" + jQSelectionEscapedItem + "']").val(item.substr(item.lastIndexOf("/") +
                1));
        }

        if (this.selectedItems.length === 1) {
            $(".foundation-wizard").adaptTo("foundation-wizard").toggleNext(true);
            $(".rename-item-title").get(0).value = options.itemTitle;
            $(".rename-item-name").get(0).value = options.itemName;
        } else {
        // $(".foundation-wizard").adaptTo("foundation-wizard").next();
        }

        var maxRef = $(".list-referencing").data("maxref");
        this.maxRefNo = maxRef ? parseInt(maxRef, 10) : null;

        var rcount = 0;
        var collectAllReferences = function() {
            if (rcount >= this.selectedItems.length) {
                this.referencesReady = true;
                this.renderReferences();
                this.refRendered = true;

                // special case where destination step is the only step in the wizard - Multiple items but
                // no references - Set Next button as Move
                if (this.selectedItems.length > 1 && this.totalReferences === 0) {
                    this.setMoveSubmitButton(this);
                }
                return;
            }

            this.collectReferences(this.selectedItems[rcount], function(data) {
                var pages = data.pages;
                this.references[this.selectedItems[rcount]] = pages;
                this.totalVisibleRefs += data.totalPages;
                this.hiddenRefs += data.hiddenPages;
                this.totalReferences = this.totalVisibleRefs + this.hiddenRefs;
                if (this.totalVisibleRefs <= this.maxRefNo) {
                    for (var i = 0; i < pages.length; i++) {
                        if (pages[i].path !== this.selectedItems[rcount]) {
                            this.flatReferences[pages[i].path] = pages[i];
                            pages[i].isFlatReference = true;
                        }
                        if (pages[i].path === this.selectedItems[rcount]) {
                            this.moveAssets[pages[i].path] = pages[i];
                        }
                    }
                }
                rcount++;
                collectAllReferences();
            }.bind(this));
        }.bind(this);

        collectAllReferences();

        // Initialize event listeners


        this.wizardElement.on("foundation-wizard-stepchange" + ns, function(e, el) {
            var step = $(el);

            var self = this;
            setTimeout(function() {
                // hide last step if there are no references
                if (step.is(".cq-damadmin-admin-moveasset-select-destination")) {
                    var id = $(".cq-damadmin-admin-moveasset-destination-container coral-columnview")
                        .data("foundation-collection-id");
                    id = id !== undefined ? id.substr(0, id.lastIndexOf("/")) : "";
                    var $currentFolderItem = Dam.Util.findElementByAttrValue("coral-columnview-item",
                        "data-foundation-collection-item-id", id);
                    $($currentFolderItem).click();

                    if (self.refRendered && self.totalReferences === 0) { // set Submit button if this is the last step
                        self.setMoveSubmitButton(self);
                    }
                } else if (step.is(".cq-damadmin-admin-moveasset-references")) {
                    self.adjustHeaderCheckbox();
                    if (this.refRendered) { // set submit button as this is always the last step
                        self.setMoveSubmitButton(self);
                    }
                } else {
                    self.setNextButton(self);
                }
            }, 200);
        }.bind(this));

        this.wizardElement.closest("form").off("submit" + ns).on("submit" + ns, function(e) {
            e.preventDefault();
            e.stopPropagation();

            var items = this.getSelectedItems();
            var toPath = this.getDestinationPath();

            var url = $(".foundation-wizard-control[data-foundation-wizard-control-action='cancel']").attr("href");
            var wizard = this;
            this.processMoveAssets(items, toPath, function(status) {
                var message = "";
                if (status === "success") {
                    message = Granite.I18n.get("Asset(s) has been moved");
                    sessionStorage.setItem(ACTION_STATUS_KEY, JSON.stringify({
                        status: status,
                        text: message
                    }));
                    location.href = Granite.HTTP.externalize("/assets.html") + this.getDestinationPath();
                } else if (status === "rename-failure") {
                // Rename failure; page *has* been moved, so we might as well exit wizard in new location:
                    message = Granite.I18n.get("Error changing Asset's title");
                    sessionStorage.setItem(ACTION_STATUS_KEY, JSON.stringify({
                        status: status,
                        text: message
                    }));
                    location.href = Granite.HTTP.externalize("/assets.html") + this.getDestinationPath();
                } else if (status === "cancelled") {
                    location.href = url;
                } else {
                // Move failure; stay in wizard
                    message = Granite.I18n.getVar(status);
                    this.ui.alert(Granite.I18n.get("Error"), message, "error");
                }
                wizard.waitTicker.clear();
            }.bind(this));
        }.bind(this));

        $(".cq-damadmin-admin-moveasset-references .list-referencing thead .select-adjust")
            .on("change.adjustall", function(event) {
                var selectAll = event.currentTarget.checked;
                $.each($(".cq-damadmin-admin-moveasset-references .list-referencing tbody tr .select-adjust"),
                    function(i, checkbox) {
                        checkbox.checked = selectAll;
                    }
                );
            }.bind(this));

        $(".cq-damadmin-admin-moveasset-references .list-referencing thead .select-republish")
            .on("change.republishall", function(event) {
                var selectAll = event.currentTarget.checked;
                $.each($(".cq-damadmin-admin-moveasset-references .list-referencing tbody tr .select-republish"),
                    function(i, checkbox) {
                        checkbox.checked = selectAll;
                    }
                );
            }.bind(this));
    };
    MoveAssetWizard.prototype.setMoveSubmitButton = function(self) {
        var destinationNext = $("coral-panel[selected] button[data-foundation-wizard-control-action='next']");
        destinationNext.text(Granite.I18n.get("Move"));
        destinationNext.off("click").on("click", function(event) {
            self.wizardElement.closest("form").trigger("submit" + ns);
        }.bind(self));
    };
    MoveAssetWizard.prototype.setNextButton = function(self) {
        var destinationNext = $("coral-panel[selected] button[data-foundation-wizard-control-action='next']");
        destinationNext.text(Granite.I18n.get("Next"));
        destinationNext.off("click");
    };
    MoveAssetWizard.prototype.adjustHeaderCheckbox = function() {
        var adjustRefs = $(".list-referencing tbody tr .select-adjust").length;
        var republishRefs = $(".list-referencing tbody tr .select-republish").length;
        var adjustRefSelected = $(".list-referencing tbody tr .select-adjust[checked]").length;
        var republishRefSelected = $(".list-referencing tbody tr .select-republish[checked]").length;

        if (adjustRefs === 0) {
            $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).disabled = true;
            $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).checked = false;
            $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).indeterminate = false;
        } else if (adjustRefs > adjustRefSelected) {
            if (adjustRefSelected === 0) {
                $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).indeterminate = false;
            } else {
                $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).indeterminate = true;
            }
            $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).checked = false;
        } else {
            $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).indeterminate = false;
            $(".cq-damadmin-admin-moveasset-references .list thead .select-adjust").get(0).checked =
                (adjustRefs === adjustRefSelected);
        }

        if (republishRefs === 0) {
            $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).disabled = true;
            $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).checked = false;
            $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).indeterminate = false;
        } else if (republishRefs > republishRefSelected) {
            if (republishRefSelected === 0) {
                $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).indeterminate = false;
            } else {
                $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).indeterminate = true;
            }
            $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).checked = false;
        } else {
            $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).indeterminate = false;
            $(".cq-damadmin-admin-moveasset-references .list thead .select-republish").get(0).checked =
                (republishRefs === republishRefSelected);
        }
    };

    /**
 * @param items    Array of paths for the items being moved
 * @param to       Path to move items to
 * @param callback Function to call when finished moving pages
 */
    MoveAssetWizard.prototype.processMoveAssets = function(items, to, callback) {
        this.itemsTodo = this.totalItems = items.length;
        this.status = null;

        window.onbeforeunload = null;

        var wizard = this;

        var newName = $(".rename-item-name").val();
        /*if (newName && (wizard.folderMove === true)) {
         newName = newName.toLowerCase();
        }*/

        var data = {
            cmd: "movePage",
            integrity: "true",
            _charset_: "utf-8",
            ":status": "browser",
            destParentPath: to
        };
        var from = [];
        var i;
        if (this.totalVisibleRefs > this.maxRefNo) {
            for (i = 0; i < items.length; i++) {
                from[i] = items[i];
            }
        } else {
            for (i = 0; i < items.length; i++) {
                from[i] = items[i];
                var references = this.getExcludedReferences(items[i]);
                var republishes = this.getExcludedRepublishes(items[i]);
                if (references.length > 0) {
                    data[i + "_adjust"] = references;
                }
                if (republishes.length > 0) {
                    data[i + "_publish"] = republishes;
                }
            }
        }
        data["retrieveAll"] = true;
        if (newName) {
            data["destName"] = newName;
        }

        data.srcPath = from;
        data.operation = "MOVE";
        if (items.length === 1) {
            data.description = Granite.I18n.get("Moving {0} to {1}", [ from, to ], "0 and 1 replaced by paths");
        }

        if (items.length > 1) {
            data.description = Granite.I18n.get("Moving {0} items to {1}", [ from.length, to ], "0=number 1=path");
        }

        if (this.sourceParentPath) {
            data.sourceParentPath = this.sourceParentPath;
            if (this.exceptPath) {
                data.exceptPath = this.exceptPath;
            }
        }

        wizard = this;
        if (this.isAsyncMove(items)) {
            this.showScheduleDialog(function(status) {
                if (status === "done") {
                    var schedultOptionNow = $(".schedule-move-options-now", ".schedule-move-options");
                    var scheduleOptionLater = $(".schedule-move-options-later", ".schedule-move-options");
                    var scheduleOptionDatePicker = $(".schedule-move-datepicker", "#aem-assets-move-schedule-dialog");
                    if (schedultOptionNow.length && schedultOptionNow[0].checked) {
                        data["" + schedultOptionNow[0].name] = schedultOptionNow[0].value;
                    } else if (scheduleOptionLater.length && scheduleOptionLater[0].checked) {
                        data["" + scheduleOptionLater[0].name] = scheduleOptionLater[0].value;
                        data["" + scheduleOptionDatePicker[0].name] = scheduleOptionDatePicker[0].value;
                    }

                    if ($(".cq-damadmin-admin-moveasset-rename").length) {
                        var $titleField = $(".rename-item-title");
                        data["destTitle"] = $titleField.get(0).value;
                    }

                    wizard.waitTicker = wizard.ui.waitTicker(Granite.I18n.get("Move Asset ..."),
                        Granite.I18n.get("Your asset is being moved."));
                    $.post(Granite.HTTP.externalize("/bin/asynccommand"), data, function(responseText, status, r) {
                        var statusCode = parseInt($(responseText).find("#Status").html(), 10);
                        var message = $(responseText).find("#Message").html();

                        if (statusCode === 200) {
                            this.status = "success";
                        } else {
                        //
                        // Error during move.
                        //
                            this.status = message;
                        }
                        callback(this.status);
                    });
                } else {
                    callback(status);
                }
            });
        } else {
            this.waitTicker = this.ui.waitTicker(Granite.I18n.get("Move Asset ..."),
                Granite.I18n.get("Your asset is being moved."));
            var endpoint = this.sourceParentPath ? this.sourceParentPath + ".bulkassets.move" : "/bin/wcmcommand";
            $.post(Granite.HTTP.externalize(endpoint), data, function(responseText, status, r) {
                var statusCode = parseInt($(responseText).find("#Status").html(), 10);
                var message = $(responseText).find("#Message").html();
                if (statusCode === 200) {
                    if ($(".cq-damadmin-admin-moveasset-rename").length) {
                        //
                        // Page moved; now change its title:
                        //
                        var destPath = encodeURI(message.replace("\n", ""));
                        var $titleField = $(".rename-item-title");
                        var data = {
                            _charset_: "utf-8"
                        };
                        data[$titleField.get(0).name] = $titleField.get(0).value;
                        Granite.$.ajax({
                            type: "POST",
                            url: destPath,
                            data: data,
                            success: function() {
                                this.status = "success";
                            },
                            error: function() {
                                this.status = "rename-failure";
                            },
                            complete: function() {
                                callback(this.status);
                            }
                        });
                    } else {
                        this.status = "success";
                        callback(this.status);
                    }
                } else {
                //
                // Error during move.
                //
                    this.status = message;
                    callback(this.status);
                }
            });
        }
    };

    MoveAssetWizard.prototype.itemDone = function(itemStatus, callback) {
        this.itemsTodo--;
        if (!this.status || itemStatus !== "success") {
            this.status = itemStatus;
        }

        if (this.itemsTodo <= 0) {
            callback(this.status);
        }
    };


    MoveAssetWizard.prototype.getDestinationPath = function() {
        if ($(".cq-damadmin-admin-moveasset-destination-container > " +
                ".foundation-advancedselect-values > input").length > 0) {
            return $(".cq-damadmin-admin-moveasset-destination-container > " +
                ".foundation-advancedselect-values > input").val();
        }
        return null;
    };


    MoveAssetWizard.prototype.getDestinationType = function() {
        if ($(".cq-damadmin-admin-moveasset-select-destination coral-columnview")[0].selectedItem) {
            return $(".cq-damadmin-admin-moveasset-select-destination coral-columnview")[0].selectedItem.dataset.type;
        }
        return null;
    };

    /**
 * AdobePatentID="P7396-US"
 */
    MoveAssetWizard.prototype.isAsyncMove = function(items) {
        var ism = false;
        var references = this.getTotalReferencesToAdjust();
        $.ajax({
            async: false,
            url: Granite.HTTP.externalize("/bin/asynccommand"),
            type: "GET",
            data: {
                "referenceCount": references,
                "assetCount": items.length,
                "operation": "MOVE",
                "optype": "CHKASYN"
            },
            success: function(resp) {
                var jsonRes = $.parseJSON(resp);
                ism = jsonRes.isasync;
            }
        });
        return ism;
    };

    MoveAssetWizard.prototype.totalReferencesCount = function() {
        if (this.totalVisibleRefs > this.maxRefNo) {
            return this.maxRefNo + "+";
        }
        return Object.keys(this.flatReferences).length + this.hiddenRefs;
    };

    MoveAssetWizard.prototype.setRenameOption = function(hasRenamed) {
        this.hasRenamed = hasRenamed;
    };

    MoveAssetWizard.prototype.getRenameOption = function() {
        return this.hasRenamed;
    };

    MoveAssetWizard.prototype.getSelectedItems = function() {
        return this.selectedItems;
    };

    function collected(wizard, $collection, item) {
        var references = [];
        var pages = (item) ? wizard.references[item] : null;
        if (pages && wizard.totalVisibleRefs <= wizard.maxRefNo) {
            $collection.each(function() {
                var p = $(this).closest("tr").data("path");
                if (item) {
                    var belongsToItem = false;
                    if (pages) {
                        for (var i = 0; i < pages.length; i++) {
                            // don't include current item, it may be selected because some other item is referencing it
                            if (pages[i].path === p && p !== item) {
                                belongsToItem = true;
                            }
                        }
                    }
                    if (!belongsToItem) {
                        return;
                    }
                }
                references.push(p);
            });
        }

        return references;
    }

    MoveAssetWizard.prototype.getExcludedReferences = function(item) {
        return collected(this, $(".list-referencing tbody tr .select-adjust:not([checked])"), item);
    };

    MoveAssetWizard.prototype.getSelectedReferences = function(item) {
        return collected(this, $(".list-referencing tbody tr .select-adjust[checked]"), item);
    };

    MoveAssetWizard.prototype.getExcludedRepublishes = function(item) {
        return collected(this, $(".list-referencing tbody tr .select-republish:not([checked])"), item);
    };

    MoveAssetWizard.prototype.getTotalReferencesToAdjust = function() {
        if (this.totalVisibleRefs > this.maxRefNo) {
            return 2147483647;
        }
        var totReferences = 0;
        var items = this.getSelectedItems();
        for (var i = 0; i < items.length; i++) {
            totReferences += this.getSelectedReferences(items[i]).length;
        }
        return totReferences + this.hiddenRefs;
    };

    MoveAssetWizard.prototype.collectReferences = function(item, callback) {
        wait = new Coral.Wait().set({
            centered: true,
            size: "L"
        });

        $.get(Granite.HTTP.externalize("/bin/wcm/heavymove"), {
            path: item,
            maxRefNo: this.maxRefNo,
            _charset_: "utf-8"
        },

        function(data) {
            if (!data.pages && data.success === false) {
            // error handling
            // TO DO GRANITE-2444 - Action eventing/reporting
            /* var message = Granite.I18n.getVar(data.message);
                 CUI.util.state.setSessionItem(ACTION_STATUS_KEY, {status: "error", text: message}); */
                location.href = Granite.HTTP.externalize("/assets.html") + item;
            }
            if (!data.pages) {
                return;
            }

            callback(data);
        }.bind(this), "json");
    };

    MoveAssetWizard.prototype.formatDate = function(time) {
        if (!time) {
            return "";
        }
        // set the moment locale according to the current selected global locale
        moment.locale(Granite.I18n.getLocale());
        var formatted = moment(time).fromNow();
        return formatted;
    };

    MoveAssetWizard.prototype.isRestricted = function(code) {
        var charVal = String.fromCharCode(code);
        if (Dam.Util.NameValidation !== undefined) {
            return this.folderMove
                ? !Dam.Util.NameValidation.isValidFolderName(charVal)
                : !Dam.Util.NameValidation.isValidFileName(charVal);
        } else {
            // this block is to support backward compatibility
            return (_ILLEGAL_FILENAME_CHARS.indexOf(charVal) > -1);
        }
    };

    MoveAssetWizard.prototype.replaceRestrictedCodes = function(name) {
        var jcrValidName = "";
        for (var i = 0, ln = name.length; i < ln; i++) {
            if (this.isRestricted(name.charCodeAt(i))) {
                jcrValidName += "-";
            } else {
                jcrValidName += name[i];
            }
        }
        return jcrValidName;
    };

    function getPageHtml(page) {
        return $("<tr>").attr("is", "coral-table-row").attr("data-path", page.path).append(
            $("<td>").attr("is", "coral-table-cell").attr("class", "select-option").append(
                // All flat references are adjustable, Fix Regression of CQ-35954, an item which is published can
                // still be adjustable because it may be referenced by another item in the move list
                (page.isFlatReference === true
                    ? $("<coral-checkbox>").attr("class", "select-adjust")
                    : $("<coral-icon>").attr("icon", "minus").attr("size", "S"))
            )
        ).append(
            $("<td>").attr("is", "coral-table-cell").attr("class", "select-option").append(
                (page.published
                    ? (page.deactivated
                        ? $("<coral-icon>").attr("icon", "minus").attr("size", "S")
                        : $("<coral-checkbox>").attr("class", "select-republish"))
                    : $("<coral-icon>").attr("icon", "minus").attr("size", "S"))
            )
        ).append(
            $("<td>").attr("is", "coral-table-cell").attr("class", "main").attr("value", page.path).append(
                $("<p>").attr("class", "full-info").append(
                    $("<span>").attr("class", "title").append(page.title)
                ).append(
                    $("<span>").attr("class", "path").append(page.path)
                )
            )
        ).append(
            $("<td>").attr("is", "coral-table-cell").attr("class", "modified").attr("value", page.lastModified).append(
                $("<span>").attr("class", "date").append(page.lastModifiedString)
            ).append(
                $("<span>").attr("class", "user").append(page.lastModifiedBy)
            )
        ).append(
            $("<td>").attr("is", "coral-table-cell").attr("class", "modified")
                .attr("value", page.replication.published).append(
                    $("<span>").attr("class", "date").append(page.publishedString)
                ).append(
                    $("<span>").attr("class", "user").append(page.publishedBy)
                )
        )[0].outerHTML;
    }

    MoveAssetWizard.prototype.renderReferences = function() {
        var getFormattedDate = this.formatDate.bind(this);

        var selector = ".list-referencing";

        $(".cq-damadmin-admin-moveasset-rename .info-details .badge").html(this.totalReferencesCount());
        if (this.totalVisibleRefs === 0) {
        // step over Adjust/Republish step
            this.wizardElement.adaptTo("foundation-wizard").remove($(".cq-damadmin-admin-moveasset-references"));
            $(".cq-damadmin-admin-moveasset-references").remove();
            $(selector).find("header").hide();
            if (wait) {
                wait.hide();
            }
            return;
        } else if (this.maxRefNo && this.totalVisibleRefs > this.maxRefNo) {
            $(selector).find("table[is='coral-table']").hide();
            if (wait) {
                wait.hide();
            }
            $(selector).find(".notice").remove();
            $(selector).append('<p class="notice"><coral-icon icon="alert" size="L"></coral-icon></p>');
            // eslint-disable-next-line max-len
            var message = Granite.I18n.get("The move you are about to make has more than {0} references and will be automatically adjusted and republished.", [ this.maxRefNo ]);
            $(selector).append('<p class="notice">' + message + "</p>");
            return;
        }

        var pages = $.extend(true, this.moveAssets, this.flatReferences);
        var table = $(selector).find("table[is='coral-table']");

        // remove existing table records
        $.each($("table[is='coral-table']").get(0).items.getAll(), function(i, row) {
            row.remove();
        });

        for (var u in pages) {
            pages[u].lastModifiedString = getFormattedDate(pages[u].lastModified);
            pages[u].publishedString = "";
            pages[u].publishedBy = "";
            if (pages[u].published) {
                var replicationStatus = getFormattedDate(pages[u].replication.published);
                if (pages[u].replication.action === "DEACTIVATE") {
                    replicationStatus = "Deactivated";
                    pages[u].deactivated = true;
                }
                pages[u].publishedString = replicationStatus;
                pages[u].publishedBy = pages[u].replication.publishedBy;
            }

            var html = getPageHtml(pages[u]);
            var $elData = $(html).filter("tr").toArray();
            $.each($elData, function(i, row) {
                table.get(0).items.add(row);
            });
        }

        // Show hidden references at last row
        if (this.hiddenRefs > 0) {
            $(selector).append($("<div class='hidden-references-message' " +
                "style='background-color: #fff;line-height: 3rem;text-align: center;'>" +
                Granite.I18n.get("{0} hidden references will be automatically adjusted.", this.hiddenRefs) + "</div>"));
        }

        $(".list-referencing tbody .select-republish, .list-referencing tbody .select-adjust")
            .on("change.actions", function(event) {
                event.stopPropagation();
                this.adjustHeaderCheckbox();
            }.bind(this));


        Coral.commons.nextFrame(function() {
            $.each($(selector).find("table[is='coral-table']"), function(i, table) {
                Coral.commons.ready(table, function(el) {
                    el.items.getAll().forEach(function(item) {
                        $(item).find(".select-adjust").prop("checked", true);
                        $(item).find(".select-republish").prop("checked", true);
                    });
                });
            });
        });
    };

    MoveAssetWizard.prototype.showScheduleDialog = function(callback) {
        var dialog = $("#aem-assets-move-schedule-dialog");
        if (dialog.length) {
            var isDone = false;
            $(".schedule-move-done", dialog).off("click").on("click", function() {
                callback("done");
                isDone = true;
            });

            dialog.on("coral-overlay:close", function(event) {
                if (isDone === false && this === event.target) {
                    callback("cancelled");
                }
            });

            dialog[0].show();
        } else {
            callback();
        }
    };

    function InitWizard() {
        // check if no item to move

        var destSrc = $(".cq-damadmin-admin-moveasset-select-destination").data("foundationWizardLazycontainerSrc");
        // no suffix, so no paths selected for move
        if (destSrc.indexOf("html?") > -1) {
            // there is no paths selected to move as suffix of this URL
            $(".move-asset-wizard").html("<p style='text-align:center'>" +
                    Granite.I18n.get("There is no content to display.") + "<br>" +
                    Granite.I18n.get("please go back and select asset/s to be moved.") + "</p>");
            return;
        }

        var configOptions = $(".move-wizard-config").data("config");

        $.extend(configOptions, {
            "wizardElement": $(".move-asset-wizard .foundation-wizard")
        });

        var wizard = new MoveAssetWizard(configOptions, Granite.$);

        $(".rename-item-name").on({
            keypress: function(event) {
                var charCode = event.which || event.keyCode;
                if (wizard.isRestricted(charCode)) {
                    event.preventDefault();
                }
            },
            input: function() {
                var name = $(this).val();
                var newName = wizard.replaceRestrictedCodes(name);
                if (name !== newName) {
                    $(this).val(newName);
                    var invalidCharset;
                    if (Dam.Util.NameValidation !== undefined) {
                        invalidCharset = (wizard.folderMove === true)
                            ? Dam.Util.NameValidation.getInvalidFolderCharSet()
                            : Dam.Util.NameValidation.getInvalidFileCharSet();
                    } else {
                        // This block is to support backward compatibility
                        invalidCharset = _ILLEGAL_FILENAME_CHARS.toString().replace(/[,]/g, " ");
                    }
                    var tooltip = new Coral.Tooltip().set({
                        variant: "error",
                        content: {
                            innerHTML: Granite.I18n.get("The name contained {0}. These characters are not allowed and were replaced by {1}", // eslint-disable-line max-len
                                [ invalidCharset, "-" ])
                        }
                    });
                    $(this).parent().append(tooltip);
                    tooltip.show();
                } else {
                    $(this).parent().find("coral-tooltip").remove();
                }
            }
        });

        $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
            selector: ".cq-damadmin-admin-moveasset-destination-container",
            validate: function(el) {
                var dstPath = wizard.getDestinationPath();
                var items = wizard.getSelectedItems();
                var move = false;
                var paths = $(".rename-item-name-path_0").length
                    ? $(".rename-item-name-path_0").val().split("/")
                    : null;
                var oldName = paths ? paths[paths.length - 1] : null;
                var newName = $(".rename-item-name").length ? $(".rename-item-name").val() : null;
                var destType = wizard.getDestinationType();

                // can't be moved inside itself
                if (destType === "asset" || dstPath === null || !wizard.referencesReady ||
                        $(".rename-item-name-path_0").val() === dstPath) {
                    move = false;
                } else {
                    if (oldName && newName && oldName !== newName) {
                        move = true;
                    } else {
                        var to = dstPath;

                        if (newName) {
                            to = dstPath + "/" + newName;
                        }

                        if (destType === "directory") {
                            move = true;
                            for (var cnt = 0; cnt < items.length; cnt++) {
                                var item = items[cnt];
                                if (!newName) {
                                    item = item.substring(0, item.lastIndexOf("/"));
                                }

                                if (items[cnt] === to || item === to) {
                                    move = false;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!move) {
                    return "The destination must be selected.";
                }
            }
        });
    }

    $(document).off("foundation-contentloaded.movepageWizard").on(
        "foundation-contentloaded.movepageWizard", function(e) {
            // This event is triggered multiple times during the lifetime of the wizard as it contains a lazy step,
            // but the initialization below is meant to be only done once so we bail out if it's not the first step
            if (!wizardInitialized) {
                InitWizard();
                wizardInitialized = true;
            }
        });
})(Granite, Granite.$);
