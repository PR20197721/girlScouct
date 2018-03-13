(function($) {
  CUI.FileChunkedUpload = new Class( /** @lends CUI.FileChunkedUpload# */ {
    toString: 'FileChunkedUpload',
    extend: CUI.FileUpload,

    /**
     Triggered when a file is selected and accepted into the queue

     @name CUI.FileUpload#fileselected
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when a selected file is rejected before upload

     @name CUI.FileUpload#filerejected
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {String} evt.message            The reason why the file has been rejected
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when the internal upload queue changes (file added, file uploaded, etc.)

     @name CUI.FileUpload#queuechanged
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {String} evt.operation          The operation on the queue (ADD or REMOVE)
     @param {int} evt.queueLength           The number of items in the queue
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when selected files list is processed

     @name CUI.FileUpload#filelistprocessed
     @event

     @param {Object} evt                    Event object
     @param {int} evt.addedCount            The number of files that have been added to the processing list
     @param {int} evt.rejectedCount         The number of files that have been rejected
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file starts

     @name CUI.FileUpload#fileuploadstart
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file progresses

     @name CUI.FileUpload#fileuploadprogress
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event (from which the upload ratio can be calculated)
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file is completed (for non-HTML5 uploads only, regardless of success status)

     @name CUI.FileUpload#fileuploadload
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {String} evt.content            The server response to the upload request, which needs to be analyzed to determine if upload was successful
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file succeeded

     @name CUI.FileUpload#fileuploadsuccess
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file failed

     @name CUI.FileUpload#fileuploaderror
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {String} evt.message            The reason why the file upload failed
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file has been cancelled

     @name CUI.FileUpload#fileuploadcanceled
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a file chunk starts. If chunk's offset is zero
     CUI.FileUpload#fileuploadstart is also triggered.

     @name CUI.FileChunkedUpload#filechunkeduploadstart
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.offset             Number representing offset of chunk in item
     @param {Object} evt.chunk              Object representing a file chunk
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when  upload of a chunk progresses. Take offset into account to
     calculate the progress of complete file.

     @name CUI.FileChunkedUpload#filechunkeduploadprogress
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.offset             Number representing offset of chunk in item
     @param {Object} evt.chunk              Object representing a file chunk
     @param {Object} evt.originalEvent      The original upload event (from which the upload ratio can be calculated)
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a chunk succeeded. If uploaded chunk is last chunk
     of file, CUI.FileUpload#fileuploadsuccess is also triggered.

     @name CUI.FileChunkedUpload#filechunkeduploadsuccess
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.offset             Number representing offset of chunk in item
     @param {Object} evt.chunk              Object representing a file chunk
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a chunk failed

     @name CUI.FileChunkedUpload#filechunkeduploaderror
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.offset             Number representing offset of chunk in item
     @param {Object} evt.chunk              Object representing a file chunk
     @param {Object} evt.originalEvent      The original upload event
     @param {String} evt.message            The reason why the chunk upload failed
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when upload of a chunk has been cancelled

     @name CUI.FileChunkedUpload#filechunkeduploadcanceled
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.offset             Number representing offset of chunk in item
     @param {Object} evt.chunk              Object representing a file chunk
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when query of chunk upload has been successful.

     @name CUI.FileChunkedUpload#filechunkeduploadquerysuccess
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.json               Json result of query chunk upload
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when query of chunk upload is error.

     @name CUI.FileChunkedUpload#filechunkeduploadqueryerror
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {String} evt.message            The reason why the query chunk upload failed
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when delete of chunk upload has been successful.

     @name CUI.FileChunkedUpload#filechunkeduploaddeletesuccess
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when delete of chunk upload is error.

     @name CUI.FileChunkedUpload#filechunkeduploaddeleteerror
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.item               Object representing a file item
     @param {Object} evt.originalEvent      The original upload event
     @param {String} evt.message            The reason why the delete of chunk upload failed
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when dragging into a drop zone

     @name CUI.FileUpload#dropzonedragenter
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.originalEvent      The original mouse drag event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when dragging over a drop zone

     @name CUI.FileUpload#dropzonedragover
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.originalEvent      The original mouse drag event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when dragging out of a drop zone

     @name CUI.FileUpload#dropzonedragleave
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.originalEvent      The original mouse drag event
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     Triggered when dropping files in a drop zone

     @name CUI.FileUpload#dropzonedrop
     @event

     @param {Object} evt                    Event object
     @param {Object} evt.originalEvent      The original mouse drop event
     @param {FileList} evt.files            The list of dropped files
     @param {Object} evt.fileUpload         The file upload widget
     */

    /**
     @extends CUI.FileUpload
     @classdesc A file upload widget which support chunked upload.

     @desc Creates a file upload field
     @constructs

     @param {Object}   options                                    Component options
     @param {String}   [options.name="file"]                      (Optional) name for an underlying form field.
     @param {String}   [options.placeholder=null]                 Define a placeholder for the input field
     @param {String}   [options.uploadUrl=null]                   URL where to upload the file. (If none is provided, the wigdet is disabled)
     @param {String}   [options.uploadUrlBuilder=null]            Upload URL builder
     @param {boolean}  [options.disabled=false]                   Is this component disabled?
     @param {boolean}  [options.multiple=false]                   Can the user upload more than one file?
     @param {int}      [options.sizeLimit=null]                   File size limit
     @param {Array}    [options.mimeTypes=[]]                     Mime types allowed for uploading (proper mime types, wildcard "*" and file extensions are supported)
     @param {boolean}  [options.autoStart=false]                  Should upload start automatically once the file is selected?
     @param {String}   [options.fileNameParameter=null]           Name of File name's parameter
     @param {boolean}  [options.useHTML5=true]                    (Optional) Prefer HTML5 to upload files (if browser allows it)
     @param {Mixed}    [options.dropZone=null]                    (Optional) jQuery selector or DOM element to use as dropzone (if browser allows it)
     @param {boolean}  [options.chunkUploadSupported=false]       (Optional) flag to indicate if chunk upload is supported
     @param {Number}   [options.chunkSize=null]                   (Optional) Size of chunk
     @param {Number}   [options.chunkUploadMinFileSize=null]      (Optional) Minimum file size which will use chunk upload
     @param {Object}   [options.events={}]                        (Optional) Event handlers
     */

    defaults: $.extend(CUI.FileUpload.prototype.defaults, {
      'chunkSize': null,
      'chunkUploadMinFileSize': null,
      'chunkUploadSupported': false,
    }),

    /** @ignore */
    _readDataFromMarkup: function() {
      var self = this;
      if (this.inputElement.data("chunkUploadSupported")) {
        this.options.chunkUploadSupported = this.inputElement.data("chunkUploadSupported") === true;
      }
      if (this.inputElement.data("chunkSize")) {
        this.options.chunkSize = this.inputElement.data("chunkSize");
      }
      if (this.inputElement.data("chunkUploadMinFileSize")) {
        this.options.chunkUploadMinFileSize = this.inputElement.data("chunkUploadMinFileSize");
      }
      this.inherited(arguments);
    },

    /**
    Upload a file item in chunks.

    @param {Object} item     Object representing a file item
    */
    startChunkedUpload: function(item) {
      this.resumeChunkedUpload(item, 0);
    },

    /**
    Resumes the upload of a file item from offset.

    @param {Object} item     Object representing a file item
    @param {Number} offset     Object representing a file item
    */
    resumeChunkedUpload: function(item, offset) {
      var self = this;
      if (this.canChunkedUpload(item) === false) {
        throw "cannot start chunk upload on [" + item.file.name + "]";
      }
      item.xhr = new XMLHttpRequest();
      var upload = item.xhr.upload;

      // TODO : encoding of special characters in file names
      var file = item.file;
      var fileName = item.fileName;
      var f = new FormData();
      if (self.options.fileNameParameter) {
        // Custom file and file name parameter
        var sliceMethod = this._getFileSliceMethod(file);
        var chunk = file[sliceMethod](offset, offset +
        this.options.chunkSize);
        f.append(self.inputElement.attr("name"), chunk);
        f.append(self.options.fileNameParameter || "fileName", fileName);
        f.append(self.inputElement.attr("name") + "@Offset", offset);
        f.append(self.inputElement.attr("name") + "@Length", file.size);
        if(item["parameters"] !== null && item["parameters"] !== undefined) {
          item.parameters.forEach(function(additionalParam) {
            f.append(additionalParam.name, additionalParam.value);
          });
        }
        item.xhr.addEventListener("load", function (e) {
          self._onChunkUploadLoad(e, item, offset, chunk);
        }, false);
        upload.addEventListener("progress", function(e) {
          self._onChunkUploadProgress(e, item, offset, chunk);
        }, false);
        item.xhr.addEventListener("loadstart", function(e) {
          self._onChunkUploadStart(e, item, offset, chunk);
        }, false);

        item.xhr.addEventListener("error", function(e) {
          self._onChunkUploadError(e, item, offset, chunk);
        }, false);
        item.xhr.addEventListener("timeout", function(e) {
          self._onChunkUploadError(e, item, offset, chunk);
        }, false);
        item.xhr.addEventListener("abort", function(e) {
          self._onChunkUploadCanceled(e, item, offset, chunk);
        }, false);

        f.append("_charset_", "utf-8");
        item.xhr.open("POST", self.options.uploadUrl + "?:ck=" +
        new Date().getTime(), true);
        item.xhr.send(f);
      }
    },

    /**
    Query chunk upload at the url.

    @param {String} url     The url to query chunk upload
    @param {Object} item    Object representing a file item. it is passed in
    event listener callbacks
    */
    queryChunkedUpload: function(url, item) {
      var self = this;
      var xhr = new XMLHttpRequest();
      xhr.addEventListener("load", function(e) {
        self._onQueryChunkUploadLoad(e, item);
      }, false);
      xhr.addEventListener("error", function(e) {
        self._onQueryChunkUploadError(e, item);
      }, false);
      xhr.addEventListener("timeout", function(e) {
        self._onQueryChunkUploadError(e, item);
      }, false);
      xhr.addEventListener("abort", function(e) {
        self._onQueryChunkUploadError(e, item);
      }, false);

      xhr.open("GET", url + "?:ck=" + new Date().getTime() +
      "&_charset_=utf-8", true);
      xhr.send();

    },

    /**
     * Deletes chunks at url. No-op if no chunks are present in url.
     */
    deleteChunkedUpload: function(url, item) {

      var self = this;
      var xhr = new XMLHttpRequest();
      var f = new FormData();
      f.append(":operation", "delete");
      f.append(":applyToChunks", "true");
      f.append("_charset_", "utf-8");

      xhr.addEventListener("load", function(e) {
        self._onDeleteChunkUploadLoad(e, item);
      }, false);
      xhr.addEventListener("error", function(e) {
        self._onDeleteChunkUploadError(e, item);
      }, false);
      xhr.addEventListener("timeout", function(e) {
        self._onDeleteChunkUploadError(e, item);
      }, false);
      xhr.addEventListener("abort", function(e) {
        self._onDeleteChunkUploadError(e, item);
      }, false);

      xhr.open("POST", url + "?:ck=" + new Date().getTime(), true);
      xhr.send(f);
    },

    /**
     * Returns true if chunk upload can be used for file item.
     */
    canChunkedUpload: function(item) {
      if (this.options.useHTML5 === true &&
        this.options.chunkUploadSupported === true &&
        item.file.size > this.options.chunkUploadMinFileSize &&
        window.FormData) {
        try {
          this._getFileSliceMethod(item.file);
        } catch (err) {
          return false;
        }
        return true;
      }
      return false;
    },

    /***********************************************************************
     * Cancel upload of a file item
     *
     * @param {Object} item Object representing a file item
     */
    cancelChunkedUpload: function(item) {
      item.xhr.abort();
    },

    /* * @ignore */
    _onChunkUploadStart: function(e, item, offset, chunk) {
      // if first chunk fire fileuploadstart event
      if (offset === 0) {
        this._onUploadStart(e, item);
      }
      this.$element.trigger({
        type: "filechunkeduploadstart",
        item: item,
        offset: offset,
        chunk: chunk,
        originalEvent: e,
        fileUpload: this
      });
    },

    _onChunkUploadLoad: function(e, item, offset, chunk) {
      var request = e.target;
      if (request.readyState === 4) {
        this._internalOnChunkUploadLoad(e, item, offset, chunk,
          request.status, request.responseText);
      }
    },

    /* * @ignore */
    _onChunkUploadProgress: function(e, item, offset, chunk) {
      // Update progress bar
      this.$element.trigger({
        type: "filechunkeduploadprogress",
        item: item,
        offset: offset,
        chunk: chunk,
        originalEvent: e,
        fileUpload: this
      });

    },

    /* * @ignore */
    _onChunkUploadError: function(e, item, offset, chunk) {
      this.$element.trigger({
        type: "filechunkeduploaderror",
        offset: offset,
        chunk: chunk,
        item: item,
        originalEvent: e,
        fileUpload: this
      });
    },

    /* * @ignore */
    _onChunkUploadCanceled: function(e, item, offset, chunk) {
      this.$element.trigger({
        type: "filechunkeduploadcanceled",
        offset: offset,
        chunk: chunk,
        item: item,
        originalEvent: e,
        fileUpload: this
      });
    },

    /* * @ignore */
    _onQueryChunkUploadLoad: function(e, item) {
      var request = e.target;
      if (request.readyState === 4) {
        if (CUI.util.HTTP.isOkStatus(request.status)) {
          this.$element.trigger({
            type: "filechunkeduploadquerysuccess",
            item: item,
            originalEvent: e,
            fileUpload: this
          });
        }
        else {
          this._onQueryChunkUploadError(e, item);
        }
      }
    },

    /* * @ignore */
    _onQueryChunkUploadError: function(e, item) {
      var request = e.target;
      this.$element.trigger({
        type: "filechunkeduploadqueryerror",
        item: item,
        originalEvent: e,
        message: request.responseText,
        fileUpload: this
      });
    },

    /* * @ignore */
    _onDeleteChunkUploadLoad: function(e, item) {
      var request = e.target;
      if (request.readyState === 4) {
        if (CUI.util.HTTP.isOkStatus(request.status)) {
          this.$element.trigger({
            type: "filechunkeduploaddeletesuccess",
            item: item,
            originalEvent: e,
            fileUpload: this
          });
        }
        else {
          this._onDeleteChunkUploadError(e, item);
        }
      }

    },

    /* * @ignore */
    _onDeleteChunkUploadError: function(e, item) {
      var request = e.target;
      this.$element.trigger({
        type: "filechunkeduploaddeleteerror",
        item: item,
        originalEvent: e,
        message: request.responseText,
        fileUpload: this
      });
    },

    /* * @ignore */
    _internalOnChunkUploadLoad: function(e, item, offset, chunk, requestStatus, responseText) {
      if (CUI.util.HTTP.isOkStatus(requestStatus)) {
        this.$element.trigger({
          type: "filechunkeduploadsuccess",
          offset: offset,
          chunk: chunk,
          item: item,
          originalEvent: e,
          fileUpload: this
        });
        if (offset + chunk.size === item.file.size) {
          this.$element.trigger({
            type: "fileuploadsuccess",
            item: item,
            originalEvent: e,
            fileUpload: this
          });
          // Remove file name element if needed
          if (this.fileNameElement) {
            this.fileNameElement.remove();
          }

          // Remove queue item
          this.uploadQueue.splice(this._getQueueIndex(item.fileName), 1);
          this.$element.trigger({
            type: "queuechanged",
            item: item,
            operation: "REMOVE",
            queueLength: this.uploadQueue.length,
            fileUpload: this
          });
        }
        else {
          this.resumeChunkedUpload(item, offset + chunk.size);
        }

      }
      else {
        this.$element.trigger({
          type: "filechunkeduploaderror",
          offset: offset,
          chunk: chunk,
          item: item,
          originalEvent: e,
          message: responseText,
          fileUpload: this
        });
      }
    },

    /**
     * Returns the file slice method that is applicable based on the browser
     * @param file
     * @return {string}
     * @private
     */
    _getFileSliceMethod: function(file) {
      if ('mozSlice' in file) {
        return 'mozSlice';
      }
      else if ('webkitSlice' in file) {
        return 'webkitSlice';
      }
      else if ('slice' in file) {
        return 'slice';
      }
      else {
        // file slice is not supported on the environment
        // use a fallback mechanism
        throw new Error('\'Slicing file\' operation not supported');
      }
    }
  });

  CUI.Widget.registry.register("filechunkedupload", CUI.FileChunkedUpload);

  // Data API
  if (CUI.options.dataAPI) {
    $(document).on("cui-contentloaded.data-api", function(e) {
      CUI.FileChunkedUpload.init($("[data-init~='filechunkedupload']", e.target));
    });
  }

}(window.jQuery));
