;(function ($, ns, channel, window, document, undefined) {
    "use strict";

    // @deprecated since AEM 6.3. Please use /libs/cq/gui/components/authoring/dialog/fileupload RT instead.

    var _fileUploadClass = "cq-FileUpload",
        _fileUploadLabelClass = "cq-FileUpload-label",
        _browseButtonClass = "cq-FileUpload-browse",
        _iconClass = "cq-FileUpload-icon",
        _clearButtonClass = "cq-FileUpload-clear",
        _thumbnailClass = "cq-FileUpload-thumbnail",
        _thumbnailImgClass = "cq-FileUpload-thumbnail-img",
        _thumbnailDropHereClass = "cq-FileUpload-thumbnail-dropHere",
        _fileNameClass = "cq-FileUpload-filename",
        _fileReferenceClass = "cq-FileUpload-filereference",
        _fileDeleteClass = "cq-FileUpload-filedelete",
        _fileMoveFromClass = "cq-FileUpload-filemovefrom",
        _imageEditorFields = ["imageMap", "imageCrop", "imageRotate"],

        _cqDropTargetClass = "cq-droptarget",

        _isActiveClass = "is-active",
        _isFilledClass = "is-filled",
        _isHoveredClass = "is-hovered",

         // these are the default parameter, but could be even another property
         // e.g. in video the property for fileReference is called 'asset'
        _fileNameParam = "fileName",
        _fileNameDataAttr = "fileNameParameter",

        _fileReferenceParam = "fileReference",
        _fileReferenceDataAttr = "filereferenceparameter",

        _lastModifiedParam = "jcr:lastModified",
        _lastModifiedByParam = "jcr:lastModifiedBy",

        _fileDeleteParam = "@Delete",
        _fileMoveFromParam = "@MoveFrom",

        // marker class for additional sling servlet fields:
        _additionalSlingParamClass = 'cq-FileUpload-param',
        _additionalImageEditorClass = 'cq-ImageEditor-param',

        _clearButtonTpl = "<span class='" + _clearButtonClass + " coral-Button coral-Button--quiet'>" + Granite.I18n.get("Clear") + "</span>",
        _thumbnailTpl = "<div class='" + _thumbnailClass + "'>" +
            "<div class='" + _thumbnailImgClass + "'></div>" +
            "<div class='" + _thumbnailDropHereClass + "'>" +
            "<i class='coral-Icon coral-Icon--image "+ _iconClass +"'></i>" +
            "<span class='"+ _fileUploadLabelClass +"'>" +

            Granite.I18n.get("Drop an asset here or <a class='coral-Link {0}'>browse</a> for a file to upload.", _browseButtonClass, "0 replace by class name, not visible string") +
            "</div>" +
            "</div>";

    var RENDITIONS_PATH = "/jcr:content/renditions",
        WEB_RENDITION_PREFIX = "cq5dam.web",
        THUMB_RENDITION_PREFIX = "cq5dam.thumbnail.3",
        TEMP_UPLOAD_SUFFIX = ".sftmp",
        THUMBNAIL_SUFFIX = ".img.png";

    /**
     * Returns the value carried by the design resource
     *
     * @param {HTMLElement} element     - The DOM element representing the fileUpload
     * @returns {boolean|null}
     */
    function getAllowUploadFromDesign(element) {
        var $dataElement = $(element).closest("form.cq-dialog");

        if ($dataElement.length < 1) {
            return;
        }

        var contentPath = $dataElement.attr("action").replace("_jcr_content", "jcr:content");
        contentPath = Granite.HTTP.internalize(contentPath);

        if (!contentPath || contentPath.length < 1) {
            return;
        }

        var editables = ns.author.editables.find(contentPath);

        if (editables && editables.length > 0) {
            return ns.author.editableHelper.getStyleProperty(editables[0], "allowUpload");
        }
    }

    /**
     *
     * @param widget                        - FileUpload widget
     * @param resourceURL                   - Path to the content resource
     * @param {boolean} [allowUpload=true]  - Should the component allow upload from the desktop
     * @constructor
     * @deprecated
     */
    ns.FileUploadField = function (widget, resourceURL, allowUpload) {
        this.widget = widget;
        this.resourceURL = resourceURL;

        // allowUpload defines if "disk file selection" is allowed or not
        var $input = this.widget.$inputContainer.find("input[type='file']");

        // Allow File upload by default unless its not explicitly disabled
        if (allowUpload == null) {
            // Configuration at the dialog resource level
            this.widget.options.allowUpload = typeof $input.data('allowupload') === 'undefined' || !!$input.data('allowupload');
        } else {
            if (typeof allowUpload === "string") {
                allowUpload = allowUpload == "true";
            }
            // Configuration provided by the constructor
            this.widget.options.allowUpload = allowUpload;
        }

        this._computeFieldNames();
        this._createMissingElements();
        this._bindEvents();

        this.widget.$element.addClass(_fileUploadClass);
        this.widget.$element.addClass(_cqDropTargetClass); // to allow interaction with dropController
        this.widget.$inputContainer.hide();

        // Used when uploading temp file, before form is submitted
        this.widget.options.uploadUrl = resourceURL;
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._appendThumbnail = function (thumbnailPath, $container) {
        var ckParam = "?:ck=" + (new Date()).getTime(); // Cache killer
        var $thumbnail = $("<img/>", {
            "alt": " ",
            "class": "cq-dd-image",
            "src": thumbnailPath + ckParam
        });

        $container.append($thumbnail);
        this.widget.$element.addClass(_isFilledClass);
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._createThumbnailFromRendition = function (imagePath, $container, prefix) {
        if (!prefix) {
            prefix = WEB_RENDITION_PREFIX;
        }
        var self = this;
        var requestPath = imagePath + RENDITIONS_PATH + ".1.json";

        $.getJSON(requestPath).then(function(renditionsData) {
            var webRenditionPath = null;
            for (var prop in renditionsData) {
                if (renditionsData.hasOwnProperty(prop)) {
                    if (prop.indexOf(prefix) === 0) {
                        webRenditionPath = imagePath + RENDITIONS_PATH + "/" + prop;
                        break; // Only one web rendition currently. Doing same as WCMRenditionPicker.java (2014-11-13)
                    }
                }
            }
            self._appendThumbnail(webRenditionPath ? webRenditionPath : imagePath, $container);

        }, function () {
            $container.append($("<p>" + "Expecting DAM asset to have renditions, but request to " + requestPath +  " failed" + "</p>"));
        });
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._refreshThumbnail = function () {
        var self = this,
            $thumbnail = self.widget.$element.find("." + _thumbnailImgClass),
            thumbnailDom;

        $thumbnail.empty();

        $.ajax({
            url: self.resourceURL + ".json",
            cache: false
        }).done(function (data) {

            var fn = self.fieldNames.fileName.substr(self.fieldNames.fileName.lastIndexOf("/")+1);
            var fr = self.fieldNames.fileReference.substr(self.fieldNames.fileReference.lastIndexOf("/")+1);
            var fileName = data[fn];
            var fileReference = data[fr];

            if (fileReference || fileName) {
                if (self._hasImageMimeType()) {
                    if (fileReference) {
                        // Image lives in DAM (=> rendition available)
                        self._createThumbnailFromRendition(fileReference, $thumbnail);
                    } else {
                        // Image has been uploaded from file system
                        self._appendThumbnail(self.resourceURL + THUMBNAIL_SUFFIX, $thumbnail);
                    }
                } else if (self._hasVideoMimeType() && fileReference) {
                    self._createThumbnailFromRendition(fileReference, $thumbnail, THUMB_RENDITION_PREFIX);
                } else {
                    thumbnailDom = $("<p>" + (fileReference || fileName) + "</p>");
                    $thumbnail.append(thumbnailDom);
                    self.widget.$element.addClass(_isFilledClass);
                }
            }
        });
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._addElements = function () {
        var $thumbnail = $(_thumbnailTpl);

        if (!this.widget.options.allowUpload) {
            $thumbnail.find("." + _fileUploadLabelClass).text(Granite.I18n.get('Drop an asset here.'));
        }
        // add drop here clue, thumbnail container and clear button
        this.widget.$element.append($thumbnail);
        this.widget.$element.find("." + _thumbnailImgClass).after($(_clearButtonTpl));

        // CQ-76228: the file input needs to be located under the browser button
        // or else the selected file won't be captured on Safari for iPhone/iPad
        $thumbnail
            .find("." + _browseButtonClass)
            .append(this.widget.$inputContainer.find('input[type="file"]'));
    };


    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._computeFieldNames = function () {
        var fileInput = this.widget.$element.find("input[type=file]"),
            name = fileInput.attr("name") || "",
            path = name.substr(0, name.lastIndexOf("/")) || "",
            pathMatch = path.match(/^\.(\/.+)$/);

        if (name.substr(-TEMP_UPLOAD_SUFFIX.length) === TEMP_UPLOAD_SUFFIX) {
            // File upload is already configured to upload to temp location.
            name = name.substr(0, name.length - TEMP_UPLOAD_SUFFIX.length);
        }

        if (path !== "") {
            path += "/";
        }
        this.path = path;

        this.fieldNames = {
          fileName       : fileInput.data(_fileNameDataAttr)      || path + _fileNameParam,
          fileReference  : fileInput.data(_fileReferenceDataAttr) || path + _fileReferenceParam,
          lastModified   : path + _lastModifiedParam,
          lastModifiedBy : path + _lastModifiedByParam,
          fileDelete     : name + _fileDeleteParam,
          fileMoveFrom   : name + _fileMoveFromParam,
          tempFileName   : name + TEMP_UPLOAD_SUFFIX,
          tempFileDelete : name + TEMP_UPLOAD_SUFFIX + _fileDeleteParam
        };

        if (pathMatch) {
            // name starts with './'
            this._tempFilePath = this.resourceURL + this.fieldNames.tempFileName.substr(1);

            // Image is stored in sub-path. We need to change resourceURL in order
            // to fetch thumbnail information from the right place
            this.resourceURL += pathMatch[1];
        }
        else {
            this._tempFilePath = this.resourceURL + "/" + this.fieldNames.tempFileName;
        }

        this._tempFilePath = this._tempFilePath
                .replace(new RegExp("^" + ns.HTTP.getContextPath()), "")
                .replace("_jcr_content", "jcr:content");

    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._addParamFields = function(params) {
        var i;

        if (typeof(params) === 'string') {
            try {
                params = JSON.parse(params);
            } catch (e) {
                // ignore erroneous parameters
            }
        }

        if (typeof(params) === 'object') {
            for (i in params) {
                if (params.hasOwnProperty(i)) {
                    this._appendHiddenField(i, _additionalSlingParamClass, params[i]);
                }
            }
        }
    };

    /**
     * @private
     */
    ns.FileUploadField.prototype._addImageEditorFields = function() {
        var self = this;
        _imageEditorFields.forEach(function (item) {
            self._appendHiddenField(self.path + item + _fileDeleteParam, _additionalImageEditorClass, "", true);
        });
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._addHiddenFields = function () {

        // append hidden sling servlet post parameters
        this._appendHiddenField(this.fieldNames.fileName, _fileNameClass, false, true);
        this._appendHiddenField(this.fieldNames.fileReference, _fileReferenceClass, false, true);
        this._appendHiddenField(this.fieldNames.fileDelete, _fileDeleteClass, false, true);
        this._appendHiddenField(this.fieldNames.fileMoveFrom, _fileMoveFromClass, this._tempFilePath, true);

        // avoid caching issues
        this._appendHiddenField(this.fieldNames.lastModified);
        this._appendHiddenField(this.fieldNames.lastModifiedBy);
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._removeAdditionalSlingParamHiddenFields = function () {
        this.widget.$element.find('input' + _additionalSlingParamClass).remove();
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._appendHiddenField = function (name, classNames, value, disabled) {
        var $hiddenField = this.widget.$element.closest('form').find("input[name=\"" + name + "\"]"),
            i;

        if ($hiddenField.length === 0) {
            $hiddenField = $("<input type='hidden'/>").attr("name", name);
            this.widget.$element.append($hiddenField);
        } else if ($hiddenField.closest(this.widget.$element).length === 0) {
            return; // Duplicate field elsewhere in the form
        }

        if (classNames !== undefined) {
            if (!$.isArray(classNames)) {
                classNames = [classNames];
            }
            for (i = 0; i < classNames.length; i++) {
                $hiddenField.addClass(classNames[i]);
            }
        }
        if (value !== undefined) {
            $hiddenField.attr("value", value);
        }
        if (disabled !== undefined) {
            $hiddenField.attr("disabled", true);
        }
    };


    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._createMissingElements = function () {


        // add clear button & thumbnail container
        this._addElements();

        // add hidden POST fields
        this._addHiddenFields();

        // add thumbnail if one file has been already uploaded
        this._refreshThumbnail();

        // create hidden imageEditorFields
        if(this._hasImageMimeType()) {
            this._addImageEditorFields();
        }
    };

    /**
     *
     * @private
     */
    ns.FileUploadField.prototype._bindEvents = function () {
        var self = this,
            lastUploadedFileName = null,
            tempFileUploaded = false;

        self.widget.$element.find("." + _browseButtonClass).on("click tap", function() {
            if (!self.widget.$element.hasClass("is-disabled")) {
                self.widget.$inputContainer.find("input[type='file']").trigger("click");
            }
        });

        // when clear button gets clicked
        self.widget.$element.find("." + _clearButtonClass).on("click tap", function (e) {
            // set hidden delete post parameter
            self.widget.$element.find("." + _fileDeleteClass).removeAttr("disabled").val("true");
            self.widget.$element.find("." + _fileReferenceClass).prop("disabled", "");
            self.widget.$element.find("." + _fileNameClass).removeAttr("disabled").val("");
            self.widget.$element.find("." + _additionalImageEditorClass).removeAttr("disabled").val("true");

            self._removeAdditionalSlingParamHiddenFields();

            self.widget.$element.find('.' + _fileMoveFromClass).attr("disabled", true);

            self.widget.$element.removeClass(_isFilledClass);

            self.widget.$element.find("." + _thumbnailImgClass).empty();

            self.widget.$element.find('input[type="file"]').val("");

            // trigger "change" event (Coral widgets "standard" event)
            self.widget.$element.trigger("change");

        });

        self.widget.$element.on("assetselected", function (event) {

            var assetPath = event.path;
            var assetParams = event.param;
            var assetThumbnail = event.thumbnail;
            var assetMimeType = event.mimetype;

            // Check if fileupload is enabled
            if (self.widget.$element.hasClass("is-disabled")) {
                return;
            }

            // check if drop is allowed
            if (!self._isMimeTypeAllowed(assetMimeType)) {
                return;
            }

            self._removeAdditionalSlingParamHiddenFields();

            self.widget.$element.find("." + _fileReferenceClass).removeAttr("disabled").val(assetPath);
            self.widget.$element.find("." + _additionalImageEditorClass).removeAttr("disabled").val("");
            self.widget.$element.find("." + _fileDeleteClass).val("false");
            self.widget.$element.find("." + _fileMoveFromClass).val("false");

            self.widget.$element.find("." + _thumbnailImgClass).empty().append(assetThumbnail);

            self._addParamFields(assetParams);

            self.widget.$element.addClass(_isFilledClass);

            self.widget.$element.trigger("change");
        });

        // when a file from the filesystem gets selected
        self.widget.$element.on("fileselected", function (event) {
            var $form, editablePath, editables, dropTarget, params;

            // Check if fileupload is enabled
            // not doing a || as it was causing a string extraction issue for translation
            if (self.widget.$element.hasClass("is-disabled")) {
                return;
            }

            if (!self.widget.options.allowUpload) {
                return;
            }

            self._removeAdditionalSlingParamHiddenFields();

            if (self._hasImageMimeType()) {
                $form = self.widget.$element.closest("form");

                if ($form) {
                    editablePath = $form.attr("action").replace("_jcr_content", "jcr:content");

                    if (ns.author && ns.author.editables) {
                        editables = ns.author.editables.find(editablePath);
                        if (editables.length > 0 && typeof editables[0].getDropTarget === "function") {
                            dropTarget = editables[0].getDropTarget("image");
                            if (dropTarget) {
                                params = dropTarget.params;
                                self._addParamFields(params);
                            }
                        }
                    }
                }
            }

            // Widget handles filename not the way, we need it.
            delete self.widget.fileNameElement;
            delete self.widget.options.fileNameParameter;

            // Upload to temp location
            if (self.widget.options.useHTML5) {
              event.item.fileName = self.fieldNames.tempFileName;

              self.widget.uploadFile(event.item);
            }
            else {
              var fileInput = self.widget.$element.find("input[type=file]"),
                  oldName = fileInput.attr("name");

              lastUploadedFileName = event.item.fileName;

              fileInput.attr("name", self.fieldNames.tempFileName);

              self.widget.uploadFile(event.item);

              fileInput.attr("name", oldName);
            }

            self.widget.$element.find("." + _additionalImageEditorClass).removeAttr("disabled").val("");
            self.widget.$element.trigger("change");
        });

        self.widget.$element.on("dropzonedragover", function (event) {
            self.widget.$element.addClass(_isHoveredClass);
        });

        self.widget.$element.on("dropzonedragleave", function (event) {
            self.widget.$element.removeClass(_isHoveredClass);
        });

        self.widget.$element.on("dropzonedrop", function (event) {
            self.widget.$element.removeClass(_isHoveredClass);
        });

        $(document)
            .on("dragover", function(event) {
                if (event.originalEvent.dataTransfer && event.originalEvent.dataTransfer.files) {
                    self.widget.$element.addClass(_isActiveClass);
                }
            })
            .on("dragleave", function() {
                self.widget.$element.removeClass(_isActiveClass);
            });

        // handle dnd events only in authoring environment
        // handle asset drop on IE9 (drop zone not listening to DnD events but this is a special case)
        if (Granite.author && !self.widget.options.useHTML5) {
            self.widget.$element.on("drop", function (event) {
                self.widget.$element.trigger("dropzonedrop");
            });
           // FIXME dropzone blinking
           self.widget.$element.on("dragover", function (event) {
               event.stopPropagation();
               event.preventDefault();
               self.widget.$element.trigger("dropzonedragover");
           });
           self.widget.$element.on("dragleave", function (event) {
               event.stopPropagation();
               event.preventDefault();
               self.widget.$element.trigger("dropzonedragleave");
           });
        }


        // Trigger success handler on non-HTML5 browsers
        self.widget.$element.on("fileuploadload", function (event) {
            var status = $(event.content).find("#Status").text(),
                item = event.item || {};

            item = event.item || {};
            item.file = item.file || {};
            item.file.type = item.file.type || "";
            item.file.name = item.file.name || lastUploadedFileName;

            lastUploadedFileName = null;

            self.widget._internalOnUploadLoad(event, item, status, event.content);
        });
        // when a file has been uploaded with success
        self.widget.$element.on("fileuploadsuccess", function (event) {
            var $thumbnail = self.widget.$element.find("." + _thumbnailImgClass),
                file = event.item.file,
                thumbnailDom;

            $thumbnail.empty();

            if (file.type.indexOf("image") !== -1) {

                self.widget.$element.addClass(_isFilledClass);

                (function ($thumbnail, reader) {
                    reader.onload = function(e) {
                        $thumbnail.append($("<img/>").attr("src", e.target.result).attr("alt", " "));
                    };

                    reader.readAsDataURL(file);
                }($thumbnail, new FileReader()));


            } else {
                thumbnailDom = $("<p>" + file.name  + "</p>");
                self.widget.$element.addClass(_isFilledClass);
                $thumbnail.append(thumbnailDom);
            }

            // set hidden post parameters
            self.widget.$element.find("." + _fileReferenceClass).removeAttr("disabled").val("");
            self.widget.$element.find("." + _fileDeleteClass).attr("disabled", "true");
            self.widget.$element.find("." + _fileMoveFromClass).removeAttr("disabled").val(self._tempFilePath);
            self.widget.$element.find("." + _fileNameClass).removeAttr("disabled").prop("value", file.name);

            tempFileUploaded = true;
        });

        channel
            .one("dialog-closed", function (e) {
                if (tempFileUploaded) {
                  self._deleteTempUpload();
                }
            });
    };

    /**
     * Sends a delete operation to remove the temporary file upload
     * @param {String} url Path to the temporary upload
     * @returns $.Deferred
     * @private
     */
    ns.FileUploadField.prototype._deleteTempUpload = function () {
        var data = {};
        data[this.fieldNames.tempFileDelete] = true;

        $.post(this.widget.options.uploadUrl, data);
    };

    ns.FileUploadField.prototype._isMimeTypeAllowed = function (mimeType)  {
        var isAllowed = false;

        this.widget.options.mimeTypes.some(function (allowedMimeType) {
            if (allowedMimeType === "*" || (new RegExp(allowedMimeType)).test(mimeType)) {
                isAllowed = true;
                return true;
            }
        });

        return isAllowed;
    };

    ns.FileUploadField.prototype._hasImageMimeType = function() {
        return this.widget.options.mimeTypes.indexOf("image") !== -1;
    };

    ns.FileUploadField.prototype._hasVideoMimeType = function() {
        return this.widget.options.mimeTypes.indexOf("video") !== -1;
    };

    // when a dialog shows up, initialize the fileupload field
    channel.on("dialog-loaded", function (event) {
        var $container = $(event.target);

        $container.find(".coral-FileUpload").each(function () {
            var $element = $(this);
            var widget = $element.data("fileUpload");
            var resourceURL = $element.parents("form.cq-dialog").attr("action");

            if (widget && !$element.parents(".cq-wcm-pagethumbnail").length) {
                new ns.FileUploadField(widget, resourceURL, getAllowUploadFromDesign(this));
            }
        });
    });


}(jQuery, Granite, jQuery(document), this, document));
