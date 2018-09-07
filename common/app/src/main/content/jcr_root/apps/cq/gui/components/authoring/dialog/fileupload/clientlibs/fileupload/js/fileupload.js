/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2016 Adobe Systems Incorporated
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
;(function ($, ns, channel, document, window, undefined) {
	"use strict";

	var FILEUPLOAD_EVENT_NAMESPACE = ".fileupload";
	var _thumbnailSelector = "[data-cq-fileupload-thumbnail-img]";
	var _editButtonSelector = ".cq-FileUpload-edit";
	var _isActiveClass = "is-active";
	var _isFilledClass = "is-filled";
	var _imageEditorFields = ["imageMap", "imageCrop", "imageRotate"];
	var _fileDeleteParam = "@Delete";
	var _additionalImageEditorClass = 'cq-ImageEditor-param';

	/**
	 * Returns the value carried by the design resource
	 *
	 * @param {HTMLElement} element     - The DOM element representing the fileUpload
	 * @returns {boolean|null}
	 */
	function getAllowUploadFromDesign(element) {
		// In case the dialog has been opened outside of the Editor context, be conservative and restrict the upload
		if (!ns.author) {
			return;
		}

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

	// CQ-103855
	function open(url) {
		var winMode = $("meta[name='user.preferences.winmode']").attr("content");

		if (winMode === "single") {
			window.location.href = url;
		} else {
			window.open(url);
		}
	}

	/**
	 * Creates a new File Upload component
	 *
	 * @param {{}} config                      - Configuration object
	 * @param {HTMLElement} config.element       - The File Upload element
	 * @param {boolean} [config.allowUpload]   - Should the component allow uploading files from the file system
	 * @constructor
	 */
	var FileUpload = function (config) {
		var self = this;

		self._element = config.element;
		self._$element = $(config.element);

		var $fileInput = self._$element.find("input[type=file]");
		var filename = $fileInput.attr('name');
		var path = filename.substr(0, filename.lastIndexOf("/")) || "";
		if (path !== "") {
			path += "/";
		}
		self.path = path;

		// Allow File upload by default unless it is not explicitly disabled
		if (config.allowUpload == null) {
			// Configuration at the dialog resource level
			self._allowUpload = self._element.hasAttribute("data-cq-fileupload-allowupload");
		} else {
			if (typeof config.allowUpload === "string") {
				config.allowUpload = config.allowUpload == "true";
			}
			// Configuration provided by the constructor
			self._allowUpload = config.allowUpload;
		}

		if (!self._element.action) {
			self._element.action = self._$element.closest("form.cq-dialog").attr("action");
		}

		// Avoid sending hidden inputs as additional parameters for the temporary upload
		self._element.parameters = [];

		self._inputs = [];
		self._$element.find("input[type='hidden'][data-cq-fileupload-parameter]").each(function() {
			var key = this.getAttribute("data-cq-fileupload-parameter");
			self._inputs[key] = this;
		});

		// Determine if we're in a multifield and set the indexes accordingly.
		var multiFieldParent = self._$element.parents('coral-multifield')
		if(multiFieldParent.length > 0){
			// Determine the multifield index.
			var multiFieldItemParent = self._$element.parents('coral-multifield-item')
			var currentIndex = multiFieldParent.find('coral-multifield-item').index(multiFieldItemParent);

			// Image source based on multifield position.
			var fileLocation = self._element.action + '/' + multiFieldParent[0].dataset.graniteCoralMultifieldName.split('\./')[1] + '/item' + currentIndex + '/' + self.path.replace('/', '');
			var thumbnailSrc = fileLocation + '.img.png';

			var thumbnailSection = self._$element.find(_thumbnailSelector);
			if(thumbnailSection.children().length == 0) {
				thumbnailSection.empty().append($('<img>').attr('src', thumbnailSrc));

				// Find and set the parent hero banner element switch.
				var heroBannerElement = self._$element.parents('.hero-banner-element');
				if(heroBannerElement.length > 0){
					var heroBannerImagePreview = heroBannerElement.find('.heroBannerElementImagePreview');
					if(heroBannerImagePreview.css('background-image') == 'none') {
						heroBannerImagePreview.css({'background-image': 'url(' + thumbnailSrc + ')'});
						multiFieldParent.css({'padding' : 0});
					}
				}
			}

			// TODO@MK Join these up to make less total calls.
			$.get(fileLocation + '.json').then(function(result){
				if(result.fileReference && self._inputs["filereference"].disabled) {
					self._updateHiddenInput(self._inputs["filereference"], {
						value: result.fileReference,
						disabled: false
					});
				}
				if(result.fileName && self._inputs["filername"].disabled) {
					self._updateHiddenInput(self._inputs["filername"], {
						value: result.fileName,
						disabled: false
					});
				}
			});

			self._updateHiddenInput(self._inputs["filedelete"], { value: "false" });
		}

		//self._removeDuplicateHiddenInputs();
		self._bindEvents();

		if(self._hasImageMimeType()) {
			self._addImageEditorFields();
		}
	};

	/** @private */
	FileUpload.prototype._appendHiddenInput = function (name, config) {
		var $input = this._$element.closest("form").find("input[name='" + name + "']");

		if ($input.length === 0) {
			$input = $("<input type='hidden'/>").attr("name", name);
			if(config.hasOwnProperty("class")) {
				$input.attr("class", config['class']);
			}
			this._element.appendChild($input[0]);
		} else if ($input.closest(this._element).length === 0) {
			return; // there is a duplicate input elsewhere in the form
		}

		this._updateHiddenInput($input[0], config);

		return $input[0];
	};

	/** @private */
	FileUpload.prototype._updateHiddenInput = function (element, config) {
		if (element === undefined) {
			return;
		}

		if (config !== undefined) {
			if (config.value !== undefined) {
				element.setAttribute("value", config.value);
			}

			if (config.disabled) {
				$(element).prop("disabled", true);
			} else if (config.disabled === false) {
				$(element).removeProp("disabled");
			}
		}
	};

	/** @private */
	FileUpload.prototype._removeDuplicateHiddenInputs = function () {
		var self = this;
		var $inputs = self._$element.find("input[type='hidden']");

		$inputs.each(function() {
			var $inputs = self._$element.closest("form").find("input[name='" + this.getAttribute("name") + "']");
			if ($inputs.length > 1) {
				this.parentNode.removeChild(this);
			}
		});
	};

	/** @private */
	FileUpload.prototype._addParamInputs = function(params) {
		var i;

		if (typeof(params) === "string") {
			try {
				params = JSON.parse(params);
			} catch (e) {
			}
		}

		if (typeof(params) === "object") {
			for (i in params) {
				if (params.hasOwnProperty(i)) {
					var input = this._appendHiddenInput(i, {
						value: params[i],
						disabled: false
					});
					if (input) {
						input.setAttribute("data-cq-fileupload-parameter", "additional");
					}
				}
			}
		}
	};

	/** @private */
	FileUpload.prototype._removeAdditionalSlingParamHiddenInputs = function () {
		this._$element.find("[data-cq-fileupload-parameter='additional']").remove();
	};

	/** @private */
	FileUpload.prototype._bindEvents = function () {
		var self = this;

		self._$element.find("[coral-fileupload-clear]").on("click", function (e) {
			self._updateHiddenInput(self._inputs["filename"], { value: "", disabled: false });
			self._updateHiddenInput(self._inputs["filereference"], { value: "", disabled: false });
			self._$element.find("." + _additionalImageEditorClass).removeAttr("disabled").val("true");
			self._updateHiddenInput(self._inputs["filedelete"], { value: "true", disabled: false });
			self._updateHiddenInput(self._inputs["filemovefrom"], { value: "", disabled: true });
			self._removeAdditionalSlingParamHiddenInputs();

			self._element.classList.remove(_isFilledClass);
			self._$element.find(_thumbnailSelector).empty();
		});

		// authoring drop controller e.g. drag and drop from asset finder
		self._$element.on("assetselected", function (event) {
			if (self._element.disabled) {
				return;
			}

			if (!self._isMimeTypeAllowed(event.mimetype)) {
				return;
			}

			self._removeAdditionalSlingParamHiddenInputs();
			self._updateHiddenInput(self._inputs["filereference"], { value: event.path, disabled: false });
			self._updateHiddenInput(self._inputs["filedelete"], { value: "false" });
			self._$element.find("." + _additionalImageEditorClass).removeAttr("disabled").val("true");
			self._updateHiddenInput(self._inputs["filemovefrom"], { value: "false" });
			self._addParamInputs(event.param);

			self._element.classList.add(_isFilledClass);
			self._$element.find(_thumbnailSelector).empty().append(event.thumbnail);

			// replace the backdrop.
			if(self.path.indexOf('regular') > -1){

				// Find and set the parent hero banner element switch.
				var heroBannerElement = self._$element.parents('.hero-banner-element');
				if(heroBannerElement.length > 0){
					heroBannerElement.find('.heroBannerElementImagePreview').css({'background-image': 'url(' + $(event.thumbnail).attr('src') + ')'});
				}
			}

			self._$element.trigger("change");

			// enable "edit" button since the asset is available in DAM
			self._$element.find(_editButtonSelector)
				.data("cqFileuploadFilereference", event.path)
				.prop("disabled", false);
		});

		if (self._allowUpload) {
			// file added from the file system
			self._element.on("coral-fileupload:fileadded", function (event) {
				self._removeAdditionalSlingParamHiddenInputs();

				if (self._hasImageMimeType()) {
					var $form = self._$element.closest("form");
					if ($form) {
						var editablePath = $form.attr("action").replace("_jcr_content", "jcr:content");
						if (ns.author && ns.author.editables) {
							var editables = ns.author.editables.find(editablePath);
							if (editables.length > 0 && typeof editables[0].getDropTarget === "function") {
								var dropTarget = editables[0].getDropTarget("image");
								if (dropTarget) {
									self._addParamInputs(dropTarget.params);
								}
							}
						}
					}
				}


				var temporaryFileName = self._element.getAttribute("data-cq-fileupload-temporaryfilename");

				// Determine if we're in a multifield and set the indexes accordingly.
				var multiFieldParent = self._$element.parents('coral-multifield')
				if(multiFieldParent.length > 0){
					// Determine the multifield index.
					var multiFieldItemParent = self._$element.parents('coral-multifield-item')
					var currentIndex = multiFieldParent.find('coral-multifield-item').index(multiFieldItemParent);

					// Image source based on multifield position.
					var baseFileName = './' + multiFieldParent[0].dataset.graniteCoralMultifieldName.split('\./')[1] + '/item' + currentIndex + '/';
					temporaryFileName = baseFileName + temporaryFileName;
				}


				var $fileInput = self._$element.find("input[type=file]");
				var item = event.detail.item;
				var filename = item.file.name;
				var prevName = $fileInput.attr("name");

				$fileInput.attr("name", temporaryFileName);
				self._element.upload(filename);
				$fileInput.attr("name", prevName);
				self._$element.trigger("change");

				// disable "edit" button since the asset isn't available in DAM
				self._$element.find(_editButtonSelector).prop("disabled", true);
			});

			// temporary file uploaded with success
			self._element.on("coral-fileupload:load", function (event) {
				var $thumbnail = self._$element.find(_thumbnailSelector);
				var file = event.detail.item._originalFile;

				$thumbnail.empty();

				if (file.type.indexOf("image") !== -1) {
					(function ($thumbImage, reader) {
						reader.onload = function(e) {
							$thumbImage.append($("<img/>").attr("src", e.target.result));
						};

						reader.readAsDataURL(file);
					}($thumbnail, new FileReader()));
				} else {
					var $message = $("<p>" + file.name + "</p>");
					$thumbnail.append($message);
				}

				var temporaryFilePath = self._element.getAttribute("data-cq-fileupload-temporaryfilepath");

				// Determine if we're in a multifield and set the indexes accordingly.
				var multiFieldParent = self._$element.parents('coral-multifield')
				if(multiFieldParent.length > 0){
					var temporaryFileName = self._element.getAttribute("data-cq-fileupload-temporaryfilename");
					// Determine the multifield index.
					var multiFieldItemParent = self._$element.parents('coral-multifield-item')
					var currentIndex = multiFieldParent.find('coral-multifield-item').index(multiFieldItemParent);

					// Image source based on multifield position.
					var baseFileName = multiFieldParent[0].dataset.graniteCoralMultifieldName.split('\./')[1] + '/item' + currentIndex + '/';
					temporaryFilePath = temporaryFilePath.replace(temporaryFileName, '') + baseFileName + temporaryFileName;
				}

				self._updateHiddenInput(self._inputs["filename"], { value: file.name, disabled: false });
				self._updateHiddenInput(self._inputs["filereference"], { value: "", disabled: false });
				self._updateHiddenInput(self._inputs["filedelete"], { disabled: true });
				self._updateHiddenInput(self._inputs["filemovefrom"], { value: temporaryFilePath, disabled: false });
				self._$element.find("." + _additionalImageEditorClass).removeAttr("disabled").val("true");

				self._temporaryFileUploaded = true;
				self._element.classList.add(_isFilledClass);
				self._element.classList.remove(_isActiveClass);
			});

			channel.off("dialog-closed" + FILEUPLOAD_EVENT_NAMESPACE).one("dialog-closed" + FILEUPLOAD_EVENT_NAMESPACE, function (e) {
				if (self._temporaryFileUploaded) {
					self._deleteTemporaryFileUpload();
				}
			});

			channel
				.off("dragover" + FILEUPLOAD_EVENT_NAMESPACE).on("dragover" + FILEUPLOAD_EVENT_NAMESPACE, function(event) {
				if (event.originalEvent.dataTransfer && event.originalEvent.dataTransfer.files) {
					self._element.classList.add(_isActiveClass);
				}
			})
				.off("dragleave" + FILEUPLOAD_EVENT_NAMESPACE).on("dragleave" + FILEUPLOAD_EVENT_NAMESPACE, function() {
				self._element.classList.remove(_isActiveClass);
			});
		}
	};

	/**
	 * Sends a delete operation to remove the temporary file upload
	 * @returns $.Deferred
	 * @private
	 */
	FileUpload.prototype._deleteTemporaryFileUpload = function () {
		var data = {};

		data[this._element.getAttribute("data-cq-fileupload-temporaryfiledelete")] = true;
		$.post(this._element.action, data);
	};

	/** @private */
	FileUpload.prototype._isMimeTypeAllowed = function (mimeType)  {
		var isAllowed = false;
		var mimeTypes = this._element.accept.split(",");

		if (mimeTypes == "") {
			return true;
		}

		mimeTypes.some(function (allowedMimeType) {
			if (allowedMimeType === "*" || (new RegExp(allowedMimeType)).test(mimeType)) {
				isAllowed = true;
				return true;
			}
		});

		return isAllowed;
	};

	/** @private */
	FileUpload.prototype._hasImageMimeType = function() {
		var mimeTypes = this._element.accept.split(",");
		var hasImageMimeType = false;

		for (var i = 0; i < mimeTypes.length; i++) {
			hasImageMimeType = mimeTypes[i].match("image/*");

			if (hasImageMimeType) {
				break;
			}
		}

		return hasImageMimeType;
	};

	FileUpload.prototype._addImageEditorFields = function() {
		var self = this;
		_imageEditorFields.forEach(function (item) {
			self._appendHiddenInput(self.path + item + _fileDeleteParam, {
				"class": _additionalImageEditorClass,
				disabled: true,
				value: ""
			});
		});
	};

	channel.on("foundation-contentloaded", function (event) {
		$(event.target).find("coral-fileupload.cq-FileUpload").each(function() {
			Coral.commons.ready(this, function (fileUpload) {
				var fu = new FileUpload({
					element: fileUpload,
					allowUpload: getAllowUploadFromDesign(fileUpload)}
				);
				fileUpload.fu = fu;
			});
		});
	});



	channel.off("click" + FILEUPLOAD_EVENT_NAMESPACE, _editButtonSelector).on("click" + FILEUPLOAD_EVENT_NAMESPACE, _editButtonSelector, function (event) {
		// "cq-FileUpload-edit" button is only present if the fileupload holds a fileReference
		var $target = $(event.currentTarget);
		var fileReference = $target.data("cqFileuploadFilereference");
		var viewInAdminURI = $target.data("cqFileuploadViewinadminuri");
		var url = Granite.URITemplate.expand(viewInAdminURI, {
			item: fileReference
		});

		open(Granite.HTTP.externalize(url));
	});

}(Granite.$, Granite, jQuery(document), document, this));
